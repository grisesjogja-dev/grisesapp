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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentReturn
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Place
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
import com.example.data.model.Inventaris
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
import com.example.ui.viewmodel.GrisesViewModel

@Composable
fun InventarisScreen(
  viewModel: GrisesViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val isAdminMode by viewModel.isAdminMode.collectAsStateWithLifecycle()
  val inventarisList by viewModel.allInventaris.collectAsStateWithLifecycle()

  var filterStatus by remember { mutableStateOf("Semua") } // "Semua", "Tersedia", "Dipinjam"
  var showAddEditDialog by remember { mutableStateOf(false) }
  var itemToEdit by remember { mutableStateOf<Inventaris?>(null) }
  var itemToPinjam by remember { mutableStateOf<Inventaris?>(null) }
  var itemToDelete by remember { mutableStateOf<Inventaris?>(null) }

  val filteredList = when (filterStatus) {
    "Tersedia" -> inventarisList.filter { it.statusPinjam == "Tersedia" }
    "Dipinjam" -> inventarisList.filter { it.statusPinjam != "Tersedia" }
    else -> inventarisList
  }

  val totalAset = inventarisList.size
  val totalTersedia = inventarisList.count { it.statusPinjam == "Tersedia" }
  val totalDipinjam = inventarisList.count { it.statusPinjam != "Tersedia" }

  Box(modifier = modifier.fillMaxSize()) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(4.dp))
        // Header Overview Card
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("inventaris_overview_card")
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "Total Item", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
              Text(text = "$totalAset", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = GrisesTeal)
              Text(text = "Barang Aset", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "Tersedia", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
              Text(text = "$totalTersedia", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = StatusLunasGreen)
              Text(text = "Siap Pakai", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "Dipinjam", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
              Text(text = "$totalDipinjam", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = StatusPendingAmber)
              Text(text = "Oleh Warga", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
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
          listOf("Semua", "Tersedia", "Dipinjam").forEach { status ->
            FilterChip(
              selected = filterStatus == status,
              onClick = { filterStatus = status },
              label = {
                Text(
                  text = status,
                  fontWeight = if (filterStatus == status) FontWeight.Bold else FontWeight.Normal,
                  fontSize = 12.sp
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = if (status == "Tersedia") StatusLunasBg else if (status == "Dipinjam") StatusPendingBg else GrisesGreenContainer,
                selectedLabelColor = if (status == "Tersedia") StatusLunasGreen else if (status == "Dipinjam") StatusPendingAmber else GrisesOnGreenContainer
              ),
              modifier = Modifier.testTag("filter_inv_$status")
            )
          }
        }
      }

      // Items List
      items(filteredList, key = { it.id }) { item ->
        InventarisCard(
          item = item,
          isAdminMode = isAdminMode,
          onPinjam = { itemToPinjam = item },
          onKembalikan = {
            viewModel.kembalikanInventaris(item)
            Toast.makeText(context, "Barang '${item.namaBarang}' telah dikembalikan!", Toast.LENGTH_SHORT).show()
          },
          onEdit = {
            itemToEdit = item
            showAddEditDialog = true
          },
          onDelete = { itemToDelete = item }
        )
      }

      item {
        Spacer(modifier = Modifier.height(80.dp))
      }
    }

    // FAB Tambah Inventaris (Admin Only)
    if (isAdminMode) {
      FloatingActionButton(
        onClick = {
          itemToEdit = null
          showAddEditDialog = true
        },
        containerColor = GrisesGreenPrimary,
        contentColor = Color.White,
        shape = CircleShape,
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(20.dp)
          .testTag("add_inventaris_fab")
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 16.dp)
        ) {
          Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Barang")
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "Tambah Aset", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }
    }
  }

  // Dialog Pinjam Inventaris
  itemToPinjam?.let { item ->
    var peminjamNama by remember { mutableStateOf("") }
    var keperluan by remember { mutableStateOf("Acara keluarga") }
    var rencanaKembali by remember { mutableStateOf("2 hari lagi") }
    var jumlahPinjam by remember { mutableStateOf("1") }

    AlertDialog(
      onDismissRequest = { itemToPinjam = null },
      title = { Text(text = "Form Peminjaman Inventaris") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "Barang: ${item.namaBarang}",
            fontWeight = FontWeight.Bold,
            color = GrisesGreenPrimary
          )
          Text(
            text = "Tersedia: ${item.jumlahTersedia} dari ${item.jumlahTotal} ${item.satuan}",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          OutlinedTextField(
            value = peminjamNama,
            onValueChange = { peminjamNama = it },
            label = { Text("Nama Peminjam (Warga/Blok)*") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = jumlahPinjam,
            onValueChange = { jumlahPinjam = it },
            label = { Text("Jumlah Dipinjam (${item.satuan})*") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = keperluan,
            onValueChange = { keperluan = it },
            label = { Text("Keperluan Pinjam*") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = rencanaKembali,
            onValueChange = { rencanaKembali = it },
            label = { Text("Rencana Tanggal Pengembalian*") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val jml = jumlahPinjam.toIntOrNull() ?: 1
            if (peminjamNama.isNotBlank() && jml > 0) {
              viewModel.pinjamInventaris(item, peminjamNama.trim(), keperluan.trim(), rencanaKembali.trim(), jml)
              Toast.makeText(context, "Peminjaman aset berhasil dicatat!", Toast.LENGTH_SHORT).show()
              itemToPinjam = null
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = GrisesGreenPrimary),
          modifier = Modifier.testTag("confirm_pinjam_btn")
        ) {
          Text("Pinjam Barang")
        }
      },
      dismissButton = {
        TextButton(onClick = { itemToPinjam = null }) {
          Text("Batal")
        }
      }
    )
  }

  // Dialog Tambah / Edit Inventaris
  if (showAddEditDialog) {
    AddEditInventarisDialog(
      item = itemToEdit,
      onDismiss = { showAddEditDialog = false },
      onSave = { updatedItem ->
        if (itemToEdit == null) {
          viewModel.addInventaris(updatedItem)
          Toast.makeText(context, "Barang aset berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
        } else {
          viewModel.updateInventaris(updatedItem)
          Toast.makeText(context, "Barang aset berhasil diperbarui!", Toast.LENGTH_SHORT).show()
        }
        showAddEditDialog = false
      }
    )
  }

  // Dialog Konfirmasi Hapus
  itemToDelete?.let { item ->
    AlertDialog(
      onDismissRequest = { itemToDelete = null },
      title = { Text(text = "Hapus Barang Inventaris?") },
      text = { Text("Apakah Anda yakin ingin menghapus '${item.namaBarang}' dari daftar aset perumahan?") },
      confirmButton = {
        Button(
          onClick = {
            viewModel.deleteInventaris(item)
            Toast.makeText(context, "Aset berhasil dihapus", Toast.LENGTH_SHORT).show()
            itemToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = StatusBelumLunasRed)
        ) {
          Text("Hapus", color = Color.White)
        }
      },
      dismissButton = {
        TextButton(onClick = { itemToDelete = null }) {
          Text("Batal")
        }
      }
    )
  }
}

@Composable
fun InventarisCard(
  item: Inventaris,
  isAdminMode: Boolean,
  onPinjam: () -> Unit,
  onKembalikan: () -> Unit,
  onEdit: () -> Unit,
  onDelete: () -> Unit
) {
  val isDipinjam = item.statusPinjam != "Tersedia"

  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("inventaris_card_${item.id}")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = item.namaBarang,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Kode: ${item.kodeBarang} • Kategori: ${item.kategori}",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (!isDipinjam) StatusLunasBg else StatusPendingBg
        ) {
          Text(
            text = if (!isDipinjam) "Tersedia (${item.jumlahTersedia}/${item.jumlahTotal})" else "Dipinjam (${item.jumlahTersedia} sisa)",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (!isDipinjam) StatusLunasGreen else StatusPendingAmber,
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
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(imageVector = Icons.Default.Place, contentDescription = null, tint = GrisesGreenPrimary, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = item.lokasiSimpan, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(
          text = "Kondisi: ${item.kondisi}",
          fontSize = 11.sp,
          color = if (item.kondisi == "Baik") StatusLunasGreen else StatusBelumLunasRed,
          fontWeight = FontWeight.SemiBold
        )
      }

      if (isDipinjam && item.namaPeminjam != null) {
        Spacer(modifier = Modifier.height(6.dp))
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(8.dp)) {
            Text(
              text = "Peminjam: ${item.namaPeminjam}",
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Keperluan: ${item.keperluanPinjam ?: "-"} • Kembali: ${item.tglKembaliRencana ?: "-"}",
              fontSize = 10.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          if (item.jumlahTersedia > 0) {
            Button(
              onClick = onPinjam,
              colors = ButtonDefaults.buttonColors(containerColor = GrisesGreenPrimary),
              shape = RoundedCornerShape(8.dp),
              contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
              modifier = Modifier.testTag("pinjam_btn_${item.id}")
            ) {
              Icon(imageVector = Icons.Default.Handshake, contentDescription = null, modifier = Modifier.size(13.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Pinjam", fontSize = 11.sp)
            }
          }

          if (isDipinjam) {
            Button(
              onClick = onKembalikan,
              colors = ButtonDefaults.buttonColors(containerColor = GrisesGoldPrimary),
              shape = RoundedCornerShape(8.dp),
              contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
              modifier = Modifier.testTag("kembalikan_btn_${item.id}")
            ) {
              Icon(imageVector = Icons.Default.AssignmentReturn, contentDescription = null, modifier = Modifier.size(13.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Kembalikan", fontSize = 11.sp)
            }
          }
        }

        if (isAdminMode) {
          Row {
            IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
              Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(15.dp))
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
              Icon(imageVector = Icons.Default.Delete, contentDescription = "Hapus", tint = StatusBelumLunasRed, modifier = Modifier.size(15.dp))
            }
          }
        }
      }
    }
  }
}

@Composable
fun AddEditInventarisDialog(
  item: Inventaris?,
  onDismiss: () -> Unit,
  onSave: (Inventaris) -> Unit
) {
  var namaBarang by remember { mutableStateOf(item?.namaBarang ?: "") }
  var kodeBarang by remember { mutableStateOf(item?.kodeBarang ?: "INV-0${System.currentTimeMillis() % 100}") }
  var kategori by remember { mutableStateOf(item?.kategori ?: "Peralatan Tenda") }
  var jumlahTotal by remember { mutableStateOf(item?.jumlahTotal?.toString() ?: "1") }
  var satuan by remember { mutableStateOf(item?.satuan ?: "Unit") }
  var kondisi by remember { mutableStateOf(item?.kondisi ?: "Baik") }
  var lokasiSimpan by remember { mutableStateOf(item?.lokasiSimpan ?: "Gudang Fasum") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(text = if (item == null) "Tambah Barang Inventaris" else "Edit Inventaris", fontWeight = FontWeight.Bold) },
    text = {
      Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        OutlinedTextField(
          value = namaBarang,
          onValueChange = { namaBarang = it },
          label = { Text("Nama Barang Aset*") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = kodeBarang,
            onValueChange = { kodeBarang = it },
            label = { Text("Kode Barang") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = kategori,
            onValueChange = { kategori = it },
            label = { Text("Kategori") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = jumlahTotal,
            onValueChange = { jumlahTotal = it },
            label = { Text("Jml Total*") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = satuan,
            onValueChange = { satuan = it },
            label = { Text("Satuan (Unit/Pcs)") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = kondisi,
            onValueChange = { kondisi = it },
            label = { Text("Kondisi (Baik/Rusak)") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = lokasiSimpan,
            onValueChange = { lokasiSimpan = it },
            label = { Text("Lokasi Simpan") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val jml = jumlahTotal.toIntOrNull() ?: 1
          if (namaBarang.isNotBlank()) {
            val updated = Inventaris(
              id = item?.id ?: 0L,
              kodeBarang = kodeBarang.trim(),
              namaBarang = namaBarang.trim(),
              kategori = kategori.trim(),
              jumlahTotal = jml,
              jumlahTersedia = item?.jumlahTersedia ?: jml,
              satuan = satuan.trim(),
              kondisi = kondisi.trim(),
              lokasiSimpan = lokasiSimpan.trim(),
              statusPinjam = item?.statusPinjam ?: "Tersedia",
              namaPeminjam = item?.namaPeminjam,
              tglPinjam = item?.tglPinjam,
              tglKembaliRencana = item?.tglKembaliRencana,
              keperluanPinjam = item?.keperluanPinjam
            )
            onSave(updated)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = GrisesGreenPrimary)
      ) {
        Text("Simpan Aset")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Batal")
      }
    }
  )
}
