package com.example.rankeuca.data.api

import com.example.rankeuca.data.model.OptionDto
import com.example.rankeuca.data.model.Question
import com.example.rankeuca.data.model.QuestionWithOptions
import com.example.rankeuca.data.model.RegisterResponse
import com.example.rankeuca.data.model.ResetResponse
import com.example.rankeuca.data.model.VoteRequest
import com.example.rankeuca.data.model.VoteResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

interface RankeUcaApi {
    suspend fun register(carnet: String): RegisterResponse
    suspend fun getQuestionsSimple(apiKey: String): List<Question>
    suspend fun getQuestionsWithOptions(apiKey: String): List<QuestionWithOptions>
    suspend fun submitVotes(apiKey: String, votes: VoteRequest): VoteResponse
    suspend fun reset(): ResetResponse
    suspend fun getOptions(apiKey: String, questionId: Int? = null): List<OptionDto>
}

class RankeUcaApiImpl(
    private val client: HttpClient,
    private val json: Json = Json { ignoreUnknownKeys = true }
) : RankeUcaApi {

    companion object {
        private const val BASE_URL = "https://qjcxdvfzyseuvezacxsd.supabase.co/functions/v1/rankeuca"
    }

    override suspend fun register(carnet: String): RegisterResponse {
        val response: HttpResponse = client.post("$BASE_URL/register") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("carnet" to carnet))
        }

        return when (response.status) {
            HttpStatusCode.OK, HttpStatusCode.Created -> response.body()
            else -> {
                try {
                    response.body<RegisterResponse>()
                } catch (e: Exception) {
                    throw ApiException.UnknownError("Error al registrar: ${response.status}")
                }
            }
        }
    }

    override suspend fun getQuestionsSimple(apiKey: String): List<Question> {
        val response: HttpResponse = client.get("$BASE_URL/questions") {
            header("Authorization", "Bearer $apiKey")
        }

        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            HttpStatusCode.Unauthorized -> throw ApiException.Unauthorized("API key inválida")
            else -> throw ApiException.UnknownError("Error al obtener preguntas: ${response.status}")
        }
    }

    override suspend fun getQuestionsWithOptions(apiKey: String): List<QuestionWithOptions> {
        val response: HttpResponse = client.get("$BASE_URL/parcialtres/questions") {
            header("Authorization", "Bearer $apiKey")
        }

        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            HttpStatusCode.Unauthorized -> throw ApiException.Unauthorized("API key inválida")
            else -> throw ApiException.UnknownError("Error al obtener preguntas: ${response.status}")
        }
    }

    override suspend fun getOptions(apiKey: String, questionId: Int?): List<OptionDto> {
        val response: HttpResponse = client.get("$BASE_URL/options") {
            header("Authorization", "Bearer $apiKey")
            if (questionId != null) {
                url {
                    parameters.append("questionId", questionId.toString())
                }
            }
        }

        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            HttpStatusCode.Unauthorized -> throw ApiException.Unauthorized("API key inválida")
            else -> throw ApiException.UnknownError("Error al obtener opciones: ${response.status}")
        }
    }

    override suspend fun submitVotes(apiKey: String, votes: VoteRequest): VoteResponse {
        val response: HttpResponse = client.post("$BASE_URL/parcialtres/votes") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $apiKey")
            setBody(votes)
        }

        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            HttpStatusCode.Unauthorized -> throw ApiException.Unauthorized("API key inválida")
            HttpStatusCode.BadRequest -> {
                val errorMsg = try { response.body<String>() } catch (e: Exception) { "Voto inválido" }
                throw ApiException.BadRequest(errorMsg)
            }
            else -> throw ApiException.UnknownError("Error al enviar voto: ${response.status}")
        }
    }

    override suspend fun reset(): ResetResponse {
        val response: HttpResponse = client.post("$BASE_URL/reset") {
            contentType(ContentType.Application.Json)
        }

        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            else -> throw ApiException.UnknownError("Error al resetear: ${response.status}")
        }
    }
}

sealed class ApiException(message: String) : Exception(message) {
    class Unauthorized(message: String) : ApiException(message)
    class BadRequest(message: String) : ApiException(message)
    class UnknownError(message: String) : ApiException(message)
}