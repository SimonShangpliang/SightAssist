package com.example.signify.presentation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Vibrator
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.signify.data.TfLiteSignClassifier
import com.example.signify.domain.Classification
import com.example.signify.ui.TTSViewmodel
import com.example.signify.ui.theme.SignifyTheme
import com.example.signify.ui.vibrateOnTextChange

@Composable
fun CurrencyScreen (applicationContext: Context){


    var startCapture by remember {
        mutableStateOf(true)
    }
    var setCapture:(Boolean)->Unit={
        startCapture=it
    }
    val context= LocalContext.current
    val ttsViewmodel=viewModel<TTSViewmodel>()
    val vibrator = context.getSystemService(Vibrator::class.java)

    DisposableEffect(Unit) {
        onDispose {
            // Call the stopTextToSpeech method when the screen is no longer visible
            ttsViewmodel.stopTextToSpeech()
        }
    }
    LaunchedEffect(Unit)
    {
ttsViewmodel.onTextFieldValueChange("Currency Detection Screen. Double Tap for instructions.")
        ttsViewmodel.textToSpeech(context = context)
    }
    Surface(modifier= Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
//        detectTapGestures(onDoubleTap = {
//            startCapture=true
//
//        }


        }) {
        SignifyTheme {


                var classifications by remember {
                    mutableStateOf(emptyList<Classification>())
                }
//
              var analyzer = remember {
                  LanguageImageAnalyzer(
                      classifier = TfLiteSignClassifier(
                          context = applicationContext
                      ), onResults = {
                          classifications = it
                      },
                     startCapture
                  )
              }

              val controller = remember {
                  LifecycleCameraController(applicationContext).apply {

                      setEnabledUseCases(CameraController.IMAGE_ANALYSIS or CameraController.IMAGE_CAPTURE)
                      setImageAnalysisAnalyzer(
                          ContextCompat.getMainExecutor(applicationContext),
                          analyzer
                      )
                  }
              }
            var capturedImage: ImageProxy? by remember { mutableStateOf(null) }

            //val classifier = TfLiteSignClassifier(context = applicationContext)
//            val analyzer = LanguageImageAnalyzer(classifier = classifier, onResults = {
//                          classifications = it
//                      },
//                     startCapture)

                // A surface container using the 'background' color from the theme

                Box(modifier = Modifier.fillMaxSize())
                {
                    CameraPreview(controller, Modifier.fillMaxSize(),setCapture)


                    Box(modifier = Modifier.fillMaxHeight(0.50f).fillMaxWidth().align(Alignment.BottomCenter).background(Color.Black).
                    pointerInput(Unit){
                        detectTapGestures (onDoubleTap = {
                            ttsViewmodel.onTextFieldValueChange("Place Phone and currency at a pencil height distant. Long Press for Result")
                            ttsViewmodel.textToSpeech(context = context)
                        },
                            onLongPress = {
                                classifications.forEach {
                                    ttsViewmodel.onTextFieldValueChange(it.name)}
                                vibrateOnTextChange(vibrator )
                                ttsViewmodel.textToSpeech(context = context)
                            }

                        )

                        { }
                    }

                    ) {
                        // ... UI components and camera preview
                        Column() {

                            Button(onClick = {
                                // Trigger capturing the image
//                                takeCurrPhoto(controller, applicationContext) { it ->
//                                    capturedImage = it
//                                }

                                if(analyzer.cont==true){
                                analyzer.cont=false}
                                else{
                                    analyzer.cont=true
                                }
                                Log.d("MainActivity",analyzer.cont.toString())

                            }) {
                                if(analyzer.cont==true){
                                Text(text = "Stop analyzing")}
                                else
                                {
                                    Text(text = "Start analyzing")

                            }
                            }
                            Button(onClick = {
                                // Trigger capturing the image
                                takeCurrPhoto(controller, applicationContext) { it ->
                                    capturedImage = it
                                }
                            }) {
                                Text(text = "Capture Image")
                            }
                           // var ttsViewmodel=viewmodel<TTSViewmodel>()
                            Button(onClick = {
                                // Trigger capturing the image
ttsViewmodel.textToSpeech(context)
                            }) {
                                Text(text = "Speak")
                            }

                            Button(onClick = {
                                // Trigger capturing the image
                                val image = capturedImage

                                if (capturedImage != null) {
                                    // Perform analysis on the captured image
                                  //  analyzer.analyze(capturedImage!!)
                           //         val results = classifier.classify(image!!.cropTo224x224()!!, image!!.imageInfo.rotationDegrees)
                             //       classifications = results

                                    // Optionally, you can log the results for verification
                               //     Log.d("MainActivity", "Classification results: $results")
                                }
                            }
                            ) {
                                Text(text = "Analyze Image")
                            }
                        }

                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                    ) {
                        capturedImage?.let { imageProxy ->
                            val bitmap: Bitmap? = imageProxy.toBitmap()
                            bitmap?.let {
                                val imageBitmap: ImageBitmap = it.asImageBitmap()
                                Image(
                                    bitmap = imageBitmap,
                                    contentDescription = "Captured Image",
                                    modifier = Modifier
                                )
                            }
                        }

                        classifications.forEach {
                            if (it.score > 0.70f) {
                                Text(
                                    text = it.name,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(8.dp),
                                    textAlign = TextAlign.Center,
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                    }

                }
            }
        }
   // }
}
fun ImageProxy.cropTo600x600(): Bitmap? {
    val bitmap: Bitmap? = this.toBitmap()
    bitmap?.let {
        val width = it.width
        val height = it.height

        val left = (width - 800) / 2
        val top = (height - 800) / 2
        val right = left + 800
        val bottom = top + 800

        // Ensure the cropped area doesn't exceed the bounds of the original image
        val cropLeft = if (left < 0) 0 else left
        val cropTop = if (top < 0) 0 else top
        val cropRight = if (right > width) width else right
        val cropBottom = if (bottom > height) height else bottom

        // Create a 600x600 bitmap from the cropped area without scaling
        return Bitmap.createBitmap(it, cropLeft, cropTop, cropRight - cropLeft, cropBottom - cropTop)
    }
    return null
}




fun ImageProxy.toBitmap(): Bitmap? {
    val buffer = planes[0].buffer
    buffer.rewind()
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}
fun takeCurrPhoto(controller: LifecycleCameraController, applicationContext:Context,onPhotoTaken:(ImageProxy)->Unit)
{

    controller.takePicture(
        ContextCompat.getMainExecutor(applicationContext),
        object: ImageCapture.OnImageCapturedCallback(){
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)

              //  val bitmap = image.toBitmap()
             //   val byteArray= compressBitmap(bitmap)
                onPhotoTaken(image)
               Log.d("MainActivity2",image.imageInfo.rotationDegrees.toString())
            }
            override fun onError(exception: ImageCaptureException){
                super.onError(exception)
                Log.d("Camera","Couldnt take pricture")
            }



        }

    )
}