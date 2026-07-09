package com.example.rankeuca.utils

import com.example.rankeuca.data.database.entities.OptionEntity
import com.example.rankeuca.data.database.entities.QuestionEntity
import com.example.rankeuca.data.model.OptionDto
import com.example.rankeuca.data.model.QuestionWithOptions
import com.example.rankeuca.data.model.UpdatedOption

fun QuestionWithOptions.toEntity(): QuestionEntity {
    return QuestionEntity(
        id = id,
        text = text
    )
}

fun com.example.rankeuca.data.model.OptionWithVotes.toEntity(questionId: Int): OptionEntity {
    return OptionEntity(
        id = id,
        questionId = questionId,
        value = value,
        votes = votes,
        imageUrl = null // No tenemos imageUrl en este formato
    )
}

fun OptionDto.toEntity(questionId: Int): OptionEntity {
    return OptionEntity(
        id = id,
        questionId = questionId,
        value = value,
        votes = votes,
        imageUrl = imageUrl
    )
}

fun QuestionWithOptions.flattenToEntities(): Pair<QuestionEntity, List<OptionEntity>> {
    val questionEntity = toEntity()
    val optionEntities = options.map { it.toEntity(id) }
    return Pair(questionEntity, optionEntities)
}

fun UpdatedOption.toOptionEntity(existingOption: OptionEntity): OptionEntity {
    return existingOption.copy(
        votes = votes
    )
}

fun com.example.rankeuca.data.model.Question.toQuestionEntity(): QuestionEntity {
    return QuestionEntity(
        id = id,
        text = title
    )
}


fun List<OptionDto>.toOptionEntities(questionId: Int): List<OptionEntity> {
    return this.map { it.toEntity(questionId) }
}