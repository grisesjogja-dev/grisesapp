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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.PriceCheck
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.JimpitanRecord
import com.example.ui.components.formatRupiah
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
import com.example.ui.viewmodel.GrisesViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun JimpitanScreen(
  viewModel: GrisesViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val isAdminMode by viewModel.isAdminMode.collectAsStateWithLifecycle()
  val jimpitanList by viewModel.currentJimpitanList.collectAsStateWithLifecycle()
  val allJimpitan by viewModel.allJimpitan.collectAsStateWithLifecycle()
  val summary by viewModel.jimpitanSummary.collectAsStateWithLifecycle()
  val selectedDate by viewModel.selectedJimpitanDate.collectAsStateWithLifecycle()

  val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
  val today = dateFormat.format(Date())
  val cal = Calendar.getInstance()
  cal.add(Calendar.DAY_OF_YEAR, -1)
  val yesterday = dateFormat.format(cal.time)

  var selectedTab by remember { mutableIntStateOf(0) } // 0: Input/Status Hari Ini, 1: Laporan & Riwayat
  var showSetorDialog by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(4.dp))
      // Header Banner Jimpitan Ronda
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = GrisesGoldContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(48.dp)
              .clip(CircleShape)
              .background(GrisesGoldPrimary)
          ) {
            Icon(
              imageVector = Icons.Default.NightlightRound,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(26.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "Jimpitan Ronda Malam",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 16.sp,
              color = GrisesOnGoldContainer
            )
            Text(
              text = "Tarif: Rp 500 / rumah / hari • Transparan & Real-Time",
              fontSize = 11.sp,
              color = GrisesOnGoldContainer.copy(alpha = 0.85f)
            )
          }
        }
      }
    }

    // Real-Time Stats Overview
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("jimpitan_realtime_stats_card")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Laporan Real-Time Jimpitan",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text(text = "Terkumpul Hari Ini", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
              Text(
                text = formatRupiah(summary.totalHariIni),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = GrisesGreenPrimary
              )
              Text(
                text = "${summary.jumlahRumahHariIni} rumah berpartisipasi",
                fontSize = 10.sp,
                color = StatusLunasGreen
              )
            }

            Column(horizontalAlignment = Alignment.End) {
              Text(text = "Akumulasi Bulan Ini", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
              Text(
                text = formatRupiah(summary.totalBulanIni),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = GrisesGoldPrimary
              )
              Text(
                text = "Belum Setor: ${formatRupiah(summary.totalBelumSetor)}",
                fontSize = 10.sp,
                color = StatusPendingAmber
              )
            }
          }

          // Button Setor ke Kas Paguyuban (Admin / Petugas Ronda)
          if (isAdminMode && summary.totalBelumSetor > 0) {
            Spacer(modifier = Modifier.height(12.dp))
            Button(
              onClick = { showSetorDialog = true },
              colors = ButtonDefaults.buttonColors(containerColor = GrisesGreenPrimary),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("setor_jimpitan_ke_kas_btn")
            ) {
              Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Setor ke Kas Paguyuban (${formatRupiah(summary.totalBelumSetor)})", fontSize = 12.sp)
            }
          }
        }
      }
    }

    // Tabs: Input Harian vs Riwayat Laporan
    item {
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = GrisesGreenPrimary,
        modifier = Modifier.clip(RoundedCornerShape(12.dp))
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = { Text("Input & Status Harian", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = { Text("Riwayat Per Tanggal", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
        )
      }
    }

    if (selectedTab == 0) {
      // Date Selector Chips (Hari Ini, Kemarin)
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (selectedDate == today) GrisesGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier.clickable { viewModel.setSelectedJimpitanDate(today) }
            ) {
              Text(
                text = "Hari Ini ($today)",
                color = if (selectedDate == today) Color.White else MaterialTheme.colorScheme.onSurface,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (selectedDate == yesterday) GrisesGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier.clickable { viewModel.setSelectedJimpitanDate(yesterday) }
            ) {
              Text(
                text = "Kemarin ($yesterday)",
                color = if (selectedDate == yesterday) Color.White else MaterialTheme.colorScheme.onSurface,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
          }

          // Admin One-Tap: Tandai Semua 500
          if (isAdminMode) {
            TextButton(
              onClick = {
                viewModel.markAllJimpitanCollected(selectedDate, 500L)
                Toast.makeText(context, "Semua rumah berhasil ditandai Rp 500!", Toast.LENGTH_SHORT).show()
              },
              modifier = Modifier.testTag("mark_all_jimpitan_btn")
            ) {
              Icon(imageVector = Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Ambil Semua", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      // Houses Grid for Jimpitan
      items(jimpitanList, key = { "${it.wargaId}_${it.tanggal}" }) { record ->
        JimpitanHouseCard(
          record = record,
          isAdminMode = isAdminMode,
          onToggle = {
            viewModel.toggleJimpitanForWarga(record)
          }
        )
      }
    } else {
      // Riwayat Laporan Jimpitan
      val groupedByDate = allJimpitan.groupBy { it.tanggal }

      items(groupedByDate.keys.toList().sortedDescending()) { date ->
        val records = groupedByDate[date] ?: emptyList()
        val totalAmount = records.filter { it.isCollected }.sumOf { it.nominal }
        val rumahCount = records.count { it.isCollected }
        val isAllSetor = records.isNotEmpty() && records.all { it.isSetorKas }

        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("riwayat_jimpitan_$date")
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(GrisesGreenContainer)
              ) {
                Icon(
                  imageVector = Icons.Default.CalendarToday,
                  contentDescription = null,
                  tint = GrisesGreenPrimary,
                  modifier = Modifier.size(18.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "Tanggal: $date",
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "$rumahCount rumah • Petugas: ${records.firstOrNull()?.petugasRonda ?: "Tim Ronda"}",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = formatRupiah(totalAmount),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = GrisesGreenPrimary
              )
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isAllSetor) StatusLunasBg else StatusPendingBg
              ) {
                Text(
                  text = if (isAllSetor) "Sudah ke Kas" else "Belum Setor",
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isAllSetor) StatusLunasGreen else StatusPendingAmber,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(60.dp))
    }
  }

  // Dialog Setor ke Kas Paguyuban
  if (showSetorDialog) {
    var petugasNama by remember { mutableStateOf("Ari Wibowo (Koordinator Ronda)") }

    AlertDialog(
      onDismissRequest = { showSetorDialog = false },
      title = { Text(text = "Setor Jimpitan ke Kas Paguyuban") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "Total Uang Jimpitan Belum Disetor: ${formatRupiah(summary.totalBelumSetor)}",
            fontWeight = FontWeight.Bold,
            color = GrisesGreenPrimary
          )
          Text(
            text = "Dana ini akan langsung dipindahkan dan tercatat pada Buku Kas Masuk Paguyuban Griya Sedayu Selaras secara transparan.",
            fontSize = 11.sp
          )
          OutlinedTextField(
            value = petugasNama,
            onValueChange = { petugasNama = it },
            label = { Text("Nama Petugas / Penyortir Ronda") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.setorJimpitanToKas(selectedDate, summary.totalBelumSetor, petugasNama)
            Toast.makeText(context, "Jimpitan berhasil disetor ke Buku Kas!", Toast.LENGTH_SHORT).show()
            showSetorDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = GrisesGreenPrimary),
          modifier = Modifier.testTag("confirm_setor_jimpitan_btn")
        ) {
          Text("Konfirmasi Setor Kas")
        }
      },
      dismissButton = {
        TextButton(onClick = { showSetorDialog = false }) {
          Text("Batal")
        }
      }
    )
  }
}

@Composable
fun JimpitanHouseCard(
  record: JimpitanRecord,
  isAdminMode: Boolean,
  onToggle: () -> Unit
) {
  val isCollected = record.isCollected

  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isCollected) StatusLunasBg else MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(enabled = isAdminMode) { onToggle() }
      .testTag("jimpitan_house_${record.wargaId}")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(if (isCollected) StatusLunasGreen else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
          Icon(
            imageVector = if (isCollected) Icons.Default.Check else Icons.Default.Close,
            contentDescription = null,
            tint = if (isCollected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = record.alamat,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Bpk/Ibu ${record.namaKK}",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = if (isCollected) formatRupiah(record.nominal) else "Rp 0",
          fontSize = 13.sp,
          fontWeight = FontWeight.ExtraBold,
          color = if (isCollected) StatusLunasGreen else StatusBelumLunasRed
        )

        if (isAdminMode) {
          Spacer(modifier = Modifier.width(8.dp))
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = if (isCollected) GrisesGreenContainer else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.clickable { onToggle() }
          ) {
            Text(
              text = if (isCollected) "Ubah" else "+ Ambil",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = if (isCollected) GrisesOnGreenContainer else GrisesGreenPrimary,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
            )
          }
        }
      }
    }
  }
}
