package com.example.rankeuca.data.model

import kotlinx.serialization.Serializable

@Serializable
data class VoteRequest(
    val votes: List<VoteItem>
)

@Serializable
data class VoteItem(
    val questionId: Int,
    val optionId: Int
)