package com.example.data

import com.example.model.BloodCenter
import com.example.model.BloodInventory
import com.example.model.UrgencyLevel
import com.example.model.UrgentEmergency

object BloodRepository {
  // Base reference location (San Francisco healthcare district / downtown coordinates)
  val defaultCenterLat = 37.7749
  val defaultCenterLng = -122.4194

  val bloodCenters = listOf(
    BloodCenter(
      id = "c1",
      name = "City General Hospital & Blood Bank",
      category = "Hospital Blood Bank",
      address = "1001 Potrero Ave, San Francisco, CA",
      latitude = 37.7558,
      longitude = -122.4048,
      distanceKm = 1.2,
      phone = "+1 (415) 555-0192",
      operatingHours = "Open 24/7 • Emergency Ready",
      urgencyLevel = UrgencyLevel.CRITICAL,
      inventory = BloodInventory(
        oNegative = 1, // Critical!
        oPositive = 14,
        aPositive = 8,
        aNegative = 3,
        bPositive = 10,
        bNegative = 2,
        abPositive = 6,
        abNegative = 1
      ),
      hasActiveDriveToday = true,
      driveDetails = "Emergency Drive today until 8:00 PM (Snacks & donor badge provided)",
      rating = 4.9
    ),
    BloodCenter(
      id = "c2",
      name = "Red Cross Regional Blood Donation Center",
      category = "Red Cross Center",
      address = "1663 Mission St, San Francisco, CA",
      latitude = 37.7712,
      longitude = -122.4199,
      distanceKm = 2.4,
      phone = "+1 (800) 733-2767",
      operatingHours = "8:00 AM – 7:00 PM",
      urgencyLevel = UrgencyLevel.HIGH,
      inventory = BloodInventory(
        oNegative = 4,
        oPositive = 28,
        aPositive = 22,
        aNegative = 7,
        bPositive = 18,
        bNegative = 5,
        abPositive = 12,
        abNegative = 4
      ),
      hasActiveDriveToday = true,
      driveDetails = "Platelet & Whole Blood Walk-ins Welcome",
      rating = 4.8
    ),
    BloodCenter(
      id = "c3",
      name = "St. Jude Community Transfusion Unit",
      category = "Community Health Hub",
      address = "350 Parnassus Ave, San Francisco, CA",
      latitude = 37.7634,
      longitude = -122.4578,
      distanceKm = 3.8,
      phone = "+1 (415) 555-0348",
      operatingHours = "7:30 AM – 6:00 PM",
      urgencyLevel = UrgencyLevel.STABLE,
      inventory = BloodInventory(
        oNegative = 8,
        oPositive = 32,
        aPositive = 19,
        aNegative = 9,
        bPositive = 24,
        bNegative = 6,
        abPositive = 15,
        abNegative = 5
      ),
      hasActiveDriveToday = false,
      rating = 4.7
    ),
    BloodCenter(
      id = "c4",
      name = "Metro Life Mobile Blood Bus",
      category = "Mobile Donation Drive",
      address = "Market St & 4th St Plaza, San Francisco, CA",
      latitude = 37.7858,
      longitude = -122.4065,
      distanceKm = 1.9,
      phone = "+1 (415) 555-0811",
      operatingHours = "9:00 AM – 5:00 PM (Today Only)",
      urgencyLevel = UrgencyLevel.HIGH,
      inventory = BloodInventory(
        oNegative = 2,
        oPositive = 11,
        aPositive = 9,
        aNegative = 2,
        bPositive = 7,
        bNegative = 3,
        abPositive = 4,
        abNegative = 1
      ),
      hasActiveDriveToday = true,
      driveDetails = "Mobile donor vehicle parked next to station entrance",
      rating = 4.9
    ),
    BloodCenter(
      id = "c5",
      name = "Bay Area Trauma & Blood Institute",
      category = "Hospital Blood Bank",
      address = "505 Parnassus Ave, San Francisco, CA",
      latitude = 37.7629,
      longitude = -122.4589,
      distanceKm = 4.1,
      phone = "+1 (415) 555-0922",
      operatingHours = "Open 24/7",
      urgencyLevel = UrgencyLevel.CRITICAL,
      inventory = BloodInventory(
        oNegative = 0, // Out of stock!
        oPositive = 19,
        aPositive = 14,
        aNegative = 4,
        bPositive = 12,
        bNegative = 1,
        abPositive = 8,
        abNegative = 2
      ),
      hasActiveDriveToday = false,
      rating = 4.6
    )
  )

  val urgentEmergencies = listOf(
    UrgentEmergency(
      id = "u1",
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
    UrgentEmergency(
      id = "u2",
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
    UrgentEmergency(
      id = "u3",
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
}
