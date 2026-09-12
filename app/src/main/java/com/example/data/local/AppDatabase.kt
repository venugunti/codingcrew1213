package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [
    BloodCenterEntity::class,
    EmergencyRequestEntity::class,
    DonationRecordEntity::class,
    DonorProfileEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun bloodCenterDao(): BloodCenterDao
  abstract fun emergencyRequestDao(): EmergencyRequestDao
  abstract fun donationRecordDao(): DonationRecordDao
  abstract fun donorProfileDao(): DonorProfileDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "raktsetu_database.db"
        )
          .addCallback(object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
              super.onCreate(db)
              // Populate default data on DB creation
              CoroutineScope(Dispatchers.IO).launch {
                INSTANCE?.let { database ->
                  seedDatabase(database)
                }
              }
            }
          })
          .build()
        INSTANCE = instance
        instance
      }
    }

    suspend fun seedDatabase(database: AppDatabase) {
      // Seed blood centers
      val initialCenters = listOf(
        BloodCenterEntity(
          id = "c1",
          name = "City General Hospital & Blood Bank",
          category = "Hospital Blood Bank",
          address = "1001 Potrero Ave, San Francisco, CA",
          latitude = 37.7558,
          longitude = -122.4048,
          distanceKm = 1.2,
          phone = "+1 (415) 555-0192",
          operatingHours = "Open 24/7 • Emergency Ready",
          urgencyLevel = "CRITICAL",
          oNegative = 1,
          oPositive = 14,
          aPositive = 8,
          aNegative = 3,
          bPositive = 10,
          bNegative = 2,
          abPositive = 6,
          abNegative = 1,
          hasActiveDriveToday = true,
          driveDetails = "Emergency Drive today until 8:00 PM (Snacks & donor badge provided)",
          rating = 4.9
        ),
        BloodCenterEntity(
          id = "c2",
          name = "Red Cross Regional Blood Donation Center",
          category = "Red Cross Center",
          address = "1663 Mission St, San Francisco, CA",
          latitude = 37.7712,
          longitude = -122.4199,
          distanceKm = 2.4,
          phone = "+1 (800) 733-2767",
          operatingHours = "8:00 AM – 7:00 PM",
          urgencyLevel = "HIGH",
          oNegative = 4,
          oPositive = 28,
          aPositive = 22,
          aNegative = 7,
          bPositive = 18,
          bNegative = 5,
          abPositive = 12,
          abNegative = 4,
          hasActiveDriveToday = true,
          driveDetails = "Platelet & Whole Blood Walk-ins Welcome",
          rating = 4.8
        ),
        BloodCenterEntity(
          id = "c3",
          name = "St. Jude Community Transfusion Unit",
          category = "Community Health Hub",
          address = "350 Parnassus Ave, San Francisco, CA",
          latitude = 37.7634,
          longitude = -122.4578,
          distanceKm = 3.8,
          phone = "+1 (415) 555-0348",
          operatingHours = "7:30 AM – 6:00 PM",
          urgencyLevel = "STABLE",
          oNegative = 8,
          oPositive = 32,
          aPositive = 19,
          aNegative = 9,
          bPositive = 24,
          bNegative = 6,
          abPositive = 15,
          abNegative = 5,
          hasActiveDriveToday = false,
          driveDetails = null,
          rating = 4.7
        ),
        BloodCenterEntity(
          id = "c4",
          name = "Metro Life Mobile Blood Bus",
          category = "Mobile Donation Drive",
          address = "Market St & 4th St Plaza, San Francisco, CA",
          latitude = 37.7858,
          longitude = -122.4065,
          distanceKm = 1.9,
          phone = "+1 (415) 555-0811",
          operatingHours = "9:00 AM – 5:00 PM (Today Only)",
          urgencyLevel = "HIGH",
          oNegative = 2,
          oPositive = 11,
          aPositive = 9,
          aNegative = 2,
          bPositive = 7,
          bNegative = 3,
          abPositive = 4,
          abNegative = 1,
          hasActiveDriveToday = true,
          driveDetails = "Mobile donor vehicle parked next to station entrance",
          rating = 4.9
        ),
        BloodCenterEntity(
          id = "c5",
          name = "Bay Area Trauma & Blood Institute",
          category = "Hospital Blood Bank",
          address = "505 Parnassus Ave, San Francisco, CA",
          latitude = 37.7629,
          longitude = -122.4589,
          distanceKm = 4.1,
          phone = "+1 (415) 555-0922",
          operatingHours = "Open 24/7",
          urgencyLevel = "CRITICAL",
          oNegative = 0,
          oPositive = 19,
          aPositive = 14,
          aNegative = 4,
          bPositive = 12,
          bNegative = 1,
          abPositive = 8,
          abNegative = 2,
          hasActiveDriveToday = false,
          driveDetails = null,
          rating = 4.6
        )
      )
      database.bloodCenterDao().insertCenters(initialCenters)

      // Seed emergency requests
      val initialEmergencies = listOf(
        EmergencyRequestEntity(
          hospitalName = "City General Hospital",
          patientCase = "Critical Cardiac Surgery • ICU Bed 4",
          bloodGroup = "O- Negative",
          unitsNeeded = 3,
          distanceKm = 1.2,
          address = "1001 Potrero Ave, San Francisco, CA",
          latitude = 37.7558,
          longitude = -122.4048,
          phone = "+1 (415) 555-0192",
          postedAgo = "12 mins ago"
        ),
        EmergencyRequestEntity(
          hospitalName = "Bay Area Trauma Institute",
          patientCase = "Emergency Road Accident Victim",
          bloodGroup = "B+ Positive",
          unitsNeeded = 2,
          distanceKm = 4.1,
          address = "505 Parnassus Ave, San Francisco, CA",
          latitude = 37.7629,
          longitude = -122.4589,
          phone = "+1 (415) 555-0922",
          postedAgo = "34 mins ago"
        ),
        EmergencyRequestEntity(
          hospitalName = "Metro Pediatric Wing",
          patientCase = "Thalassemia Transfusion Support",
          bloodGroup = "A- Negative",
          unitsNeeded = 2,
          distanceKm = 2.8,
          address = "1663 Mission St, San Francisco, CA",
          latitude = 37.7712,
          longitude = -122.4199,
          phone = "+1 (800) 733-2767",
          postedAgo = "1 hour ago"
        )
      )
      initialEmergencies.forEach { database.emergencyRequestDao().insertEmergency(it) }

      // Seed donation history
      val initialDonations = listOf(
        DonationRecordEntity(
          centerName = "City General Hospital Blood Bank",
          bloodType = "O+",
          donationDate = "May 12, 2026",
          donationType = "Whole Blood (450 ml)",
          status = "Verified & Transfused"
        ),
        DonationRecordEntity(
          centerName = "Red Cross Regional Center",
          bloodType = "O+",
          donationDate = "Jan 20, 2026",
          donationType = "Platelet Apheresis",
          status = "Verified & Transfused"
        )
      )
      initialDonations.forEach { database.donationRecordDao().insertDonation(it) }

      // Seed donor profile
      database.donorProfileDao().insertOrUpdateProfile(
        DonorProfileEntity(
          id = 1,
          name = "Dr. Rahul Sharma",
          donorId = "RS-99482",
          bloodType = "O+",
          totalDonations = 8,
          livesSaved = 24,
          lastDonationDate = "May 12, 2026",
          isEligibleNow = true,
          city = "San Francisco, CA",
          contactPhone = "+1 (415) 555-0192"
        )
      )
    }
  }
}
