package com.example.signify.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class STTViewModel: ViewModel() {

    var state by mutableStateOf(STTScreenState())
        private set

    fun changeTextValue(text:String){
        viewModelScope.launch {
            //val newText=state.text+" "+text
            state = state.copy(
                text = text
            )
        }
    }
}