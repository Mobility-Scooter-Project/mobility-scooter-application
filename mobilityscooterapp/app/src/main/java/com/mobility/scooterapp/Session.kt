package com.mobility.scooterapp



data class Session(
    val date: String? = null,
    val dateTimeString: String? = null,
    val start_time: String? = null,
    val session_length: String? = null,
    val video_url: String? = null,
    val thumbnail_url: String? = null,
    val encryptedFilePath: String? = null,
    val estimateData : String? = null
)