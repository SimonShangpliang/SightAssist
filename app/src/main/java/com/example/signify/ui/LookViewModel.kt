package com.example.signify.ui

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.signify.presentation.byteArrayToBitmap
import com.google.ai.client.generativeai.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerationConfig
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch
import java.lang.Exception

class LookViewModel: ViewModel() {

    private val _uiState:MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState.Initial)
     val uiState=_uiState.asStateFlow()
    private var generativeModel: GenerativeModel
init{
   val config= generationConfig {temperature=0.70f  }
 generativeModel=GenerativeModel(
        modelName = "gemini-pro-vision",
        apiKey = "AIzaSyDt-sDiRFzo203g38_safthogeiXCZFTqM",
        generationConfig = config
    )
}
    fun questioning(userInput:String,selectedImages:List<ByteArray>){
     _uiState.value=HomeUiState.Loading
        val prompt=userInput
        viewModelScope.launch(Dispatchers.IO) {
            try {
            val content=content{
                for(bitmap in selectedImages)
                {
                    //image(byteArrayToBitmap(bitmap))
                    blob("image/jpeg",bitmap)
                    }
                text(prompt)
            }
                var output=""
               output+= generativeModel.generateContent(content).text
                _uiState.value =HomeUiState.Success(output)
                Log.d("MainActivity","Response done")

//                generativeModel.generateContentStream(content).collect{
//output+=it.text
//                    _uiState.value =HomeUiState.Success(output)
//                }
                Log.d("MainActivity",output)

            }catch (e:Exception){
_uiState.value=HomeUiState.Error(e.localizedMessage?:"Error in Generating content")
                Log.d("MainActivity",e.localizedMessage)
            }

        }

    }
}
sealed interface HomeUiState{
    object Initial:HomeUiState
    object Loading:HomeUiState
     data class Success(val outputText:String):HomeUiState
    data class Error(val error:String):HomeUiState

}