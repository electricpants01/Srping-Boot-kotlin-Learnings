package com.locotoinnovations.mitocodespringboot.web

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/hello")
class HelloWorldController {

    @GetMapping("/greet")
    fun helloWorld() = "Hello World!"
}