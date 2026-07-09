package com.example.rankeuca.ui.screens.massvote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rankeuca.data.api.ApiException
import com.example.rankeuca.data.database.entities.OptionEntity
import com.example.rankeuca.data.database.entities.QuestionEntity
import com.example.rankeuca.data.model.VoteItem
import com.example.rankeuca.data.repositories.QuestionRepository
import com.example.rankeuca.data.repositories.VoteRepository
import com.example.rankeuca.utils.ApiKeyManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MassVoteUiState(
    val questions: List<QuestionEntity> = emptyList(),
    val optionsMap: Map<Int, List<OptionEntity>> = emptyMap(),
    val selectedOptions: Map<Int, Int> = emptyMap(),
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val submitError: String? = null,
    val canSubmit: Boolean = false,
    val isApiKeyValid: Boolean = true
)

@HiltViewModel
class MassVoteViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
    private val voteRepository: VoteRepository,
    private val apiKeyManager: ApiKeyManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MassVoteUiState())
    val uiState: StateFlow<MassVoteUiState> = _uiState.asStateFlow()

    private var apiKey: String = ""

    init {
        loadApiKey()
    }

    private fun loadApiKey() {
        viewModelScope.launch {
            apiKey = apiKeyManager.getApiKey() ?: ""
            if (apiKey.isNotEmpty()) {
                _uiState.update { it.copy(isApiKeyValid = true) }
                loadQuestions()
            } else {
                _uiState.update {
                    it.copy(
                        error = "No se encontró API key. Por favor regístrate.",
                        isApiKeyValid = false
                    )
                }
            }
        }
    }

    fun loadQuestions() {
        if (apiKey.isEmpty()) {
            _uiState.update {
                it.copy(
                    error = "API key no disponible",
                    isApiKeyValid = false
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Sincronizar con la API (si falla, usar datos locales)
                try {
                    questionRepository.syncQuestions(apiKey)
                    _uiState.update { it.copy(isApiKeyValid = true) }
                } catch (e: ApiException.Unauthorized) {
                    _uiState.update {
                        it.copy(
                            error = "API key inválida. Por favor regístrate nuevamente.",
                            isApiKeyValid = false,
                            isLoading = false
                        )
                    }
                    return@launch
                } catch (e: Exception) {
                    // Si falla la red, seguimos con datos locales
                }

                // Cargar datos de Room usando el flujo combinado
                questionRepository.getQuestionsWithOptions().collect { dataList ->
                    val questionList = dataList.map { it.first }
                    val optionsMap = dataList.associate { it.first.id to it.second }

                    _uiState.update { state ->
                        state.copy(
                            questions = questionList,
                            optionsMap = optionsMap,
                            isLoading = false,
                            canSubmit = state.selectedOptions.size == questionList.size && questionList.isNotEmpty()
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al cargar preguntas: ${e.message}"
                    )
                }
            }
        }
    }

    fun selectOption(questionId: Int, optionId: Int) {
        _uiState.update { state ->
            val newSelected = state.selectedOptions.toMutableMap()
            newSelected[questionId] = optionId

            state.copy(
                selectedOptions = newSelected,
                canSubmit = newSelected.size == state.questions.size && state.questions.isNotEmpty()
            )
        }
    }

    fun submitVotes(onSuccess: (Int) -> Unit) {
        if (apiKey.isEmpty()) {
            _uiState.update {
                it.copy(
                    submitError = "API key no disponible",
                    isApiKeyValid = false
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, submitError = null) }

            try {
                val votes = _uiState.value.selectedOptions.map { (questionId, optionId) ->
                    VoteItem(questionId, optionId)
                }

                val response = voteRepository.submitVotes(apiKey, votes)

                // Write-through: actualizar local solo si la API responde bien
                response.updated.forEach { updated ->
                    questionRepository.updateOptionVotes(
                        optionId = updated.id,
                        newVotes = updated.votes
                    )
                }

                val firstQuestionId = _uiState.value.questions.firstOrNull()?.id ?: 0

                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        canSubmit = false,
                        submitError = null
                    )
                }

                onSuccess(firstQuestionId)

            } catch (e: ApiException.Unauthorized) {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        submitError = "API key inválida. Regístrate nuevamente.",
                        isApiKeyValid = false
                    )
                }
            } catch (e: ApiException.BadRequest) {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        submitError = "Error en el voto: ${e.message}"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        submitError = "Error de red: ${e.message}"
                    )
                }
            }
        }
    }

    fun resetApiKey() {
        viewModelScope.launch {
            apiKeyManager.clearApiKey()
            apiKey = ""
            _uiState.update {
                it.copy(
                    isApiKeyValid = false,
                    error = "API key eliminada. Por favor regístrate.",
                    questions = emptyList(),
                    optionsMap = emptyMap(),
                    selectedOptions = emptyMap(),
                    canSubmit = false
                )
            }
        }
    }
}