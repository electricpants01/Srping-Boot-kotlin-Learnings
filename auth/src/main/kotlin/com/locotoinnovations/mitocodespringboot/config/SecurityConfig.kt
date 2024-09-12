package com.locotoinnovations.mitocodespringboot.config

import com.locotoinnovations.mitocodespringboot.service.CustomUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtRequestFilter: JwtRequestFilter,
) {

    companion object {
        val WHITE_LIST_AUTH = listOf(
            "/api/v1/auth/authenticate",
            "/api/v1/auth/register",
            "/api/v1/auth/register-admin",
            "/api/v1/auth/refresh"
        )
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() } // Disable CSRF for APIs
            .authorizeHttpRequests { authz ->
                authz
                    .requestMatchers(*WHITE_LIST_AUTH.toTypedArray()).permitAll() // Public access to login/registration
//                    .requestMatchers("api/v1/admin/**").hasRole("ADMIN") // Admin routes require ADMIN role
//                    .requestMatchers("/api/v1/user/**").hasAnyRole("USER", "ADMIN") // User routes accessible by USER or ADMIN
                .anyRequest().authenticated() // All other requests require authentication
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // No sessions; JWT is stateless
            }
            // Ensure that JwtRequestFilter only applies to authenticated requests
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}

@Configuration
class ApplicationConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManager(
        authenticationConfiguration: AuthenticationConfiguration
    ): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    @Bean
    fun authenticationProvider(
        userDetailsService: CustomUserDetailsService, // Autowire CustomUserDetailsService here
        passwordEncoder: PasswordEncoder // Inject PasswordEncoder directly here
    ): DaoAuthenticationProvider {
        val provider = DaoAuthenticationProvider()
        provider.setUserDetailsService(userDetailsService)
        provider.setPasswordEncoder(passwordEncoder)
        return provider
    }
}