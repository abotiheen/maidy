package com.example.maidy.core.model

data class User(
    val id: String = "",                    // Firestore will generate this
    val fullName: String = "",
    val phoneNumber: String = "",
    val password: String = "",               // Note: storing password is NOT secure, but for learning
    val createdAt: Long = 0L,               // Timestamp
    val role: String = "customer"           // or "maid"
)
