package com.example.signify.presentation

import android.util.Log
import android.util.Size
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.signify.domain.Classification
import com.example.signify.domain.SignClassifier

class  LanguageImageAnalyzer
    (private val classifier:SignClassifier,
     private val onResults:(List<Classification>)->Unit,
     private val startCapture:Boolean
) :ImageAnalysis.Analyzer{
     var cont=true
    private var frameSkipCounter =0

    override fun analyze(image: ImageProxy) {
        if(cont) {
            if (frameSkipCounter % 30 == 0) {

                val rotationDegrees = image.imageInfo.rotationDegrees
                val bitmap = image.toBitmap()
                val results = classifier.classify(bitmap!!, rotationDegrees)
                Log.d("MainActivity", results.toString())
                onResults(results)
            }
            frameSkipCounter++}
            image.close()

    }
}
