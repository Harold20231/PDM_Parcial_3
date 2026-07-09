package com.example.rankeuca

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.runtime.LaunchedEffect
import com.example.rankeuca.ui.navigation.RankeUcaNavHost
import com.ejemplo.rankeuca.ui.theme.RankeUcaTheme
import com.example.rankeuca.utils.ApiKeyManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var apiKeyManager: ApiKeyManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LaunchedEffect(Unit) {
                apiKeyManager.saveApiKey("117fe66d-3f04-450d-8e29-dabfdc444fc3")
                apiKeyManager.saveCarnet("0073523")
            }
            RankeUcaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RankeUcaNavHost()
                }
            }
        }
    }
}