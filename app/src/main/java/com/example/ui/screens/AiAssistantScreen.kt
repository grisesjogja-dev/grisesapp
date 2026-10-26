package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Warga
import com.example.ui.components.formatRupiah
import com.example.ui.components.sendWhatsAppMessage
import com.example.ui.theme.GrisesGoldContainer
import com.example.ui.theme.GrisesGoldPrimary
import com.example.ui.theme.GrisesGreenContainer
import com.example.ui.theme.GrisesGreenPrimary
import com.example.ui.theme.GrisesOnGoldContainer
import com.example.ui.theme.GrisesOnGreenContainer
import com.example.ui.theme.GrisesTeal
import com.example.ui.theme.GrisesTealContainer
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.viewmodel.AiAssistantViewModel
import com.example.ui.viewmodel.AiUiState
import com.example.ui.viewmodel.ChatMessage
import com.example.ui.viewmodel.GrisesViewModel

@Composable
fun AiAssistantScreen(
  grisesViewModel: GrisesViewModel,
  aiViewModel: AiAssistantViewModel = viewModel(),
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var selectedSubTab by rememberSaveable { mutableIntStateOf(0) }
  val subTabs = listOf(
    "Draf Surat Edaran" to Icons.Default.Description,
    "Draf Tagihan WA" to Icons.Default.Sms,
    "Analisis Kas" to Icons.Default.PieChart,
    "Tanya Asisten AI" to Icons.Default.ChatBubbleOutline
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Header Banner
    Card(
      shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
      colors = CardDefaults.cardColors(containerColor = GrisesGreenPrimary),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("ai_header_card")
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
      ) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.2f))
        ) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = Color(0xFFFFD54F),
            modifier = Modifier.size(26.dp)
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "Asisten AI GRISES",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFFFFD54F)
            ) {
              Text(
                text = "Gemini 3.8 Flash",
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1B3B2B),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
          Text(
            text = "Otomasi surat edaran, penagihan santun, & analisis kas paguyuban",
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.9f)
          )
        }
      }
    }

    // Sub-tab Navigation
    ScrollableTabRow(
      selectedTabIndex = selectedSubTab,
      containerColor = MaterialTheme.colorScheme.surface,
      edgePadding = 12.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      subTabs.forEachIndexed { index, (title, icon) ->
        val isSelected = selectedSubTab == index
        Tab(
          selected = isSelected,
          onClick = { selectedSubTab = index },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (isSelected) GrisesGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) GrisesGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          },
          modifier = Modifier.testTag("ai_subtab_$index")
        )
      }
    }

    // Content based on selected sub tab
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      when (selectedSubTab) {
        0 -> SuratEdaranTabContent(aiViewModel = aiViewModel)
        1 -> WaBillingTabContent(aiViewModel = aiViewModel, grisesViewModel = grisesViewModel)
        2 -> KasAnalysisTabContent(aiViewModel = aiViewModel, grisesViewModel = grisesViewModel)
        3 -> ChatAssistantTabContent(aiViewModel = aiViewModel)
      }
    }
  }
}

// ==========================================
// SUB-TAB 0: DRAF SURAT EDARAN & PENGUMUMAN
// ==========================================
@Composable
private fun SuratEdaranTabContent(aiViewModel: AiAssistantViewModel) {
  val context = LocalContext.current
  val uiState by aiViewModel.edaranUiState.collectAsStateWithLifecycle()

  var judul by rememberSaveable { mutableStateOf("Kerja Bakti Bersih Lingkungan & Drainase") }
  var kategori by rememberSaveable { mutableStateOf("Kerja Bakti") }
  var tanggal by rememberSaveable { mutableStateOf("Minggu, 18 Mei 2025") }
  var waktu by rememberSaveable { mutableStateOf("07.00 WIB s/d Selesai") }
  var lokasi by rememberSaveable { mutableStateOf("Fasum Lapangan & Saluran Air Utama GRISES") }
  var instruksi by rememberSaveable { mutableStateOf("Membawa cangkul, sabit, dan sapu lidi. Disediakan snack & kopi bersama.") }

  val presets = listOf(
    "Kerja Bakti" to Pair("Kerja Bakti Bersih Lingkungan & Drainase", "Membawa cangkul, sabit, dan sapu lidi. Disediakan snack & kopi bersama."),
    "Rapat Warga" to Pair("Rapat Pleno Paguyuban Warga GRISES", "Agenda: Pembahasan transparansi kas, rencana portal keamanan baru, dan rembug warga."),
    "Himbauan Ronda" to Pair("Himbauan Ketertiban Siskamling & Jimpitan Ronda", "Mengaktifkan kembali jadwal ronda malam dan memastikan jimpitan Rp 500 disiapkan setiap malam."),
    "Peringatan HUT RI" to Pair("Persiapan Perayaan HUT Kemerdekaan RI di Lingkungan GRISES", "Lomba anak-anak, tirakatan malam 17-an, dan gotong royong pasang bendera/umbul-umbul.")
  )

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Text(
        text = "Pilih Template Cepat atau Kustomisasi:",
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(6.dp))
      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(presets) { (name, details) ->
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (kategori == name) GrisesGreenContainer else MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
            modifier = Modifier
              .clickable {
                kategori = name
                judul = details.first
                instruksi = details.second
              }
              .testTag("preset_$name")
          ) {
            Text(
              text = name,
              fontSize = 11.sp,
              fontWeight = if (kategori == name) FontWeight.Bold else FontWeight.Normal,
              color = if (kategori == name) GrisesOnGreenContainer else MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
          }
        }
      }
    }

    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedTextField(
            value = judul,
            onValueChange = { judul = it },
            label = { Text("Judul Pengumuman") },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_edaran_judul"),
            shape = RoundedCornerShape(10.dp)
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedTextField(
              value = tanggal,
              onValueChange = { tanggal = it },
              label = { Text("Hari / Tanggal") },
              modifier = Modifier
                .weight(1f)
                .testTag("input_edaran_tanggal"),
              shape = RoundedCornerShape(10.dp)
            )
            OutlinedTextField(
              value = waktu,
              onValueChange = { waktu = it },
              label = { Text("Waktu") },
              modifier = Modifier
                .weight(1f)
                .testTag("input_edaran_waktu"),
              shape = RoundedCornerShape(10.dp)
            )
          }

          OutlinedTextField(
            value = lokasi,
            onValueChange = { lokasi = it },
            label = { Text("Tempat / Lokasi") },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_edaran_lokasi"),
            shape = RoundedCornerShape(10.dp)
          )

          OutlinedTextField(
            value = instruksi,
            onValueChange = { instruksi = it },
            label = { Text("Catatan / Himbauan Tambahan") },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_edaran_instruksi"),
            minLines = 2,
            shape = RoundedCornerShape(10.dp)
          )

          Button(
            onClick = {
              aiViewModel.generateSuratEdaran(
                judul = judul,
                kategori = kategori,
                tanggalKegiatan = tanggal,
                waktu = waktu,
                lokasi = lokasi,
                instruksiKhusus = instruksi
              )
            },
            colors = ButtonDefaults.buttonColors(containerColor = GrisesGreenPrimary),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("btn_generate_edaran")
          ) {
            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Susun Pengumuman dengan Gemini AI")
          }
        }
      }
    }

    // AI Result Card
    item {
      AiResultView(
        uiState = uiState,
        title = "Draf Pengumuman Warga GRISES",
        onClear = { aiViewModel.clearEdaranState() },
        onShareWa = { text -> sendWhatsAppMessage(context, "", text) }
      )
    }
  }
}

// ==========================================
// SUB-TAB 1: DRAF TAGIHAN WA SANTUN
// ==========================================
@Composable
private fun WaBillingTabContent(
  aiViewModel: AiAssistantViewModel,
  grisesViewModel: GrisesViewModel
) {
  val context = LocalContext.current
  val uiState by aiViewModel.waBillingUiState.collectAsStateWithLifecycle()
  val allWarga by grisesViewModel.allWarga.collectAsStateWithLifecycle()

  var selectedWargaName by rememberSaveable { mutableStateOf("Budi Santoso") }
  var selectedBlok by rememberSaveable { mutableStateOf("Blok A No. 01") }
  var selectedPhone by rememberSaveable { mutableStateOf("") }
  var periode by rememberSaveable { mutableStateOf("Mei 2025") }
  var nominalIuran by rememberSaveable { mutableStateOf("35000") }
  var nominalJimpitan by rememberSaveable { mutableStateOf("15000") }
  var gayaBahasa by rememberSaveable { mutableStateOf("Sangat Santun & Akrab Guyub") }
  var metodeBayar by rememberSaveable {
    mutableStateOf("Transfer Bank BPD DIY 006.211.045892 (a.n Paguyuban GRISES) atau tunai via pos ronda")
  }

  var isDropdownOpen by remember { mutableStateOf(false) }

  val gayaList = listOf(
    "Sangat Santun & Akrab Guyub",
    "Formal Sopan Pengurus Paguyuban",
    "Pengingat Halus Warga Baru / Simpatik"
  )

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Text(
            text = "Data Warga Penerima Tagihan:",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )

          // Select from actual resident list
          Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
              value = "$selectedWargaName ($selectedBlok)",
              onValueChange = {},
              readOnly = true,
              label = { Text("Pilih Warga (dari 58 KK GRISES)") },
              trailingIcon = {
                IconButton(onClick = { isDropdownOpen = true }) {
                  Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null)
                }
              },
              modifier = Modifier
                .fillMaxWidth()
                .clickable { isDropdownOpen = true }
                .testTag("select_warga_billing"),
              shape = RoundedCornerShape(10.dp)
            )

            DropdownMenu(
              expanded = isDropdownOpen,
              onDismissRequest = { isDropdownOpen = false }
            ) {
              allWarga.take(30).forEach { w ->
                DropdownMenuItem(
                  text = {
                    Text(
                      text = "${w.alamatLengkap} - ${w.namaKK}",
                      fontSize = 12.sp
                    )
                  },
                  onClick = {
                    selectedWargaName = w.namaKK
                    selectedBlok = w.alamatLengkap
                    selectedPhone = w.noHp
                    isDropdownOpen = false
                  }
                )
              }
            }
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedTextField(
              value = periode,
              onValueChange = { periode = it },
              label = { Text("Bulan / Periode") },
              modifier = Modifier
                .weight(1f)
                .testTag("input_billing_periode"),
              shape = RoundedCornerShape(10.dp)
            )
            OutlinedTextField(
              value = nominalIuran,
              onValueChange = { nominalIuran = it },
              label = { Text("Iuran (Rp)") },
              modifier = Modifier
                .weight(1f)
                .testTag("input_billing_iuran"),
              shape = RoundedCornerShape(10.dp)
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedTextField(
              value = nominalJimpitan,
              onValueChange = { nominalJimpitan = it },
              label = { Text("Jimpitan Ronda (Rp)") },
              modifier = Modifier
                .weight(1f)
                .testTag("input_billing_jimpitan"),
              shape = RoundedCornerShape(10.dp)
            )
          }

          // Gaya Bahasa Chips
          Text(
            text = "Gaya Bahasa Pengingat:",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(gayaList) { g ->
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (gayaBahasa == g) GrisesTealContainer else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.clickable { gayaBahasa = g }
              ) {
                Text(
                  text = g,
                  fontSize = 10.sp,
                  fontWeight = if (gayaBahasa == g) FontWeight.Bold else FontWeight.Normal,
                  color = if (gayaBahasa == g) GrisesTeal else MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }

          OutlinedTextField(
            value = metodeBayar,
            onValueChange = { metodeBayar = it },
            label = { Text("Petunjuk / Rekening Pembayaran") },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_billing_metode"),
            shape = RoundedCornerShape(10.dp)
          )

          Button(
            onClick = {
              val iuran = nominalIuran.toLongOrNull() ?: 35000L
              val jimpitan = nominalJimpitan.toLongOrNull() ?: 15000L
              aiViewModel.generateWaBillingMessage(
                namaWarga = selectedWargaName,
                blokRumah = selectedBlok,
                periode = periode,
                nominalIuran = iuran,
                nominalJimpitan = jimpitan,
                gayaBahasa = gayaBahasa,
                metodeBayar = metodeBayar
              )
            },
            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("btn_generate_wa_billing")
          ) {
            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Susun Pesan WhatsApp Santun")
          }
        }
      }
    }

    item {
      AiResultView(
        uiState = uiState,
        title = "Draf Pesan WhatsApp Warga",
        onClear = { aiViewModel.clearWaBillingState() },
        onShareWa = { text -> sendWhatsAppMessage(context, selectedPhone, text) }
      )
    }
  }
}

// ==========================================
// SUB-TAB 2: ANALISIS FINANSIAL KAS CERDAS
// ==========================================
@Composable
private fun KasAnalysisTabContent(
  aiViewModel: AiAssistantViewModel,
  grisesViewModel: GrisesViewModel
) {
  val context = LocalContext.current
  val uiState by aiViewModel.kasAnalysisUiState.collectAsStateWithLifecycle()
  val kasSummary by grisesViewModel.kasSummary.collectAsStateWithLifecycle()
  val jimpitanSummary by grisesViewModel.jimpitanSummary.collectAsStateWithLifecycle()
  val iuranList by grisesViewModel.currentPeriodIuran.collectAsStateWithLifecycle()
  val allWarga by grisesViewModel.allWarga.collectAsStateWithLifecycle()

  val totalWarga = allWarga.size
  val countLunas = iuranList.count { it.isLunas }
  val countBelum = totalWarga - countLunas

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Data Real-Time Kas Paguyuban:",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = GrisesGreenContainer
            ) {
              Text(
                text = "Live Sync",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = GrisesOnGreenContainer,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text(text = "Saldo Kas Saat Ini", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text(text = formatRupiah(kasSummary.saldoSaatIni), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GrisesGreenPrimary)
            }
            Column(horizontalAlignment = Alignment.End) {
              Text(text = "Jimpitan Bulan Ini", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text(text = formatRupiah(jimpitanSummary.totalBulanIni), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GrisesGoldPrimary)
            }
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text(text = "Total Pemasukan", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text(text = formatRupiah(kasSummary.totalMasuk), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
            Column(horizontalAlignment = Alignment.End) {
              Text(text = "Total Pengeluaran", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text(text = formatRupiah(kasSummary.totalKeluar), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "Kepatuhan Iuran: $countLunas dari $totalWarga KK Lunas",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = "$countBelum KK Belum",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFC62828)
            )
          }

          Button(
            onClick = {
              aiViewModel.generateKasAnalysis(
                saldoSaatIni = kasSummary.saldoSaatIni,
                totalPemasukan = kasSummary.totalMasuk,
                totalPengeluaran = kasSummary.totalKeluar,
                totalJimpitanBulanIni = jimpitanSummary.totalBulanIni,
                jumlahWargaLunas = countLunas,
                jumlahWargaBelumLunas = countBelum,
                totalWarga = totalWarga
              )
            },
            colors = ButtonDefaults.buttonColors(containerColor = GrisesGreenPrimary),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("btn_generate_kas_analysis")
          ) {
            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Analisis Laporan Keuangan Cerdas")
          }
        }
      }
    }

    item {
      AiResultView(
        uiState = uiState,
        title = "Laporan Analisis & Rekomendasi Finansial",
        onClear = { aiViewModel.clearKasAnalysisState() },
        onShareWa = { text -> sendWhatsAppMessage(context, "", text) }
      )
    }
  }
}

// ==========================================
// SUB-TAB 3: TANYA ASISTEN GRISES (CHAT)
// ==========================================
@Composable
private fun ChatAssistantTabContent(aiViewModel: AiAssistantViewModel) {
  val chatHistory by aiViewModel.chatHistory.collectAsStateWithLifecycle()
  val chatUiState by aiViewModel.chatUiState.collectAsStateWithLifecycle()
  var inputMessage by rememberSaveable { mutableStateOf("") }
  val listState = rememberLazyListState()

  val suggestedPrompts = listOf(
    "Bagaimana aturan pinjam tenda/kursi inventaris?",
    "Berapa nominal jimpitan harian dan tujuannya?",
    "Tips meningkatkan kehadiran ronda malam warga",
    "Buatkan susunan acara rembug warga bulanan"
  )

  LaunchedEffect(chatHistory.size, chatUiState) {
    if (chatHistory.isNotEmpty()) {
      listState.animateScrollToItem(chatHistory.size)
    }
  }

  Column(modifier = Modifier.fillMaxSize()) {
    // Suggested chips
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 8.dp)
    ) {
      items(suggestedPrompts) { p ->
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier.clickable {
            inputMessage = p
            aiViewModel.sendChatMessage(p)
            inputMessage = ""
          }
        ) {
          Text(
            text = p,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }
    }

    // Chat history
    LazyColumn(
      state = listState,
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(chatHistory) { msg ->
        ChatBubble(message = msg)
      }

      // Streaming state indicator
      if (chatUiState is AiUiState.Loading) {
        item {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
          ) {
            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = GrisesGreenPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Asisten GRISES sedang berpikir...", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }
      } else if (chatUiState is AiUiState.Streaming) {
        val streamText = (chatUiState as AiUiState.Streaming).text
        item {
          ChatBubble(message = ChatMessage(isUser = false, text = streamText))
        }
      } else if (chatUiState is AiUiState.Error) {
        val errMsg = (chatUiState as AiUiState.Error).message
        item {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFFFEBEE),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(10.dp)
            ) {
              Icon(imageVector = Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFC62828))
              Spacer(modifier = Modifier.width(8.dp))
              Text(text = errMsg, fontSize = 11.sp, color = Color(0xFFC62828))
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Input bar
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      OutlinedTextField(
        value = inputMessage,
        onValueChange = { inputMessage = it },
        placeholder = { Text("Tanya seputar tata tertib / kegiatan GRISES...", fontSize = 12.sp) },
        modifier = Modifier
          .weight(1f)
          .testTag("input_chat_ai"),
        shape = RoundedCornerShape(24.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = GrisesGreenPrimary,
          unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        ),
        maxLines = 3
      )

      IconButton(
        onClick = {
          if (inputMessage.isNotBlank()) {
            aiViewModel.sendChatMessage(inputMessage.trim())
            inputMessage = ""
          }
        },
        enabled = inputMessage.isNotBlank() && chatUiState !is AiUiState.Loading,
        modifier = Modifier
          .size(44.dp)
          .clip(CircleShape)
          .background(if (inputMessage.isNotBlank()) GrisesGreenPrimary else MaterialTheme.colorScheme.surfaceVariant)
          .testTag("btn_send_chat_ai")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.Send,
          contentDescription = "Kirim",
          tint = if (inputMessage.isNotBlank()) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
  val isUser = message.isUser
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
  ) {
    if (!isUser) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(28.dp)
          .clip(CircleShape)
          .background(GrisesGreenPrimary)
      ) {
        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
      }
      Spacer(modifier = Modifier.width(6.dp))
    }

    Surface(
      shape = RoundedCornerShape(
        topStart = 14.dp,
        topEnd = 14.dp,
        bottomStart = if (isUser) 14.dp else 2.dp,
        bottomEnd = if (isUser) 2.dp else 14.dp
      ),
      color = if (isUser) GrisesGreenPrimary else MaterialTheme.colorScheme.surface,
      tonalElevation = if (isUser) 0.dp else 1.dp,
      shadowElevation = if (isUser) 1.dp else 0.dp,
      modifier = Modifier.widthIn(max = 290.dp)
    ) {
      Text(
        text = message.text,
        fontSize = 12.sp,
        color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
      )
    }
  }
}

@Composable
private fun AiResultView(
  uiState: AiUiState,
  title: String,
  onClear: () -> Unit,
  onShareWa: (String) -> Unit
) {
  val context = LocalContext.current

  when (uiState) {
    is AiUiState.Idle -> {
      // Nothing to display
    }
    is AiUiState.Loading -> {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 6.dp)
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          CircularProgressIndicator(modifier = Modifier.size(20.dp), color = GrisesGreenPrimary, strokeWidth = 2.dp)
          Spacer(modifier = Modifier.width(12.dp))
          Text(text = "Sedang diproses oleh Gemini 3.8 Flash...", fontSize = 12.sp)
        }
      }
    }
    is AiUiState.Streaming, is AiUiState.Success -> {
      val resultText = if (uiState is AiUiState.Streaming) uiState.text else (uiState as AiUiState.Success).text
      val isFinished = uiState is AiUiState.Success

      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("ai_result_card")
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = GrisesGreenPrimary, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            if (isFinished) {
              Surface(shape = RoundedCornerShape(6.dp), color = GrisesGreenContainer) {
                Text(text = "Selesai", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GrisesOnGreenContainer, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
              }
            } else {
              Surface(shape = RoundedCornerShape(6.dp), color = GrisesGoldContainer) {
                Text(text = "Menulis...", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GrisesOnGoldContainer, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = resultText,
              fontSize = 12.sp,
              lineHeight = 18.sp,
              color = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.padding(12.dp)
            )
          }

          if (isFinished) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              OutlinedButton(
                onClick = {
                  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                  val clip = ClipData.newPlainText("GRISES AI Result", resultText)
                  clipboard.setPrimaryClip(clip)
                  Toast.makeText(context, "Teks berhasil disalin ke clipboard!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                  .weight(1f)
                  .testTag("btn_copy_ai_result"),
                shape = RoundedCornerShape(8.dp)
              ) {
                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Salin", fontSize = 11.sp)
              }

              Button(
                onClick = { onShareWa(resultText) },
                colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                modifier = Modifier
                  .weight(1.3f)
                  .testTag("btn_share_wa_ai_result"),
                shape = RoundedCornerShape(8.dp)
              ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Kirim ke WA", fontSize = 11.sp)
              }
            }
          }
        }
      }
    }
    is AiUiState.Error -> {
      val errorMsg = uiState.message
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("ai_error_card")
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFC62828))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Gagal Memproses Permintaan", fontWeight = FontWeight.Bold, color = Color(0xFFC62828), fontSize = 13.sp)
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(text = errorMsg, fontSize = 11.sp, color = Color(0xFF424242))
          Spacer(modifier = Modifier.height(10.dp))
          Button(
            onClick = onClear,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Tutup", fontSize = 11.sp)
          }
        }
      }
    }
  }
}
