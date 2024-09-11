package com.locotoinnovations.mitocodespringboot.web

import com.locotoinnovations.mitocodespringboot.config.JwtUtil
import com.locotoinnovations.mitocodespringboot.domain.Role
import com.locotoinnovations.mitocodespringboot.service.CustomUserDetailsService
import com.locotoinnovations.mitocodespringboot.service.RefreshTokenService
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val jwtUtil: JwtUtil,
    private val userDetailsService: CustomUserDetailsService,
    private val passwordEncoder: PasswordEncoder,
    private val refreshTokenService: RefreshTokenService,
) {

    @PostMapping("/authenticate")
    fun createAuthenticationToken(
        @RequestBody authenticationRequest: AuthenticationRequest
    ): ResponseEntity<Map<String, String>> {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(authenticationRequest.email, authenticationRequest.password)
        )

        val userDetails = userDetailsService.loadUserByUsername(authenticationRequest.email)
        val accessToken = jwtUtil.generateToken(userDetails)
        val refreshToken = jwtUtil.generateRefreshToken(userDetails)

        // Save refresh token in the database
        refreshTokenService.saveRefreshToken(refreshToken, authenticationRequest.email, jwtUtil.getExpiryDate(refreshToken))

        return ResponseEntity.ok(mapOf("accessToken" to accessToken, "refreshToken" to refreshToken))
    }

    @PostMapping("/register")
    fun registerUser(
        @RequestBody registrationRequest: RegistrationRequest
    ): ResponseEntity<String> {
        userDetailsService.registerUser(registrationRequest = registrationRequest, passwordEncoder =  passwordEncoder) // Pass PasswordEncoder
        return ResponseEntity.ok("User registered successfully")
    }

    @PostMapping("/register-admin")
    fun createAdmin(
        @RequestBody adminRequest: AdminRegistrationRequest
    ): ResponseEntity<String> {
        userDetailsService.registerUser(
            registrationRequest = RegistrationRequest(
                email = adminRequest.email,
                password = adminRequest.password,
            ),
            passwordEncoder = passwordEncoder,
            role = Role.ROLE_ADMIN
        )
        return ResponseEntity.ok("Admin user created successfully")
    }

    @PostMapping("/logout")
    fun logout(@RequestBody logoutRequest: LogoutRequest): ResponseEntity<String> {
        // Revoke refresh token
        refreshTokenService.revokeRefreshToken(logoutRequest.refreshToken)
        return ResponseEntity.ok("Logged out successfully")
    }
}

data class AuthenticationRequest(
    val email: String,
    val password: String
)

data class RegistrationRequest(
    val email: String,
    val password: String
)

data class AdminRegistrationRequest(
    val email: String,
    val password: String
)

data class LogoutRequest(
    val refreshToken: String
)