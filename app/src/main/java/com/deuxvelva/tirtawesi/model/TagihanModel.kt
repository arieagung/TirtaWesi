package com.deuxvelva.tirtawesi.model

import com.deuxvelva.tirtawesi.tools.log

data class TagihanModel(var listener: DataListener? = null) {
    var price: Int = 1000
    private val wargaData = WargaModel()
    private var meterModel = MeterModel()

    init {
        meterModel.listener = object : MeterModel.DataListener{
            override fun onDataChanged(lastMeter: Int) {
                if (listener!=null){
                    listener!!.onDataChanged()
                }
            }
        }
    }

    fun bill():Int {
        log("Bill ${usage()}")
        return price * usage()
    }

    fun usage():Int {
        return meterModel.lastMeter() - wargaData.meterPaid()
    }

    fun runningMeter(): Int{
        return meterModel.lastMeter()
    }

    interface DataListener{
        fun onDataChanged()
    }
}