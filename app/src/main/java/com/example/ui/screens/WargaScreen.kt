package com.example.ui.screens

import android.content.Context
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import com.example.data.model.Warga
import com.example.ui.components.formatPhoneNumberForWhatsApp
import com.example.ui.components.sendWhatsAppMessage
import com.example.ui.theme.GrisesGoldContainer
import com.example.ui.theme.GrisesGoldPrimary
import com.example.ui.theme.GrisesGreenContainer
import com.example.ui.theme.GrisesGreenPrimary
import com.example.ui.theme.GrisesOnGoldContainer
import com.example.ui.theme.GrisesOnGreenContainer
import com.example.ui.theme.GrisesTeal
import com.example.ui.theme.GrisesTealContainer
import com.example.ui.theme.StatusBelumLunasBg
import com.example.ui.theme.StatusBelumLunasRed
import com.example.ui.theme.StatusLunasBg
import com.example.ui.theme.StatusLunasGreen
import com.example.ui.theme.StatusPendingAmber
import com.example.ui.theme.StatusPendingBg
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.viewmodel.GrisesViewModel

@Composable
fun WargaScreen(
  viewModel: GrisesViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val isAdminMode by viewModel.isAdminMode.collectAsStateWithLifecycle()
  val allWargaList by viewModel.allWarga.collectAsStateWithLifecycle()
  val wargaList by viewModel.filteredWarga.collectAsStateWithLifecycle()
  val searchQuery by viewModel.wargaSearchQuery.collectAsStateWithLifecycle()
  val selectedBlok by viewModel.selectedBlokFilter.collectAsStateWithLifecycle()
  val selectedStatus by viewModel.selectedStatusFilter.collectAsStateWithLifecycle()
  val stats by viewModel.wargaStats.collectAsStateWithLifecycle()

  var showAddEditDialog by remember { mutableStateOf(false) }
  var wargaToEdit by remember { mutableStateOf<Warga?>(null) }
  var wargaToDelete by remember { mutableStateOf<Warga?>(null) }
  var showResetConfirmDialog by remember { mutableStateOf(false) }

  val totalUnit = allWargaList.size
  val dihuniPemilik = allWargaList.count { it.statusHunian == "Dihuni Pemilik" }
  val dihuniKontrak = allWargaList.count { it.statusHunian == "Dihuni Kontrak" }
  val kosongBayarIuran = allWargaList.count { it.statusHunian == "Kosong (Bayar Iuran)" }
  val totalKosong = allWargaList.count { !it.isDihuni }

  Box(modifier = modifier.fillMaxSize()) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(4.dp))
        // Header Overview Card
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("warga_stats_card")
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "DATA WARGA & RUMAH GSS",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = GrisesGreenPrimary
              )
              if (isAdminMode) {
                TextButton(
                  onClick = { showResetConfirmDialog = true },
                  contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                  Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(13.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(text = "Sync PDF (58)", fontSize = 11.sp)
                }
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceAround
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Total Unit", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                Text(text = "$totalUnit", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
                Text(text = "Rumah", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
              }
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Dihuni", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                Text(text = "${stats.third}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = StatusLunasGreen)
                Text(text = "$dihuniPemilik Ttp • $dihuniKontrak Ktr", fontSize = 9.sp, color = StatusLunasGreen)
              }
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Kosong", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                Text(text = "$totalKosong", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = StatusPendingAmber)
                Text(text = "$kosongBayarIuran Bayar Iuran", fontSize = 9.sp, color = StatusPendingAmber)
              }
            }
          }
        }
      }

      // Search Field
      item {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { viewModel.setWargaSearchQuery(it) },
          label = { Text("Cari Warga (Nama, Blok, No. Rumah, Status)") },
          leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = GrisesGreenPrimary)
          },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { viewModel.setWargaSearchQuery("") }) {
                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("warga_search_input")
        )
      }

      // Blok Filter Chips (A: 16, B: 27, C: 15)
      item {
        val bloks = listOf("Semua", "A", "B", "C")
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(bloks) { blok ->
            val labelText = when (blok) {
              "Semua" -> "Semua Blok ($totalUnit)"
              "A" -> "Blok A (16)"
              "B" -> "Blok B (27)"
              "C" -> "Blok C (15)"
              else -> "Blok $blok"
            }
            FilterChip(
              selected = selectedBlok == blok,
              onClick = { viewModel.setSelectedBlokFilter(blok) },
              label = {
                Text(
                  text = labelText,
                  fontWeight = if (selectedBlok == blok) FontWeight.Bold else FontWeight.Normal,
                  fontSize = 11.sp
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = GrisesGreenContainer,
                selectedLabelColor = GrisesOnGreenContainer
              ),
              modifier = Modifier.testTag("filter_chip_$blok")
            )
          }
        }
      }

      // Status Hunian Filter Chips
      item {
        val statusList = listOf("Semua", "Dihuni Pemilik", "Dihuni Kontrak", "Kosong (Bayar Iuran)", "Kosong")
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(statusList) { st ->
            FilterChip(
              selected = selectedStatus == st,
              onClick = { viewModel.setSelectedStatusFilter(st) },
              label = {
                Text(
                  text = st,
                  fontSize = 10.sp,
                  fontWeight = if (selectedStatus == st) FontWeight.Bold else FontWeight.Normal
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = if (st == "Dihuni Pemilik") StatusLunasBg else if (st.startsWith("Kosong")) StatusPendingBg else GrisesGreenContainer,
                selectedLabelColor = if (st == "Dihuni Pemilik") StatusLunasGreen else if (st.startsWith("Kosong")) StatusPendingAmber else GrisesOnGreenContainer
              )
            )
          }
        }
      }

      // Warga List Items
      if (wargaList.isEmpty()) {
        item {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 24.dp)
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
            ) {
              Icon(
                imageVector = Icons.Default.People,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(48.dp)
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "Tidak ada data warga ditemukan",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = "Coba ubah kata kunci pencarian atau filter status",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.outline
              )
            }
          }
        }
      }

      items(wargaList, key = { it.id }) { warga ->
        WargaItemCard(
          warga = warga,
          isAdminMode = isAdminMode,
          onEdit = {
            wargaToEdit = warga
            showAddEditDialog = true
          },
          onDelete = {
            wargaToDelete = warga
          },
          onWhatsApp = {
            val sapaan = "Halo Bpk/Ibu ${warga.namaKK} (${warga.alamatLengkap} Perum Griya Sedayu Selaras), salam silaturahmi dari Pengurus Paguyuban GRISES."
            sendWhatsAppMessage(context, warga.noHp, sapaan)
          }
        )
      }

      item {
        Spacer(modifier = Modifier.height(80.dp))
      }
    }

    // Floating Action Button to Add Warga (Admin Only)
    if (isAdminMode) {
      FloatingActionButton(
        onClick = {
          wargaToEdit = null
          showAddEditDialog = true
        },
        containerColor = GrisesGreenPrimary,
        contentColor = Color.White,
        shape = CircleShape,
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(20.dp)
          .testTag("add_warga_fab")
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 16.dp)
        ) {
          Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Warga")
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "Tambah Warga", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }
    }
  }

  // Dialog Sync Ulang Data PDF
  if (showResetConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showResetConfirmDialog = false },
      title = { Text(text = "Sinkronkan Data Checklist PDF?") },
      text = {
        Text("Tindakan ini akan memuat ulang 58 data warga resmi Perum Griya Sedayu Selaras sesuai dokumen PDF (Blok A 01-16, Blok B 01-27, Blok C 01-15).")
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.reloadOfficialChecklistData()
            Toast.makeText(context, "58 data warga checklist PDF berhasil dimuat!", Toast.LENGTH_SHORT).show()
            showResetConfirmDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = GrisesGreenPrimary)
        ) {
          Text("Sinkronkan Sekarang")
        }
      },
      dismissButton = {
        TextButton(onClick = { showResetConfirmDialog = false }) {
          Text("Batal")
        }
      }
    )
  }

  // Dialog Tambah / Edit Warga
  if (showAddEditDialog) {
    AddEditWargaDialog(
      warga = wargaToEdit,
      onDismiss = { showAddEditDialog = false },
      onSave = { newOrUpdatedWarga ->
        if (wargaToEdit == null) {
          viewModel.addWarga(newOrUpdatedWarga)
          Toast.makeText(context, "Data warga berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
        } else {
          viewModel.updateWarga(newOrUpdatedWarga)
          Toast.makeText(context, "Data warga berhasil diperbarui!", Toast.LENGTH_SHORT).show()
        }
        showAddEditDialog = false
      }
    )
  }

  // Dialog Konfirmasi Hapus Warga
  wargaToDelete?.let { warga ->
    AlertDialog(
      onDismissRequest = { wargaToDelete = null },
      title = { Text(text = "Hapus Data Warga?") },
      text = {
        Text("Apakah Anda yakin ingin menghapus data Bpk/Ibu ${warga.namaKK} (${warga.alamatLengkap})? Data iuran dan jimpitan terkait juga akan dibersihkan.")
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.deleteWarga(warga)
            Toast.makeText(context, "Data warga berhasil dihapus", Toast.LENGTH_SHORT).show()
            wargaToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = StatusBelumLunasRed),
          modifier = Modifier.testTag("confirm_delete_warga_button")
        ) {
          Text("Hapus", color = Color.White)
        }
      },
      dismissButton = {
        TextButton(onClick = { wargaToDelete = null }) {
          Text("Batal")
        }
      }
    )
  }
}

@Composable
fun WargaItemCard(
  warga: Warga,
  isAdminMode: Boolean,
  onEdit: () -> Unit,
  onDelete: () -> Unit,
  onWhatsApp: () -> Unit
) {
  val badgeBgColor = when (warga.statusHunian) {
    "Dihuni Pemilik" -> StatusLunasBg
    "Dihuni Kontrak" -> GrisesTealContainer
    "Kosong (Bayar Iuran)" -> StatusPendingBg
    "Kosong (Ada Pemilik)" -> MaterialTheme.colorScheme.surfaceVariant
    else -> StatusBelumLunasBg
  }

  val badgeTextColor = when (warga.statusHunian) {
    "Dihuni Pemilik" -> StatusLunasGreen
    "Dihuni Kontrak" -> GrisesTeal
    "Kosong (Bayar Iuran)" -> StatusPendingAmber
    "Kosong (Ada Pemilik)" -> MaterialTheme.colorScheme.onSurfaceVariant
    else -> StatusBelumLunasRed
  }

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("warga_card_${warga.id}")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(if (warga.isDihuni) GrisesGreenContainer else MaterialTheme.colorScheme.surfaceVariant)
          ) {
            Text(
              text = "${warga.blok} ${warga.noRumah}",
              fontSize = 11.sp,
              fontWeight = FontWeight.ExtraBold,
              color = if (warga.isDihuni) GrisesOnGreenContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = warga.namaKK,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = warga.alamatLengkap,
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = badgeBgColor
        ) {
          Text(
            text = warga.statusHunian,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = badgeTextColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          if (warga.isDihuni) {
            Text(
              text = "Keluarga: ${warga.jumlahAnggota} Jiwa • ${warga.catatan}",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          } else {
            Text(
              text = warga.catatan,
              fontSize = 11.sp,
              color = badgeTextColor,
              fontWeight = FontWeight.SemiBold
            )
          }
        }

        // WhatsApp Action Button (Active only if not Belum Ada Pemilik)
        if (warga.statusHunian != "Belum Ada Pemilik") {
          Button(
            onClick = onWhatsApp,
            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
            shape = RoundedCornerShape(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.testTag("wa_button_${warga.id}")
          ) {
            Icon(
              imageVector = Icons.Default.Chat,
              contentDescription = "WhatsApp",
              tint = Color.White,
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "WA", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }

      // Admin Action Row: Edit & Delete
      if (isAdminMode) {
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          TextButton(
            onClick = onEdit,
            modifier = Modifier.testTag("edit_warga_btn_${warga.id}")
          ) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Edit", fontSize = 11.sp)
          }
          TextButton(
            onClick = onDelete,
            colors = ButtonDefaults.textButtonColors(contentColor = StatusBelumLunasRed),
            modifier = Modifier.testTag("delete_warga_btn_${warga.id}")
          ) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Hapus", fontSize = 11.sp)
          }
        }
      }
    }
  }
}

@Composable
fun AddEditWargaDialog(
  warga: Warga?,
  onDismiss: () -> Unit,
  onSave: (Warga) -> Unit
) {
  var namaKK by remember { mutableStateOf(warga?.namaKK ?: "") }
  var blok by remember { mutableStateOf(warga?.blok ?: "A") }
  var noRumah by remember { mutableStateOf(warga?.noRumah ?: "") }
  var noHp by remember { mutableStateOf(warga?.noHp ?: "") }
  var statusHunian by remember { mutableStateOf(warga?.statusHunian ?: "Dihuni Pemilik") }
  var jumlahAnggota by remember { mutableStateOf(warga?.jumlahAnggota?.toString() ?: "4") }
  var isDihuni by remember { mutableStateOf(warga?.isDihuni ?: true) }
  var catatan by remember { mutableStateOf(warga?.catatan ?: "") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = if (warga == null) "Tambah Data Warga Baru" else "Edit Data Warga",
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        OutlinedTextField(
          value = namaKK,
          onValueChange = { namaKK = it },
          label = { Text("Nama Warga / Pemilik*") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_nama_kk")
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = blok,
            onValueChange = { blok = it.uppercase() },
            label = { Text("Blok (A/B/C)*") },
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("input_blok")
          )
          OutlinedTextField(
            value = noRumah,
            onValueChange = { noRumah = it },
            label = { Text("No. Rumah*") },
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("input_no_rumah")
          )
        }

        OutlinedTextField(
          value = noHp,
          onValueChange = { noHp = it },
          label = { Text("No. WhatsApp (e.g. 08123456789)*") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_no_hp")
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = statusHunian,
            onValueChange = { statusHunian = it },
            label = { Text("Status Hunian") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = jumlahAnggota,
            onValueChange = { jumlahAnggota = it },
            label = { Text("Jml Jiwa") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
        }

        OutlinedTextField(
          value = catatan,
          onValueChange = { catatan = it },
          label = { Text("Catatan / Keterangan Status") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(text = "Status Rumah Aktif Dihuni", fontSize = 12.sp)
          Switch(
            checked = isDihuni,
            onCheckedChange = { isDihuni = it }
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (namaKK.isNotBlank() && blok.isNotBlank() && noRumah.isNotBlank()) {
            val updated = Warga(
              id = warga?.id ?: 0L,
              namaKK = namaKK.trim(),
              blok = blok.trim(),
              noRumah = noRumah.trim(),
              noHp = noHp.trim(),
              statusHunian = statusHunian.trim(),
              jumlahAnggota = jumlahAnggota.toIntOrNull() ?: 3,
              isDihuni = isDihuni,
              pekerjaan = if (isDihuni) "Warga Perum GSS" else "",
              catatan = catatan.trim()
            )
            onSave(updated)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = GrisesGreenPrimary),
        modifier = Modifier.testTag("save_warga_button")
      ) {
        Text("Simpan Data")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Batal")
      }
    }
  )
}
