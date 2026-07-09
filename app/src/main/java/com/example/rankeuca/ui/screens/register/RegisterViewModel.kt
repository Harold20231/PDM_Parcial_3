package com.example.rankeuca.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rankeuca.data.api.RankeUcaApi
import com.example.rankeuca.utils.ApiKeyManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val api: RankeUcaApi,
    private val apiKeyManager: ApiKeyManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(carnet: String, onSuccess: () -> Unit) {
        if (carnet.isEmpty()) {
            _uiState.update { it.copy(error = "El carnet no puede estar vacío") }
            return
        }
       viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, success = false) }

            try {
                val response = api.register(carnet)
                if (response.ok && response.apiKey != null) {
                    apiKeyManager.saveApiKey(response.apiKey)
                    apiKeyManager.saveCarnet(carnet)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            success = true
                        )
                    }
                    onSuccess()
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = response.message ?: "Error al registrar"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error de red: ${e.message}"
                    )
                }
            }
        }
    }
}