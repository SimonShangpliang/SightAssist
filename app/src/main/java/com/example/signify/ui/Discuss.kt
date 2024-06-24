package com.example.signify.ui


import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.signify.presentation.LookPreview
import com.example.signify.presentation.SpeechRecognizerContract
import com.example.signify.presentation.takePhoto
import com.google.accompanist.permissions.rememberPermissionState
import com.spr.jetpack_loading.components.indicators.lineScaleIndicator.LineScaleIndicator
import com.spr.jetpack_loading.enums.PunchType
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Singleton
import kotlin.coroutines.coroutineContext

@Composable
fun Discuss(applicationContext: Context,viewModel: DiscussViewModel= androidx.lifecycle.viewmodel.compose.viewModel()){
    val context = LocalContext.current


    val appUiState=viewModel.uiState.collectAsState()
    val coroutineScope= rememberCoroutineScope()

    val sttViewModel=viewModel<STTViewModel>()
    val ttsViewmodel= viewModel<TTSViewmodel>()




    DiscussScreen(applicationContext = applicationContext,appUiState.value){inputText->
        coroutineScope.launch {

            if(sttViewModel.state.text!=null){

                    var output= viewModel.questioning(sttViewModel.state.text!!)
                Log.d("Mainede",output)
                ttsViewmodel.onTextFieldValueChange(output)
                ttsViewmodel.textToSpeech(context =context)
                //delay(500)


            }else
            {

            }

        }



    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscussScreen(applicationContext: Context, uiState: DiscussUiState=DiscussUiState.Loading, onSendClicked:suspend (String)->Unit) {

//    val imageBitmaps= rememberSaveable(saver=UriCustomSaver()) {
//
//mutableStateListOf()
//    }
    val sttViewModel=viewModel<STTViewModel>()

    val ttsViewmodel= viewModel<TTSViewmodel>()
    var detectedText by remember {
        mutableStateOf("")
    }
    var texton by remember {
        mutableStateOf("")
    }
    var scope = rememberCoroutineScope()
    var isListening by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    val recognizerIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
        }
    }

    LaunchedEffect(Unit) {
        ttsViewmodel.onTextFieldValueChange("Let's discuss anything. Double tap for to start discussing.")
        ttsViewmodel.textToSpeech(context = context)
    }
    //var sendClickedComplete by remember { mutableStateOf(false) } // Track completion
//
//    LaunchedEffect(sendClickedComplete) {
//        if (sendClickedComplete) {
//            when (uiState) {
//                is DiscussUiState.Success -> {
//                    ttsViewmodel.onTextFieldValueChange(uiState.outputText)
//                    ttsViewmodel.textToSpeech(context)
//                }
//                else -> {
//                    ttsViewmodel.onTextFieldValueChange("Please try again")
//                    ttsViewmodel.textToSpeech(context)
//                }
//            }
//            sendClickedComplete = false // Reset completion
//        }
//    }
    DisposableEffect(Unit) {
        onDispose {
            ttsViewmodel.stopTextToSpeech()
            speechRecognizer.destroy()
        }
    }

    val speechRecognizerListener = rememberUpdatedState(object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}
        override fun onError(error: Int) {
            if (isListening) {
                speechRecognizer.startListening(recognizerIntent)
            }
        }

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            matches?.let {
                val text = it.joinToString(separator = " ")
                sttViewModel.changeTextValue(text)
                detectedText +=" "+text
                if (isListening) {
                    speechRecognizer.startListening(recognizerIntent)
                }
                Log.d("mean",text)
            }
        }
        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    })

    DisposableEffect(speechRecognizerListener) {
        speechRecognizer.setRecognitionListener(speechRecognizerListener.value)
        onDispose {}
    }

    val vibrator = context.getSystemService(Vibrator::class.java)

LaunchedEffect(isListening)
{
    while(isListening)
    {Log.d("Main","vibrate")
         vibrateOnTextChange(vibrator)
            delay(300)

    }
}

    Column {

//     TextField(value =detectedText, onValueChange ={
//          ttsViewmodel.onTextFieldValueChange(detectedText)
//      } )
//        TextField(value = texton, onValueChange ={it->
//            texton=it
//            sttViewModel.changeTextValue(it)
//
//        } )
        var recording by remember{ mutableStateOf(false) }
        Box(modifier = Modifier
            .background(Color.Black)
            .fillMaxWidth()
            .fillMaxHeight()


            .pointerInput(Unit) {
                detectTapGestures(
//                    onDoubleTap = {
//
//                    ttsViewmodel.onTextFieldValueChange("Long Press for Recording Audio. Draw a horizontal line for generating your output. Draw a vertical I for getting the audio Answer")
//                    ttsViewmodel.textToSpeech(context)
//
//
//                },
                    onDoubleTap = {
                        if (!isListening) {
                            scope.launch {
                                ttsViewmodel.justSpeech("Recording Audio", context = context)
                                vibrateOnTextChange(vibrator)
                                delay(1000)
                                isListening = true
                                speechRecognizer.startListening(recognizerIntent)
                                recording = true;

                            }
                        } else {
                            scope.launch {

                                vibrateOnTextChange(vibrator)
                                isListening = false
                                speechRecognizer.stopListening()
                                delay(1000)
                                Log.d("Main", detectedText)
                                recording = false;
                                if (detectedText != "") {
                                    onSendClicked(detectedText)
                                    //  sendClickedComplete = true
//                                    when (uiState) {
//                                        is DiscussUiState.Success -> {
//                                            ttsViewmodel.onTextFieldValueChange(uiState.outputText)
//                                            ttsViewmodel.textToSpeech(context)
//                                        }
//
//                                        else -> {
//                                            ttsViewmodel.onTextFieldValueChange("Please try again")
//                                            ttsViewmodel.textToSpeech(context)
//                                        }
                                    //   }
                                } else {

                                }
                            }
                        }
                    })

            }


//                detectTransformGestures { _, _, _, numberOfPointers ->
//                   Log.d("MainActivity2",numberOfPointers.toString())
//                }


//            .pointerInput(Unit) {
//
//                detectHorizontalDragGestures(onDragEnd = {
//                    ttsViewmodel.justSpeech(
//                        "Generating Output.Draw a vertical I for getting audio Response",
//                        context = context
//                    )
//
//                    onSendClicked("")
//                }) { change, dragAmount -> }
//            }
            .pointerInput(Unit) {
                detectVerticalDragGestures(onDragEnd = {
                    vibrateOnTextChange(vibrator)

                    ttsViewmodel.onTextFieldValueChange(detectedText)
                    ttsViewmodel.textToSpeech(context)

                }) { change, dragAmount -> }
            }
        )
        {


            when(uiState)
            {
                is DiscussUiState.Loading->{

                    LoadingAnimation(modifier = Modifier.align(Alignment.Center))
                }
                is DiscussUiState.Success->{
//                    val scrollState= rememberScrollState()
//                    Card(modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp)
//                        .verticalScroll(scrollState),shape=MaterialTheme.shapes.large){
//                        Text(text = uiState.outputText)
//                        Log.d("MainActivity2",uiState.outputText)
//                        detectedText=uiState.outputText
//
//                    }
                    Box(modifier=Modifier.align(Alignment.Center)){
                        PulsatingCircles(text = "Discuss")}

                }
                is DiscussUiState.Error->{
                    Log.d("Mainactivity","error")
                    detectedText= uiState.error
                }
                else-> {
                    if (recording) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .width((5 - 1) * 30.dp)
                                    .height(100.dp).padding(end=70.dp),
                                contentAlignment = Alignment.Center,

                            ) {
                                if (recording) {
                                    LineScaleIndicator(
                                        punchType = PunchType.PULSE_OUT_PUNCH,
                                        distanceOnXAxis = 50f, // Adjust as needed
                                        rectCount = 5,
                                        animationDuration = 500,
                                        lineHeight = 170,
                                        penThickness = 25f


                                    )
                                }
                            }
                        }

                    } else {
                        Box(modifier = Modifier.align(Alignment.Center)) {
//                            if (recording) {
//                                LineScaleIndicator(
//                                    punchType = PunchType.PULSE_OUT_PUNCH,
//                                    distanceOnXAxis = 50f
//                                )
//                            } else {
                                PulsatingCircles(text = "Discuss")
                            //}
                        }
                    }
                }
                }
        }
//        Button(onClick = {
//            Log.d("MainActivity", "here")
//        }
//           // pickMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))}
//
//
//        ){
//            Text("press")
//        }
Box(modifier= Modifier
    .fillMaxWidth()
    .fillMaxHeight()
    .pointerInput(Unit) {
        detectTapGestures(onDoubleTap = {
            vibrateOnTextChange(vibrator)

            ttsViewmodel.stopTextToSpeech()
        })
    }){






        }}
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun DiscussScreen(applicationContext: Context, uiState: DiscussUiState = DiscussUiState.Loading, onSendClicked: (String) -> Unit) {
//    val sttViewModel = viewModel<STTViewModel>()
//    val ttsViewmodel = viewModel<TTSViewmodel>()
//    val coroutineScope = rememberCoroutineScope()
//
//    var detectedText by remember { mutableStateOf("") }
//    var texton by remember { mutableStateOf("") }
//    var isListening by remember { mutableStateOf(false) }
//
//    val context = LocalContext.current
//    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
//    val recognizerIntent = remember {
//        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
//            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
//            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
//        }
//    }
//
//    LaunchedEffect(Unit) {
//        ttsViewmodel.onTextFieldValueChange("Let's discuss anything. Double tap for instructions")
//        ttsViewmodel.textToSpeech(context = context)
//    }
//
//    DisposableEffect(Unit) {
//        onDispose {
//            ttsViewmodel.stopTextToSpeech()
//            speechRecognizer.destroy()
//        }
//    }
//
//    val speechRecognizerListener = rememberUpdatedState(object : RecognitionListener {
//        override fun onReadyForSpeech(params: Bundle?) {}
//        override fun onBeginningOfSpeech() {}
//        override fun onRmsChanged(rmsdB: Float) {}
//        override fun onBufferReceived(buffer: ByteArray?) {}
//        override fun onEndOfSpeech() {}
//        override fun onError(error: Int) {
//            if (isListening) {
//                speechRecognizer.startListening(recognizerIntent)
//            }
//        }
//        override fun onResults(results: Bundle?) {
//            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
//            matches?.let {
//                val text = it.joinToString(separator = " ")
//                sttViewModel.changeTextValue(text)
//                detectedText = text
//                if (isListening) {
//                    speechRecognizer.startListening(recognizerIntent)
//                }
//            }
//        }
//        override fun onPartialResults(partialResults: Bundle?) {}
//        override fun onEvent(eventType: Int, params: Bundle?) {}
//    })
//
//    DisposableEffect(speechRecognizerListener) {
//        speechRecognizer.setRecognitionListener(speechRecognizerListener.value)
//        onDispose {}
//    }
//
//    Column {
//        TextField(value = texton, onValueChange = { it ->
//            texton = it
//            sttViewModel.changeTextValue(it)
//        })
//
//        Box(
//            modifier = Modifier
//                .background(Color.Black)
//                .fillMaxWidth()
//                .fillMaxHeight(0.7f)
//                .padding(16.dp)
//        ) {
//            Column {
//                Button(onClick = {
//                    if (!isListening) {
//                        isListening = true
//                        speechRecognizer.startListening(recognizerIntent)
//                    }
//                }) {
//                    Text("Start Listening")
//                }
//                Button(onClick = {
//                    if (isListening) {
//                        isListening = false
//                        speechRecognizer.stopListening()
//                    }
//                }) {
//                    Text("Stop Listening")
//                }
//                Button(onClick = {
//                    onSendClicked(detectedText)
//                }) {
//                    Text("Send")
//                }
//            }
//        }
//
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .fillMaxHeight()
//        ) {
//            when (uiState) {
//                is DiscussUiState.Loading -> {
//                    Box {
//                        CircularProgressIndicator()
//                        detectedText = "Loading, Please wait"
//                    }
//                }
//                is DiscussUiState.Success -> {
//                    val scrollState = rememberScrollState()
//                    Card(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(16.dp)
//                            .verticalScroll(scrollState),
//                        shape = MaterialTheme.shapes.large
//                    ) {
//                        Text(text = uiState.outputText)
//                        detectedText = uiState.outputText
//                    }
//                }
//                is DiscussUiState.Error -> {
//                    detectedText = uiState.error
//                }
//                else -> {
//                    Text("Starting Point")
//                }
//            }
//        }
//    }
//}