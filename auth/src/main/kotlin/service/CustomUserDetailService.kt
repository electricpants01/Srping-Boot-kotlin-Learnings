package com.locotoinnovations.service

import com.locotoinnovations.domain.Role
import com.locotoinnovations.domain.User
import com.locotoinnovations.mitocodespringboot.repository.UserRepository
import com.locotoinnovations.web.RegistrationRequest
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(email: String): UserDetails {
        return userRepository.findByEmail(email)
            ?: throw UsernameNotFoundException("User not found :(")
    }

    fun registerUser(
        registrationRequest: RegistrationRequest,
        passwordEncoder: PasswordEncoder,
        role: Role = Role.ROLE_USER,
    ) {
        // Check if the user already exists
        if (userRepository.findByEmail(registrationRequest.email) != null) {
            throw UserAlreadyExistsException("User already exists")
        }

        // Create a new user
        val user = User(
            email = registrationRequest.email,
            paswd = passwordEncoder.encode(registrationRequest.password),
            roles = setOf(role)
        )

        // Save the user to the repository
        userRepository.save(user)
    }
}

class UserAlreadyExistsException(message: String) : RuntimeException(message)