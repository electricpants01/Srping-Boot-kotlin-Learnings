package com.locotoinnovations.mitocodespringboot.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
data class RefreshToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val token: String,
    val username: String,
    val expiryDate: LocalDateTime,
    val revoked: Boolean = false
)