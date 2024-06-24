
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.Image
import android.os.Vibrator
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.camera.core.AspectRatio
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.signify.presentation.TextRecognitionAnalyzer
import com.example.signify.ui.HomeUiState
import com.example.signify.ui.LoadingAnimation
import com.example.signify.ui.LookBitmapsViewModel
import com.example.signify.ui.LookViewModel
import com.example.signify.ui.PulsatingCircles
import com.example.signify.ui.TTSViewmodel
import com.example.signify.ui.vibrateOnTextChange
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

@Composable
fun TextRecognitionScreen(applicationContext: Context) {
    CameraContent(applicationContext)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CameraContent(applicationContext: Context) {
    val ttsViewmodel=viewModel<TTSViewmodel>()
    val context: Context = LocalContext.current
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val cameraController: LifecycleCameraController = remember { LifecycleCameraController(context) }
    var detectedText: String by remember { mutableStateOf("No text detected yet..") }

    fun onTextUpdated(updatedText: String) {
        detectedText = updatedText
    }
    val viewModel= viewModel<LookBitmapsViewModel>()
    val bitmaps by viewModel.bitmaps.collectAsState()
    LaunchedEffect(Unit )
    {
        ttsViewmodel.onTextFieldValueChange("Text Detection. Double tap for Instructions.")
        ttsViewmodel.textToSpeech(context)
    }
    var progress by remember {
        mutableStateOf(false)
    }
    val vibrator = context.getSystemService(Vibrator::class.java)


 //   val vibrator = ContextCompat.getSystemServiceAmbient<Vibrator>()


    DisposableEffect(Unit) {
        onDispose {
            // Call the stopTextToSpeech method when the screen is no longer visible
ttsViewmodel.stopTextToSpeech()
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {},
    ) { paddingValues: PaddingValues ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.BottomCenter
        ) {

            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                factory = { context ->

                    PreviewView(context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        setBackgroundColor(Color.BLACK)
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        scaleType = PreviewView.ScaleType.FILL_START}
                    .also { previewView ->
                        startTextRecognition(
                            context = context,
                            cameraController = cameraController,
                            lifecycleOwner = lifecycleOwner,
                            previewView = previewView,
                            onDetectedTextUpdated = ::onTextUpdated
                        )
                    }
                }
            )
            val viewModel2=viewModel<LookViewModel>()
            val uiState=viewModel2.uiState.collectAsState().value
          when(uiState)
           {
               is HomeUiState.Success-> {
                   detectedText=uiState.outputText


               }
               is HomeUiState.Loading->
               {
                  detectedText="Loading"

               }
               is HomeUiState.Error->
               {
                   detectedText=uiState.error
               }
               else->
               {

               }




           }
            val scope= rememberCoroutineScope()
Box(modifier= Modifier
    .background(androidx.compose.ui.graphics.Color.Black)
    .fillMaxWidth()
    .fillMaxHeight(0.5f)
    .pointerInput(Unit) {
        detectTapGestures(onDoubleTap = {
            ttsViewmodel.onTextFieldValueChange("Let me read out anything for you. Long Press for reading out. Secondly ,For simple text, Draw a vertical line for getting Text response whereas for Complex Text, Draw a horizontal line for getting Text response ")
            ttsViewmodel.textToSpeech(context = context)
            vibrateOnTextChange(vibrator)
        },
            onLongPress = {
                scope.launch {
                    viewModel.removePhoto()

                    takeTextPhoto(
                        controller = cameraController,
                        applicationContext,
                        onDetectedTextUpdated = ::onTextUpdated,
                        onPhotoTaken = viewModel::onTakePhoto
                    )
                    vibrateOnTextChange(vibrator)

                    delay(1000)
                    val bitmap: Bitmap? = BitmapFactory.decodeByteArray(
                        viewModel.bitmaps.value[0],
                        0,
                        viewModel.bitmaps.value[0].size
                    )
                    Log.d("MainActivity", viewModel.bitmaps.value.size.toString())
               //     processImage(bitmap!!, { it -> detectedText = it }, 90)
                    scope.launch {
                    var output=    viewModel2.questioning("I'm blind please Read out the text in the image meaningfully in proper sequence",viewModel.bitmaps.value)
                       Log.d("Mainede",output)
                        ttsViewmodel.onTextFieldValueChange(output)
                        ttsViewmodel.textToSpeech(context)
                    }

                }
            }
        ) {

        }


    }
    .pointerInput(Unit) {
        detectVerticalDragGestures(onDragEnd = {
            Log.d("MainActivity", "MovingV")
            ttsViewmodel.onTextFieldValueChange(detectedText)
            ttsViewmodel.textToSpeech(context)
            vibrateOnTextChange(vibrator)

        }) { change, dragAmount ->

        }
    }
    .pointerInput(Unit) {
        detectHorizontalDragGestures(onDragEnd = {
            Log.d("MainActivity", "MovingH")
            ttsViewmodel.justSpeech("Uploading Image on Ai server", context = context)
            vibrateOnTextChange(vibrator)

            scope.launch {
                viewModel2.questioning(
                    "Im Blind ,i can't read please read every text presented on the image exactly as it is: Please make it lengthy so i can know everything there in the page. If there are complex equations tell me in words also: ",
                    bitmaps
                )
            }
        }) { change, dragAmount -> }

    }

){    when(uiState)
{
    is HomeUiState.Success-> {
        //detectedText=uiState.outputText
        Box(modifier=Modifier.align(Alignment.Center)){
            PulsatingCircles(text = "ReRead")
        }

    }
    is HomeUiState.Loading->
    {
        //detectedText="Loading"
        LoadingAnimation(modifier = Modifier.align(Alignment.Center))
        LaunchedEffect(Unit) {
            ttsViewmodel.justSpeech("Please wait .", context = context)

            while (uiState is HomeUiState.Loading) {
                vibrateOnTextChange(vibrator)
                delay(300)
            }
        }
    }
    is HomeUiState.Error->
    {
        Box(modifier=Modifier.align(Alignment.Center)){
            PulsatingCircles(text = "ReRead")}
       // detectedText=uiState.error
    }
    else->
    {
        Box(modifier=Modifier.align(Alignment.Center)){
            PulsatingCircles(text = "Read")}
    }




}
    //    Column {
//            Row() {
//                Button(onClick = {
//                    Log.d("MainActivity", "pressed")
//                    vibrateOnTextChange(vibrator)
//
//                    takeTextPhoto(
//                        controller = cameraController,
//                        applicationContext,
//                        onDetectedTextUpdated = ::onTextUpdated,
//                        onPhotoTaken = viewModel::onTakePhoto
//                    )
//                }) {
//                    Text("press")
//                }
//                Button(onClick = {
//                    Log.d("MainActivity", "pressed")
//                    ttsViewmodel.onTextFieldValueChange(detectedText)
//                    ttsViewmodel.textToSpeech(context)
//                }) {
//                    Text("Speak")
//                }
//                Button(onClick = {
//                    ttsViewmodel.stopTextToSpeech()
//                }) {
//                    Text("Stop Speak")
//                }
//            }
//
//                val scrollState= rememberScrollState()
//                Button(onClick = {
//                    scope.launch {
//                        viewModel2.questioning("I'm blind please Read out the text in the image like a text reader:",viewModel.bitmaps.value)
//                    }
//                }){
//                    Text("Send to Gemini")
//                }
//            Text(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(androidx.compose.ui.graphics.Color.White)
//                    .padding(16.dp)
//                    .height(50.dp)
//                    .verticalScroll(scrollState)
//                ,
//                text = detectedText,
//            )
  //          }
        }
    }}
}

private fun startTextRecognition(
    context: Context,
    cameraController: LifecycleCameraController,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    onDetectedTextUpdated: (String) -> Unit
) {
Log.d("MainActivity","startingText")
    cameraController.imageAnalysisTargetSize = CameraController.OutputSize(AspectRatio.RATIO_16_9)
//    cameraController.setImageAnalysisAnalyzer(
//        ContextCompat.getMainExecutor(context),
//        TextRecognitionAnalyzer(onDetectedTextUpdated = onDetectedTextUpdated)
//    )

    cameraController.bindToLifecycle(lifecycleOwner)
    previewView.controller = cameraController
}
fun takeTextPhoto(controller: LifecycleCameraController, applicationContext:Context,
              onDetectedTextUpdated: (String) -> Unit,onPhotoTaken:(ByteArray)->Unit
)
{

    controller.takePicture(
        ContextCompat.getMainExecutor(applicationContext),
        object: ImageCapture.OnImageCapturedCallback(){
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)

               val bitmap = image.toBitmap()
           //   val byteArray= compressBitmap(bitmap)
                    //,image.height,image.width)
          //            onPhotoTaken(byteArray)

                val byteArray= compressBitmap(bitmap)
                onPhotoTaken(byteArray)
                image.close()
                Log.d("MainActivity2",image.imageInfo.rotationDegrees.toString())
            }
            override fun onError(exception: ImageCaptureException){
                super.onError(exception)
                Log.d("Camera","Couldnt take pricture")
            }



        }

    )
}
//private fun compressBitmap(bitmap: Bitmap): ByteArray {
//    val stream = ByteArrayOutputStream()
//    //  val stream2= ByteArrayOutputStream()
////   bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream2)
////    val byteArray2 = stream2.toByteArray()
////    Log.d("MainActivity2",byteArray2.size.toString())
//    bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream) // Adjust quality (50 in this case)
//    val byteArray = stream.toByteArray()
//    //  Log.d("MainActivity2",byteArray.size.toString())
//    bitmap.recycle() // Recycle the Bitmap to free up memory
//    return byteArray
//}
fun processImage(bitmap: Bitmap,onDetectedTextUpdated: (String) -> Unit,rotation:Int) {
    val inputImage = InputImage.fromBitmap(bitmap, rotation)

    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    recognizer.process(inputImage)
        .addOnSuccessListener { visionText ->
            val detectedText = StringBuilder()

            for (textBlock in visionText.textBlocks) {
                val trimmedText = textBlock.text.trim()
                if (trimmedText.isNotEmpty()) {
                    detectedText.append(trimmedText)
                    detectedText.append(" ")
                }
            }

            val concatenatedText = detectedText.toString().trim()
val text=concatenatedText.replace("\n"," ")
            Log.d("MainActivity", "ggg"+text)

// Handle the concatenated text
            onDetectedTextUpdated(text)
        }
        .addOnFailureListener { e ->
            // Handle failures
            Log.e("TextRecognition", "Text recognition failed: ${e.message}", e)
        }
}
private fun compressBitmap(bitmap: Bitmap): ByteArray {
    val stream = ByteArrayOutputStream()
    //  val stream2= ByteArrayOutputStream()
//   bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream2)
//    val byteArray2 = stream2.toByteArray()
//    Log.d("MainActivity2",byteArray2.size.toString())
    bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream) // Adjust quality (50 in this case)
    val byteArray = stream.toByteArray()
    //  Log.d("MainActivity2",byteArray.size.toString())
    bitmap.recycle() // Recycle the Bitmap to free up memory
    return byteArray
}