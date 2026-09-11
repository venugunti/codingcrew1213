package com.example.model

enum class UrgencyLevel {
  CRITICAL,
  HIGH,
  STABLE
}

data class BloodInventory(
  val oNegative: Int,
  val oPositive: Int,
  val aPositive: Int,
  val aNegative: Int,
  val bPositive: Int,
  val bNegative: Int,
  val abPositive: Int,
  val abNegative: Int
)

data class BloodCenter(
  val id: String,
  val name: String,
  val category: String, // "Hospital Blood Bank", "Red Cross Center", "Mobile Donation Drive"
  val address: String,
  val latitude: Double,
  val longitude: Double,
  val distanceKm: Double,
  val phone: String,
  val operatingHours: String,
  val urgencyLevel: UrgencyLevel,
  val inventory: BloodInventory,
  val hasActiveDriveToday: Boolean = false,
  val driveDetails: String? = null,
  val rating: Double = 4.8
)

data class UrgentEmergency(
  val id: String,
  val hospitalName: String,
  val patientCase: String,
  val bloodGroup: String,
  val unitsNeeded: Int,
  val distanceKm: Double,
  val address: String,
  val latitude: Double,
  val longitude: Double,
  val phone: String,
  val postedAgo: String
)

data class DonorProfile(
  val name: String = "Dr. Rahul Sharma",
  val donorId: String = "RS-99482",
  val bloodType: String = "O+",
  val totalDonations: Int = 8,
  val livesSaved: Int = 24,
  val lastDonationDate: String = "May 12, 2026",
  val isEligibleNow: Boolean = true,
  val nextEligibleDate: String = "Ready to Donate",
  val city: String = "Metro City"
)
