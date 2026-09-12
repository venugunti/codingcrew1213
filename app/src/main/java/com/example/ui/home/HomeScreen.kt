package com.example.ui.home

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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BloodRepository
import com.example.data.local.LocalBloodRepository
import com.example.model.UrgentEmergency
import com.example.ui.eligibility.EligibilityDialog
import com.example.ui.sos.EmergencySosDialog
import com.example.ui.theme.AvailableGreen
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.DarkCrimson
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.OffWhiteBg
import com.example.ui.theme.SteelBlue
import com.example.util.MapsHelper

@Composable
fun HomeScreen(
  modifier: Modifier = Modifier,
  onNavigateToMap: (targetCenterId: String?) -> Unit = {},
  onNotificationClick: () -> Unit = {}
) {
  val context = LocalContext.current
  val localRepo = remember { LocalBloodRepository(context) }
  val urgentEmergencies by localRepo.emergenciesFlow.collectAsState(initial = BloodRepository.urgentEmergencies)

  var showEligibilityDialog by remember { mutableStateOf(false) }
  var showSosDialog by remember { mutableStateOf(false) }
  var showQuickPledgeNotice by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(OffWhiteBg)
      .padding(horizontal = 20.dp)
      .padding(top = 16.dp, bottom = 86.dp),
    verticalArrangement = Arrangement.spacedBy(18.dp)
  ) {
    // Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = CircleShape,
            color = Color(0xFFFFECEE),
            modifier = Modifier.size(44.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text("🩸", fontSize = 22.sp)
            }
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "RaktSetu",
              fontSize = 22.sp,
              fontWeight = FontWeight.ExtraBold,
              color = DeepNavy
            )
            Text(
              text = "Google Maps Blood Network",
              fontSize = 12.sp,
              color = Color.Gray
            )
          }
        }

        IconButton(
          onClick = onNotificationClick,
          modifier = Modifier
            .background(Color.White, CircleShape)
            .size(40.dp)
            .testTag("notification_bell_btn")
        ) {
          Icon(Icons.Default.Notifications, contentDescription = "Alerts", tint = DeepNavy)
        }
      }
    }

    // Hero: Urgent Call Section (Matching HTML mock + Google Maps action)
    item {
      Card(
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("hero_urgent_card")
      ) {
        Box(
          modifier = Modifier
            .background(
              Brush.linearGradient(
                colors = listOf(CrimsonRed, DarkCrimson)
              )
            )
            .padding(22.dp)
        ) {
          Column {
            Surface(
              shape = RoundedCornerShape(20.dp),
              color = Color.White.copy(alpha = 0.22f)
            ) {
              Text(
                text = "URGENT CALL",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "O- Negative Needed",
              fontSize = 23.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )

            Text(
              text = "City General Hospital • 1.2 km away",
              fontSize = 13.sp,
              color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              Button(
                onClick = {
                  // Direct navigation to Google Maps for City Hospital
                  val hospital = BloodRepository.bloodCenters.first()
                  MapsHelper.openNavigationInGoogleMaps(
                    context,
                    hospital.latitude,
                    hospital.longitude,
                    hospital.name
                  )
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = Color.White,
                  contentColor = CrimsonRed
                ),
                modifier = Modifier
                  .weight(1f)
                  .height(44.dp)
                  .testTag("donate_now_btn")
              ) {
                Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Donate Now", fontWeight = FontWeight.Bold, fontSize = 13.sp)
              }

              OutlinedButton(
                onClick = {
                  // Switch to Map tab focused on City General Hospital
                  onNavigateToMap("c1")
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                  contentColor = Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
                modifier = Modifier
                  .weight(1f)
                  .height(44.dp)
                  .testTag("view_on_map_btn")
              ) {
                Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("View on Map", fontSize = 13.sp)
              }
            }
          }
        }
      }
    }

    // Google Maps Integration Feature Banner
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onNavigateToMap(null) }
          .testTag("google_maps_banner_card")
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFFE8F5E9),
            modifier = Modifier.size(52.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = AvailableGreen,
                modifier = Modifier.size(28.dp)
              )
            }
          }

          Spacer(modifier = Modifier.width(14.dp))

          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Google Maps Connected",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = DeepNavy
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = AvailableGreen
              ) {
                Text(
                  "LIVE",
                  color = Color.White,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
              }
            }
            Text(
              text = "5 blood banks & 2 mobile drives in your area",
              fontSize = 12.sp,
              color = Color.Gray
            )
          }

          Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Explore",
            tint = CrimsonRed,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }

    // Stats Grid (Matching HTML Mock: 1,240 Active Donors, 85 Requests Met)
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        StatCard(
          number = "1,240",
          label = "Active Donors",
          emoji = "👥",
          modifier = Modifier.weight(1f)
        )
        StatCard(
          number = "85",
          label = "Requests Met",
          emoji = "❤️",
          modifier = Modifier.weight(1f)
        )
      }
    }

    // Quick Actions Menu (Matching HTML Mock)
    item {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
          text = "Quick Actions",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = DeepNavy
        )

        ActionMenuItem(
          iconEmoji = "📅",
          title = "Schedule Donation",
          subtitle = "Find nearby blood banks & drives on Map",
          onClick = { onNavigateToMap(null) },
          testTag = "action_schedule_donation"
        )

        ActionMenuItem(
          iconEmoji = "📝",
          title = "Eligibility Check",
          subtitle = "Verify if you can donate today",
          onClick = { showEligibilityDialog = true },
          testTag = "action_eligibility_check"
        )

        ActionMenuItem(
          iconEmoji = "🚨",
          title = "Request Blood",
          subtitle = "Create an emergency SOS broadcast",
          onClick = { showSosDialog = true },
          testTag = "action_request_blood"
        )
      }
    }

    // Active Emergency Requests Section with direct Google Maps link
    item {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Active Hospital Requests",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = DeepNavy
          )
          Text(
            text = "View All",
            fontSize = 12.sp,
            color = CrimsonRed,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onNavigateToMap(null) }
          )
        }

        urgentEmergencies.forEach { emergency ->
          HospitalRequestRow(
            emergency = emergency,
            onDirectionsClick = {
              MapsHelper.openNavigationInGoogleMaps(
                context,
                emergency.latitude,
                emergency.longitude,
                emergency.hospitalName
              )
            }
          )
        }
      }
    }
  }

  // Dialogs
  if (showEligibilityDialog) {
    EligibilityDialog(
      onDismiss = { showEligibilityDialog = false },
      onNavigateToMap = { onNavigateToMap(null) }
    )
  }

  if (showSosDialog) {
    EmergencySosDialog(
      onDismiss = { showSosDialog = false },
      onBroadcastSuccess = { _, _ -> },
      onOpenMap = { onNavigateToMap(null) }
    )
  }
}

@Composable
fun StatCard(
  number: String,
  label: String,
  emoji: String,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(2.dp),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(emoji, fontSize = 20.sp)
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = number,
        fontSize = 20.sp,
        fontWeight = FontWeight.ExtraBold,
        color = DeepNavy
      )
      Text(
        text = label,
        fontSize = 12.sp,
        color = Color.Gray,
        fontWeight = FontWeight.Medium
      )
    }
  }
}

@Composable
fun ActionMenuItem(
  iconEmoji: String,
  title: String,
  subtitle: String,
  onClick: () -> Unit,
  testTag: String
) {
  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .testTag(testTag)
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFFFECEE),
        modifier = Modifier.size(46.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Text(iconEmoji, fontSize = 22.sp)
        }
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          color = DeepNavy
        )
        Text(
          text = subtitle,
          fontSize = 12.sp,
          color = Color.Gray
        )
      }

      Text(
        text = "→",
        fontSize = 18.sp,
        color = Color.Gray,
        fontWeight = FontWeight.Bold
      )
    }
  }
}

@Composable
fun HospitalRequestRow(
  emergency: UrgentEmergency,
  onDirectionsClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(2.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = CrimsonRed,
        modifier = Modifier.size(42.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Text(
            text = emergency.bloodGroup.take(2),
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.sp
          )
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = "${emergency.bloodGroup} • ${emergency.unitsNeeded} units",
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = DeepNavy
        )
        Text(
          text = "${emergency.hospitalName} • ${emergency.distanceKm} km",
          fontSize = 12.sp,
          color = Color.Gray
        )
      }

      Button(
        onClick = onDirectionsClick,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFECEE)),
        modifier = Modifier.height(36.dp)
      ) {
        Icon(Icons.Default.Directions, contentDescription = null, tint = CrimsonRed, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Navigate", color = CrimsonRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}
