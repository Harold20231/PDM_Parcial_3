package com.example.rankeuca.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Question(
    val id: Int,
    val title: String
)

@Serializable
data class QuestionWithOptions(
    val id: Int,
    val text: String,
    val options: List<OptionWithVotes>
)

@Serializable
data class OptionWithVotes(
    val id: Int,
    val value: String,
    val votes: Int
)

@Serializable
data class QuestionDto(
    val id: Int,
    val text: String = "",
    val options: List<OptionDto> = emptyList()
)

@Serializable
data class OptionDto(
    val id: Int,
    val value: String = "",
    val votes: Int = 0,
    val imageUrl: String? = null
)