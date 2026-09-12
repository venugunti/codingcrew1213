package com.example.ui.profile

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.data.local.LocalBloodRepository
import com.example.model.DonorProfile
import com.example.ui.database.DatabaseInspectorDialog
import com.example.ui.theme.AvailableGreen
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.DarkCrimson
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.OffWhiteBg

@Composable
fun ProfileScreen(
  modifier: Modifier = Modifier,
  onNavigateToMap: () -> Unit = {}
) {
  val context = LocalContext.current
  val localRepo = remember { LocalBloodRepository(context) }
  val profile by localRepo.profileFlow.collectAsState(initial = DonorProfile())
  val donations by localRepo.donationsFlow.collectAsState(initial = emptyList())
  val centersCount by localRepo.centersCount.collectAsState(initial = 5)
  val emergenciesCount by localRepo.emergenciesCount.collectAsState(initial = 3)
  val donationsCount by localRepo.donationsCount.collectAsState(initial = 2)

  var autoLocationAlerts by remember { mutableStateOf(true) }
  var googleMapsDirectNav by remember { mutableStateOf(true) }
  var showDbInspector by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(OffWhiteBg)
      .padding(horizontal = 20.dp)
      .padding(top = 16.dp, bottom = 86.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Title
    item {
      Text(
        text = "Donor Profile & Card",
        fontSize = 22.sp,
        fontWeight = FontWeight.ExtraBold,
        color = DeepNavy
      )
    }

    // Digital Donor ID Card
    item {
      Card(
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("donor_card")
      ) {
        Box(
          modifier = Modifier
            .background(
              Brush.linearGradient(
                colors = listOf(DeepNavy, Color(0xFF14213D))
              )
            )
            .padding(20.dp)
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🩸", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  "RAKTSETU DONOR PASS",
                  color = Color.White.copy(alpha = 0.8f),
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp
                )
              }

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = CrimsonRed
              ) {
                Text(
                  text = profile.bloodType,
                  color = Color.White,
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 14.sp,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
              text = profile.name,
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )

            Text(
              text = "ID: ${profile.donorId} • Verified Blood Hero",
              fontSize = 12.sp,
              color = Color.White.copy(alpha = 0.75f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

            Spacer(modifier = Modifier.height(14.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text("Total Donations", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                Text("${profile.totalDonations} Sessions", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
              }
              Column {
                Text("Lives Impacted", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                Text("${profile.livesSaved} Lives", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA8DADC))
              }
              Column {
                Text("Status", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                Text("Eligible", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AvailableGreen)
              }
            }
          }
        }
      }
    }

    // Google Maps Navigation Settings Card
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text("Google Maps & Alert Settings", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DeepNavy)
          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("Direct Google Maps Navigation", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = DeepNavy)
              Text("Launch turn-by-turn driving route when tapping centers", fontSize = 11.sp, color = Color.Gray)
            }
            Switch(
              checked = googleMapsDirectNav,
              onCheckedChange = { googleMapsDirectNav = it },
              colors = SwitchDefaults.colors(checkedThumbColor = CrimsonRed, checkedTrackColor = Color(0xFFFFECEE))
            )
          }

          Spacer(modifier = Modifier.height(12.dp))
          HorizontalDivider(color = Color(0xFFF1F5F9))
          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("Nearby SOS Proximity Alerts", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = DeepNavy)
              Text("Receive notifications for critical calls within 10 km", fontSize = 11.sp, color = Color.Gray)
            }
            Switch(
              checked = autoLocationAlerts,
              onCheckedChange = { autoLocationAlerts = it },
              colors = SwitchDefaults.colors(checkedThumbColor = CrimsonRed, checkedTrackColor = Color(0xFFFFECEE))
            )
          }
        }
      }
    }

    // Local Database (Room / SQLite) Card
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth().testTag("local_database_card")
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFEFF6FF),
                modifier = Modifier.size(38.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(Icons.Default.Storage, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(20.dp))
                }
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text("Room Database (SQLite)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DeepNavy)
                Text("raktsetu_database.db • Local Persistent", fontSize = 11.sp, color = AvailableGreen, fontWeight = FontWeight.Medium)
              }
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = Color(0xFFE8F5E9)
            ) {
              Text(
                text = "ONLINE",
                color = AvailableGreen,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            DbStatBadge(label = "Centers", count = "$centersCount", modifier = Modifier.weight(1f))
            DbStatBadge(label = "Emergencies", count = "$emergenciesCount", modifier = Modifier.weight(1f))
            DbStatBadge(label = "Donations", count = "$donationsCount", modifier = Modifier.weight(1f))
          }

          Spacer(modifier = Modifier.height(14.dp))

          Button(
            onClick = { showDbInspector = true },
            colors = ButtonDefaults.buttonColors(containerColor = DeepNavy),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(44.dp)
              .testTag("inspect_database_btn")
          ) {
            Icon(Icons.Default.Storage, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Inspect Database Schema & Tables", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
        }
      }
    }

    // Donation History item (Dynamic from Room Database)
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Recent Donation History", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DeepNavy)
            Text("${donations.size} Records", fontSize = 12.sp, color = Color.Gray)
          }
          Spacer(modifier = Modifier.height(12.dp))

          if (donations.isEmpty()) {
            Text("No donation records found.", fontSize = 12.sp, color = Color.Gray)
          } else {
            donations.forEach { record ->
              HistoryItem(
                date = record.donationDate,
                center = record.centerName,
                type = "${record.donationType} (${record.bloodType})",
                status = record.status
              )
              Spacer(modifier = Modifier.height(10.dp))
            }
          }
        }
      }
    }
  }

  if (showDbInspector) {
    DatabaseInspectorDialog(
      centersCount = centersCount,
      emergenciesCount = emergenciesCount,
      donationsCount = donationsCount,
      onDismiss = { showDbInspector = false }
    )
  }
}

@Composable
fun DbStatBadge(label: String, count: String, modifier: Modifier = Modifier) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = Color(0xFFF8F9FA),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(count, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = DeepNavy)
      Text(label, fontSize = 10.sp, color = Color.Gray)
    }
  }
}

@Composable
fun HistoryItem(date: String, center: String, type: String, status: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(Color(0xFFF8F9FA), RoundedCornerShape(12.dp))
      .padding(12.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Surface(
      shape = CircleShape,
      color = Color(0xFFE8F5E9),
      modifier = Modifier.size(36.dp)
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AvailableGreen, modifier = Modifier.size(20.dp))
      }
    }
    Spacer(modifier = Modifier.width(12.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(center, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = DeepNavy)
      Text("$type • $date", fontSize = 11.sp, color = Color.Gray)
      Text(status, fontSize = 10.sp, color = AvailableGreen, fontWeight = FontWeight.Bold)
    }
  }
}
