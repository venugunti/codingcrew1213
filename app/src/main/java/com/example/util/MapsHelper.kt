package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object MapsHelper {
  /**
   * Opens Google Maps for turn-by-turn navigation to the given coordinates
   */
  fun openNavigationInGoogleMaps(
    context: Context,
    latitude: Double,
    longitude: Double,
    label: String = "Destination"
  ) {
    try {
      // Primary intent: Direct Google Navigation
      val navUri = Uri.parse("google.navigation:q=$latitude,$longitude&mode=d")
      val mapIntent = Intent(Intent.ACTION_VIEW, navUri).apply {
        setPackage("com.google.android.apps.maps")
      }

      if (mapIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(mapIntent)
      } else {
        // Secondary intent: Generic geo URI with query label
        val geoUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude(${Uri.encode(label)})")
        val genericIntent = Intent(Intent.ACTION_VIEW, geoUri)
        if (genericIntent.resolveActivity(context.packageManager) != null) {
          context.startActivity(genericIntent)
        } else {
          // Web Fallback: Google Maps web URL
          val webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$latitude,$longitude")
          val webIntent = Intent(Intent.ACTION_VIEW, webUri)
          context.startActivity(webIntent)
        }
      }
    } catch (e: Exception) {
      // Fail-safe web browser fallback
      try {
        val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$latitude,$longitude")
        context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
      } catch (ex: Exception) {
        Toast.makeText(context, "Could not launch Google Maps: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
      }
    }
  }

  /**
   * Dials the blood bank or hospital phone number
   */
  fun dialPhoneNumber(context: Context, phoneNumber: String) {
    try {
      val cleanNumber = phoneNumber.replace(Regex("[^0-9+]"), "")
      val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanNumber"))
      context.startActivity(dialIntent)
    } catch (e: Exception) {
      Toast.makeText(context, "Unable to dial: $phoneNumber", Toast.LENGTH_SHORT).show()
    }
  }
}
