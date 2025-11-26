package com.example.maidy.core.model

data class Maid(
    val id: String = "",
    val createdAt: Long = 0L,
    val fullName: String = "",
    val profileImageUrl: String = "",
    val phoneNumber: String = "",
    val phoneVerified: Boolean = false,
    val password: String = "",                // Password for authentication
    val bio: String = "",
    val verified: Boolean = false,
    val averageRating: Double = 0.0,
    val reviewCount: Int = 0,
    val services: List<String> = emptyList(),
    val specialtyTag: String = "",
    val hourlyRate: Double = 0.0,
    val available: Boolean = true,
    val fcmToken: String = ""                 // Firebase Cloud Messaging token for notifications
)

data class MaidReview(
    val id: String = "",
    val reviewerName: String = "",
    val rating: Int = 5,
    val comment: String = "",
    val date: String = "",
    val timestamp: Long = 0L
)

// Predefined service options
object ServiceOptions {
    val services = listOf(
        "Kitchen Cleaning",
        "Bathroom Cleaning",
        "Laundry",
        "Dusting",
        "Vacuuming",
        "Window Washing",
        "Deep Cleaning",
        "Ironing",
        "Carpet Cleaning",
        "General Cleaning"
    )
}

// Predefined specialty tags (Maid Classes)
object SpecialtyTags {
    val tags = listOf(
        "Gold",
        "Silver",
        "Bronze"
    )
}
