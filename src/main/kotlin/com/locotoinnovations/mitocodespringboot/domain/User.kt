package com.locotoinnovations.mitocodespringboot.domain

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "users")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true, nullable = false)
    val email: String,

    @Column(nullable = false)
    val paswd: String,  // Make this private to avoid the conflict

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    val roles: Set<Role> = setOf(Role.ROLE_USER)
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> = roles

    override fun getPassword(): String = this.paswd

    override fun getUsername(): String = email

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}

enum class Role : GrantedAuthority {
    ROLE_USER,
    ROLE_ADMIN;

    override fun getAuthority(): String = name
}