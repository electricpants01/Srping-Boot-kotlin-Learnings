package com.locotoinnovations.mitocodespringboot.repository

import com.locotoinnovations.mitocodespringboot.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
}