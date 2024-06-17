package com.example.signify.ui


import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class LookBitmapsViewModel: ViewModel() {
    private val _bitmaps= MutableStateFlow<List<ByteArray>>(emptyList())
    val bitmaps=_bitmaps.asStateFlow()

    fun onTakePhoto(bitmap:ByteArray){
    _bitmaps.value+=bitmap}
    fun removePhoto()
    {
        _bitmaps.value= emptyList()
    }
}

