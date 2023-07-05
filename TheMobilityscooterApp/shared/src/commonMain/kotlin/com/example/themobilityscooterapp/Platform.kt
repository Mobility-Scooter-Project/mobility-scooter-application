package com.example.themobilityscooterapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform