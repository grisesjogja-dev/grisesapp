package com.example.data.repository

import com.example.data.db.GrisesDatabase
import com.example.data.model.AgendaKegiatan
import com.example.data.model.Inventaris
import com.example.data.model.IuranRecord
import com.example.data.model.JimpitanRecord
import com.example.data.model.KasTransaction
import com.example.data.model.Warga
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GrisesRepository(private val db: GrisesDatabase) {

  // --- WARGA ---
  val allWarga: Flow<List<Warga>> = db.wargaDao().getAllWarga()

  suspend fun insertWarga(warga: Warga): Long {
    val newId = db.wargaDao().insertWarga(warga)
    // Automatically create current month iuran for the newly added citizen
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val cal = java.util.Calendar.getInstance()
    val month = cal.get(java.util.Calendar.MONTH) + 1
    val year = cal.get(java.util.Calendar.YEAR)

    db.iuranDao().insertIuran(
      IuranRecord(
        wargaId = if (newId > 0) newId else warga.id,
        namaKK = warga.namaKK,
        alamat = "Blok ${warga.blok} No. ${warga.noRumah}",
        bulan = month,
        tahun = year,
        nominalIuran = 50000L,
        nominalSampah = 15000L,
        totalTagihan = 65000L,
        isLunas = false
      )
    )
    return newId
  }

  suspend fun updateWarga(warga: Warga) {
    db.wargaDao().updateWarga(warga)
  }

  suspend fun deleteWarga(warga: Warga) {
    db.wargaDao().deleteWarga(warga)
    db.iuranDao().deleteByWargaId(warga.id)
    db.jimpitanDao().deleteByWargaId(warga.id)
  }

  // --- IURAN ---
  val allIuran: Flow<List<IuranRecord>> = db.iuranDao().getAllIuran()

  fun getIuranByPeriode(bulan: Int, tahun: Int): Flow<List<IuranRecord>> =
    db.iuranDao().getIuranByPeriode(bulan, tahun)

  suspend fun markIuranPaid(
    iuran: IuranRecord,
    metodeBayar: String,
    penerima: String
  ) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = dateFormat.format(Date())
    val updated = iuran.copy(
      isLunas = true,
      tanggalBayar = today,
      metodeBayar = metodeBayar,
      penerima = penerima
    )
    db.iuranDao().updateIuran(updated)

    // Also automatically log to Kas Masuk
    db.kasDao().insertKas(
      KasTransaction(
        tipe = "MASUK",
        kategori = "Iuran Warga",
        nominal = iuran.totalTagihan,
        tanggal = today,
        keterangan = "Pembayaran Iuran ${iuran.namaKK} (${iuran.alamat}) Periode Bln ${iuran.bulan}/${iuran.tahun}",
        pencatat = penerima,
        noBukti = "IUR-${iuran.id}-${System.currentTimeMillis() % 10000}"
      )
    )
  }

  suspend fun markIuranUnpaid(iuran: IuranRecord) {
    val updated = iuran.copy(
      isLunas = false,
      tanggalBayar = null,
      penerima = null
    )
    db.iuranDao().updateIuran(updated)
  }

  // --- JIMPITAN RONDA (Rp 500 / rumah / hari) ---
  val allJimpitan: Flow<List<JimpitanRecord>> = db.jimpitanDao().getAllJimpitan()

  fun getJimpitanByTanggal(tanggal: String): Flow<List<JimpitanRecord>> =
    db.jimpitanDao().getJimpitanByTanggal(tanggal)

  suspend fun toggleJimpitanStatus(record: JimpitanRecord) {
    db.jimpitanDao().updateJimpitan(record.copy(isCollected = !record.isCollected))
  }

  suspend fun saveOrUpdateJimpitanList(records: List<JimpitanRecord>) {
    db.jimpitanDao().insertAll(records)
  }

  suspend fun setorJimpitanToKas(tanggal: String, totalNominal: Long, petugas: String) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = dateFormat.format(Date())
    db.jimpitanDao().markSetorKas(tanggal)
    db.kasDao().insertKas(
      KasTransaction(
        tipe = "MASUK",
        kategori = "Jimpitan Ronda",
        nominal = totalNominal,
        tanggal = today,
        keterangan = "Setor Uang Jimpitan Ronda Tanggal $tanggal (Rp 500/rumah)",
        pencatat = petugas,
        noBukti = "JMP-${System.currentTimeMillis() % 10000}"
      )
    )
  }

  // --- KAS MASUK & KELUAR ---
  val allKas: Flow<List<KasTransaction>> = db.kasDao().getAllKas()

  suspend fun addKasTransaction(transaction: KasTransaction): Long =
    db.kasDao().insertKas(transaction)

  suspend fun deleteKasTransaction(transaction: KasTransaction) =
    db.kasDao().deleteKas(transaction)

  // --- INVENTARIS ---
  val allInventaris: Flow<List<Inventaris>> = db.inventarisDao().getAllInventaris()

  suspend fun addInventaris(item: Inventaris): Long =
    db.inventarisDao().insertInventaris(item)

  suspend fun updateInventaris(item: Inventaris) =
    db.inventarisDao().updateInventaris(item)

  suspend fun deleteInventaris(item: Inventaris) =
    db.inventarisDao().deleteInventaris(item)

  suspend fun pinjamInventaris(
    item: Inventaris,
    namaPeminjam: String,
    keperluan: String,
    rencanaKembali: String,
    jumlahDipinjam: Int
  ) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = dateFormat.format(Date())
    val remaining = (item.jumlahTersedia - jumlahDipinjam).coerceAtLeast(0)
    val updated = item.copy(
      statusPinjam = if (remaining == 0) "Dipinjam Penuh" else "Dipinjam Sebagian",
      jumlahTersedia = remaining,
      namaPeminjam = namaPeminjam,
      keperluanPinjam = keperluan,
      tglPinjam = today,
      tglKembaliRencana = rencanaKembali
    )
    db.inventarisDao().updateInventaris(updated)
  }

  suspend fun kembalikanInventaris(item: Inventaris) {
    val updated = item.copy(
      statusPinjam = "Tersedia",
      jumlahTersedia = item.jumlahTotal,
      namaPeminjam = null,
      keperluanPinjam = null,
      tglPinjam = null,
      tglKembaliRencana = null
    )
    db.inventarisDao().updateInventaris(updated)
  }

  // --- AGENDA KEGIATAN ---
  val allAgenda: Flow<List<AgendaKegiatan>> = db.agendaDao().getAllAgenda()

  suspend fun addAgenda(agenda: AgendaKegiatan): Long =
    db.agendaDao().insertAgenda(agenda)

  suspend fun updateAgenda(agenda: AgendaKegiatan) =
    db.agendaDao().updateAgenda(agenda)

  suspend fun deleteAgenda(agenda: AgendaKegiatan) =
    db.agendaDao().deleteAgenda(agenda)

  suspend fun resetInitialData() {
    GrisesDatabase.populateInitialData(db)
  }
}
