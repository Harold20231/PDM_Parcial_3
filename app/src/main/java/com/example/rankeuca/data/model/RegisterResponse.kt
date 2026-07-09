package com.example.rankeuca.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    val ok: Boolean,
    val apiKey: String? = null,
    val message: String? = null
)

@Serializable
data class ResetResponse(
    val ok: Boolean,
    val message: String? = null
)