package com.example.signify.ui

import android.os.Vibrator
import android.util.Log
import android.view.WindowInsets.Side
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.signify.presentation.SpeechRecognizerContract
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.spr.jetpack_loading.components.indicators.lineScaleIndicator.LineScaleIndicator
import com.spr.jetpack_loading.enums.PunchType

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(navController: NavController)
{


val permissionState= rememberPermissionState(permission = android.Manifest.permission.RECORD_AUDIO)
    SideEffect {
        permissionState.launchPermissionRequest()
    }
    val context= LocalContext.current
    val gradientBrush = Brush.linearGradient(
        colors = listOf(Color.Blue, Color.Magenta)

    )
val ttsViewmodel= viewModel<TTSViewmodel>()
    val sttViewModel=viewModel<STTViewModel>()
    val speechRecognizerLauncher= rememberLauncherForActivityResult(contract = SpeechRecognizerContract(), onResult ={
        sttViewModel.changeTextValue(it.toString())
    } )
    LaunchedEffect(Unit )
    {
ttsViewmodel.onTextFieldValueChange("Hello There. Double Tap on upper half of screen for speech instructions and bottom half for tap instructions.")
        ttsViewmodel.textToSpeech(context = context
        )
    }
    DisposableEffect(Unit ){
        onDispose {
            ttsViewmodel.stopTextToSpeech()

        }
    }
    val vibrator = context.getSystemService(Vibrator::class.java)
Column() {
    Box(modifier = Modifier
        .fillMaxHeight(0.5f)
        .fillMaxWidth()
        .background(Color.Black)
        .pointerInput(Unit)
        {
            detectTapGestures(

                onLongPress = {
                    speechRecognizerLauncher.launch(Unit)
                    vibrateOnTextChange(vibrator)

                },
                onDoubleTap = {
                    vibrateOnTextChange(vibrator)

                    ttsViewmodel.onTextFieldValueChange(
                        """
   To navigate, say one of the following commands:
        - "Currency" for Currency Detection Screen
        - "Text" for Text Detection Screen
        - "Look"  for Look Screen
        - "Discuss" for Discuss Screen
    """
                    )
                    ttsViewmodel.textToSpeech(context = context)
                })


        }

        .pointerInput(Unit) {
            detectHorizontalDragGestures(onDragEnd = {
                navController.navigate(Screen.CurrencyScreen.route)
                vibrateOnTextChange(vibrator)

            }

            ) { change, dragAmount ->
            }
        }


    ) {
        Column(modifier= Modifier.align(Alignment.Center), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally)
        {
//            LoadingAnimation(modifier=Modifier, circleColor = Color.White)
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .width((5 - 1) * 30.dp)
                        .height(100.dp).padding(end=70.dp),
                    contentAlignment = Alignment.Center,

                    ) {   LineScaleIndicator(
                punchType = PunchType.PULSE_OUT_PUNCH,
                distanceOnXAxis = 50f, // Adjust as needed
                rectCount = 5,
                animationDuration = 500,
                lineHeight = 100,
                penThickness = 25f


            )}}
            Spacer(modifier = Modifier.height(5.dp))
            Text(text="Speech Instructions", style= TextStyle(fontFamily = FontFamily.Monospace ,fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, brush = gradientBrush))

        }


    }
    Box(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .background(Color.White)
        .pointerInput(Unit)
        {
            detectTapGestures(onDoubleTap = {
                vibrateOnTextChange(vibrator)
                ttsViewmodel.onTextFieldValueChange(
                    "-  Follow these tap gestures:\n" +
                            "        - Draw a horizontal line on Upper half of screen for currency detection.\n" +
                            "- Draw a horizontal line on bottom half of screen for Discuss Screen.\n" +
                            "        - Draw a vertical line from top to bottom  on bottom half of screen to access the Look function.\n" +
                            "        - Long press on bottom half of screen to recognize text.\n"

                )

                ttsViewmodel.textToSpeech(context)
            },
                onLongPress = {
                    navController.navigate(Screen.TextRecognitionScreen.route)
                    vibrateOnTextChange(vibrator)
                }
            ) {


            }


        }
        .pointerInput(Unit) {
            detectVerticalDragGestures(onDragEnd = {
                navController.navigate(Screen.TextRecognitionScreen.route)
                vibrateOnTextChange(vibrator)
            }) { change, dragAmount -> }
        }
        .pointerInput(Unit)
        {
            detectHorizontalDragGestures(onDragEnd = {
                navController.navigate(Screen.DiscussScreen.route)
                vibrateOnTextChange(vibrator)
            }) { change, dragAmount -> }
        }
    )

    {
        Column(modifier= Modifier.align(Alignment.Center), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally)
        {
            LoadingAnimation(modifier = Modifier, circleColor = Color.Black)

            Text(
                text = "Tap Instructions",
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    brush = gradientBrush
                )
            )
        }
    }

}
}
//Column() {
////    Button(onClick = {
////        speechRecognizerLauncher.launch(Unit)
////        Log.d("MainActivity", sttViewModel.state.text ?: "NOT there")
////    }) {
////        Text("Record")
////    }
//
//    when {
//        sttViewModel.state.text?.contains("currency", ignoreCase = true) == true -> {
//            sttViewModel.changeTextValue("")
//            navController.navigate(Screen.CurrencyScreen.route)
//            // Code to execute when "currency" is found in the text
//        }
//
//        sttViewModel.state.text?.contains("discuss", ignoreCase = true) == true -> {
//            sttViewModel.changeTextValue("")
//
//            navController.navigate(Screen.DiscussScreen.route)
//            // Code to execute when "discuss" is found in the text
//        }
//
//        sttViewModel.state.text?.contains("look", ignoreCase = true) == true -> {
//            sttViewModel.changeTextValue("")
//
//            navController.navigate(Screen.LookScreen.route)
//            // Code to execute when "look" is found in the text
//        }
//
//        sttViewModel.state.text?.contains("text", ignoreCase = true) == true -> {
//            sttViewModel.changeTextValue("")
//
//            navController.navigate(Screen.TextRecognitionScreen.route)
//            // Code to execute when "text" is found in the text
//        }
//
//        else -> {
//            // Code to execute for other cases
//        }
//    }
////    Button(onClick = {
////        ttsViewmodel.onTextFieldValueChange("Draw a horizontal line for detecting currency. Long Press for recognizing Text. Draw a vertical line from top to bottom to Access Look function")
////        ttsViewmodel.textToSpeech(context = context)
////    }) {
////        Text(text = "Get Instructions")
////    }
////    Button(onClick = {
////        navController.navigate(Screen.DiscussScreen.route)
////    }) {
////        Text(text = "Discuss")
////    }
////    Button(onClick = { navController.navigate(Screen.CurrencyScreen.route)/*TODO*/ }) {
////        Text(text = "Currency")
////    }
////    Button(onClick = { navController.navigate(Screen.TextRecognitionScreen.route) }) {
////        Text(text = "Recognize Text")
////    }
////    Button(onClick = { navController.navigate(Screen.LookScreen.route)/*TODO*/ }) {
////        Text("Look")
////    }
//}
