package com.deuxvelva.tirtawesi.controller

import android.widget.TextView
import com.deuxvelva.tirtawesi.model.WargaModel
import com.deuxvelva.tirtawesi.tools.log

class WargaController(
                      var tvKavling: TextView,
                      var tvName: TextView,
                      var tvMeterPaid: TextView
                      ){

    private val wargaData = WargaModel(listener = object: WargaModel.Listener{
        override fun onDataChanged() {
            updateUI()
        }
    })

    init {
//        log("wargadoc ${wargaData.userExists()}")
//        if (!wargaData.userExists()) {
//
//        }
    }


    fun updateUI(){
        tvKavling.text = wargaData.kavling()
        tvName.text = wargaData.nama()
        tvMeterPaid.text = wargaData.meterPaid().toString()
    }
}