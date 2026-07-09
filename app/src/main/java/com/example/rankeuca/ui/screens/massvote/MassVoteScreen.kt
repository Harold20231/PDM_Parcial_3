package com.example.rankeuca.ui.screens.massvote

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rankeuca.data.database.entities.OptionEntity
import com.example.rankeuca.data.database.entities.QuestionEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MassVoteScreen(
    onVoteSuccess: (Int) -> Unit,
    onBack: () -> Unit,
    onRegisterClick: () -> Unit,
    viewModel: MassVoteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadQuestions()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Voto Masivo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    !uiState.isApiKeyValid && uiState.questions.isEmpty() -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "No hay API key disponible",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Por favor regístrate para obtener una API key",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = onRegisterClick) {
                                Text("Registrarse")
                            }
                        }
                    }

                    uiState.isLoading && uiState.questions.isEmpty() -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Cargando preguntas...")
                        }
                    }

                    uiState.error != null && uiState.questions.isEmpty() -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Error: ${uiState.error}",
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.loadQuestions() }) {
                                Text("Reintentar")
                            }
                            if (!uiState.isApiKeyValid) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(onClick = onRegisterClick) {
                                    Text("Registrarse")
                                }
                            }
                        }
                    }

                    else -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = "Responde cada pregunta",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                uiState.questions.forEach { question ->
                                    val options = uiState.optionsMap[question.id] ?: emptyList()
                                    QuestionCard(
                                        question = question,
                                        options = options,
                                        selectedOptionId = uiState.selectedOptions[question.id],
                                        onOptionSelected = { optionId ->
                                            viewModel.selectOption(question.id, optionId)
                                        }
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    viewModel.submitVotes(
                                        onSuccess = { firstQuestionId -> onVoteSuccess(firstQuestionId) }
                                    )
                                },
                                enabled = uiState.canSubmit && !uiState.isSubmitting,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (uiState.isSubmitting) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.height(24.dp),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                } else {
                                    Text("Votar")
                                }
                            }

                            if (uiState.submitError != null) {
                                Text(
                                    text = uiState.submitError!!,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }

                            if (!uiState.isApiKeyValid) {
                                Text(
                                    text = "API key inválida. Regístrate nuevamente.",
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                                Button(
                                    onClick = onRegisterClick,
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Text("Registrarse")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionCard(
    question: QuestionEntity,
    options: List<OptionEntity>,
    selectedOptionId: Int?,
    onOptionSelected: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = question.text,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            options.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedOptionId == option.id,
                        onClick = { onOptionSelected(option.id) }
                    )
                    Text(
                        text = option.value,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}