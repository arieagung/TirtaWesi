package com.deuxvelva.tirtawesi

import android.app.Application
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.FontAwesomeIcons
import com.joanzapata.iconify.fonts.FontAwesomeModule

class TirtaWesi: Application(){
    override fun onCreate() {
        super.onCreate()
        Iconify.with(FontAwesomeModule())

    }
}