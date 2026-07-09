package com.example.rankeuca.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "options",
    foreignKeys = [
        ForeignKey(
            entity = QuestionEntity::class,
            parentColumns = ["id"],
            childColumns = ["questionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class OptionEntity(
    @PrimaryKey
    val id: Int,
    val questionId: Int,
    val value: String,
    val votes: Int = 0,
    val imageUrl: String? = null
)