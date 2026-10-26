package com.example.data.model

/**
 * Data Resmi Warga Perumahan Griya Sedayu Selaras (GSS)
 * Berdasarkan Dokumen Checklist Resmi: DATA_WARGA_PERUM_GSS_-_CheckList_DataWarga.pdf
 * Total: 58 Unit Rumah (Blok A: 16, Blok B: 27, Blok C: 15)
 */
object OfficialWargaData {

  data class WargaChecklistEntry(
    val no: Int,
    val nama: String,
    val blok: String,
    val noRumah: String,
    val statusRumah: String
  )

  val rawList = listOf(
    // Blok A
    WargaChecklistEntry(1, "DESTI", "A", "01", "DI HUNI PEMILIK"),
    WargaChecklistEntry(2, "AGUS", "A", "02", "KOSONG (MEMBAYAR IURAN)"),
    WargaChecklistEntry(3, "DANANG", "A", "03", "DI HUNI PEMILIK"),
    WargaChecklistEntry(4, "NANDO", "A", "04", "DI HUNI PEMILIK"),
    WargaChecklistEntry(5, "DWI", "A", "05", "KOSONG (ADA PEMILIK)"),
    WargaChecklistEntry(6, "HANTORO", "A", "06", "DI HUNI KONTRAK"),
    WargaChecklistEntry(7, "ANGGO", "A", "07", "KOSONG (ADA PEMILIK)"),
    WargaChecklistEntry(8, "IRVAN", "A", "08", "DI HUNI PEMILIK"),
    WargaChecklistEntry(9, "TRI S", "A", "09", "DI HUNI PEMILIK"),
    WargaChecklistEntry(10, "ILHAM", "A", "10", "DI HUNI PEMILIK"),
    WargaChecklistEntry(11, "CATUR", "A", "11", "DI HUNI PEMILIK"),
    WargaChecklistEntry(12, "IDHAM", "A", "12", "KOSONG (MEMBAYAR IURAN)"),
    WargaChecklistEntry(13, "WALUYO", "A", "13", "DI HUNI PEMILIK"),
    WargaChecklistEntry(14, "OCHIN/ TONO", "A", "14", "DI HUNI KONTRAK"),
    WargaChecklistEntry(15, "FAJAR", "A", "15", "DI HUNI PEMILIK"),
    WargaChecklistEntry(16, "EDI", "A", "16", "KOSONG (MEMBAYAR IURAN)"),

    // Blok B
    WargaChecklistEntry(17, "GALIH", "B", "01", "KOSONG (MEMBAYAR IURAN)"),
    WargaChecklistEntry(18, "AHMAD 2", "B", "02", "DI HUNI PEMILIK"),
    WargaChecklistEntry(19, "WIBI", "B", "03", "KOSONG (ADA PEMILIK)"),
    WargaChecklistEntry(20, "FIRMAN", "B", "04", "DI HUNI KONTRAK"),
    WargaChecklistEntry(21, "ANDRE", "B", "05", "KOSONG (MEMBAYAR IURAN)"),
    WargaChecklistEntry(22, "RATNA/FITRAH", "B", "06", "DI HUNI PEMILIK"),
    WargaChecklistEntry(23, "ANANG", "B", "07", "DI HUNI PEMILIK"),
    WargaChecklistEntry(24, "ASROFI", "B", "08", "DI HUNI PEMILIK"),
    WargaChecklistEntry(25, "DANANG SUNYOTO", "B", "09", "KOSONG (ADA PEMILIK)"),
    WargaChecklistEntry(26, "ELVIRA", "B", "10", "KOSONG (ADA PEMILIK)"),
    WargaChecklistEntry(27, "RIMBA", "B", "11", "KOSONG (ADA PEMILIK)"),
    WargaChecklistEntry(28, "RESKY", "B", "12", "KOSONG (MEMBAYAR IURAN)"),
    WargaChecklistEntry(29, "GALIH", "B", "13", "DI HUNI PEMILIK"),
    WargaChecklistEntry(30, "SUNTI", "B", "14", "DI HUNI PEMILIK"),
    WargaChecklistEntry(31, "GALIH", "B", "15", "KOSONG (MEMBAYAR IURAN)"),
    WargaChecklistEntry(32, "IRWAN", "B", "16", "KOSONG (MEMBAYAR IURAN)"),
    WargaChecklistEntry(33, "WINARDI", "B", "17", "KOSONG (ADA PEMILIK)"),
    WargaChecklistEntry(34, "GALIH", "B", "18", "KOSONG (MEMBAYAR IURAN)"),
    WargaChecklistEntry(35, "EKO", "B", "19", "DI HUNI PEMILIK"),
    WargaChecklistEntry(36, "DYO", "B", "20", "DI HUNI PEMILIK"),
    WargaChecklistEntry(37, "RONI", "B", "21", "KOSONG (ADA PEMILIK)"),
    WargaChecklistEntry(38, "MBAK BELLA", "B", "22", "KOSONG (ADA PEMILIK)"),
    WargaChecklistEntry(39, "SONY", "B", "23", "DI HUNI PEMILIK"),
    WargaChecklistEntry(40, "BENI", "B", "24", "DI HUNI PEMILIK"),
    WargaChecklistEntry(41, "DEWO", "B", "25", "DI HUNI PEMILIK"),
    WargaChecklistEntry(42, "HENGKI", "B", "26", "DI HUNI PEMILIK"),
    WargaChecklistEntry(43, "DWI", "B", "27", "DI HUNI PEMILIK"),

    // Blok C
    WargaChecklistEntry(44, "GAPTA", "C", "01", "DI HUNI PEMILIK"),
    WargaChecklistEntry(45, "GALIH", "C", "02", "KOSONG (MEMBAYAR IURAN)"),
    WargaChecklistEntry(46, "TRI CAHYADI", "C", "03", "DI HUNI PEMILIK"),
    WargaChecklistEntry(47, "GALIH", "C", "04", "KOSONG (MEMBAYAR IURAN)"),
    WargaChecklistEntry(48, "ALID", "C", "05", "DI HUNI PEMILIK"),
    WargaChecklistEntry(49, "YULIA", "C", "06", "DI HUNI PEMILIK"),
    WargaChecklistEntry(50, "(Unit C 07)", "C", "07", "BELUM ADA PEMILIK"),
    WargaChecklistEntry(51, "HANS", "C", "08", "DI HUNI PEMILIK"),
    WargaChecklistEntry(52, "RANTO", "C", "09", "KOSONG (ADA PEMILIK)"),
    WargaChecklistEntry(53, "RUDI", "C", "10", "DI HUNI PEMILIK"),
    WargaChecklistEntry(54, "FALLAH", "C", "11", "DI HUNI PEMILIK"),
    WargaChecklistEntry(55, "JOKO TRI", "C", "12", "DI HUNI PEMILIK"),
    WargaChecklistEntry(56, "SAYOGO", "C", "13", "DI HUNI PEMILIK"),
    WargaChecklistEntry(57, "DIAN", "C", "14", "DI HUNI PEMILIK"),
    WargaChecklistEntry(58, "ROBY C", "C", "15", "DI HUNI PEMILIK")
  )

  fun getOfficialWargaList(): List<Warga> {
    return rawList.map { entry ->
      val isDihuni = entry.statusRumah.startsWith("DI HUNI")
      val statusHunian = when {
        entry.statusRumah == "DI HUNI PEMILIK" -> "Dihuni Pemilik"
        entry.statusRumah == "DI HUNI KONTRAK" -> "Dihuni Kontrak"
        entry.statusRumah == "KOSONG (MEMBAYAR IURAN)" -> "Kosong (Bayar Iuran)"
        entry.statusRumah == "KOSONG (ADA PEMILIK)" -> "Kosong (Ada Pemilik)"
        else -> "Belum Ada Pemilik"
      }

      val cleanPhone = "0812345" + String.format("%04d", entry.no)
      val jumlahJiwa = when {
        !isDihuni -> 0
        entry.statusRumah == "DI HUNI KONTRAK" -> 2
        else -> 3 + (entry.no % 3)
      }

      Warga(
        id = entry.no.toLong(),
        namaKK = entry.nama,
        blok = entry.blok,
        noRumah = entry.noRumah,
        noHp = cleanPhone,
        statusHunian = statusHunian,
        jumlahAnggota = jumlahJiwa,
        isDihuni = isDihuni,
        pekerjaan = if (isDihuni) "Warga Perum GSS" else "",
        catatan = "Status Checklist: ${entry.statusRumah}"
      )
    }
  }
}
