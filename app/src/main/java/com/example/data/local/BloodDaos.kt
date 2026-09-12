package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BloodCenterDao {
  @Query("SELECT * FROM blood_centers ORDER BY distanceKm ASC")
  fun getAllCenters(): Flow<List<BloodCenterEntity>>

  @Query("SELECT * FROM blood_centers WHERE id = :id LIMIT 1")
  suspend fun getCenterById(id: String): BloodCenterEntity?

  @Query("SELECT COUNT(*) FROM blood_centers")
  fun getCentersCount(): Flow<Int>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCenters(centers: List<BloodCenterEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCenter(center: BloodCenterEntity)

  @Query("UPDATE blood_centers SET oNegative = :oNeg WHERE id = :id")
  suspend fun updateStock(id: String, oNeg: Int)
}

@Dao
interface EmergencyRequestDao {
  @Query("SELECT * FROM emergency_requests ORDER BY timestamp DESC")
  fun getAllEmergencies(): Flow<List<EmergencyRequestEntity>>

  @Query("SELECT COUNT(*) FROM emergency_requests")
  fun getEmergenciesCount(): Flow<Int>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertEmergency(emergency: EmergencyRequestEntity): Long

  @Query("DELETE FROM emergency_requests WHERE id = :id")
  suspend fun deleteEmergency(id: Long)
}

@Dao
interface DonationRecordDao {
  @Query("SELECT * FROM donation_records ORDER BY timestamp DESC")
  fun getAllDonations(): Flow<List<DonationRecordEntity>>

  @Query("SELECT COUNT(*) FROM donation_records")
  fun getDonationsCount(): Flow<Int>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertDonation(donation: DonationRecordEntity): Long
}

@Dao
interface DonorProfileDao {
  @Query("SELECT * FROM donor_profile WHERE id = 1 LIMIT 1")
  fun getProfile(): Flow<DonorProfileEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateProfile(profile: DonorProfileEntity)
}
