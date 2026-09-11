package com.example.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.home.HomeScreen
import com.example.ui.map.GoogleMapScreen
import com.example.ui.profile.ProfileScreen
import com.example.ui.sos.EmergencySosDialog
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.DeepNavy
import kotlinx.coroutines.launch

enum class AppTab(val title: String) {
  HOME("Home"),
  MAP("Map"),
  SOS("SOS"),
  PROFILE("Profile")
}

@Composable
fun RaktSetuApp() {
  var currentTab by remember { mutableStateOf(AppTab.HOME) }
  var targetCenterForMap by remember { mutableStateOf<String?>(null) }
  var showSosDialog by remember { mutableStateOf(false) }

  val snackbarHostState = remember { SnackbarHostState() }
  val coroutineScope = rememberCoroutineScope()

  Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    bottomBar = {
      NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier
          .height(72.dp)
          .testTag("bottom_nav_bar")
      ) {
        // Home Tab
        NavigationBarItem(
          selected = currentTab == AppTab.HOME,
          onClick = { currentTab = AppTab.HOME },
          icon = {
            Icon(
              imageVector = if (currentTab == AppTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
              contentDescription = "Home"
            )
          },
          label = { Text("Home", fontSize = 11.sp) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = CrimsonRed,
            selectedTextColor = CrimsonRed,
            indicatorColor = Color(0xFFFFECEE)
          ),
          modifier = Modifier.testTag("nav_home_tab")
        )

        // Search / Google Map Tab
        NavigationBarItem(
          selected = currentTab == AppTab.MAP,
          onClick = {
            targetCenterForMap = null
            currentTab = AppTab.MAP
          },
          icon = {
            Icon(
              imageVector = if (currentTab == AppTab.MAP) Icons.Filled.Map else Icons.Outlined.Map,
              contentDescription = "Map"
            )
          },
          label = { Text("Map", fontSize = 11.sp) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = CrimsonRed,
            selectedTextColor = CrimsonRed,
            indicatorColor = Color(0xFFFFECEE)
          ),
          modifier = Modifier.testTag("nav_map_tab")
        )

        // Emergency SOS Tab (Highlight)
        NavigationBarItem(
          selected = false,
          onClick = { showSosDialog = true },
          icon = {
            Icon(
              imageVector = Icons.Filled.Warning,
              contentDescription = "SOS",
              tint = CrimsonRed,
              modifier = Modifier.size(24.dp)
            )
          },
          label = {
            Text(
              "SOS",
              fontSize = 11.sp,
              color = CrimsonRed
            )
          },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = CrimsonRed,
            selectedTextColor = CrimsonRed,
            indicatorColor = Color(0xFFFFECEE)
          ),
          modifier = Modifier.testTag("nav_sos_tab")
        )

        // Profile Tab
        NavigationBarItem(
          selected = currentTab == AppTab.PROFILE,
          onClick = { currentTab = AppTab.PROFILE },
          icon = {
            Icon(
              imageVector = if (currentTab == AppTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
              contentDescription = "Profile"
            )
          },
          label = { Text("Profile", fontSize = 11.sp) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = CrimsonRed,
            selectedTextColor = CrimsonRed,
            indicatorColor = Color(0xFFFFECEE)
          ),
          modifier = Modifier.testTag("nav_profile_tab")
        )
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (currentTab) {
        AppTab.HOME -> {
          HomeScreen(
            onNavigateToMap = { centerId ->
              targetCenterForMap = centerId
              currentTab = AppTab.MAP
            },
            onNotificationClick = {
              coroutineScope.launch {
                snackbarHostState.showSnackbar("🚨 Emergency Broadcast: O- needed at City Hospital (1.2 km)")
              }
            }
          )
        }

        AppTab.MAP -> {
          GoogleMapScreen(
            initialTargetCenterId = targetCenterForMap,
            onNavigateToHome = { currentTab = AppTab.HOME }
          )
        }

        AppTab.SOS -> {
          // Handled via dialog
        }

        AppTab.PROFILE -> {
          ProfileScreen(
            onNavigateToMap = {
              targetCenterForMap = null
              currentTab = AppTab.MAP
            }
          )
        }
      }

      if (showSosDialog) {
        EmergencySosDialog(
          onDismiss = { showSosDialog = false },
          onBroadcastSuccess = { bloodGroup, hospital ->
            coroutineScope.launch {
              snackbarHostState.showSnackbar("Broadcasting SOS for $bloodGroup to donors near $hospital")
            }
          },
          onOpenMap = {
            targetCenterForMap = null
            currentTab = AppTab.MAP
          }
        )
      }
    }
  }
}
