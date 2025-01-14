package com.locotoinnovations.mitocodespringboot.repository

import com.locotoinnovations.mitocodespringboot.domain.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {
    fun findByToken(token: String): RefreshToken?
    fun findByUsername(username: String): List<RefreshToken>
}