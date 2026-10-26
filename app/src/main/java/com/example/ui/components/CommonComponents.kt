package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AgendaKegiatan
import com.example.data.model.IuranRecord
import com.example.ui.theme.GrisesGoldContainer
import com.example.ui.theme.GrisesGoldPrimary
import com.example.ui.theme.GrisesGreenContainer
import com.example.ui.theme.GrisesGreenPrimary
import com.example.ui.theme.GrisesOnGoldContainer
import com.example.ui.theme.GrisesOnGreenContainer
import com.example.ui.theme.StatusBelumLunasBg
import com.example.ui.theme.StatusBelumLunasRed
import com.example.ui.theme.StatusLunasBg
import com.example.ui.theme.StatusLunasGreen
import com.example.ui.theme.StatusPendingAmber
import com.example.ui.theme.StatusPendingBg
import java.net.URLEncoder
import java.text.NumberFormat
import java.util.Locale

/**
 * Format Currency to Indonesian Rupiah (e.g. Rp 65.000)
 */
fun formatRupiah(amount: Long): String {
  val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
  format.maximumFractionDigits = 0
  return format.format(amount).replace("Rp", "Rp ")
}

/**
 * Convert Indonesian phone (e.g. 08123456789) to international format 628123456789
 */
fun formatPhoneNumberForWhatsApp(phone: String): String {
  val digits = phone.filter { it.isDigit() }
  return when {
    digits.startsWith("0") -> "62" + digits.substring(1)
    digits.startsWith("62") -> digits
    else -> digits
  }
}

/**
 * Open WhatsApp with pre-filled message, with fallback to web URL, share sheet, or clipboard copy.
 */
fun sendWhatsAppMessage(context: Context, phone: String, message: String) {
  val cleanPhone = formatPhoneNumberForWhatsApp(phone)
  val encodedMsg = try {
    URLEncoder.encode(message, "UTF-8")
  } catch (e: Exception) {
    message
  }

  try {
    val waUri = if (cleanPhone.isNotEmpty()) {
      Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone&text=$encodedMsg")
    } else {
      Uri.parse("https://api.whatsapp.com/send?text=$encodedMsg")
    }
    val intent = Intent(Intent.ACTION_VIEW, waUri)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    context.startActivity(intent)
  } catch (e: Exception) {
    try {
      // Fallback to general text share
      val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, message)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
      }
      context.startActivity(Intent.createChooser(shareIntent, "Kirim Pesan WhatsApp / Aplikasi Lain"))
    } catch (e2: Exception) {
      // Copy to clipboard
      val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
      val clip = ClipData.newPlainText("Pesan GRISES", message)
      clipboard.setPrimaryClip(clip)
      Toast.makeText(context, "Teks berhasil disalin ke clipboard!", Toast.LENGTH_SHORT).show()
    }
  }
}

/**
 * Template Pesan Tagihan Iuran Warga Otomatis
 */
fun generateTagihanWhatsAppMessage(iuran: IuranRecord, noRekening: String = "006.211.045892 (Bank BPD DIY)", namaBendahara: String = "Bpk. Eko Prasetyo"): String {
  val namaBulan = when (iuran.bulan) {
    1 -> "Januari"; 2 -> "Februari"; 3 -> "Maret"; 4 -> "April"
    5 -> "Mei"; 6 -> "Juni"; 7 -> "Juli"; 8 -> "Agustus"
    9 -> "September"; 10 -> "Oktober"; 11 -> "November"; 12 -> "Desember"
    else -> "Bulan ${iuran.bulan}"
  }

  return """
*PAGUYUBAN PERUM GRIYA SEDAYU SELARAS (GRISES)*
_Guyub Rukun, Aman, & Transparan_
------------------------------------------------
Yth. Bpk/Ibu *${iuran.namaKK}*
Alamat: *${iuran.alamat}*

Semoga Bpk/Ibu sekeluarga senantiasa dalam keadaan sehat dan penuh berkah.

Kami menyampaikan rincian Iuran Paguyuban untuk periode:
🗓 *Periode: $namaBulan ${iuran.tahun}*
------------------------------------------------
• Iuran Paguyuban & Keamanan : ${formatRupiah(iuran.nominalIuran)}
• Pengelolaan Sampah Lingkungan: ${formatRupiah(iuran.nominalSampah)}
*TOTAL TAGIHAN: ${formatRupiah(iuran.totalTagihan)}*
Status Saat Ini: *BELUM LUNAS* ⚠️
------------------------------------------------
Pembayaran dapat disalurkan melalui:
🏦 Transfer: *$noRekening*
a.n. *Paguyuban Griya Sedayu Selaras*
(Atau bayar tunai langsung ke Bendahara / Petugas Ronda malam)

Mohon konfirmasi setelah melakukan pembayaran ke:
📞 Bendahara Paguyuban: $namaBendahara

Terima kasih atas partisipasi aktif Bpk/Ibu dalam menjaga kenyamanan dan ketertiban lingkungan kita bersama. 🙏✨
""".trimIndent()
}

/**
 * Template Broadcast Pengumuman Agenda Kegiatan ke Grup WhatsApp
 */
fun generateAgendaWhatsAppMessage(agenda: AgendaKegiatan): String {
  return """
📢 *PENGUMUMAN KEGIATAN PERUM GRIYA SEDAYU SELARAS* 📢

Kepada Yth. Seluruh Warga Perum Griya Sedayu Selaras,
Diberitahukan bahwa akan diadakan agenda kegiatan:

📌 *Kegiatan:* ${agenda.judul}
📅 *Hari/Tanggal:* ${agenda.tanggal}
⏰ *Waktu:* ${agenda.jam}
📍 *Tempat:* ${agenda.lokasi}
👤 *Koordinator:* ${agenda.penanggungJawab}

📝 *Deskripsi / Keperluan:*
${agenda.deskripsi}

Mari bersama-sama kita semarakkan dan hadir tepat waktu demi kenyamanan dan kerukunan lingkungan kita.
_Guyub Rukun Griya Sedayu Selaras_ 🙏
""".trimIndent()
}

@Composable
fun StatusBadge(
  isLunas: Boolean,
  modifier: Modifier = Modifier
) {
  val bgColor = if (isLunas) StatusLunasBg else StatusBelumLunasBg
  val textColor = if (isLunas) StatusLunasGreen else StatusBelumLunasRed
  val text = if (isLunas) "LUNAS" else "BELUM LUNAS"
  val icon = if (isLunas) Icons.Default.Check else Icons.Default.Close

  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(bgColor)
      .padding(horizontal = 8.dp, vertical = 4.dp)
  ) {
    Icon(
      imageVector = icon,
      contentDescription = text,
      tint = textColor,
      modifier = Modifier.size(14.dp)
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(
      text = text,
      color = textColor,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold
    )
  }
}

@Composable
fun JimpitanStatusBadge(
  isCollected: Boolean,
  nominal: Long = 500L,
  modifier: Modifier = Modifier
) {
  val bgColor = if (isCollected) GrisesGreenContainer else StatusPendingBg
  val textColor = if (isCollected) GrisesGreenPrimary else StatusPendingAmber
  val text = if (isCollected) "Terisi ${formatRupiah(nominal)}" else "Belum Diambil"

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(bgColor)
      .padding(horizontal = 8.dp, vertical = 3.dp)
  ) {
    Text(
      text = text,
      color = textColor,
      fontSize = 11.sp,
      fontWeight = FontWeight.SemiBold
    )
  }
}

@Composable
fun AdminModeSwitchCard(
  isAdminMode: Boolean,
  onToggleAdminMode: (Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isAdminMode) GrisesGreenContainer else MaterialTheme.colorScheme.surfaceVariant
    ),
    modifier = modifier
      .fillMaxWidth()
      .testTag("admin_mode_switch_card")
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(if (isAdminMode) GrisesGreenPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
          Icon(
            imageVector = if (isAdminMode) Icons.Default.AdminPanelSettings else Icons.Default.Person,
            contentDescription = "Mode Akses",
            tint = if (isAdminMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = if (isAdminMode) "Mode Pengurus (Admin Aktif)" else "Mode Warga (Transparansi Publik)",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = if (isAdminMode) GrisesOnGreenContainer else MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = if (isAdminMode) "Akses penuh kelola iuran, jimpitan, kas & warga" else "Akses melihat laporan kas, agenda & inventaris",
            fontSize = 11.sp,
            color = if (isAdminMode) GrisesOnGreenContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
      Switch(
        checked = isAdminMode,
        onCheckedChange = onToggleAdminMode,
        colors = SwitchDefaults.colors(
          checkedThumbColor = GrisesGreenPrimary,
          checkedTrackColor = GrisesGreenPrimary.copy(alpha = 0.3f)
        ),
        modifier = Modifier.testTag("admin_switch_toggle")
      )
    }
  }
}
