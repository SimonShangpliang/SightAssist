package com.example.signify.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class DiscussViewModel: ViewModel() {

    private val _uiState: MutableStateFlow<DiscussUiState> = MutableStateFlow(DiscussUiState.Initial)
    val uiState=_uiState.asStateFlow()
    private var generativeModel: GenerativeModel
    init{
        val config= generationConfig {temperature=0.70f  }
        generativeModel= GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = "AIzaSyDxpCRBLAKinrybN5TObHYI8PaSqmGS-vs",
            generationConfig = config
        )
    }
    suspend fun questioning(userInput: String): String {
        _uiState.value = DiscussUiState.Loading
        val prompt = userInput
        var output = ""

        try {
            val content = content {
                text(prompt)
            }

            // Use withContext to execute the code on the IO dispatcher and wait for it to complete
            output = withContext(Dispatchers.IO) {
                generativeModel.generateContent(content).text ?: ""
            }

            _uiState.value = DiscussUiState.Success(output)
            Log.d("MainActivity", "Response done")
        } catch (e: Exception) {
            _uiState.value = DiscussUiState.Error(e.localizedMessage ?: "Error in Generating content")
            Log.d("MainActivity", e.localizedMessage ?: "Error in Generating content")
        }

        Log.d("mainede", output)
        return output
    }
}
sealed interface DiscussUiState{
    object Initial: DiscussUiState
    object Loading: DiscussUiState
    data class Success(val outputText:String): DiscussUiState
    data class Error(val error:String): DiscussUiState

}