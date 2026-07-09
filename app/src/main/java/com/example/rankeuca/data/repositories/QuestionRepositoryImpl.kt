package com.example.rankeuca.data.repositories

import com.example.rankeuca.data.api.ApiException
import com.example.rankeuca.data.api.RankeUcaApi
import com.example.rankeuca.data.database.dao.OptionDao
import com.example.rankeuca.data.database.dao.QuestionDao
import com.example.rankeuca.data.database.entities.OptionEntity
import com.example.rankeuca.data.database.entities.QuestionEntity
import com.example.rankeuca.utils.flattenToEntities
import com.example.rankeuca.utils.toOptionEntities
import com.example.rankeuca.utils.toQuestionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class QuestionRepositoryImpl @Inject constructor(
    private val questionDao: QuestionDao,
    private val optionDao: OptionDao,
    private val api: RankeUcaApi
) : QuestionRepository {

    override fun getQuestions(): Flow<List<QuestionEntity>> {
        return questionDao.getAllQuestions()
    }

    override fun getOptionsForQuestion(questionId: Int): Flow<List<OptionEntity>> {
        return optionDao.getOptionsForQuestion(questionId)
    }

    override fun getQuestionsWithOptions(): Flow<List<Pair<QuestionEntity, List<OptionEntity>>>> {
        return flow {
            val questions = questionDao.getAllQuestionsSuspend()
            val result = questions.map { question ->
                val options = optionDao.getOptionsForQuestionSuspend(question.id)
                Pair(question, options)
            }
            emit(result)
        }
    }

    override suspend fun syncQuestions(apiKey: String) {
        try {

            val questionDtos = api.getQuestionsWithOptions(apiKey)


            questionDao.deleteAll()
            optionDao.deleteAll()

            questionDtos.forEach { dto ->
                val (question, options) = dto.flattenToEntities()
                questionDao.insertQuestions(listOf(question))
                optionDao.insertOptions(options)
            }
        } catch (e: ApiException.Unauthorized) {
            throw e
        } catch (e: Exception) {

        }
    }

    override suspend fun syncQuestionsSimple(apiKey: String) {
        try {
            // Usamos el endpoint GET /questions (simple)
            val questions = api.getQuestionsSimple(apiKey)


            questions.forEach { question ->
                val options = api.getOptions(apiKey, question.id)
                val questionEntity = question.toQuestionEntity()


                questionDao.insertQuestions(listOf(questionEntity))

                val optionEntities = options.toOptionEntities(question.id)
                optionDao.insertOptions(optionEntities)
            }
        } catch (e: Exception) {
            throw e
        }
    }
    override suspend fun updateOptionVotes(optionId: Int, newVotes: Int) {
        val option = optionDao.getOptionById(optionId)
        option?.let {
            val updatedOption = it.copy(votes = newVotes)
            optionDao.insertOptions(listOf(updatedOption))
        }
    }
}