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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.launch
import javax.inject.Singleton
import kotlin.coroutines.coroutineContext

@Composable
fun Discuss(applicationContext: Context,viewModel: DiscussViewModel= androidx.lifecycle.viewmodel.compose.viewModel()){


    val appUiState=viewModel.uiState.collectAsState()
    val coroutineScope= rememberCoroutineScope()

    val sttViewModel=viewModel<STTViewModel>()




    DiscussScreen(applicationContext = applicationContext,appUiState.value){inputText->
        coroutineScope.launch {

            if(sttViewModel.state.text!=null){

                     viewModel.questioning(sttViewModel.state.text!!)


            }else
            {

            }

        }



    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscussScreen(applicationContext: Context, uiState: DiscussUiState=DiscussUiState.Loading, onSendClicked:(String)->Unit) {

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

    val speechRecognizerLauncher= rememberLauncherForActivityResult(contract = SpeechRecognizerContract(), onResult ={
        sttViewModel.changeTextValue(it.toString())
    } )
//    val pickMediaLauncher= rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()){
//   it?.let{
//        imageUris.add(it)
//    }}
    val context= LocalContext.current

    LaunchedEffect(Unit)
    {
        ttsViewmodel.onTextFieldValueChange("Discuss Screen. Double tap for instructions")
        ttsViewmodel.textToSpeech(context = context)

    }
    DisposableEffect(Unit )
    {
        onDispose {
            ttsViewmodel.stopTextToSpeech()
        }
    }




    Column {

//     TextField(value =detectedText, onValueChange ={
//          ttsViewmodel.onTextFieldValueChange(detectedText)
//      } )
        TextField(value = texton, onValueChange ={it->
            texton=it
            sttViewModel.changeTextValue(it)

        } )
        val vibrator = context.getSystemService(Vibrator::class.java)
        Box(modifier = Modifier
            .background(Color.Black)
            .fillMaxWidth()
            .fillMaxHeight(0.7f)


            .pointerInput(Unit) {
                detectTapGestures(onDoubleTap = {

                    ttsViewmodel.onTextFieldValueChange("Long Press for Recording Audio. Draw a horizontal line for generating your output. Draw a vertical I for getting the audio Answer")
                    ttsViewmodel.textToSpeech(context)


                },
                    onLongPress = {
                        ttsViewmodel.justSpeech("Recording Audio", context = context)
                        vibrateOnTextChange(vibrator)

                        speechRecognizerLauncher.launch(Unit)
                    })

            }


//                detectTransformGestures { _, _, _, numberOfPointers ->
//                   Log.d("MainActivity2",numberOfPointers.toString())
//                }


            .pointerInput(Unit) {

                detectHorizontalDragGestures(onDragEnd = {
                    ttsViewmodel.justSpeech(
                        "Generating Output.Draw a vertical I for getting audio Response",
                        context = context
                    )

                    onSendClicked("")
                }) { change, dragAmount -> }
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures(onDragEnd = {
                    vibrateOnTextChange(vibrator)

                    ttsViewmodel.onTextFieldValueChange(detectedText)
                    ttsViewmodel.textToSpeech(context)

                }) { change, dragAmount -> }
            }
        )
        {

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
Box(modifier=Modifier.fillMaxWidth().fillMaxHeight().pointerInput(Unit){
    detectTapGestures(onDoubleTap = {
        vibrateOnTextChange(vibrator)

        ttsViewmodel.stopTextToSpeech()})
}){
            Button(onClick = {
                onSendClicked("")
            })
            {Text("send")}
            Button(onClick = {
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

                ttsViewmodel.onTextFieldValueChange(detectedText)
                ttsViewmodel.textToSpeech(context = context)
            })
            {Text("Speak")}


            when(uiState)
            {
                is DiscussUiState.Loading->{
                    Box {
                        CircularProgressIndicator()
                        detectedText="Loading, Please wait"
                    }
                }
                is DiscussUiState.Success->{
                    val scrollState= rememberScrollState()
                    Card(modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .verticalScroll(scrollState),shape=MaterialTheme.shapes.large){
                        Text(text = uiState.outputText)
                        Log.d("MainActivity2",uiState.outputText)
                        detectedText=uiState.outputText
                    }

                }
                is DiscussUiState.Error->{
                    Log.d("Mainactivity","error")
                    detectedText= uiState.error
                }
                else->
                {Text("Starting Point")

                }
            }


        }}
}
