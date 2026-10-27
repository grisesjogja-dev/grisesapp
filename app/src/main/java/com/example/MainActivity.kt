package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.filled.AutoAwesome
import com.example.ui.screens.AiAssistantScreen
import com.example.util.configureAppCheck
import com.example.ui.screens.AgendaScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InventarisScreen
import com.example.ui.screens.IuranScreen
import com.example.ui.screens.JimpitanScreen
import com.example.ui.screens.KasScreen
import com.example.ui.screens.WargaScreen
import com.example.ui.screens.WebDashboardScreen
import com.example.ui.theme.GrisesGoldPrimary
import com.example.ui.theme.GrisesGreenContainer
import com.example.ui.theme.GrisesGreenPrimary
import com.example.ui.theme.GrisesOnGreenContainer
import com.example.ui.theme.GrisesTeal
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.GrisesViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Configure Firebase App Check with launch intent before any UI or Firebase operations
    configureAppCheck(this, intent)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        MainAppContent()
      }
    }
  }
}

data class NavModule(
  val title: String,
  val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(viewModel: GrisesViewModel = viewModel()) {
  var isWebModeActive by rememberSaveable { mutableStateOf(true) }
  var currentTab by rememberSaveable { mutableIntStateOf(0) }
  val isAdminMode by viewModel.isAdminMode.collectAsStateWithLifecycle()

  if (isWebModeActive) {
    WebDashboardScreen(
      onSwitchToNative = { isWebModeActive = false },
      onOpenTab = { tab ->
        currentTab = tab
        isWebModeActive = false
      }
    )
    return
  }

  // Handle Android system back button: return to Home (tab 0) if on secondary screens, or back to Web mode
  BackHandler {
    if (currentTab != 0) {
      currentTab = 0
    } else {
      isWebModeActive = true
    }
  }

  val navModules = listOf(
    NavModule("Beranda", Icons.Default.Home),
    NavModule("Asisten AI", Icons.Default.AutoAwesome),
    NavModule("Iuran & WA", Icons.AutoMirrored.Filled.ReceiptLong),
    NavModule("Jimpitan", Icons.Default.NightlightRound),
    NavModule("Buku Kas", Icons.Default.AccountBalanceWallet),
    NavModule("Data Warga", Icons.Default.Group),
    NavModule("Inventaris", Icons.Default.Inventory2),
    NavModule("Agenda", Icons.Default.Event),
    NavModule("Versi Web", Icons.Default.Public)
  )

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(GrisesGreenPrimary)
            ) {
              Icon(
                imageVector = Icons.Default.Home,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "GRISES",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Perum Griya Sedayu Selaras",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        },
        actions = {
          // Quick Switcher to Versi Web
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = GrisesGreenContainer,
            modifier = Modifier
              .clickable { isWebModeActive = true }
              .testTag("appbar_switch_to_web")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Public,
                contentDescription = null,
                tint = GrisesGreenPrimary,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Versi Web",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = GrisesOnGreenContainer
              )
            }
          }
          Spacer(modifier = Modifier.width(6.dp))
          // Interactive quick role pill in AppBar
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isAdminMode) GrisesGreenContainer else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
              .clickable { viewModel.toggleAdminMode(!isAdminMode) }
              .testTag("appbar_role_toggle")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Icon(
                imageVector = if (isAdminMode) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                contentDescription = null,
                tint = if (isAdminMode) GrisesGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (isAdminMode) "Admin" else "Warga",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isAdminMode) GrisesOnGreenContainer else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
          Spacer(modifier = Modifier.width(8.dp))
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.background
        )
      )
    },
    bottomBar = {
      NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("main_bottom_nav")
      ) {
        // Show 5 primary destinations in bottom bar
        listOf(0, 1, 2, 3, 4).forEach { index ->
          val module = navModules[index]
          val isSelected = currentTab == index
          NavigationBarItem(
            selected = isSelected,
            onClick = { currentTab = index },
            icon = {
              Icon(
                imageVector = module.icon,
                contentDescription = module.title,
                modifier = Modifier.size(22.dp)
              )
            },
            label = {
              Text(
                text = module.title,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = GrisesGreenPrimary,
              selectedTextColor = GrisesGreenPrimary,
              indicatorColor = GrisesGreenContainer
            ),
            modifier = Modifier.testTag("nav_item_${module.title.lowercase().replace(" ", "_")}")
          )
        }
      }
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // Horizontal Scrollable Module Chips for quick switching to all 7 modules (including Inventaris & Agenda)
      LazyRow(
        modifier = Modifier
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.background)
          .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        itemsIndexed(navModules) { index, module ->
          val isSelected = currentTab == index
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (isSelected) GrisesGreenPrimary else MaterialTheme.colorScheme.surface,
            tonalElevation = if (isSelected) 2.dp else 0.dp,
            shadowElevation = if (isSelected) 1.dp else 0.dp,
            modifier = Modifier
              .clickable {
                if (index == 8) {
                  isWebModeActive = true
                } else {
                  currentTab = index
                }
              }
              .testTag("module_chip_$index")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Icon(
                imageVector = module.icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(15.dp)
              )
              Spacer(modifier = Modifier.width(5.dp))
              Text(
                text = module.title,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      }

      // Main Content Screen Switching
      Box(modifier = Modifier.fillMaxSize()) {
        when (currentTab) {
          0 -> HomeScreen(
            viewModel = viewModel,
            onNavigateToTab = { currentTab = it }
          )
          1 -> AiAssistantScreen(grisesViewModel = viewModel)
          2 -> IuranScreen(viewModel = viewModel)
          3 -> JimpitanScreen(viewModel = viewModel)
          4 -> KasScreen(viewModel = viewModel)
          5 -> WargaScreen(viewModel = viewModel)
          6 -> InventarisScreen(viewModel = viewModel)
          7 -> AgendaScreen(viewModel = viewModel)
          8 -> WebDashboardScreen(onSwitchToNative = { isWebModeActive = false })
          else -> HomeScreen(
            viewModel = viewModel,
            onNavigateToTab = { currentTab = it }
          )
        }
      }
    }
  }
}
