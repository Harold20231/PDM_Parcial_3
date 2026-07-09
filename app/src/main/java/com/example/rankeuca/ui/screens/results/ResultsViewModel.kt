package com.example.rankeuca.ui.screens.results

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rankeuca.data.database.entities.OptionEntity
import com.example.rankeuca.data.database.entities.QuestionEntity
import com.example.rankeuca.data.repositories.QuestionRepository
import com.example.rankeuca.utils.ApiKeyManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ResultsUiState(
    val question: QuestionEntity? = null,
    val options: List<OptionEntity> = emptyList(),
    val userVotedOptionId: Int? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ResultsViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
    private val apiKeyManager: ApiKeyManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResultsUiState())
    val uiState: StateFlow<ResultsUiState> = _uiState.asStateFlow()

    private var apiKey: String = ""
    private var currentQuestionId: Int = 0
    private var userVotedOptionId: Int? = null

    init {
        loadApiKey()
    }

    private fun loadApiKey() {
        viewModelScope.launch {
            apiKey = apiKeyManager.getApiKey() ?: ""
        }
    }

    fun loadResults(questionId: Int) {
        currentQuestionId = questionId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // Sincronizar con API
                if (apiKey.isNotEmpty()) {
                    try {
                        questionRepository.syncQuestions(apiKey)
                    } catch (e: Exception) {

                    }
                }

                // Cargar datos de Room
                questionRepository.getQuestionsWithOptions().collect { dataList ->
                    val foundData = dataList.find { it.first.id == questionId }
                    if (foundData != null) {
                        val sortedOptions = foundData.second.sortedByDescending { it.votes }
                        _uiState.update { state ->
                            state.copy(
                                question = foundData.first,
                                options = sortedOptions,
                                isLoading = false,
                                userVotedOptionId = userVotedOptionId
                            )
                        }
                    }
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al cargar resultados: ${e.message}"
                    )
                }
            }
        }
    }

    fun setUserVotedOption(optionId: Int) {
        userVotedOptionId = optionId
        _uiState.update { state ->
            state.copy(userVotedOptionId = optionId)
        }
    }

    fun clearState() {
        userVotedOptionId = null
        _uiState.update { ResultsUiState() }
    }
}

