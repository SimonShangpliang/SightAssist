package com.example.signify.ui

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.util.Locale

class TTSViewmodel:ViewModel() {

        private val _state = mutableStateOf(TTSScreenState())
        val state: State<TTSScreenState> = _state
        private  var  textToSpeech: TextToSpeech? = null


        fun onTextFieldValueChange(text:String){
            _state.value = state.value.copy(
                text = text
            )
        }
    fun stopTextToSpeech() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
    }

        fun textToSpeech(context: Context){
            stopTextToSpeech()
            _state.value = state.value.copy(
                isButtonEnabled = false
            )
            textToSpeech = TextToSpeech(
                context
            ) {
                if (it == TextToSpeech.SUCCESS) {
                    textToSpeech?.let { txtToSpeech ->
                        txtToSpeech.language = Locale.US
                        txtToSpeech.setSpeechRate(1.0f)
                        txtToSpeech.speak(
                            _state.value.text,
                            TextToSpeech.QUEUE_ADD,
                            null,
                            null
                        )
                    }
                }
                _state.value = state.value.copy(
                    isButtonEnabled = true
                )
            }
        }
    fun justSpeech(text: String,context:Context){
        stopTextToSpeech()

        textToSpeech = TextToSpeech(
            context
        ) {
            if (it == TextToSpeech.SUCCESS) {
                textToSpeech?.let { txtToSpeech ->
                    txtToSpeech.language = Locale.US
                    txtToSpeech.setSpeechRate(1.0f)
                    txtToSpeech.speak(
                        text,
                        TextToSpeech.QUEUE_ADD,
                        null,
                        null
                    )
                }
            }
            _state.value = state.value.copy(
                isButtonEnabled = true
            )
        }
    }

}