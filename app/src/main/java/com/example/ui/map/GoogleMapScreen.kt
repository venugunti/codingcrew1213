package com.example.ui.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.BloodRepository
import com.example.model.BloodCenter
import com.example.model.UrgencyLevel
import com.example.model.UrgentEmergency
import com.example.ui.theme.AvailableGreen
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.DarkCrimson
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.OffWhiteBg
import com.example.ui.theme.WarningAmber
import com.example.util.MapsHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

enum class MapFilter {
  ALL,
  URGENT_ONLY,
  BLOOD_DRIVES,
  O_NEGATIVE
}

@Composable
fun GoogleMapScreen(
  modifier: Modifier = Modifier,
  initialTargetCenterId: String? = null,
  onNavigateToHome: () -> Unit = {}
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()

  // Camera state for Google Maps
  val defaultLocation = LatLng(BloodRepository.defaultCenterLat, BloodRepository.defaultCenterLng)
  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(defaultLocation, 13f)
  }

  // Location permission state
  var hasLocationPermission by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
      ) == PackageManager.PERMISSION_GRANTED
    )
  }

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    hasLocationPermission = isGranted
    if (isGranted) {
      coroutineScope.launch {
        cameraPositionState.animate(
          CameraUpdateFactory.newLatLngZoom(defaultLocation, 14f)
        )
      }
    }
  }

  // UI state
  var searchQuery by remember { mutableStateOf("") }
  var selectedFilter by remember { mutableStateOf(MapFilter.ALL) }
  var isSatelliteMode by remember { mutableStateOf(false) }
  var isListViewMode by remember { mutableStateOf(false) }
  var selectedCenter by remember { mutableStateOf<BloodCenter?>(null) }
  var selectedEmergency by remember { mutableStateOf<UrgentEmergency?>(null) }
  var showScheduleSuccessDialog by remember { mutableStateOf<String?>(null) }

  // Check if an initial center was targeted (e.g. from Home screen click)
  LaunchedEffect(initialTargetCenterId) {
    if (initialTargetCenterId != null) {
      val found = BloodRepository.bloodCenters.find { it.id == initialTargetCenterId }
      if (found != null) {
        selectedCenter = found
        selectedEmergency = null
        cameraPositionState.animate(
          CameraUpdateFactory.newLatLngZoom(LatLng(found.latitude, found.longitude), 15f)
        )
      }
    }
  }

  // Filter centers based on search & active filter
  val filteredCenters = remember(searchQuery, selectedFilter) {
    BloodRepository.bloodCenters.filter { center ->
      val matchesSearch = searchQuery.isBlank() ||
        center.name.contains(searchQuery, ignoreCase = true) ||
        center.address.contains(searchQuery, ignoreCase = true) ||
        center.category.contains(searchQuery, ignoreCase = true)

      val matchesFilter = when (selectedFilter) {
        MapFilter.ALL -> true
        MapFilter.URGENT_ONLY -> center.urgencyLevel == UrgencyLevel.CRITICAL
        MapFilter.BLOOD_DRIVES -> center.hasActiveDriveToday
        MapFilter.O_NEGATIVE -> center.inventory.oNegative <= 2
      }
      matchesSearch && matchesFilter
    }
  }

  val filteredEmergencies = remember(searchQuery, selectedFilter) {
    if (selectedFilter == MapFilter.BLOOD_DRIVES) {
      emptyList()
    } else {
      BloodRepository.urgentEmergencies.filter { emergency ->
        searchQuery.isBlank() ||
          emergency.hospitalName.contains(searchQuery, ignoreCase = true) ||
          emergency.bloodGroup.contains(searchQuery, ignoreCase = true)
      }
    }
  }

  Box(modifier = modifier.fillMaxSize()) {
    if (isListViewMode) {
      // List View for users wanting text list
      CentersListView(
        centers = filteredCenters,
        emergencies = filteredEmergencies,
        onCenterClick = { center ->
          selectedCenter = center
          selectedEmergency = null
          isListViewMode = false
          coroutineScope.launch {
            cameraPositionState.animate(
              CameraUpdateFactory.newLatLngZoom(LatLng(center.latitude, center.longitude), 15f)
            )
          }
        },
        onDirectionsClick = { center ->
          MapsHelper.openNavigationInGoogleMaps(context, center.latitude, center.longitude, center.name)
        }
      )
    } else {
      // Live Google Map
      GoogleMap(
        modifier = Modifier
          .fillMaxSize()
          .testTag("google_map_view"),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
          isMyLocationEnabled = hasLocationPermission,
          mapType = if (isSatelliteMode) MapType.HYBRID else MapType.NORMAL
        ),
        uiSettings = MapUiSettings(
          zoomControlsEnabled = false,
          compassEnabled = true,
          myLocationButtonEnabled = false,
          mapToolbarEnabled = true
        ),
        onMapClick = {
          selectedCenter = null
          selectedEmergency = null
        }
      ) {
        // Blood Donation Centers Markers
        filteredCenters.forEach { center ->
          val markerPosition = LatLng(center.latitude, center.longitude)
          val hue = when (center.urgencyLevel) {
            UrgencyLevel.CRITICAL -> BitmapDescriptorFactory.HUE_RED
            UrgencyLevel.HIGH -> BitmapDescriptorFactory.HUE_ORANGE
            UrgencyLevel.STABLE -> BitmapDescriptorFactory.HUE_AZURE
          }

          Marker(
            state = MarkerState(position = markerPosition),
            title = center.name,
            snippet = "O- stock: ${center.inventory.oNegative} • ${center.distanceKm} km away",
            icon = BitmapDescriptorFactory.defaultMarker(hue),
            onClick = {
              selectedCenter = center
              selectedEmergency = null
              coroutineScope.launch {
                cameraPositionState.animate(CameraUpdateFactory.newLatLng(markerPosition))
              }
              true
            }
          )
        }

        // Active Emergency SOS Markers
        filteredEmergencies.forEach { emergency ->
          val markerPosition = LatLng(emergency.latitude, emergency.longitude)
          Marker(
            state = MarkerState(position = markerPosition),
            title = "🚨 URGENT: ${emergency.bloodGroup}",
            snippet = "${emergency.hospitalName} • ${emergency.unitsNeeded} units needed",
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE),
            onClick = {
              selectedEmergency = emergency
              selectedCenter = null
              coroutineScope.launch {
                cameraPositionState.animate(CameraUpdateFactory.newLatLng(markerPosition))
              }
              true
            }
          )
        }
      }
    }

    // Top Search & Filter Bar Overlay
    Column(
      modifier = Modifier
        .align(Alignment.TopCenter)
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      // Search Bar
      Surface(
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 6.dp,
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = CrimsonRed,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search blood banks, hospitals...", fontSize = 14.sp) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = Color.Transparent,
              unfocusedBorderColor = Color.Transparent
            ),
            modifier = Modifier
              .weight(1f)
              .testTag("map_search_input")
          )
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { searchQuery = "" }) {
              Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Filter Chips Row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        FilterChip(
          selected = selectedFilter == MapFilter.ALL,
          onClick = { selectedFilter = MapFilter.ALL },
          label = { Text("All Centers (${filteredCenters.size})") },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = CrimsonRed,
            selectedLabelColor = Color.White
          ),
          modifier = Modifier.testTag("filter_all")
        )
        FilterChip(
          selected = selectedFilter == MapFilter.URGENT_ONLY,
          onClick = { selectedFilter = MapFilter.URGENT_ONLY },
          label = { Text("🚨 Critical Stock") },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = DarkCrimson,
            selectedLabelColor = Color.White
          ),
          modifier = Modifier.testTag("filter_urgent")
        )
        FilterChip(
          selected = selectedFilter == MapFilter.BLOOD_DRIVES,
          onClick = { selectedFilter = MapFilter.BLOOD_DRIVES },
          label = { Text("🚌 Blood Drives") },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = DeepNavy,
            selectedLabelColor = Color.White
          ),
          modifier = Modifier.testTag("filter_drives")
        )
        FilterChip(
          selected = selectedFilter == MapFilter.O_NEGATIVE,
          onClick = { selectedFilter = MapFilter.O_NEGATIVE },
          label = { Text("O- Needed") },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = WarningAmber,
            selectedLabelColor = Color.White
          ),
          modifier = Modifier.testTag("filter_o_neg")
        )
      }
    }

    // Floating Map Controls (Right Side)
    Column(
      modifier = Modifier
        .align(Alignment.CenterEnd)
        .padding(end = 16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Toggle Map / List view
      FloatingActionButton(
        onClick = { isListViewMode = !isListViewMode },
        containerColor = Color.White,
        contentColor = DeepNavy,
        elevation = FloatingActionButtonDefaults.elevation(4.dp),
        modifier = Modifier
          .size(44.dp)
          .testTag("toggle_list_map_btn")
      ) {
        Icon(
          imageVector = if (isListViewMode) Icons.Default.Map else Icons.Default.List,
          contentDescription = "Toggle View"
        )
      }

      // Toggle Satellite / Normal Map
      if (!isListViewMode) {
        FloatingActionButton(
          onClick = { isSatelliteMode = !isSatelliteMode },
          containerColor = if (isSatelliteMode) DeepNavy else Color.White,
          contentColor = if (isSatelliteMode) Color.White else DeepNavy,
          elevation = FloatingActionButtonDefaults.elevation(4.dp),
          modifier = Modifier
            .size(44.dp)
            .testTag("toggle_satellite_btn")
        ) {
          Icon(Icons.Default.Layers, contentDescription = "Toggle Layer")
        }

        // My Location Button
        FloatingActionButton(
          onClick = {
            if (hasLocationPermission) {
              coroutineScope.launch {
                cameraPositionState.animate(
                  CameraUpdateFactory.newLatLngZoom(defaultLocation, 14f)
                )
              }
            } else {
              permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
          },
          containerColor = Color.White,
          contentColor = CrimsonRed,
          elevation = FloatingActionButtonDefaults.elevation(4.dp),
          modifier = Modifier
            .size(44.dp)
            .testTag("my_location_btn")
        ) {
          Icon(Icons.Default.MyLocation, contentDescription = "My Location")
        }
      }
    }

    // Bottom Selected Detail Card (Center or Emergency)
    AnimatedVisibility(
      visible = selectedCenter != null || selectedEmergency != null,
      enter = slideInVertically(initialOffsetY = { it }),
      exit = slideOutVertically(targetOffsetY = { it }),
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(16.dp)
        .padding(bottom = 72.dp) // accommodate bottom nav
    ) {
      if (selectedCenter != null) {
        val center = selectedCenter!!
        BloodCenterCard(
          center = center,
          onClose = { selectedCenter = null },
          onDirections = {
            MapsHelper.openNavigationInGoogleMaps(context, center.latitude, center.longitude, center.name)
          },
          onCall = {
            MapsHelper.dialPhoneNumber(context, center.phone)
          },
          onSchedule = {
            showScheduleSuccessDialog = center.name
          }
        )
      } else if (selectedEmergency != null) {
        val emergency = selectedEmergency!!
        EmergencyCallCard(
          emergency = emergency,
          onClose = { selectedEmergency = null },
          onDirections = {
            MapsHelper.openNavigationInGoogleMaps(context, emergency.latitude, emergency.longitude, emergency.hospitalName)
          },
          onCall = {
            MapsHelper.dialPhoneNumber(context, emergency.phone)
          }
        )
      }
    }
  }

  // Appointment confirmation dialog
  if (showScheduleSuccessDialog != null) {
    AlertDialog(
      onDismissRequest = { showScheduleSuccessDialog = null },
      icon = {
        Icon(
          Icons.Default.CheckCircle,
          contentDescription = null,
          tint = AvailableGreen,
          modifier = Modifier.size(48.dp)
        )
      },
      title = { Text("Appointment Scheduled!", fontWeight = FontWeight.Bold) },
      text = {
        Column {
          Text("You have pledged a donation at:")
          Text(showScheduleSuccessDialog ?: "", fontWeight = FontWeight.SemiBold, color = CrimsonRed)
          Spacer(modifier = Modifier.height(8.dp))
          Text("Time: Today at 3:00 PM • Fast-track Donor Pass Generated.")
          Text("Directions and live navigation are ready in Google Maps.")
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val center = BloodRepository.bloodCenters.find { it.name == showScheduleSuccessDialog }
            showScheduleSuccessDialog = null
            if (center != null) {
              MapsHelper.openNavigationInGoogleMaps(context, center.latitude, center.longitude, center.name)
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
          modifier = Modifier.testTag("open_maps_from_dialog_btn")
        ) {
          Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Open Google Maps")
        }
      },
      dismissButton = {
        TextButton(onClick = { showScheduleSuccessDialog = null }) {
          Text("Done")
        }
      }
    )
  }
}

@Composable
fun BloodCenterCard(
  center: BloodCenter,
  onClose: () -> Unit,
  onDirections: () -> Unit,
  onCall: () -> Unit,
  onSchedule: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(8.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("center_detail_card")
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      // Header row
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = when (center.urgencyLevel) {
            UrgencyLevel.CRITICAL -> Color(0xFFFFEBEE)
            UrgencyLevel.HIGH -> Color(0xFFFFF3E0)
            UrgencyLevel.STABLE -> Color(0xFFE8F5E9)
          }
        ) {
          Text(
            text = when (center.urgencyLevel) {
              UrgencyLevel.CRITICAL -> "CRITICAL NEED"
              UrgencyLevel.HIGH -> "HIGH DEMAND"
              UrgencyLevel.STABLE -> "OPERATIONAL"
            },
            color = when (center.urgencyLevel) {
              UrgencyLevel.CRITICAL -> CrimsonRed
              UrgencyLevel.HIGH -> WarningAmber
              UrgencyLevel.STABLE -> AvailableGreen
            },
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "${center.distanceKm} km away",
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            color = DeepNavy
          )
          IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Center Name & Category
      Text(
        text = center.name,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        color = DeepNavy,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      Text(
        text = "${center.category} • ${center.address}",
        fontSize = 12.sp,
        color = Color.Gray,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      if (center.hasActiveDriveToday && center.driveDetails != null) {
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .background(Color(0xFFE3F2FD), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text("🚌 ", fontSize = 12.sp)
          Text(center.driveDetails, fontSize = 11.sp, color = DeepNavy, fontWeight = FontWeight.Medium)
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Blood Inventory Stock Pills
      Text(
        text = "Current Blood Stock (Units):",
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = DeepNavy
      )
      Spacer(modifier = Modifier.height(4.dp))
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        StockPill(type = "O-", units = center.inventory.oNegative, isCritical = center.inventory.oNegative <= 2)
        StockPill(type = "O+", units = center.inventory.oPositive)
        StockPill(type = "A+", units = center.inventory.aPositive)
        StockPill(type = "A-", units = center.inventory.aNegative, isCritical = center.inventory.aNegative <= 2)
        StockPill(type = "B+", units = center.inventory.bPositive)
        StockPill(type = "B-", units = center.inventory.bNegative)
        StockPill(type = "AB+", units = center.inventory.abPositive)
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Action Buttons
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Button(
          onClick = onDirections,
          colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .weight(1.3f)
            .height(44.dp)
            .testTag("get_directions_btn")
        ) {
          Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Directions", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
          onClick = onCall,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .weight(1f)
            .height(44.dp)
            .testTag("call_center_btn")
        ) {
          Icon(Icons.Default.Call, contentDescription = null, tint = DeepNavy, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Call", color = DeepNavy, fontSize = 13.sp)
        }

        Button(
          onClick = onSchedule,
          colors = ButtonDefaults.buttonColors(containerColor = DeepNavy),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .weight(1.2f)
            .height(44.dp)
            .testTag("schedule_visit_btn")
        ) {
          Text("Donate", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
fun EmergencyCallCard(
  emergency: UrgentEmergency,
  onClose: () -> Unit,
  onDirections: () -> Unit,
  onCall: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(8.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("emergency_detail_card")
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = CrimsonRed
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("URGENT SOS CALL", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }

        IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
        }
      }

      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Needed: ${emergency.bloodGroup} (${emergency.unitsNeeded} Units)",
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = CrimsonRed
      )
      Text(
        text = "${emergency.hospitalName} • ${emergency.distanceKm} km away",
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        color = DeepNavy
      )
      Text(
        text = emergency.patientCase,
        fontSize = 12.sp,
        color = Color.Gray
      )

      Spacer(modifier = Modifier.height(14.dp))

      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Button(
          onClick = onDirections,
          colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .weight(1.4f)
            .height(44.dp)
            .testTag("emergency_navigate_btn")
        ) {
          Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Navigate to Hospital", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
          onClick = onCall,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .weight(1f)
            .height(44.dp)
            .testTag("emergency_call_btn")
        ) {
          Icon(Icons.Default.Call, contentDescription = null, tint = DeepNavy, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Call ICU", color = DeepNavy, fontSize = 13.sp)
        }
      }
    }
  }
}

@Composable
fun StockPill(type: String, units: Int, isCritical: Boolean = false) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = if (isCritical) Color(0xFFFFEBEE) else Color(0xFFF1F5F9),
    border = if (isCritical) androidx.compose.foundation.BorderStroke(1.dp, CrimsonRed) else null
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
      Text(
        text = type,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = if (isCritical) CrimsonRed else DeepNavy
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = "$units",
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = if (isCritical) CrimsonRed else Color.Gray
      )
    }
  }
}

@Composable
fun CentersListView(
  centers: List<BloodCenter>,
  emergencies: List<UrgentEmergency>,
  onCenterClick: (BloodCenter) -> Unit,
  onDirectionsClick: (BloodCenter) -> Unit
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(OffWhiteBg)
      .padding(horizontal = 16.dp)
      .padding(top = 110.dp, bottom = 80.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    if (emergencies.isNotEmpty()) {
      item {
        Text("🚨 Active Emergency Broadcasts", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DeepNavy)
      }
      items(emergencies) { emergency ->
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "${emergency.bloodGroup} Needed (${emergency.unitsNeeded} units)",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = CrimsonRed
              )
              Text(
                text = "${emergency.hospitalName} • ${emergency.distanceKm} km away",
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = DeepNavy
              )
              Text(
                text = emergency.patientCase,
                fontSize = 11.sp,
                color = Color.Gray
              )
            }
          }
        }
      }
    }

    item {
      Text("Nearby Blood Banks & Donation Centers", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DeepNavy)
    }

    items(centers) { center ->
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onCenterClick(center) }
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = center.name,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = DeepNavy
              )
              Text(
                text = "${center.category} • ${center.distanceKm} km away",
                fontSize = 12.sp,
                color = Color.Gray
              )
            }
            IconButton(onClick = { onDirectionsClick(center) }) {
              Icon(Icons.Default.Directions, contentDescription = "Directions", tint = CrimsonRed)
            }
          }

          Spacer(modifier = Modifier.height(8.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            StockPill(type = "O-", units = center.inventory.oNegative, isCritical = center.inventory.oNegative <= 2)
            StockPill(type = "O+", units = center.inventory.oPositive)
            StockPill(type = "A+", units = center.inventory.aPositive)
            StockPill(type = "B+", units = center.inventory.bPositive)
          }
        }
      }
    }
  }
}
