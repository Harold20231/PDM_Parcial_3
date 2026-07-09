package com.example.rankeuca.data.repositories

import com.example.rankeuca.data.database.entities.OptionEntity
import com.example.rankeuca.data.database.entities.QuestionEntity
import kotlinx.coroutines.flow.Flow

interface QuestionRepository {
    fun getQuestions(): Flow<List<QuestionEntity>>
    fun getOptionsForQuestion(questionId: Int): Flow<List<OptionEntity>>
    fun getQuestionsWithOptions(): Flow<List<Pair<QuestionEntity, List<OptionEntity>>>>
    suspend fun syncQuestions(apiKey: String)
    suspend fun syncQuestionsSimple(apiKey: String)
    suspend fun updateOptionVotes(optionId: Int, newVotes: Int)
}