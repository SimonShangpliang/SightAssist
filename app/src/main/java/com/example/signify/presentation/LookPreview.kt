package com.example.signify.presentation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.util.Size
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream

@Composable
fun LookPreview(
    controller:LifecycleCameraController,
    modifier: Modifier=Modifier
){
    val lifecycleOwner= LocalLifecycleOwner.current
    AndroidView(factory = {
        PreviewView(it).apply{
            this.controller=controller
            controller.bindToLifecycle(lifecycleOwner)
        }
    },
        modifier=modifier
//            .pointerInput(Unit) {
//            detectTapGestures(onDoubleTap = {
//                setCapture.invoke(false)
//            })
//        }
    )
}

fun takePhoto(controller: LifecycleCameraController, applicationContext:Context,onPhotoTaken:(ByteArray)->Unit)
{

    controller.takePicture(
        ContextCompat.getMainExecutor(applicationContext),
        object:OnImageCapturedCallback(){
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)

                val bitmap = image.toBitmap()
               val byteArray= compressBitmap(bitmap)
                onPhotoTaken(byteArray)
                Log.d("MainActivity2",bitmap.toString())
            }
            override fun onError(exception:ImageCaptureException){
                super.onError(exception)
                Log.d("Camera","Couldnt take pricture")
            }



        }

    )
}
//  val stream2= ByteArrayOutputStream()
//   bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream2)
//    val byteArray2 = stream2.toByteArray()
//    Log.d("MainActivity2",byteArray2.size.toString())
private fun compressBitmap(bitmap: Bitmap): ByteArray {
    val stream = ByteArrayOutputStream()

    bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream) // Adjust quality (50 in this case)
    val byteArray = stream.toByteArray()
    bitmap.recycle() // Recycle the Bitmap to free up memory
    return byteArray
}
fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
}
