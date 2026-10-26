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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.KasTransaction
import com.example.ui.components.formatRupiah
import com.example.ui.theme.GrisesGoldContainer
import com.example.ui.theme.GrisesGoldPrimary
import com.example.ui.theme.GrisesGreenContainer
import com.example.ui.theme.GrisesGreenPrimary
import com.example.ui.theme.GrisesOnGreenContainer
import com.example.ui.theme.StatusBelumLunasBg
import com.example.ui.theme.StatusBelumLunasRed
import com.example.ui.theme.StatusLunasBg
import com.example.ui.theme.StatusLunasGreen
import com.example.ui.viewmodel.GrisesViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KasScreen(
  viewModel: GrisesViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val isAdminMode by viewModel.isAdminMode.collectAsStateWithLifecycle()
  val kasList by viewModel.allKas.collectAsStateWithLifecycle()
  val kasSummary by viewModel.kasSummary.collectAsStateWithLifecycle()

  var filterTipe by remember { mutableStateOf("Semua") } // "Semua", "MASUK", "KELUAR"
  var showAddDialog by remember { mutableStateOf(false) }
  var transactionToDelete by remember { mutableStateOf<KasTransaction?>(null) }

  val filteredKas = when (filterTipe) {
    "MASUK" -> kasList.filter { it.tipe == "MASUK" }
    "KELUAR" -> kasList.filter { it.tipe == "KELUAR" }
    else -> kasList
  }

  Box(modifier = modifier.fillMaxSize()) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(4.dp))
        // Hero Saldo Kas Card
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = GrisesGreenPrimary),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("kas_transparansi_card")
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "BUKU KAS UMUM PAGUYUBAN",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.85f),
                letterSpacing = 0.5.sp
              )
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White.copy(alpha = 0.2f)
              ) {
                Text(
                  text = "Akuntabel",
                  color = Color.White,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = formatRupiah(kasSummary.saldoSaatIni),
              fontSize = 30.sp,
              fontWeight = FontWeight.ExtraBold,
              color = Color.White
            )
            Text(
              text = "Saldo Berjalan Kas RT / Paguyuban",
              fontSize = 11.sp,
              color = Color.White.copy(alpha = 0.75f)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              // Total Masuk
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.15f),
                modifier = Modifier.weight(1f)
              ) {
                Row(
                  modifier = Modifier.padding(10.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = Color(0xFF81C784),
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Column {
                    Text(text = "Total Masuk", fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
                    Text(
                      text = formatRupiah(kasSummary.totalMasuk),
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color.White
                    )
                  }
                }
              }

              // Total Keluar
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.15f),
                modifier = Modifier.weight(1f)
              ) {
                Row(
                  modifier = Modifier.padding(10.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = null,
                    tint = Color(0xFFE57373),
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Column {
                    Text(text = "Total Keluar", fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
                    Text(
                      text = formatRupiah(kasSummary.totalKeluar),
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color.White
                    )
                  }
                }
              }
            }
          }
        }
      }

      // Filter Chips
      item {
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          listOf("Semua", "MASUK", "KELUAR").forEach { tipe ->
            FilterChip(
              selected = filterTipe == tipe,
              onClick = { filterTipe = tipe },
              label = {
                Text(
                  text = when (tipe) {
                    "MASUK" -> "Kas Masuk"
                    "KELUAR" -> "Kas Keluar"
                    else -> "Semua Transaksi"
                  },
                  fontWeight = if (filterTipe == tipe) FontWeight.Bold else FontWeight.Normal,
                  fontSize = 12.sp
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = when (tipe) {
                  "MASUK" -> StatusLunasBg
                  "KELUAR" -> StatusBelumLunasBg
                  else -> GrisesGreenContainer
                },
                selectedLabelColor = when (tipe) {
                  "MASUK" -> StatusLunasGreen
                  "KELUAR" -> StatusBelumLunasRed
                  else -> GrisesOnGreenContainer
                }
              ),
              modifier = Modifier.testTag("filter_kas_$tipe")
            )
          }
        }
      }

      // List of Kas Transactions
      items(filteredKas, key = { it.id }) { item ->
        val isMasuk = item.tipe == "MASUK"
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("kas_item_${item.id}")
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isMasuk) StatusLunasBg else StatusBelumLunasBg)
            ) {
              Icon(
                imageVector = if (isMasuk) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                contentDescription = item.tipe,
                tint = if (isMasuk) StatusLunasGreen else StatusBelumLunasRed,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = item.kategori,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "${if (isMasuk) "+" else "-"} ${formatRupiah(item.nominal)}",
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 14.sp,
                  color = if (isMasuk) StatusLunasGreen else StatusBelumLunasRed
                )
              }
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = item.keterangan,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.height(4.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "📅 ${item.tanggal} • ${item.pencatat}",
                  fontSize = 10.sp,
                  color = MaterialTheme.colorScheme.outline
                )
                if (item.noBukti.isNotEmpty()) {
                  Text(
                    text = item.noBukti,
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.outline
                  )
                }
              }
            }

            if (isAdminMode) {
              IconButton(
                onClick = { transactionToDelete = item },
                modifier = Modifier.size(28.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Delete,
                  contentDescription = "Hapus Transaksi",
                  tint = MaterialTheme.colorScheme.outline,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(80.dp))
      }
    }

    // FAB Tambah Transaksi Kas (Admin Only)
    if (isAdminMode) {
      FloatingActionButton(
        onClick = { showAddDialog = true },
        containerColor = GrisesGreenPrimary,
        contentColor = Color.White,
        shape = CircleShape,
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(20.dp)
          .testTag("add_kas_fab")
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 16.dp)
        ) {
          Icon(imageVector = Icons.Default.Add, contentDescription = "Catat Kas")
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "Catat Kas", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }
    }
  }

  // Dialog Tambah Transaksi Kas
  if (showAddDialog) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = dateFormat.format(Date())

    var tipe by remember { mutableStateOf("MASUK") }
    var kategori by remember { mutableStateOf("Iuran Warga") }
    var nominalStr by remember { mutableStateOf("") }
    var keterangan by remember { mutableStateOf("") }
    var pencatat by remember { mutableStateOf("Bendahara Paguyuban") }
    var tanggal by remember { mutableStateOf(today) }

    val kategoriMasuk = listOf("Iuran Warga", "Jimpitan Ronda", "Donasi Warga", "Saldo Awal", "Lainnya")
    val kategoriKeluar = listOf("Keamanan & Satpam", "Kebersihan & Sampah", "Perbaikan Fasum", "Konsumsi Pertemuan", "Sosial & Santunan", "Pembelian Inventaris", "Lainnya")

    AlertDialog(
      onDismissRequest = { showAddDialog = false },
      title = { Text(text = "Catat Transaksi Kas Baru", fontWeight = FontWeight.Bold) },
      text = {
        Column(
          verticalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          // Toggle Tipe Masuk / Keluar
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = {
                tipe = "MASUK"
                kategori = "Iuran Warga"
              },
              colors = ButtonDefaults.buttonColors(
                containerColor = if (tipe == "MASUK") StatusLunasGreen else MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (tipe == "MASUK") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
              ),
              modifier = Modifier.weight(1f)
            ) {
              Text("Kas Masuk (+)")
            }
            Button(
              onClick = {
                tipe = "KELUAR"
                kategori = "Keamanan & Satpam"
              },
              colors = ButtonDefaults.buttonColors(
                containerColor = if (tipe == "KELUAR") StatusBelumLunasRed else MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (tipe == "KELUAR") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
              ),
              modifier = Modifier.weight(1f)
            ) {
              Text("Kas Keluar (-)")
            }
          }

          // Kategori Selector
          Text(text = "Kategori Transaksi:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
          val currentOptions = if (tipe == "MASUK") kategoriMasuk else kategoriKeluar
          Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            currentOptions.take(3).forEach { kat ->
              FilterChip(
                selected = kategori == kat,
                onClick = { kategori = kat },
                label = { Text(kat, fontSize = 10.sp) }
              )
            }
          }
          Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            currentOptions.drop(3).forEach { kat ->
              FilterChip(
                selected = kategori == kat,
                onClick = { kategori = kat },
                label = { Text(kat, fontSize = 10.sp) }
              )
            }
          }

          OutlinedTextField(
            value = nominalStr,
            onValueChange = { nominalStr = it.filter { c -> c.isDigit() } },
            label = { Text("Jumlah Nominal (Rupiah)*") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_nominal_kas")
          )

          OutlinedTextField(
            value = keterangan,
            onValueChange = { keterangan = it },
            label = { Text("Keterangan Transaksi*") },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_keterangan_kas")
          )

          OutlinedTextField(
            value = pencatat,
            onValueChange = { pencatat = it },
            label = { Text("Nama Pencatat / PJ") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val nominal = nominalStr.toLongOrNull() ?: 0L
            if (nominal > 0 && keterangan.isNotBlank()) {
              viewModel.addKasTransaction(
                tipe = tipe,
                kategori = kategori,
                nominal = nominal,
                keterangan = keterangan.trim(),
                pencatat = pencatat.trim(),
                tanggal = today
              )
              Toast.makeText(context, "Transaksi Kas berhasil dicatat!", Toast.LENGTH_SHORT).show()
              showAddDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = GrisesGreenPrimary),
          modifier = Modifier.testTag("save_kas_btn")
        ) {
          Text("Simpan Transaksi")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddDialog = false }) {
          Text("Batal")
        }
      }
    )
  }

  // Dialog Konfirmasi Hapus Transaksi Kas
  transactionToDelete?.let { tx ->
    AlertDialog(
      onDismissRequest = { transactionToDelete = null },
      title = { Text(text = "Hapus Transaksi Kas?") },
      text = {
        Text("Apakah Anda yakin ingin menghapus catatan kas '${tx.keterangan}' sebesar ${formatRupiah(tx.nominal)}?")
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.deleteKasTransaction(tx)
            Toast.makeText(context, "Transaksi berhasil dihapus", Toast.LENGTH_SHORT).show()
            transactionToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = StatusBelumLunasRed)
        ) {
          Text("Hapus", color = Color.White)
        }
      },
      dismissButton = {
        TextButton(onClick = { transactionToDelete = null }) {
          Text("Batal")
        }
      }
    )
  }
}
