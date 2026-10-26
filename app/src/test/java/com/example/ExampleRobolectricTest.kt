package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.IuranRecord
import com.example.data.model.JimpitanRecord
import com.example.data.model.KasTransaction
import com.example.ui.components.formatRupiah
import com.example.ui.components.generateTagihanWhatsAppMessage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("GRISES", appName)
  }

  @Test
  fun `jimpitan default tariff is 500 rupiah per house`() {
    val record = JimpitanRecord(
      wargaId = 1L,
      namaKK = "Bambang Sutrisno",
      alamat = "Blok A No. 01",
      tanggal = "2026-10-25"
    )
    assertEquals(500L, record.nominal)
    assertTrue(record.isCollected)
  }

  @Test
  fun `kas transaction calculation`() {
    val tx1 = KasTransaction(tipe = "MASUK", kategori = "Iuran", nominal = 100000L, tanggal = "2026-10-25", keterangan = "Iuran")
    val tx2 = KasTransaction(tipe = "KELUAR", kategori = "Fasum", nominal = 40000L, tanggal = "2026-10-25", keterangan = "Lampu")
    val balance = tx1.nominal - tx2.nominal
    assertEquals(60000L, balance)
  }

  @Test
  fun `whatsapp invoice message contains key community details`() {
    val iuran = IuranRecord(
      wargaId = 1L,
      namaKK = "Bambang Sutrisno",
      alamat = "Blok A No. 01",
      bulan = 10,
      tahun = 2026,
      nominalIuran = 50000L,
      nominalSampah = 15000L,
      totalTagihan = 65000L,
      isLunas = false
    )
    val msg = generateTagihanWhatsAppMessage(iuran)
    assertTrue(msg.contains("GRIYA SEDAYU SELARAS"))
    assertTrue(msg.contains("Bambang Sutrisno"))
    assertTrue(msg.contains("Blok A No. 01"))
    assertTrue(msg.contains("BELUM LUNAS"))
    assertTrue(msg.contains("65.000"))
  }

  @Test
  fun `official checklist contains 58 houses across Blok A, B, and C`() {
    val list = com.example.data.model.OfficialWargaData.getOfficialWargaList()
    assertEquals(58, list.size)
    assertEquals(16, list.count { it.blok == "A" })
    assertEquals(27, list.count { it.blok == "B" })
    assertEquals(15, list.count { it.blok == "C" })

    // Verify first and last entries match PDF
    assertEquals("DESTI", list[0].namaKK)
    assertEquals("01", list[0].noRumah)
    assertEquals("ROBY C", list[57].namaKK)
    assertEquals("15", list[57].noRumah)
  }
}
