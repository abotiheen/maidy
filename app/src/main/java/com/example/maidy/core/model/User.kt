package com.example.maidy.core.model

data class User(
    val id: String = "",
    val fullName: String = "",
    val phoneNumber: String = "",
    val profileImageUrl: String = "",
    val createdAt: Long = 0L,
    val role: String = "customer"           // or "maid"
)
