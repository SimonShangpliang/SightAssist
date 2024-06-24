package com.example.signify.ui

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.signify.presentation.LookPreview
import com.example.signify.presentation.SpeechRecognizerContract
import com.example.signify.presentation.takePhoto
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Singleton
import kotlin.coroutines.coroutineContext

@Composable
fun Look(applicationContext: Context,viewModel: LookViewModel= androidx.lifecycle.viewmodel.compose.viewModel()){


    val appUiState=viewModel.uiState.collectAsState()
    val coroutineScope= rememberCoroutineScope()

    val sttViewModel=viewModel<STTViewModel>()


    val ttsViewmodel= viewModel<TTSViewmodel>()
val context= LocalContext.current
    var isListening by remember { mutableStateOf(false) }
    val vibrator = context.getSystemService(Vibrator::class.java)

    LaunchedEffect(isListening)
    {
        while(isListening)
        {Log.d("Main","vibrate")
            vibrateOnTextChange(vibrator)
            delay(300)

        }
    }
    LookScreen(applicationContext = applicationContext,appUiState.value){inputText,selectedItems->
coroutineScope.launch {

if(sttViewModel.state.text==null){
         var output=   viewModel.questioning("Im blind , in a story type way tell me what's in the image? Please describe as much things as possible",selectedItems)

Log.d("Mainede",output)
    ttsViewmodel.onTextFieldValueChange(output)
    ttsViewmodel.textToSpeech(context = context)
        }else
{            viewModel.questioning(sttViewModel.state.text!!,selectedItems)


}

}



    }

}

@Composable
fun LookScreen(applicationContext: Context, uiState: HomeUiState=HomeUiState.Loading, onSendClicked:(String, List<ByteArray>)->Unit) {

//    val imageBitmaps= rememberSaveable(saver=UriCustomSaver()) {
//
//mutableStateListOf()
//    }
    val sttViewModel=viewModel<STTViewModel>()

    val ttsViewmodel= viewModel<TTSViewmodel>()
    var detectedText by remember {
        mutableStateOf("Nothing detected Yet")
    }
    val viewModel= viewModel<LookBitmapsViewModel>()
    val bitmaps by viewModel.bitmaps.collectAsState()

    val speechRecognizerLauncher= rememberLauncherForActivityResult(contract = SpeechRecognizerContract(), onResult ={
        sttViewModel.changeTextValue(it.toString())
    } )
//    val pickMediaLauncher= rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()){
//   it?.let{
//        imageUris.add(it)
//    }}
    val context= LocalContext.current
    var isListening by remember { mutableStateOf(false) }
    val vibrator = context.getSystemService(Vibrator::class.java)

//    LaunchedEffect(isListening)
//    {

//    }
    LaunchedEffect(Unit)
    {
        ttsViewmodel.onTextFieldValueChange("Anything you want to see, i'll describe it for you. Long press for taking photos. Double press for describing the photo")
        ttsViewmodel.textToSpeech(context = context)

    }
    DisposableEffect(Unit )
    {
        onDispose {
            ttsViewmodel.stopTextToSpeech()
        }
    }


    val controller = remember {
        LifecycleCameraController(applicationContext).apply {

            setEnabledUseCases( CameraController.IMAGE_CAPTURE)

        }
    }


    Column {
        LookPreview(controller = controller,
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f) )
//        Button(onClick = {
//            takePhoto(controller, applicationContext = applicationContext,viewModel::onTakePhoto)
//        }) { // Button to take a photo
//            Text("Take Photo")
//        }
        Box(modifier = Modifier
            .background(Color.Black)
            .fillMaxWidth()
            .fillMaxHeight()


            .pointerInput(Unit) {
                detectTapGestures(onDoubleTap = {

//                  ttsViewmodel.onTextFieldValueChange("Long Press on Center for taking Photo. Draw a horizontal line on Center for recording your input question. Draw a vertical I on Center for generating the response answer.Double tap on bottom to get the Audio Response")
//                  ttsViewmodel.textToSpeech(context)
//                    ttsViewmodel.justSpeech("Photo Captured", context = context)
//
//                    takePhoto(
//                        controller,
//                        applicationContext = applicationContext,
//                        viewModel::onTakePhoto
//                    )
//                    vibrateOnTextChange(vibrator)
onSendClicked("",bitmaps)

                },
                    onLongPress = {
                        ttsViewmodel.justSpeech("Photo Captured", context = context)

                        takePhoto(
                            controller,
                            applicationContext = applicationContext,
                            viewModel::onTakePhoto
                        )
                        vibrateOnTextChange(vibrator)


                    })

            }

                
//                detectTransformGestures { _, _, _, numberOfPointers ->
//                   Log.d("MainActivity2",numberOfPointers.toString())
//                }
                

//            .pointerInput(Unit) {
//
//                detectHorizontalDragGestures(onDragEnd = {
//                    ttsViewmodel.justSpeech("Recording Audio", context = context)
//                    vibrateOnTextChange(vibrator)
//
//                    speechRecognizerLauncher.launch(Unit)
//                }) { change, dragAmount -> }
//            }
//            .pointerInput(Unit) {
//                detectVerticalDragGestures(onDragEnd = {
//                    ttsViewmodel.justSpeech("Generating Output. Double Tap on Bottom for audio Response", context = context)
//                    vibrateOnTextChange(vibrator)
//
//                    onSendClicked("", bitmaps)
//                }) { change, dragAmount -> }
//            }
        )
        {
            when(uiState)
            {
                is HomeUiState.Loading->{
                    LoadingAnimation(modifier = Modifier.align(Alignment.Center))
                    LaunchedEffect(Unit) {
                        ttsViewmodel.justSpeech("Please wait while im thinking about the answer.", context = context)

                        while (uiState is HomeUiState.Loading) {
                            vibrateOnTextChange(vibrator)
                            delay(300)
                        }
                    }
                }
                is HomeUiState.Success->{
                    Box(modifier=Modifier.align(Alignment.Center)){
                        PulsatingCircles(text = "ReCapture")}

                }
                is HomeUiState.Error->{
                    Log.d("Mainactivity","error")
                    Box(modifier=Modifier.align(Alignment.Center)){
                        PulsatingCircles(text = "ReCapture")}
                    detectedText= uiState.error
                }
                else->
                { Box(modifier=Modifier.align(Alignment.Center)){
                    PulsatingCircles(text = "Capture")}
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
        Box(modifier=Modifier.fillMaxWidth().fillMaxHeight().background(Color.Blue).pointerInput(Unit){
            detectTapGestures(onDoubleTap = {ttsViewmodel.onTextFieldValueChange(detectedText)
            ttsViewmodel.textToSpeech(context)
            })

        }){
//        Button(onClick = {
//            onSendClicked("",bitmaps)
//        })
//        {Text("send")}
//        Button(onClick = {
//            when(uiState)
//            {
//                is HomeUiState.Success->{
//
//                    ttsViewmodel.onTextFieldValueChange(uiState.outputText)
//ttsViewmodel.textToSpeech(context = context)
//                }
//                else->
//                {
//                    ttsViewmodel.onTextFieldValueChange("Couldnt generate Text, Please Try again")
//
//                }
//
//            }

//               ttsViewmodel.onTextFieldValueChange(detectedText)
//ttsViewmodel.textToSpeech(context = context)
//        })
//        {Text("Speak")}





    }}
}
fun vibrateOnTextChange(vibrator: Vibrator) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Vibrate for 50 milliseconds
        val vibrationEffect = VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(vibrationEffect)
    } else {
        // For devices below Android O, vibrate for 50 milliseconds (no amplitude control)
        vibrator.vibrate(50)
    }
}