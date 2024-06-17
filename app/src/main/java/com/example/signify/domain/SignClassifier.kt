package com.example.signify.domain

import android.graphics.Bitmap

interface SignClassifier {
    fun classify(bitmap: Bitmap,rotation:Int):List<Classification>
}