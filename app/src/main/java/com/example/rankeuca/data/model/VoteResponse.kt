package com.example.rankeuca.data.model

import kotlinx.serialization.Serializable

@Serializable
data class VoteResponse(
    val updated: List<UpdatedOption>
)

@Serializable
data class UpdatedOption(
    val id: Int,
    val questionId: Int,
    val votes: Int
)