package com.example.ui.database

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AvailableGreen
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.DeepNavy

@Composable
fun DatabaseInspectorDialog(
  centersCount: Int,
  emergenciesCount: Int,
  donationsCount: Int,
  onDismiss: () -> Unit
) {
  val schemaSql = """
    -- RaktSetu SQLite / Room Database Schema
    -- File: raktsetu_database.db
    
    CREATE TABLE blood_centers (
      id TEXT PRIMARY KEY NOT NULL,
      name TEXT NOT NULL,
      category TEXT NOT NULL,
      address TEXT NOT NULL,
      latitude REAL NOT NULL,
      longitude REAL NOT NULL,
      distanceKm REAL NOT NULL,
      phone TEXT NOT NULL,
      operatingHours TEXT NOT NULL,
      urgencyLevel TEXT NOT NULL,
      oNegative INTEGER NOT NULL,
      oPositive INTEGER NOT NULL,
      aPositive INTEGER NOT NULL,
      aNegative INTEGER NOT NULL,
      bPositive INTEGER NOT NULL,
      bNegative INTEGER NOT NULL,
      abPositive INTEGER NOT NULL,
      abNegative INTEGER NOT NULL,
      hasActiveDriveToday INTEGER NOT NULL,
      driveDetails TEXT,
      rating REAL NOT NULL
    );

    CREATE TABLE emergency_requests (
      id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
      hospitalName TEXT NOT NULL,
      patientCase TEXT NOT NULL,
      bloodGroup TEXT NOT NULL,
      unitsNeeded INTEGER NOT NULL,
      distanceKm REAL NOT NULL,
      address TEXT NOT NULL,
      latitude REAL NOT NULL,
      longitude REAL NOT NULL,
      phone TEXT NOT NULL,
      postedAgo TEXT NOT NULL,
      timestamp INTEGER NOT NULL
    );

    CREATE TABLE donation_records (
      id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
      centerName TEXT NOT NULL,
      bloodType TEXT NOT NULL,
      donationDate TEXT NOT NULL,
      donationType TEXT NOT NULL,
      status TEXT NOT NULL,
      timestamp INTEGER NOT NULL
    );

    CREATE TABLE donor_profile (
      id INTEGER PRIMARY KEY NOT NULL,
      name TEXT NOT NULL,
      donorId TEXT NOT NULL,
      bloodType TEXT NOT NULL,
      totalDonations INTEGER NOT NULL,
      livesSaved INTEGER NOT NULL,
      lastDonationDate TEXT NOT NULL,
      isEligibleNow INTEGER NOT NULL,
      city TEXT NOT NULL,
      contactPhone TEXT NOT NULL
    );
  """.trimIndent()

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(24.dp),
    containerColor = Color.White,
    title = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Storage, contentDescription = null, tint = CrimsonRed, modifier = Modifier.size(24.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text("Room SQLite Database", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = DeepNavy)
            Text("raktsetu_database.db • Active", fontSize = 11.sp, color = AvailableGreen, fontWeight = FontWeight.Bold)
          }
        }
        IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        Text("Local Database Tables & Live Records:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DeepNavy)
        Spacer(modifier = Modifier.height(8.dp))

        TableStatRow(name = "blood_centers", count = "$centersCount rows", desc = "Hospitals, blood banks & stock levels")
        Spacer(modifier = Modifier.height(6.dp))
        TableStatRow(name = "emergency_requests", count = "$emergenciesCount rows", desc = "Critical SOS broadcasts & patient requests")
        Spacer(modifier = Modifier.height(6.dp))
        TableStatRow(name = "donation_records", count = "$donationsCount rows", desc = "User donation history & confirmed pledges")
        Spacer(modifier = Modifier.height(6.dp))
        TableStatRow(name = "donor_profile", count = "1 row", desc = "User donor pass, blood group & stats")

        Spacer(modifier = Modifier.height(14.dp))
        Text("SQL Schema Definition (DDL):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DeepNavy)
        Spacer(modifier = Modifier.height(6.dp))

        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFF1E293B),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = schemaSql,
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            color = Color(0xFFF1F5F9),
            modifier = Modifier.padding(10.dp)
          )
        }
      }
    },
    confirmButton = {
      TextButton(onClick = onDismiss, modifier = Modifier.testTag("close_db_inspector_btn")) {
        Text("Close", fontWeight = FontWeight.Bold, color = DeepNavy)
      }
    }
  )
}

@Composable
fun TableStatRow(name: String, count: String, desc: String) {
  Card(
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DeepNavy, fontFamily = FontFamily.Monospace)
        Text(desc, fontSize = 11.sp, color = Color.Gray)
      }
      Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFFFFECEE)
      ) {
        Text(
          text = count,
          color = CrimsonRed,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
      }
    }
  }
}
