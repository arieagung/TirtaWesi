package com.deuxvelva.tirtawesi.controller

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.alexzaitsev.meternumberpicker.MeterView
import com.deuxvelva.tirtawesi.tools.simpleDate
import com.deuxvelva.tirtawesi.model.MeterModel
import com.deuxvelva.tirtawesi.model.WargaModel
import com.deuxvelva.tirtawesi.tools.log
import java.util.*

class MeterController(
        var context:Context,
        var btnUpdateMeter: Button,
        var meterView: MeterView,
        var tvLastUpdate: TextView){

    var meterListener: MeterListener? = null
    var meterModel: MeterModel =  MeterModel()

    init {
        meterModel.listener = object : MeterModel.DataListener {
            override fun onDataChanged(lastMeter: Int) {
                updateUI()
                if (meterListener != null) {
                    meterListener!!.onMeterChanged(lastMeter)
                }
            }
        }

        btnUpdateMeter.setOnClickListener {
            updateMeter(meterView.value)
        }
    }

    private fun resetMeteran(){
        meterView.value = meterModel.lastMeter()
    }

    private fun updateMeter(value: Int){
        if (meterModel.lastMeter() == value) {
            Toast.makeText(context, "Posisi meteran sama dengan sebelumnya", Toast.LENGTH_LONG).show()

            return
        }

        if (value < meterModel.bottomThreshold ) {
            Toast.makeText(context, "Posisi meteran lebih kecil sebelumnya", Toast.LENGTH_LONG).show()
            resetMeteran()
            return
        }

        meterModel.updateMeter(value, Date())
    }

    @SuppressLint("SetTextI18n")
    fun updateUI(){
        when (meterModel.lastUpdate()){
            null -> tvLastUpdate.visibility = View.GONE
            else -> {
                tvLastUpdate.visibility = View.VISIBLE
                tvLastUpdate.text = "Terakhir update : ${simpleDate(meterModel.lastUpdate()!!)}"
            }
        }
        meterView.value = meterModel.lastMeter()
    }

    interface MeterListener{
        fun onMeterChanged(value:Int)
    }
}