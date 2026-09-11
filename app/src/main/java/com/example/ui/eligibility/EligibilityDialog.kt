package com.example.ui.eligibility

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AvailableGreen
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.DeepNavy

@Composable
fun EligibilityDialog(
  onDismiss: () -> Unit,
  onNavigateToMap: () -> Unit
) {
  var q1Age by remember { mutableStateOf(true) }
  var q2Weight by remember { mutableStateOf(true) }
  var q3Timing by remember { mutableStateOf(true) }
  var q4Wellness by remember { mutableStateOf(true) }
  var q5Tattoos by remember { mutableStateOf(true) }

  val isEligible = q1Age && q2Weight && q3Timing && q4Wellness && q5Tattoos

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
          Text("📝", fontSize = 22.sp)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Eligibility Check", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DeepNavy)
        }
        IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
        }
      }
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          "Verify if you can safely donate blood today (WHO & Red Cross guidelines):",
          fontSize = 13.sp,
          color = Color.Gray
        )
        Spacer(modifier = Modifier.height(14.dp))

        EligibilityCheckItem(
          label = "Age between 18 and 65 years",
          checked = q1Age,
          onCheckedChange = { q1Age = it }
        )
        EligibilityCheckItem(
          label = "Weight is 50 kg (110 lbs) or more",
          checked = q2Weight,
          onCheckedChange = { q2Weight = it }
        )
        EligibilityCheckItem(
          label = "At least 90 days since last blood donation",
          checked = q3Timing,
          onCheckedChange = { q3Timing = it }
        )
        EligibilityCheckItem(
          label = "Feeling healthy today with no active fever/cold",
          checked = q4Wellness,
          onCheckedChange = { q4Wellness = it }
        )
        EligibilityCheckItem(
          label = "No new tattoos or piercings in last 6 months",
          checked = q5Tattoos,
          onCheckedChange = { q5Tattoos = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = if (isEligible) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = if (isEligible) Icons.Default.CheckCircle else Icons.Default.Info,
              contentDescription = null,
              tint = if (isEligible) AvailableGreen else CrimsonRed,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = if (isEligible) "You are Eligible to Donate!" else "Temporary Deferral",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (isEligible) AvailableGreen else CrimsonRed
              )
              Text(
                text = if (isEligible)
                  "Ready to save up to 3 lives today. Find the closest center on Google Maps."
                else
                  "Please ensure all health criteria are met before visiting a donation bank.",
                fontSize = 11.sp,
                color = DeepNavy
              )
            }
          }
        }
      }
    },
    confirmButton = {
      if (isEligible) {
        Button(
          onClick = {
            onDismiss()
            onNavigateToMap()
          },
          colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.testTag("eligibility_go_to_map_btn")
        ) {
          Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Find Nearby Centers")
        }
      } else {
        TextButton(onClick = onDismiss) {
          Text("Understood", color = DeepNavy)
        }
      }
    },
    dismissButton = {
      if (isEligible) {
        TextButton(onClick = onDismiss) {
          Text("Cancel", color = Color.Gray)
        }
      }
    }
  )
}

@Composable
fun EligibilityCheckItem(
  label: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 3.dp)
      .clickable { onCheckedChange(!checked) }
  ) {
    Checkbox(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = CheckboxDefaults.colors(
        checkedColor = CrimsonRed,
        uncheckedColor = Color.Gray
      )
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(label, fontSize = 13.sp, color = DeepNavy)
  }
}
