package com.example.mobilityscootersafetyassessmentsystem

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform