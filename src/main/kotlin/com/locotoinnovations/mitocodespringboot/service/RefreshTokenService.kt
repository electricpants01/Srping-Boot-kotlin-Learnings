package com.locotoinnovations.mitocodespringboot.service

import com.locotoinnovations.mitocodespringboot.domain.RefreshToken
import com.locotoinnovations.mitocodespringboot.repository.RefreshTokenRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository
) {

    fun saveRefreshToken(token: String, username: String, expiryDate: LocalDateTime) {
        val refreshToken = RefreshToken(token = token, username = username, expiryDate = expiryDate)
        refreshTokenRepository.save(refreshToken)
    }

    fun getRefreshToken(token: String): RefreshToken? {
        return refreshTokenRepository.findByToken(token)
    }

    @Transactional
    fun revokeRefreshToken(token: String) {
        val refreshToken = refreshTokenRepository.findByToken(token) ?: return
        refreshToken.copy(revoked = true).let {
            refreshTokenRepository.save(it)
        }
    }

    fun isTokenRevoked(token: String): Boolean {
        val refreshToken = refreshTokenRepository.findByToken(token) ?: return true
        return refreshToken.revoked
    }
}