package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AgendaKegiatan
import com.example.data.model.Inventaris
import com.example.data.model.IuranRecord
import com.example.data.model.JimpitanRecord
import com.example.data.model.KasTransaction
import com.example.data.model.OfficialWargaData
import com.example.data.model.Warga
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Database(
  entities = [
    Warga::class,
    IuranRecord::class,
    JimpitanRecord::class,
    KasTransaction::class,
    Inventaris::class,
    AgendaKegiatan::class
  ],
  version = 2,
  exportSchema = false
)
abstract class GrisesDatabase : RoomDatabase() {
  abstract fun wargaDao(): WargaDao
  abstract fun iuranDao(): IuranDao
  abstract fun jimpitanDao(): JimpitanDao
  abstract fun kasDao(): KasDao
  abstract fun inventarisDao(): InventarisDao
  abstract fun agendaDao(): AgendaDao

  companion object {
    @Volatile
    private var INSTANCE: GrisesDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): GrisesDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          GrisesDatabase::class.java,
          "grises_sedayu_database.db"
        )
          .fallbackToDestructiveMigration(true)
          .addCallback(DatabaseCallback(scope))
          .build()
        INSTANCE = instance
        instance
      }
    }

    private class DatabaseCallback(
      private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
          scope.launch(Dispatchers.IO) {
            populateInitialData(database)
          }
        }
      }

      override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        INSTANCE?.let { database ->
          scope.launch(Dispatchers.IO) {
            // Automatically ensure all 58 official warga from PDF are present
            if (database.wargaDao().getCount() < 58) {
              populateInitialData(database)
            }
          }
        }
      }
    }

    suspend fun populateInitialData(db: GrisesDatabase) {
      val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
      val today = dateFormat.format(Date())
      val cal = Calendar.getInstance()
      cal.add(Calendar.DAY_OF_YEAR, -1)
      val yesterday = dateFormat.format(cal.time)
      val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
      val currentYear = Calendar.getInstance().get(Calendar.YEAR)

      // 1. Data Resmi Warga Perumahan Griya Sedayu Selaras (58 Rumah dari Dokumen PDF)
      val wargaList = OfficialWargaData.getOfficialWargaList()
      db.wargaDao().clearAllWarga()
      db.wargaDao().insertAll(wargaList)

      // 2. Data Iuran Bulan Ini (Berdasarkan status rumah di checklist)
      db.iuranDao().clearAllIuran()
      val iuranList = wargaList.mapIndexed { index, w ->
        // Warga yang wajib iuran: Dihuni Pemilik, Dihuni Kontrak, dan Kosong (Membayar Iuran)
        val isWajibBayar = w.statusHunian != "Belum Ada Pemilik" && w.statusHunian != "Kosong (Ada Pemilik)"
        val isPaid = isWajibBayar && (index % 3 != 1) // mix status lunas untuk demo realitas
        val nominal = if (isWajibBayar) 65000L else 0L

        IuranRecord(
          id = w.id,
          wargaId = w.id,
          namaKK = w.namaKK,
          alamat = w.alamatLengkap,
          bulan = currentMonth,
          tahun = currentYear,
          nominalIuran = if (isWajibBayar) 50000L else 0L,
          nominalSampah = if (isWajibBayar) 15000L else 0L,
          totalTagihan = nominal,
          isLunas = isPaid,
          tanggalBayar = if (isPaid) "$currentYear-${String.format("%02d", currentMonth)}-05" else null,
          metodeBayar = if (index % 2 == 0) "Transfer Bank" else "Tunai",
          penerima = if (isPaid) "Eko (Bendahara - B 19)" else null,
          catatan = w.catatan
        )
      }
      db.iuranDao().insertAll(iuranList)

      // 3. Jimpitan Ronda (500 / rumah / hari)
      db.jimpitanDao().clearAllJimpitan()
      val occupiedWarga = wargaList.filter { it.isDihuni }
      val jimpitanYesterday = occupiedWarga.map { w ->
        JimpitanRecord(
          wargaId = w.id,
          namaKK = w.namaKK,
          alamat = w.alamatLengkap,
          tanggal = yesterday,
          nominal = 500L,
          isCollected = true,
          petugasRonda = "Regu Ronda Malam (Joko Tri & Rudi)",
          isSetorKas = true
        )
      }
      val jimpitanToday = occupiedWarga.take(28).map { w ->
        JimpitanRecord(
          wargaId = w.id,
          namaKK = w.namaKK,
          alamat = w.alamatLengkap,
          tanggal = today,
          nominal = 500L,
          isCollected = true,
          petugasRonda = "Regu Ronda Malam (Sony & Beni)",
          isSetorKas = false
        )
      }
      db.jimpitanDao().insertAll(jimpitanYesterday)
      db.jimpitanDao().insertAll(jimpitanToday)

      // 4. Kas Masuk & Keluar Transparan
      val kasList = listOf(
        KasTransaction(
          tipe = "MASUK",
          kategori = "Saldo Awal",
          nominal = 6500000L,
          tanggal = "$currentYear-${String.format("%02d", currentMonth)}-01",
          keterangan = "Saldo awal kas paguyuban Griya Sedayu Selaras",
          pencatat = "Eko (Bendahara - B 19)",
          noBukti = "KAS-IN-001"
        ),
        KasTransaction(
          tipe = "MASUK",
          kategori = "Iuran Warga",
          nominal = 1625000L,
          tanggal = "$currentYear-${String.format("%02d", currentMonth)}-05",
          keterangan = "Setoran Iuran & Sampah Warga 25 Rumah (Rp 65.000/rumah)",
          pencatat = "Eko (Bendahara - B 19)",
          noBukti = "KAS-IN-002"
        ),
        KasTransaction(
          tipe = "MASUK",
          kategori = "Jimpitan Ronda",
          nominal = 210000L,
          tanggal = yesterday,
          keterangan = "Setor Jimpitan Ronda malam minggu lalu (Rp 500/rumah)",
          pencatat = "Hengki (Seksi Ronda - B 26)",
          noBukti = "KAS-IN-003"
        ),
        KasTransaction(
          tipe = "MASUK",
          kategori = "Donasi Warga",
          nominal = 350000L,
          tanggal = "$currentYear-${String.format("%02d", currentMonth)}-08",
          keterangan = "Donasi sukarela warga Blok B untuk pengecatan pos ronda",
          pencatat = "Eko (Bendahara - B 19)",
          noBukti = "KAS-IN-004"
        ),
        KasTransaction(
          tipe = "KELUAR",
          kategori = "Kebersihan & Sampah",
          nominal = 550000L,
          tanggal = "$currentYear-${String.format("%02d", currentMonth)}-06",
          keterangan = "Retribusi angkutan truk sampah kelurahan Sedayu periode berjalan",
          pencatat = "Eko (Bendahara - B 19)",
          noBukti = "KAS-OUT-001"
        ),
        KasTransaction(
          tipe = "KELUAR",
          kategori = "Keamanan & Satpam",
          nominal = 1350000L,
          tanggal = "$currentYear-${String.format("%02d", currentMonth)}-10",
          keterangan = "Honor petugas keamanan pos jaga utama Perum Griya Sedayu Selaras",
          pencatat = "Eko (Bendahara - B 19)",
          noBukti = "KAS-OUT-002"
        ),
        KasTransaction(
          tipe = "KELUAR",
          kategori = "Perbaikan Fasum",
          nominal = 220000L,
          tanggal = "$currentYear-${String.format("%02d", currentMonth)}-12",
          keterangan = "Perbaikan lampu penerangan jalan Blok B & Blok C",
          pencatat = "Dewo (Seksi Fasum - B 25)",
          noBukti = "KAS-OUT-003"
        ),
        KasTransaction(
          tipe = "KELUAR",
          kategori = "Konsumsi Pertemuan",
          nominal = 135000L,
          tanggal = yesterday,
          keterangan = "Konsumsi kopi & snack ronda malam gabungan warga Blok A & B",
          pencatat = "Sony (Seksi Ronda - B 23)",
          noBukti = "KAS-OUT-004"
        )
      )
      db.kasDao().insertAll(kasList)

      // 5. Inventarisasi Perumahan
      val inventarisList = listOf(
        Inventaris(
          kodeBarang = "INV-TND-01",
          namaBarang = "Tenda Paguyuban 3x6 Meter",
          kategori = "Peralatan Tenda",
          jumlahTotal = 2,
          jumlahTersedia = 1,
          satuan = "Unit",
          kondisi = "Baik",
          lokasiSimpan = "Gudang Fasum Blok C",
          statusPinjam = "Dipinjam",
          namaPeminjam = "Sony (Blok B 23)",
          tglPinjam = today,
          tglKembaliRencana = "2 hari lagi",
          keperluanPinjam = "Syukuran keluarga"
        ),
        Inventaris(
          kodeBarang = "INV-KRS-01",
          namaBarang = "Kursi Lipat Chitose",
          kategori = "Meja & Kursi",
          jumlahTotal = 100,
          jumlahTersedia = 70,
          satuan = "Unit",
          kondisi = "Baik",
          lokasiSimpan = "Gudang Fasum Blok C",
          statusPinjam = "Dipinjam",
          namaPeminjam = "Sony (Blok B 23)",
          tglPinjam = today,
          tglKembaliRencana = "2 hari lagi",
          keperluanPinjam = "Syukuran keluarga 30 kursi"
        ),
        Inventaris(
          kodeBarang = "INV-SND-01",
          namaBarang = "Sound System Portabel + 2 Mic Wireless",
          kategori = "Sound System",
          jumlahTotal = 1,
          jumlahTersedia = 1,
          satuan = "Set",
          kondisi = "Baik",
          lokasiSimpan = "Balai Warga Blok A",
          statusPinjam = "Tersedia"
        ),
        Inventaris(
          kodeBarang = "INV-MPT-01",
          namaBarang = "Mesin Potong Rumput Stihl 2-Tak",
          kategori = "Alat Kebersihan",
          jumlahTotal = 1,
          jumlahTersedia = 1,
          satuan = "Unit",
          kondisi = "Baik",
          lokasiSimpan = "Gudang Fasum Blok C",
          statusPinjam = "Tersedia"
        ),
        Inventaris(
          kodeBarang = "INV-TNG-01",
          namaBarang = "Tangga Lipat Aluminium 3.8 Meter",
          kategori = "Alat Fasum",
          jumlahTotal = 1,
          jumlahTersedia = 1,
          satuan = "Unit",
          kondisi = "Baik",
          lokasiSimpan = "Pos Ronda Utama",
          statusPinjam = "Tersedia"
        ),
        Inventaris(
          kodeBarang = "INV-GBK-01",
          namaBarang = "Gerobak Sorong Sampah Artco",
          kategori = "Alat Kebersihan",
          jumlahTotal = 2,
          jumlahTersedia = 2,
          satuan = "Unit",
          kondisi = "Baik",
          lokasiSimpan = "Samping Pos Ronda",
          statusPinjam = "Tersedia"
        ),
        Inventaris(
          kodeBarang = "INV-TRP-01",
          namaBarang = "Terpal Biru Tebal 4x6 Meter",
          kategori = "Peralatan Tenda",
          jumlahTotal = 4,
          jumlahTersedia = 4,
          satuan = "Lembar",
          kondisi = "Baik",
          lokasiSimpan = "Gudang Fasum",
          statusPinjam = "Tersedia"
        ),
        Inventaris(
          kodeBarang = "INV-LMP-01",
          namaBarang = "Lampu Sorot LED 100 Watt Stand",
          kategori = "Peralatan Acara",
          jumlahTotal = 2,
          jumlahTersedia = 2,
          satuan = "Unit",
          kondisi = "Baik",
          lokasiSimpan = "Balai Warga",
          statusPinjam = "Tersedia"
        )
      )
      db.inventarisDao().insertAll(inventarisList)

      // 6. Agenda Kegiatan Paguyuban
      val agendaList = listOf(
        AgendaKegiatan(
          judul = "Kerja Bakti Massal Saluran Drainase & Pohon Rindang",
          tanggal = today,
          jam = "07:00 - 10:00 WIB",
          lokasi = "Lingkungan Blok A, B, C & Balai Pertemuan GSS",
          kategori = "Kerja Bakti",
          deskripsi = "Pembersihan saluran air menyambut musim penghujan dan perapian dahan pohon agar lampu jalan terang merata.",
          penanggungJawab = "Dewo (Seksi Fasum - B 25)",
          status = "Akan Datang"
        ),
        AgendaKegiatan(
          judul = "Pertemuan Rutin & Arisan Paguyuban Bulanan",
          tanggal = "$currentYear-${String.format("%02d", currentMonth)}-28",
          jam = "19:30 WIB - Selesai",
          lokasi = "Balai Pertemuan Perum Griya Sedayu Selaras",
          kategori = "Rapat Warga",
          deskripsi = "Agenda: Laporan keuangan kas transparan bulan ini, evaluasi jadwal ronda malam, serta rencana pengadaan CCTV gerbang utama.",
          penanggungJawab = "Pengurus Paguyuban GSS",
          status = "Akan Datang"
        ),
        AgendaKegiatan(
          judul = "Ronda Malam Bersama Regu Blok B & C",
          tanggal = today,
          jam = "22:00 - 04:00 WIB",
          lokasi = "Pos Ronda Utama & Keliling Seluruh Blok GSS",
          kategori = "Ronda Malam",
          deskripsi = "Penarikan koin jimpitan ronda Rp 500/rumah di kaleng depan rumah warga serta pemantauan keamanan lingkungan.",
          penanggungJawab = "Hengki (Koordinator Ronda)",
          status = "Akan Datang"
        ),
        AgendaKegiatan(
          judul = "Senam Sehat Minggu Pagi & Cek Tensi Lansia",
          tanggal = "$currentYear-${String.format("%02d", currentMonth)}-25",
          jam = "06:30 - 08:30 WIB",
          lokasi = "Lapangan Fasum Depan Blok B",
          kategori = "Olahraga / Senam",
          deskripsi = "Senam kebugaran bersama instruktur dan cek kesehatan gratis dibantu tenaga medis warga.",
          penanggungJawab = "Ibu-Ibu PKK Griya Sedayu Selaras",
          status = "Akan Datang"
        )
      )
      db.agendaDao().insertAll(agendaList)
    }
  }
}
