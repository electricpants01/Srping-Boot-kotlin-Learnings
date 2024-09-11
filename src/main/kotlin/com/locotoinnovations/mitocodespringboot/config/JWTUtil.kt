package com.locotoinnovations.mitocodespringboot.config

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtUtil {
    private val secretKey: SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)

    // Generate JWT token
    fun generateToken(userDetails: UserDetails): String {
        val claims: MutableMap<String, Any> = HashMap()
        claims["roles"] = userDetails.authorities.map { it.authority }

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(userDetails.username) // Here we use username
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 1)) // 1 minutes expiration
            .signWith(secretKey)
            .compact()
    }

    // Validate JWT token
    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return username == userDetails.username && !isTokenExpired(token)
    }

    fun generateRefreshToken(userDetails: UserDetails): String {
        val now = LocalDateTime.now()
        val expiration = now.plusDays(1) // 1 days

        return Jwts.builder()
            .setSubject(userDetails.username)
            .setIssuedAt(convertToDate(now))
            .setExpiration(convertToDate(expiration))
            .signWith(Keys.hmacShaKeyFor(secretKey.encoded), SignatureAlgorithm.HS256)
            .compact()
    }

    fun getExpiryDate(token: String): LocalDateTime {
        val expirationDate = Jwts.parserBuilder()
            .setSigningKey(secretKey.encoded)
            .build()
            .parseClaimsJws(token)
            .body
            .expiration

        return convertToLocalDateTime(expirationDate)
    }


    // Extract username from JWT token
    fun extractUsername(token: String): String {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).body.subject
    }

    // Check if the token is expired
    private fun isTokenExpired(token: String): Boolean {
        val expiration = extractExpiration(token)
        return expiration.before(Date())
    }

    // Extract expiration from JWT token
    private fun extractExpiration(token: String): Date {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).body.expiration
    }

    private fun convertToDate(localDateTime: LocalDateTime): Date {
        return Date.from(localDateTime.toInstant(ZoneOffset.UTC))
    }

    private fun convertToLocalDateTime(date: Date): LocalDateTime {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
    }
}