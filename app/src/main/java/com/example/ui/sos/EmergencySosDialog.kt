package com.example.ui.sos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AvailableGreen
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.DarkCrimson
import com.example.ui.theme.DeepNavy
import com.example.util.MapsHelper

@Composable
fun EmergencySosDialog(
  onDismiss: () -> Unit,
  onBroadcastSuccess: (bloodGroup: String, hospital: String) -> Unit,
  onOpenMap: () -> Unit
) {
  val context = LocalContext.current
  val bloodGroups = listOf("O-", "O+", "A+", "A-", "B+", "B-", "AB+", "AB-")

  var selectedGroup by remember { mutableStateOf("O-") }
  var hospitalName by remember { mutableStateOf("City General Hospital") }
  var unitsNeeded by remember { mutableStateOf("2") }
  var contactPhone by remember { mutableStateOf("+1 (415) 555-0192") }
  var isBroadcastSent by remember { mutableStateOf(false) }

  if (isBroadcastSent) {
    AlertDialog(
      onDismissRequest = onDismiss,
      icon = {
        Icon(
          imageVector = Icons.Default.Warning,
          contentDescription = null,
          tint = CrimsonRed,
          modifier = Modifier.size(44.dp)
        )
      },
      title = { Text("🚨 SOS Broadcast Active!", fontWeight = FontWeight.Bold) },
      text = {
        Column {
          Text(
            "Emergency broadcast for $selectedGroup blood ($unitsNeeded units) has been dispatched to 48 donors within a 5 km radius.",
            fontSize = 14.sp
          )
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            "Location: $hospitalName",
            fontWeight = FontWeight.Bold,
            color = DeepNavy,
            fontSize = 13.sp
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            "You can track matching donors and surrounding blood bank inventories on Google Maps.",
            fontSize = 12.sp,
            color = Color.Gray
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            onDismiss()
            onOpenMap()
          },
          colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
          modifier = Modifier.testTag("sos_view_on_map_btn")
        ) {
          Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("View on Google Maps")
        }
      },
      dismissButton = {
        TextButton(onClick = onDismiss) {
          Text("Close")
        }
      }
    )
    return
  }

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
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFFFEBEE),
            modifier = Modifier.size(36.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text("🚨", fontSize = 18.sp)
            }
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text("Request Emergency Blood", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DeepNavy)
            Text("Broadcast SOS to nearby donors", fontSize = 11.sp, color = Color.Gray)
          }
        }
        IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
        }
      }
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text("Select Required Blood Group:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = DeepNavy)
        Spacer(modifier = Modifier.height(6.dp))

        // Blood Group Selector Grid
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          bloodGroups.take(4).forEach { group ->
            BloodGroupChip(
              group = group,
              isSelected = selectedGroup == group,
              onClick = { selectedGroup = group },
              modifier = Modifier.weight(1f)
            )
          }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          bloodGroups.drop(4).forEach { group ->
            BloodGroupChip(
              group = group,
              isSelected = selectedGroup == group,
              onClick = { selectedGroup = group },
              modifier = Modifier.weight(1f)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = hospitalName,
          onValueChange = { hospitalName = it },
          label = { Text("Hospital Name / Location") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("sos_hospital_input")
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = unitsNeeded,
            onValueChange = { unitsNeeded = it },
            label = { Text("Units Needed") },
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("sos_units_input")
          )

          OutlinedTextField(
            value = contactPhone,
            onValueChange = { contactPhone = it },
            label = { Text("Attendant Contact") },
            singleLine = true,
            modifier = Modifier
              .weight(2f)
              .testTag("sos_phone_input")
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Fast Red Cross Helpline Call
        OutlinedButton(
          onClick = {
            MapsHelper.dialPhoneNumber(context, "1910")
          },
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("call_emergency_helpline_btn")
        ) {
          Icon(Icons.Default.Call, contentDescription = null, tint = CrimsonRed, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Call National Blood Helpline (1910)", fontSize = 12.sp, color = CrimsonRed, fontWeight = FontWeight.Bold)
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          isBroadcastSent = true
          onBroadcastSuccess(selectedGroup, hospitalName)
        },
        colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("confirm_sos_broadcast_btn")
      ) {
        Text("Broadcast SOS", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = Color.Gray)
      }
    }
  )
}

@Composable
fun BloodGroupChip(
  group: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = if (isSelected) CrimsonRed else Color(0xFFF1F5F9),
    modifier = modifier
      .height(38.dp)
      .clickable { onClick() }
  ) {
    Box(contentAlignment = Alignment.Center) {
      Text(
        text = group,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        color = if (isSelected) Color.White else DeepNavy
      )
    }
  }
}
