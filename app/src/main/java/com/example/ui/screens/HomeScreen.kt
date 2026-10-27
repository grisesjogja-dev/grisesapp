package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.AgendaKegiatan
import com.example.ui.components.AdminModeSwitchCard
import com.example.ui.components.formatRupiah
import com.example.ui.theme.GrisesGoldContainer
import com.example.ui.theme.GrisesGoldPrimary
import com.example.ui.theme.GrisesGreenContainer
import com.example.ui.theme.GrisesGreenPrimary
import com.example.ui.theme.GrisesOnGoldContainer
import com.example.ui.theme.GrisesOnGreenContainer
import com.example.ui.theme.GrisesTeal
import com.example.ui.theme.GrisesTealContainer
import com.example.ui.theme.StatusBelumLunasRed
import com.example.ui.theme.StatusLunasGreen
import com.example.ui.viewmodel.GrisesViewModel

@Composable
fun HomeScreen(
  viewModel: GrisesViewModel,
  onNavigateToTab: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val isAdminMode by viewModel.isAdminMode.collectAsStateWithLifecycle()
  val kasSummary by viewModel.kasSummary.collectAsStateWithLifecycle()
  val jimpitanSummary by viewModel.jimpitanSummary.collectAsStateWithLifecycle()
  val iuranList by viewModel.currentPeriodIuran.collectAsStateWithLifecycle()
  val agendaList by viewModel.allAgenda.collectAsStateWithLifecycle()
  val wargaStats by viewModel.wargaStats.collectAsStateWithLifecycle()

  val totalIuran = iuranList.size
  val paidIuran = iuranList.count { it.isLunas }
  val iuranProgress = if (totalIuran > 0) paidIuran.toFloat() / totalIuran else 0f

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(4.dp))
      // Admin Mode Switch
      AdminModeSwitchCard(
        isAdminMode = isAdminMode,
        onToggleAdminMode = { viewModel.toggleAdminMode(it) }
      )
    }

    // Hero Banner Card
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("hero_banner_card")
      ) {
        Column {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(140.dp)
          ) {
            Image(
              painter = painterResource(id = R.drawable.grises_hero_banner_1792989681550),
              contentDescription = "Perum Griya Sedayu Selaras",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(
                  Brush.verticalGradient(
                    colors = listOf(
                      Color.Transparent,
                      Color.Black.copy(alpha = 0.7f)
                    )
                  )
                )
            )
            Column(
              modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(14.dp)
            ) {
              Text(
                text = "GRISES",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
              )
              Text(
                text = "Perum Griya Sedayu Selaras • Guyub & Transparan",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.9f)
              )
            }
          }
        }
      }
    }

    // Asisten AI Feature Card (Gemini 3.8 Flash)
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onNavigateToTab(1) } // Asisten AI Tab
          .testTag("home_ai_assistant_card")
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(46.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(
                Brush.linearGradient(
                  colors = listOf(GrisesGreenPrimary, GrisesTeal)
                )
              )
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = Color(0xFFFFD54F),
              modifier = Modifier.size(26.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Asisten AI Paguyuban",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = GrisesGoldContainer
              ) {
                Text(
                  text = "Gemini AI",
                  fontSize = 9.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = GrisesOnGoldContainer,
                  modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                )
              }
            }
            Text(
              text = "Draf surat edaran, WA tagihan santun, & analisis kas",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = GrisesGreenPrimary,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }

    // Saldo Kas Real-Time Card
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(18.dp))
          .background(
            Brush.horizontalGradient(
              colors = listOf(GrisesGreenPrimary, Color(0xFF059669), Color(0xFF0D9488))
            )
          )
          .clickable { onNavigateToTab(4) } // Tab Kas
          .testTag("kas_saldo_card")
      ) {
        Column(
          modifier = Modifier.padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.AccountBalanceWallet,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "SALDO KAS PAGUYUBAN",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.85f),
                letterSpacing = 0.5.sp
              )
            }
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = Color.White.copy(alpha = 0.2f)
            ) {
              Text(
                text = "Transparan",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = formatRupiah(kasSummary.saldoSaatIni),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )

          Spacer(modifier = Modifier.height(14.dp))
          HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text(
                text = "Total Pemasukan",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.75f)
              )
              Text(
                text = formatRupiah(kasSummary.totalMasuk),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB9F6CA)
              )
            }
            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "Total Pengeluaran",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.75f)
              )
              Text(
                text = formatRupiah(kasSummary.totalKeluar),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFCDD2)
              )
            }
          }
        }
      }
    }

    // Quick Stats Grid: Jimpitan & Iuran Warga
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Jimpitan Card
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier
            .weight(1f)
            .clickable { onNavigateToTab(3) } // Jimpitan Tab
            .testTag("home_jimpitan_stat_card")
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                  .size(32.dp)
                  .clip(CircleShape)
                  .background(GrisesGoldContainer)
              ) {
                Icon(
                  imageVector = Icons.Default.Savings,
                  contentDescription = null,
                  tint = GrisesGoldPrimary,
                  modifier = Modifier.size(18.dp)
                )
              }
              Text(
                text = "500/rumah",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = GrisesGoldPrimary
              )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "Jimpitan Hari Ini",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = formatRupiah(jimpitanSummary.totalHariIni),
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "${jimpitanSummary.jumlahRumahHariIni} rumah terkumpul",
              fontSize = 10.sp,
              color = StatusLunasGreen
            )
          }
        }

        // Iuran Progress Card
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier
            .weight(1f)
            .clickable { onNavigateToTab(2) } // Iuran Tab
            .testTag("home_iuran_stat_card")
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                  .size(32.dp)
                  .clip(CircleShape)
                  .background(GrisesTealContainer)
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                  contentDescription = null,
                  tint = GrisesTeal,
                  modifier = Modifier.size(18.dp)
                )
              }
              Text(
                text = "${(iuranProgress * 100).toInt()}%",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = GrisesTeal
              )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "Iuran Bulan Ini",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = "$paidIuran / $totalIuran Lunas",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
              progress = { iuranProgress },
              modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
              color = GrisesGreenPrimary,
              trackColor = GrisesGreenContainer
            )
          }
        }
      }
    }

    // Laporan Ringkasan Bulanan (PDF / Cetak) Card
    item {
      MonthlyReportCard(
        kasSummary = kasSummary,
        jimpitanSummary = jimpitanSummary,
        iuranList = iuranList,
        onPrintPdf = { onNavigateToTab(8) },
        onShareWa = {
          val paidCount = iuranList.count { it.isLunas }
          val totalWajib = iuranList.size
          val totalTerkumpul = iuranList.filter { it.isLunas }.sumOf { it.totalTagihan }
          val totalTunggakan = iuranList.filter { !it.isLunas }.sumOf { it.totalTagihan }
          val text = """
            📋 *LAPORAN RINGKASAN KEUANGAN BULANAN*
            *PAGUYUBAN WARGA PERUM GRIYA SEDAYU SELARAS (GRISES)*
            RT 05 / RW 12, Kelurahan Argorejo, Sedayu, Bantul
            📅 *Periode: Oktober 2026*
            ───────────────────────
            💵 *MUTASI KAS PAGUYUBAN*
            • Total Pemasukan: ${formatRupiah(kasSummary.totalMasuk)}
            • Total Pengeluaran: ${formatRupiah(kasSummary.totalKeluar)}
            • Saldo Kas Saat Ini: ${formatRupiah(kasSummary.saldoSaatIni)}

            📬 *REALISASI IURAN WARGA*
            • Lunas: $paidCount / $totalWajib Rumah
            • Terkumpul: ${formatRupiah(totalTerkumpul)}
            • Sisa Tunggakan: ${formatRupiah(totalTunggakan)}

            🪙 *JIMPITAN RONDA (Rp 500/hari)*
            • Total Jimpitan Bulan Ini: ${formatRupiah(jimpitanSummary.totalBulanIni)}
            • Hari Ini: ${formatRupiah(jimpitanSummary.totalHariIni)} (${jimpitanSummary.jumlahRumahHariIni} rumah)

            Terima kasih atas kebersamaan dan transparansi seluruh warga GRISES.
          """.trimIndent()
          val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
          }
          val shareIntent = Intent.createChooser(sendIntent, "Bagikan Laporan Bulanan GRISES")
          context.startActivity(shareIntent)
        }
      )
    }

    // Quick Menu Shortcuts
    item {
      Text(
        text = "Menu Administrasi Paguyuban",
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        QuickActionButton(
          title = "Data Warga",
          subtitle = "${wargaStats.first} KK",
          icon = Icons.Default.Group,
          color = GrisesGreenPrimary,
          onClick = { onNavigateToTab(5) }
        )
        QuickActionButton(
          title = "Iuran & WA",
          subtitle = "Tagihan",
          icon = Icons.Default.NotificationsActive,
          color = Color(0xFF25D366),
          onClick = { onNavigateToTab(2) }
        )
        QuickActionButton(
          title = "Jimpitan",
          subtitle = "Rp 500/hari",
          icon = Icons.Default.NightlightRound,
          color = GrisesGoldPrimary,
          onClick = { onNavigateToTab(3) }
        )
        QuickActionButton(
          title = "Inventaris",
          subtitle = "Aset Perum",
          icon = Icons.Default.Inventory2,
          color = GrisesTeal,
          onClick = { onNavigateToTab(6) }
        )
      }
    }

    // Agenda Terdekat Section
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Agenda Kegiatan Terdekat",
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          color = MaterialTheme.colorScheme.onSurface
        )
        TextButton(onClick = { onNavigateToTab(7) }) {
          Text(text = "Lihat Semua", color = GrisesGreenPrimary, fontSize = 12.sp)
        }
      }
    }

    items(agendaList.take(2)) { agenda ->
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onNavigateToTab(7) }
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(44.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(GrisesGreenContainer)
          ) {
            Icon(
              imageVector = Icons.Default.CalendarMonth,
              contentDescription = null,
              tint = GrisesGreenPrimary,
              modifier = Modifier.size(24.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = agenda.judul,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "${agenda.tanggal} • ${agenda.jam}",
              fontSize = 11.sp,
              color = GrisesGreenPrimary,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = "📍 ${agenda.lokasi}",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
fun QuickActionButton(
  title: String,
  subtitle: String,
  icon: ImageVector,
  color: Color,
  onClick: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .clip(RoundedCornerShape(12.dp))
      .clickable { onClick() }
      .padding(6.dp)
  ) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .size(52.dp)
        .clip(CircleShape)
        .background(color.copy(alpha = 0.15f))
    ) {
      Icon(
        imageVector = icon,
        contentDescription = title,
        tint = color,
        modifier = Modifier.size(26.dp)
      )
    }
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = title,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onSurface
    )
    Text(
      text = subtitle,
      fontSize = 10.sp,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}

@Composable
fun MonthlyReportCard(
  kasSummary: com.example.data.model.KasSummary,
  jimpitanSummary: com.example.data.model.JimpitanSummary,
  iuranList: List<com.example.data.model.IuranRecord>,
  onPrintPdf: () -> Unit,
  onShareWa: () -> Unit,
  modifier: Modifier = Modifier
) {
  val paidIurans = iuranList.filter { it.isLunas }
  val totalIurans = iuranList.size
  val totalTerkumpul = paidIurans.sumOf { it.totalTagihan }
  val iuranPct = if (totalIurans > 0) (paidIurans.size * 100) / totalIurans else 0
  val netKas = kasSummary.totalMasuk - kasSummary.totalKeluar
  val isSurplus = netKas >= 0

  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier
      .fillMaxWidth()
      .testTag("monthly_report_card")
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(GrisesGreenContainer)
          ) {
            Icon(
              imageVector = Icons.Default.Description,
              contentDescription = null,
              tint = GrisesGreenPrimary,
              modifier = Modifier.size(20.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Laporan Ringkasan Bulanan",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Periode: Oktober 2026",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = GrisesGreenContainer
        ) {
          Text(
            text = "PDF / Cetak",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = GrisesGreenPrimary,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 3 Summary Columns
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Iuran
        Column(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(GrisesGreenContainer.copy(alpha = 0.5f))
            .padding(8.dp)
        ) {
          Text(
            text = "Iuran ($iuranPct%)",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = GrisesGreenPrimary
          )
          Spacer(modifier = Modifier.height(3.dp))
          Text(
            text = formatRupiah(totalTerkumpul),
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "${paidIurans.size}/$totalIurans lunas",
            fontSize = 9.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        // Jimpitan
        Column(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(GrisesGoldContainer.copy(alpha = 0.5f))
            .padding(8.dp)
        ) {
          Text(
            text = "Jimpitan",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = GrisesGoldPrimary
          )
          Spacer(modifier = Modifier.height(3.dp))
          Text(
            text = formatRupiah(jimpitanSummary.totalBulanIni),
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Rp 500/hari",
            fontSize = 9.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        // Mutasi Kas
        Column(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(GrisesTealContainer.copy(alpha = 0.5f))
            .padding(8.dp)
        ) {
          Text(
            text = if (isSurplus) "Kas (Surplus)" else "Kas (Defisit)",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = GrisesTeal
          )
          Spacer(modifier = Modifier.height(3.dp))
          Text(
            text = formatRupiah(kasSummary.saldoSaatIni),
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Saldo akhir",
            fontSize = 9.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))
      HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
      Spacer(modifier = Modifier.height(12.dp))

      // Action Buttons: Cetak PDF & Bagikan WA
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedButton(
          onClick = onShareWa,
          modifier = Modifier
            .weight(1f)
            .testTag("home_share_report_btn")
        ) {
          Icon(
            imageVector = Icons.Default.Share,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text("Bagikan WA", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        Button(
          onClick = onPrintPdf,
          colors = ButtonDefaults.buttonColors(containerColor = GrisesGreenPrimary),
          modifier = Modifier
            .weight(1f)
            .testTag("home_print_report_btn")
        ) {
          Icon(
            imageVector = Icons.Default.Print,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text("Cetak PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
