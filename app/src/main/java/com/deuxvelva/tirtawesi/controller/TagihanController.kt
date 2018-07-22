package com.deuxvelva.tirtawesi.controller

import android.widget.TextView
import com.deuxvelva.tirtawesi.model.MeterModel
import com.deuxvelva.tirtawesi.tools.currentMonth
import com.deuxvelva.tirtawesi.tools.monthName
import com.deuxvelva.tirtawesi.tools.todayDate
import com.deuxvelva.tirtawesi.tools.log
import com.deuxvelva.tirtawesi.model.PaymentModel
import com.deuxvelva.tirtawesi.model.TagihanModel
import com.deuxvelva.tirtawesi.model.WargaModel


class TagihanController(

        var tvBulanTagihan:TextView,
        var tvPemakaian: TextView,
        var tvTagihan: TextView){

    var DUE_DATE: Int = 15
    var paymentData: PaymentModel? = null
    var dataTagihan: TagihanModel? = null


    init {
        dataTagihan = TagihanModel(object : TagihanModel.DataListener{
            override fun onDataChanged() {
                updateUI()
            }
        })
    }


    private fun bulanTagihan(): Int{
        val currentMonth = currentMonth()
        val nextMonth = currentMonth() + 1
        val todayDate = todayDate()

        if (paymentData != null) {
            log("Last paid Invoice month ${paymentData!!.lastPaidDate()} ${paymentData!!.month()}")
            if (todayDate <= DUE_DATE ) {
                if (paymentData!!.month() == currentMonth) {
                    return nextMonth
                }
            } else {
                return nextMonth
            }
        } else {
            if (todayDate > DUE_DATE) {
                return nextMonth
            }
        }

        return currentMonth
    }

    fun updateUI(){
        tvBulanTagihan.text = monthName(bulanTagihan()).toUpperCase()
        tvPemakaian.text = dataTagihan!!.usage().toString()
        tvTagihan.text = dataTagihan!!.bill().toString()
    }
}