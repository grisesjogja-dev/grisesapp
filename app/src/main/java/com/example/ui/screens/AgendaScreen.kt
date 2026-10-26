package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AgendaKegiatan
import com.example.ui.components.generateAgendaWhatsAppMessage
import com.example.ui.components.sendWhatsAppMessage
import com.example.ui.theme.GrisesGreenContainer
import com.example.ui.theme.GrisesGreenPrimary
import com.example.ui.theme.GrisesOnGreenContainer
import com.example.ui.theme.StatusBelumLunasRed
import com.example.ui.theme.StatusLunasBg
import com.example.ui.theme.StatusLunasGreen
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.viewmodel.GrisesViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AgendaScreen(
  viewModel: GrisesViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val isAdminMode by viewModel.isAdminMode.collectAsStateWithLifecycle()
  val agendaList by viewModel.allAgenda.collectAsStateWithLifecycle()

  var filterKategori by remember { mutableStateOf("Semua") }
  var showAddEditDialog by remember { mutableStateOf(false) }
  var agendaToEdit by remember { mutableStateOf<AgendaKegiatan?>(null) }
  var agendaToDelete by remember { mutableStateOf<AgendaKegiatan?>(null) }

  val filteredAgenda = if (filterKategori == "Semua") {
    agendaList
  } else {
    agendaList.filter { it.kategori == filterKategori }
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
        // Header Info
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = GrisesGreenContainer),
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
                .size(44.dp)
                .clip(CircleShape)
                .background(GrisesGreenPrimary)
            ) {
              Icon(
                imageVector = Icons.Default.Event,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "Agenda Kegiatan Paguyuban",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = GrisesOnGreenContainer
              )
              Text(
                text = "Transparansi rencana kerja bakti, rapat & ronda warga",
                fontSize = 11.sp,
                color = GrisesOnGreenContainer.copy(alpha = 0.8f)
              )
            }
          }
        }
      }

      // Filter Chips
      item {
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          listOf("Semua", "Kerja Bakti", "Rapat Warga", "Ronda Malam", "Olahraga / Senam").forEach { kat ->
            FilterChip(
              selected = filterKategori == kat,
              onClick = { filterKategori = kat },
              label = {
                Text(
                  text = kat,
                  fontSize = 11.sp,
                  fontWeight = if (filterKategori == kat) FontWeight.Bold else FontWeight.Normal
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = GrisesGreenContainer,
                selectedLabelColor = GrisesOnGreenContainer
              )
            )
          }
        }
      }

      // Agenda List
      items(filteredAgenda, key = { it.id }) { agenda ->
        AgendaCard(
          agenda = agenda,
          isAdminMode = isAdminMode,
          onShareWhatsApp = {
            val msg = generateAgendaWhatsAppMessage(agenda)
            sendWhatsAppMessage(context, "", msg)
          },
          onEdit = {
            agendaToEdit = agenda
            showAddEditDialog = true
          },
          onDelete = { agendaToDelete = agenda }
        )
      }

      item {
        Spacer(modifier = Modifier.height(80.dp))
      }
    }

    // FAB Tambah Agenda (Admin Only)
    if (isAdminMode) {
      FloatingActionButton(
        onClick = {
          agendaToEdit = null
          showAddEditDialog = true
        },
        containerColor = GrisesGreenPrimary,
        contentColor = Color.White,
        shape = CircleShape,
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(20.dp)
          .testTag("add_agenda_fab")
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 16.dp)
        ) {
          Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Agenda")
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "Tambah Agenda", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }
    }
  }

  // Dialog Tambah / Edit Agenda
  if (showAddEditDialog) {
    AddEditAgendaDialog(
      agenda = agendaToEdit,
      onDismiss = { showAddEditDialog = false },
      onSave = { updated ->
        if (agendaToEdit == null) {
          viewModel.addAgenda(updated)
          Toast.makeText(context, "Agenda berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
        } else {
          viewModel.updateAgenda(updated)
          Toast.makeText(context, "Agenda berhasil diperbarui!", Toast.LENGTH_SHORT).show()
        }
        showAddEditDialog = false
      }
    )
  }

  // Dialog Konfirmasi Hapus
  agendaToDelete?.let { agenda ->
    AlertDialog(
      onDismissRequest = { agendaToDelete = null },
      title = { Text(text = "Hapus Agenda Kegiatan?") },
      text = { Text("Apakah Anda yakin ingin menghapus agenda '${agenda.judul}'?") },
      confirmButton = {
        Button(
          onClick = {
            viewModel.deleteAgenda(agenda)
            Toast.makeText(context, "Agenda berhasil dihapus", Toast.LENGTH_SHORT).show()
            agendaToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = StatusBelumLunasRed)
        ) {
          Text("Hapus", color = Color.White)
        }
      },
      dismissButton = {
        TextButton(onClick = { agendaToDelete = null }) {
          Text("Batal")
        }
      }
    )
  }
}

@Composable
fun AgendaCard(
  agenda: AgendaKegiatan,
  isAdminMode: Boolean,
  onShareWhatsApp: () -> Unit,
  onEdit: () -> Unit,
  onDelete: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("agenda_card_${agenda.id}")
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = GrisesGreenContainer
        ) {
          Text(
            text = agenda.kategori,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = GrisesOnGreenContainer,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = StatusLunasBg
        ) {
          Text(
            text = agenda.status,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = StatusLunasGreen,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = agenda.judul,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(8.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, tint = GrisesGreenPrimary, modifier = Modifier.size(15.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "${agenda.tanggal} • ${agenda.jam}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
      }

      Spacer(modifier = Modifier.height(4.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.Default.Place, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(15.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = agenda.lokasi, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
      }

      Spacer(modifier = Modifier.height(4.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(15.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "Koordinator: ${agenda.penanggungJawab}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = agenda.deskripsi,
        fontSize = 11.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        lineHeight = 16.sp
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // WhatsApp Share to Group Button
        Button(
          onClick = onShareWhatsApp,
          colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
          shape = RoundedCornerShape(8.dp),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
          modifier = Modifier.testTag("share_agenda_wa_btn_${agenda.id}")
        ) {
          Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Bagikan ke WA Grup", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
fun AddEditAgendaDialog(
  agenda: AgendaKegiatan?,
  onDismiss: () -> Unit,
  onSave: (AgendaKegiatan) -> Unit
) {
  val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
  val today = dateFormat.format(Date())

  var judul by remember { mutableStateOf(agenda?.judul ?: "") }
  var tanggal by remember { mutableStateOf(agenda?.tanggal ?: today) }
  var jam by remember { mutableStateOf(agenda?.jam ?: "19:30 WIB") }
  var lokasi by remember { mutableStateOf(agenda?.lokasi ?: "Balai Warga Perum Griya Sedayu Selaras") }
  var kategori by remember { mutableStateOf(agenda?.kategori ?: "Kerja Bakti") }
  var deskripsi by remember { mutableStateOf(agenda?.deskripsi ?: "") }
  var penanggungJawab by remember { mutableStateOf(agenda?.penanggungJawab ?: "Pengurus Paguyuban") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(text = if (agenda == null) "Tambah Agenda Baru" else "Edit Agenda", fontWeight = FontWeight.Bold) },
    text = {
      Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        OutlinedTextField(
          value = judul,
          onValueChange = { judul = it },
          label = { Text("Nama Kegiatan*") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = tanggal,
            onValueChange = { tanggal = it },
            label = { Text("Tanggal (yyyy-MM-dd)*") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = jam,
            onValueChange = { jam = it },
            label = { Text("Waktu / Jam*") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
        }

        OutlinedTextField(
          value = lokasi,
          onValueChange = { lokasi = it },
          label = { Text("Lokasi Acara*") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = kategori,
            onValueChange = { kategori = it },
            label = { Text("Kategori Acara") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = penanggungJawab,
            onValueChange = { penanggungJawab = it },
            label = { Text("Penanggung Jawab") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
        }

        OutlinedTextField(
          value = deskripsi,
          onValueChange = { deskripsi = it },
          label = { Text("Deskripsi / Rincian Acara*") },
          minLines = 2,
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (judul.isNotBlank()) {
            val updated = AgendaKegiatan(
              id = agenda?.id ?: 0L,
              judul = judul.trim(),
              tanggal = tanggal.trim(),
              jam = jam.trim(),
              lokasi = lokasi.trim(),
              kategori = kategori.trim(),
              deskripsi = deskripsi.trim(),
              penanggungJawab = penanggungJawab.trim(),
              status = agenda?.status ?: "Akan Datang"
            )
            onSave(updated)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = GrisesGreenPrimary)
      ) {
        Text("Simpan Agenda")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Batal")
      }
    }
  )
}
