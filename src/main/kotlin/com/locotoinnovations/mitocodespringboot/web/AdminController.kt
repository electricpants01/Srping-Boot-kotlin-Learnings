package com.locotoinnovations.mitocodespringboot.web

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin")
class AdminController {

    @RequestMapping("/greet")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    fun greetAdmin(): ResponseEntity<String> {
        return ResponseEntity.ok("Hello Admin!")
    }
}