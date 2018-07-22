package com.deuxvelva.tirtawesi.tools

import android.util.Log

const val debugActive = true
const val debugTag = "TirtaWesi"

fun log(text: String){
    if (debugActive) Log.d(debugTag, text)
}