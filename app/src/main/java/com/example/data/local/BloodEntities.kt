package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blood_centers")
data class BloodCenterEntity(
  @PrimaryKey val id: String,
  val name: String,
  val category: String,
  val address: String,
  val latitude: Double,
  val longitude: Double,
  val distanceKm: Double,
  val phone: String,
  val operatingHours: String,
  val urgencyLevel: String, // "CRITICAL", "HIGH", "STABLE"
  val oNegative: Int,
  val oPositive: Int,
  val aPositive: Int,
  val aNegative: Int,
  val bPositive: Int,
  val bNegative: Int,
  val abPositive: Int,
  val abNegative: Int,
  val hasActiveDriveToday: Boolean,
  val driveDetails: String?,
  val rating: Double
)

@Entity(tableName = "emergency_requests")
data class EmergencyRequestEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val hospitalName: String,
  val patientCase: String,
  val bloodGroup: String,
  val unitsNeeded: Int,
  val distanceKm: Double,
  val address: String,
  val latitude: Double,
  val longitude: Double,
  val phone: String,
  val postedAgo: String,
  val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "donation_records")
data class DonationRecordEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val centerName: String,
  val bloodType: String,
  val donationDate: String,
  val donationType: String, // "Whole Blood", "Platelets", "Plasma"
  val status: String, // "Completed", "Scheduled", "Verified"
  val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "donor_profile")
data class DonorProfileEntity(
  @PrimaryKey val id: Int = 1,
  val name: String,
  val donorId: String,
  val bloodType: String,
  val totalDonations: Int,
  val livesSaved: Int,
  val lastDonationDate: String,
  val isEligibleNow: Boolean,
  val city: String,
  val contactPhone: String
)
