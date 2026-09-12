package com.example.data.local

import android.content.Context
import com.example.model.BloodCenter
import com.example.model.BloodInventory
import com.example.model.DonorProfile
import com.example.model.UrgencyLevel
import com.example.model.UrgentEmergency
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class LocalBloodRepository(context: Context) {
  private val database = AppDatabase.getInstance(context)
  private val bloodCenterDao = database.bloodCenterDao()
  private val emergencyRequestDao = database.emergencyRequestDao()
  private val donationRecordDao = database.donationRecordDao()
  private val donorProfileDao = database.donorProfileDao()

  init {
    // Ensure initial seeding is executed if database is empty
    CoroutineScope(Dispatchers.IO).launch {
      val center = bloodCenterDao.getCenterById("c1")
      if (center == null) {
        AppDatabase.seedDatabase(database)
      }
    }
  }

  // Reactive centers stream
  val centersFlow: Flow<List<BloodCenter>> = bloodCenterDao.getAllCenters().map { list ->
    list.map { entity ->
      BloodCenter(
        id = entity.id,
        name = entity.name,
        category = entity.category,
        address = entity.address,
        latitude = entity.latitude,
        longitude = entity.longitude,
        distanceKm = entity.distanceKm,
        phone = entity.phone,
        operatingHours = entity.operatingHours,
        urgencyLevel = when (entity.urgencyLevel) {
          "CRITICAL" -> UrgencyLevel.CRITICAL
          "HIGH" -> UrgencyLevel.HIGH
          else -> UrgencyLevel.STABLE
        },
        inventory = BloodInventory(
          oNegative = entity.oNegative,
          oPositive = entity.oPositive,
          aPositive = entity.aPositive,
          aNegative = entity.aNegative,
          bPositive = entity.bPositive,
          bNegative = entity.bNegative,
          abPositive = entity.abPositive,
          abNegative = entity.abNegative
        ),
        hasActiveDriveToday = entity.hasActiveDriveToday,
        driveDetails = entity.driveDetails,
        rating = entity.rating
      )
    }
  }

  // Reactive emergency requests stream
  val emergenciesFlow: Flow<List<UrgentEmergency>> = emergencyRequestDao.getAllEmergencies().map { list ->
    list.map { entity ->
      UrgentEmergency(
        id = entity.id.toString(),
        hospitalName = entity.hospitalName,
        patientCase = entity.patientCase,
        bloodGroup = entity.bloodGroup,
        unitsNeeded = entity.unitsNeeded,
        distanceKm = entity.distanceKm,
        address = entity.address,
        latitude = entity.latitude,
        longitude = entity.longitude,
        phone = entity.phone,
        postedAgo = entity.postedAgo
      )
    }
  }

  // Reactive donations stream
  val donationsFlow: Flow<List<DonationRecordEntity>> = donationRecordDao.getAllDonations()

  // Reactive profile stream
  val profileFlow: Flow<DonorProfile> = donorProfileDao.getProfile().map { entity ->
    if (entity != null) {
      DonorProfile(
        name = entity.name,
        donorId = entity.donorId,
        bloodType = entity.bloodType,
        totalDonations = entity.totalDonations,
        livesSaved = entity.livesSaved,
        lastDonationDate = entity.lastDonationDate,
        isEligibleNow = entity.isEligibleNow,
        nextEligibleDate = if (entity.isEligibleNow) "Ready to Donate" else "Deferral Active",
        city = entity.city
      )
    } else {
      DonorProfile()
    }
  }

  // Counts for Database Inspector UI
  val centersCount: Flow<Int> = bloodCenterDao.getCentersCount()
  val emergenciesCount: Flow<Int> = emergencyRequestDao.getEmergenciesCount()
  val donationsCount: Flow<Int> = donationRecordDao.getDonationsCount()

  suspend fun addEmergencyRequest(
    hospitalName: String,
    bloodGroup: String,
    unitsNeeded: Int,
    contactPhone: String
  ): Long {
    val newEntity = EmergencyRequestEntity(
      hospitalName = hospitalName,
      patientCase = "Urgent Transfusion Request",
      bloodGroup = bloodGroup,
      unitsNeeded = unitsNeeded,
      distanceKm = 1.5,
      address = "San Francisco Emergency Center",
      latitude = 37.7749,
      longitude = -122.4194,
      phone = contactPhone,
      postedAgo = "Just now"
    )
    return emergencyRequestDao.insertEmergency(newEntity)
  }

  suspend fun pledgeDonation(
    centerName: String,
    bloodType: String,
    donationType: String = "Whole Blood"
  ): Long {
    val newRecord = DonationRecordEntity(
      centerName = centerName,
      bloodType = bloodType,
      donationDate = "Today (Pledged)",
      donationType = donationType,
      status = "Pledge Confirmed"
    )
    return donationRecordDao.insertDonation(newRecord)
  }

  suspend fun updateStock(centerId: String, oNegative: Int) {
    bloodCenterDao.updateStock(centerId, oNegative)
  }
}
