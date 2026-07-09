package com.example.rankeuca.ui.screens.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MenuScreen(
    onMassVoteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "RankeUCA",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "Vota por tus favoritos",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onMassVoteClick,
            modifier = Modifier
        ) {
            Text("Voto Masivo")
        }
        Button(
            onClick = onMassVoteClick,
            modifier = Modifier
        ) {
            Text("Registro")
        }
        Button(
            onClick = onMassVoteClick,
            modifier = Modifier
        ) {
            Text("Results")
        }
    }
}