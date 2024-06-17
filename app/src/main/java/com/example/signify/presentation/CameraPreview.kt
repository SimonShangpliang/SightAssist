package com.example.signify.presentation

import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun CameraPreview(
    controller:LifecycleCameraController,
    modifier: Modifier=Modifier,
    setCapture:(Boolean)->Unit
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
