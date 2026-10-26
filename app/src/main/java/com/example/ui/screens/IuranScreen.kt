package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.IuranRecord
import com.example.data.model.Warga
import com.example.ui.components.StatusBadge
import com.example.ui.components.formatRupiah
import com.example.ui.components.generateTagihanWhatsAppMessage
import com.example.ui.components.sendWhatsAppMessage
import com.example.ui.theme.GrisesGoldPrimary
import com.example.ui.theme.GrisesGreenContainer
import com.example.ui.theme.GrisesGreenPrimary
import com.example.ui.theme.GrisesOnGreenContainer
import com.example.ui.theme.StatusBelumLunasBg
import com.example.ui.theme.StatusBelumLunasRed
import com.example.ui.theme.StatusLunasBg
import com.example.ui.theme.StatusLunasGreen
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.viewmodel.GrisesViewModel

@Composable
fun IuranScreen(
  viewModel: GrisesViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val isAdminMode by viewModel.isAdminMode.collectAsStateWithLifecycle()
  val iuranList by viewModel.currentPeriodIuran.collectAsStateWithLifecycle()
  val allWarga by viewModel.allWarga.collectAsStateWithLifecycle()
  val selectedMonth by viewModel.selectedMonth.collectAsStateWithLifecycle()
  val selectedYear by viewModel.selectedYear.collectAsStateWithLifecycle()

  var filterStatus by remember { mutableStateOf("Semua") } // "Semua", "Belum Lunas", "Lunas"
  var iuranToPay by remember { mutableStateOf<IuranRecord?>(null) }
  var showBroadcastDialog by remember { mutableStateOf(false) }

  val monthNames = listOf(
    "Januari", "Februari", "Maret", "April", "Mei", "Juni",
    "Juli", "Agustus", "September", "Oktober", "November", "Desember"
  )
  val currentMonthName = monthNames.getOrElse(selectedMonth - 1) { "Bulan $selectedMonth" }

  val totalTagihanAll = iuranList.sumOf { it.totalTagihan }
  val totalTerkumpul = iuranList.filter { it.isLunas }.sumOf { it.totalTagihan }
  val totalBelumLunas = iuranList.filter { !it.isLunas }.sumOf { it.totalTagihan }
  val countLunas = iuranList.count { it.isLunas }
  val countBelumLunas = iuranList.count { !it.isLunas }
  val progress = if (iuranList.isNotEmpty()) countLunas.toFloat() / iuranList.size else 0f

  val displayedList = when (filterStatus) {
    "Belum Lunas" -> iuranList.filter { !it.isLunas }
    "Lunas" -> iuranList.filter { it.isLunas }
    else -> iuranList
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(4.dp))
      // Period Selector Bar
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = {
              if (selectedMonth > 1) {
                viewModel.setSelectedMonth(selectedMonth - 1)
              } else {
                viewModel.setSelectedMonth(12)
                viewModel.setSelectedYear(selectedYear - 1)
              }
            }
          ) {
            Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Bulan Sebelumnya")
          }

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "$currentMonthName $selectedYear",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Periode Iuran Paguyuban",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          IconButton(
            onClick = {
              if (selectedMonth < 12) {
                viewModel.setSelectedMonth(selectedMonth + 1)
              } else {
                viewModel.setSelectedMonth(1)
                viewModel.setSelectedYear(selectedYear + 1)
              }
            }
          ) {
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Bulan Berikutnya")
          }
        }
      }
    }

    // Summary Card
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = GrisesGreenContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("iuran_summary_card")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Terkumpul Bulan Ini",
                fontSize = 11.sp,
                color = GrisesOnGreenContainer.copy(alpha = 0.8f)
              )
              Text(
                text = formatRupiah(totalTerkumpul),
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = GrisesOnGreenContainer
              )
            }
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = GrisesGreenPrimary
            ) {
              Text(
                text = "${(progress * 100).toInt()}% Terbayar",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))
          LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
              .fillMaxWidth()
              .height(6.dp)
              .clip(RoundedCornerShape(3.dp)),
            color = GrisesGreenPrimary,
            trackColor = Color.White.copy(alpha = 0.6f)
          )
          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "✅ $countLunas Rumah Lunas",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = StatusLunasGreen
            )
            Text(
              text = "⚠️ $countBelumLunas Belum (${formatRupiah(totalBelumLunas)})",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = StatusBelumLunasRed
            )
          }
        }
      }
    }

    // Action Bar: Filter & Broadcast Button
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          listOf("Semua", "Belum Lunas", "Lunas").forEach { status ->
            FilterChip(
              selected = filterStatus == status,
              onClick = { filterStatus = status },
              label = {
                Text(
                  text = status,
                  fontSize = 11.sp,
                  fontWeight = if (filterStatus == status) FontWeight.Bold else FontWeight.Normal
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = if (status == "Belum Lunas") StatusBelumLunasBg else GrisesGreenContainer,
                selectedLabelColor = if (status == "Belum Lunas") StatusBelumLunasRed else GrisesOnGreenContainer
              ),
              modifier = Modifier.testTag("filter_iuran_$status")
            )
          }
        }

        // WhatsApp Broadcast to Group button
        Button(
          onClick = { showBroadcastDialog = true },
          colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
          shape = RoundedCornerShape(10.dp),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
          modifier = Modifier.testTag("broadcast_iuran_wa_btn")
        ) {
          Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = "Grup WA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
      }
    }

    // List of Iuran
    items(displayedList, key = { it.id }) { iuran ->
      val warga = allWarga.firstOrNull { it.id == iuran.wargaId }
      IuranCard(
        iuran = iuran,
        warga = warga,
        isAdminMode = isAdminMode,
        onPayClick = { iuranToPay = iuran },
        onUnpayClick = { viewModel.markIuranUnpaid(iuran) },
        onSendWhatsAppTagihan = {
          val phone = warga?.noHp ?: ""
          val message = generateTagihanWhatsAppMessage(iuran)
          sendWhatsAppMessage(context, phone, message)
        }
      )
    }

    item {
      Spacer(modifier = Modifier.height(60.dp))
    }
  }

  // Dialog Catat Bayar Iuran
  iuranToPay?.let { iuran ->
    var metode by remember { mutableStateOf("Tunai") }
    var penerima by remember { mutableStateOf("Bendahara Paguyuban") }

    AlertDialog(
      onDismissRequest = { iuranToPay = null },
      title = { Text(text = "Catat Pembayaran Iuran") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "Warga: ${iuran.namaKK} (${iuran.alamat})",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
          Text(
            text = "Total Nominal: ${formatRupiah(iuran.totalTagihan)}",
            fontWeight = FontWeight.ExtraBold,
            color = GrisesGreenPrimary,
            fontSize = 15.sp
          )
          Text(
            text = "Rincian: Iuran Paguyuban ${formatRupiah(iuran.nominalIuran)} + Sampah ${formatRupiah(iuran.nominalSampah)}",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(4.dp))
          Text(text = "Pilih Metode Pembayaran:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

          listOf("Tunai", "Transfer Bank", "QRIS").forEach { m ->
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .fillMaxWidth()
                .clickable { metode = m }
                .padding(vertical = 2.dp)
            ) {
              RadioButton(selected = metode == m, onClick = { metode = m })
              Spacer(modifier = Modifier.width(6.dp))
              Text(text = m, fontSize = 12.sp)
            }
          }

          Text(
            text = "*Data otomatis masuk ke pembukuan Kas Masuk Paguyuban",
            fontSize = 10.sp,
            color = StatusLunasGreen
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.markIuranPaid(iuran, metode, penerima)
            Toast.makeText(context, "Iuran berhasil dicatat LUNAS & masuk Kas!", Toast.LENGTH_SHORT).show()
            iuranToPay = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = GrisesGreenPrimary),
          modifier = Modifier.testTag("confirm_pay_iuran_btn")
        ) {
          Text("Simpan & Lunas")
        }
      },
      dismissButton = {
        TextButton(onClick = { iuranToPay = null }) {
          Text("Batal")
        }
      }
    )
  }

  // Dialog Broadcast Grup WhatsApp
  if (showBroadcastDialog) {
    val belumLunasList = iuranList.filter { !it.isLunas }
    val broadcastText = """
*REKAP TAGIHAN IURAN PERUM GRIYA SEDAYU SELARAS*
Periode: *$currentMonthName $selectedYear*
------------------------------------------------
Yth. Seluruh Warga Paguyuban Griya Sedayu Selaras,

Berikut rekap iuran paguyuban & kebersihan sampah:
• Total Terkumpul: ${formatRupiah(totalTerkumpul)} ($countLunas Rumah)
• Belum Terbayar : ${formatRupiah(totalBelumLunas)} ($countBelumLunas Rumah)

Daftar Rumah yang belum melunasi periode ini:
${belumLunasList.take(15).mapIndexed { i, it -> "${i + 1}. ${it.alamat} - Bpk/Ibu ${it.namaKK}" }.joinToString("\n")}
${if (belumLunasList.size > 15) "...dan ${belumLunasList.size - 15} rumah lainnya." else ""}

Pembayaran dapat ditransfer ke:
🏦 Bank BPD DIY: 006.211.045892 (a.n Paguyuban Griya Sedayu Selaras)
Atau langsung ke Bendahara (Bpk. Eko Prasetyo).

Terima kasih atas partisipasi dan kerjasamanya! 🙏
""".trimIndent()

    AlertDialog(
      onDismissRequest = { showBroadcastDialog = false },
      title = { Text(text = "Broadcast Tagihan ke Grup WhatsApp") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(text = "Pesan ini akan dikirimkan ke WhatsApp untuk dibagikan ke grup warga Paguyuban:", fontSize = 12.sp)
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
              .fillMaxWidth()
              .height(180.dp)
          ) {
            LazyColumn(modifier = Modifier.padding(10.dp)) {
              item {
                Text(text = broadcastText, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            sendWhatsAppMessage(context, "", broadcastText)
            showBroadcastDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
          modifier = Modifier.testTag("send_broadcast_wa_btn")
        ) {
          Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Kirim ke WhatsApp")
        }
      },
      dismissButton = {
        TextButton(onClick = { showBroadcastDialog = false }) {
          Text("Tutup")
        }
      }
    )
  }
}

@Composable
fun IuranCard(
  iuran: IuranRecord,
  warga: Warga?,
  isAdminMode: Boolean,
  onPayClick: () -> Unit,
  onUnpayClick: () -> Unit,
  onSendWhatsAppTagihan: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("iuran_card_${iuran.id}")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = iuran.namaKK,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = iuran.alamat,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        StatusBadge(isLunas = iuran.isLunas)
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Total: ${formatRupiah(iuran.totalTagihan)}",
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            color = GrisesGreenPrimary
          )
          Text(
            text = "Iuran ${formatRupiah(iuran.nominalIuran)} + Sampah ${formatRupiah(iuran.nominalSampah)}",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.outline
          )
        }

        if (iuran.isLunas) {
          Text(
            text = "Dibayar: ${iuran.tanggalBayar ?: "-"} (${iuran.metodeBayar})",
            fontSize = 10.sp,
            color = StatusLunasGreen
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // WhatsApp Notification Button (Always available for unpaid, helps admins and residents remind politely)
        if (!iuran.isLunas) {
          Button(
            onClick = onSendWhatsAppTagihan,
            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
            shape = RoundedCornerShape(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.testTag("wa_tagihan_btn_${iuran.id}")
          ) {
            Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Notifikasi WA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
          }
        } else {
          Spacer(modifier = Modifier.width(1.dp))
        }

        // Admin Action: Catat Bayar / Batalkan
        if (isAdminMode) {
          if (!iuran.isLunas) {
            Button(
              onClick = onPayClick,
              colors = ButtonDefaults.buttonColors(containerColor = GrisesGreenPrimary),
              shape = RoundedCornerShape(8.dp),
              contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
              modifier = Modifier.testTag("pay_btn_${iuran.id}")
            ) {
              Icon(imageVector = Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Catat Bayar", fontSize = 11.sp)
            }
          } else {
            TextButton(
              onClick = onUnpayClick,
              colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.outline),
              modifier = Modifier.testTag("undo_pay_btn_${iuran.id}")
            ) {
              Icon(imageVector = Icons.Default.Undo, contentDescription = null, modifier = Modifier.size(12.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Batal Lunas", fontSize = 10.sp)
            }
          }
        }
      }
    }
  }
}
