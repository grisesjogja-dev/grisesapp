package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data Warga Perumahan Griya Sedayu Selaras
 */
@Entity(tableName = "warga")
data class Warga(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0L,
  val namaKK: String,
  val blok: String, // e.g. "A", "B", "C", "D"
  val noRumah: String, // e.g. "01", "02", "12A"
  val noHp: String, // WhatsApp phone e.g. "08123456789"
  val statusHunian: String = "Tetap", // "Tetap", "Kontrak", "Kos"
  val jumlahAnggota: Int = 4,
  val isDihuni: Boolean = true, // true: dihuni, false: rumah kosong
  val pekerjaan: String = "",
  val catatan: String = "",
  val createdAt: Long = System.currentTimeMillis()
) {
  val alamatLengkap: String
    get() = "Blok $blok No. $noRumah"
}

/**
 * Catatan Iuran Bulanan Warga
 */
@Entity(tableName = "iuran")
data class IuranRecord(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0L,
  val wargaId: Long,
  val namaKK: String,
  val alamat: String,
  val bulan: Int, // 1 - 12
  val tahun: Int, // e.g. 2026
  val nominalIuran: Long = 50000L, // Iuran Paguyuban/Keamanan
  val nominalSampah: Long = 15000L, // Pengelolaan Sampah
  val totalTagihan: Long = 65000L,
  val isLunas: Boolean = false,
  val tanggalBayar: String? = null,
  val metodeBayar: String = "Tunai", // "Tunai", "Transfer Bank", "QRIS"
  val penerima: String? = null,
  val catatan: String = ""
)

/**
 * Input Jimpitan Ronda Malam (Rp 500 / rumah / hari)
 */
@Entity(tableName = "jimpitan")
data class JimpitanRecord(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0L,
  val wargaId: Long,
  val namaKK: String,
  val alamat: String,
  val tanggal: String, // format yyyy-MM-dd
  val nominal: Long = 500L, // default 500/rumah/hari
  val isCollected: Boolean = true, // Apakah ada koin di kaleng
  val petugasRonda: String = "Tim Ronda",
  val isSetorKas: Boolean = false, // Apakah sudah disetor ke kas paguyuban
  val timestamp: Long = System.currentTimeMillis()
)

/**
 * Modul Kas Masuk & Keluar untuk Transparansi Keuangan Warga
 */
@Entity(tableName = "kas_transaksi")
data class KasTransaction(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0L,
  val tipe: String, // "MASUK" or "KELUAR"
  val kategori: String, // "Iuran Warga", "Jimpitan Ronda", "Donasi Warga", "Keamanan & Satpam", "Kebersihan & Sampah", "Perbaikan Fasum", "Konsumsi Pertemuan", "Sosial & Santunan", "Inventaris", "Lainnya"
  val nominal: Long,
  val tanggal: String, // yyyy-MM-dd
  val keterangan: String,
  val pencatat: String = "Bendahara Paguyuban",
  val noBukti: String = "",
  val timestamp: Long = System.currentTimeMillis()
)

/**
 * Inventarisasi Aset Paguyuban Perum Griya Sedayu Selaras
 */
@Entity(tableName = "inventaris")
data class Inventaris(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0L,
  val kodeBarang: String, // e.g. "INV-TND-01"
  val namaBarang: String,
  val kategori: String, // "Peralatan Tenda", "Meja & Kursi", "Sound System", "Alat Kebersihan", "Keamanan", "Lainnya"
  val jumlahTotal: Int,
  val jumlahTersedia: Int,
  val satuan: String = "Unit", // "Unit", "Set", "Pcs", "Roll"
  val kondisi: String = "Baik", // "Baik", "Rusak Ringan", "Rusak Berat"
  val lokasiSimpan: String = "Gudang Fasum", // "Pos Ronda Blok A", "Balai Warga", "Gudang Fasum", "Rumah Ketua RT"
  val statusPinjam: String = "Tersedia", // "Tersedia", "Dipinjam"
  val namaPeminjam: String? = null,
  val tglPinjam: String? = null,
  val tglKembaliRencana: String? = null,
  val keperluanPinjam: String? = null
)

/**
 * Agenda Kegiatan Paguyuban
 */
@Entity(tableName = "agenda")
data class AgendaKegiatan(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0L,
  val judul: String,
  val tanggal: String, // yyyy-MM-dd
  val jam: String, // e.g. "07:30 WIB"
  val lokasi: String,
  val kategori: String = "Kerja Bakti", // "Kerja Bakti", "Rapat Warga", "Ronda Malam", "Peringatan Hari Besar", "Olahraga / Senam", "Sosial"
  val deskripsi: String,
  val penanggungJawab: String = "Pengurus Paguyuban",
  val status: String = "Akan Datang" // "Akan Datang", "Sedang Berlangsung", "Selesai"
)

/**
 * Ringkasan Kas
 */
data class KasSummary(
  val totalMasuk: Long = 0L,
  val totalKeluar: Long = 0L,
  val saldoSaatIni: Long = 0L
)

/**
 * Ringkasan Jimpitan
 */
data class JimpitanSummary(
  val totalHariIni: Long = 0L,
  val jumlahRumahHariIni: Int = 0,
  val totalBulanIni: Long = 0L,
  val totalBelumSetor: Long = 0L
)
