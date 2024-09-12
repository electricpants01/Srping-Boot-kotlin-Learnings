package com.locotoinnovations.mitocodespringboot.config

import com.locotoinnovations.mitocodespringboot.service.CustomUserDetailsService
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class JwtRequestFilter(
    private val userDetailsService: CustomUserDetailsService,
    private val jwtUtil: JwtUtil,
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestUri = request.requestURI
        println("Request URI: $requestUri")  // Add logging

        val authorizationHeader = request.getHeader("Authorization")

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            val jwt = authorizationHeader.substring(7)
            val username = jwtUtil.extractUsername(jwt)
            println("Processing JWT for user: $username")  // Add logging

            if (username != null && SecurityContextHolder.getContext().authentication == null) {
                val userDetails: UserDetails = userDetailsService.loadUserByUsername(username)

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    val authToken = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authToken
                }
            }
        }
        filterChain.doFilter(request, response)
    }
}