package com.deuxvelva.tirtawesi.controller

import android.content.Context
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.deuxvelva.tirtawesi.R
import com.deuxvelva.tirtawesi.dialog.DialogKonfirmasiPembayaran
import com.deuxvelva.tirtawesi.enum.PaymentMethod
import com.deuxvelva.tirtawesi.model.*
import com.deuxvelva.tirtawesi.tools.log
import com.deuxvelva.tirtawesi.tools.simpleDate
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import java.util.*
import kotlin.collections.HashMap
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.deuxvelva.tirtawesi.adapter.PaymentListAdapter

import com.firebase.ui.auth.AuthUI.getApplicationContext



class PaymentController(var context: Context,
                        var btnPembayaran: Button,
                        var rvPaymentList: RecyclerView) {

    private val wargaData = WargaModel()
    var paymentModel: PaymentModel = PaymentModel()
    var tagihanModel: TagihanModel = TagihanModel()
    var paymentListener: PaymentListener? = null

    private val query = PaymentModel().query
    private val response = FirestoreRecyclerOptions.Builder<PaymentItemModel>()
            .setQuery(query!!, PaymentItemModel::class.java)
            .build()

    private val adapter = PaymentListAdapter(response)

    init {
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvPaymentList.layoutManager = linearLayoutManager
        rvPaymentList.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        adapter.notifyDataSetChanged()
        rvPaymentList.adapter = adapter


        btnPembayaran.setOnClickListener {
            val dialog = DialogKonfirmasiPembayaran(context, object : DialogKonfirmasiPembayaran.Listener {
                override fun onPositiveClick(jmlBayar: Int, paymentMethod: PaymentMethod) {
                    if (jmlBayar > 0) {
                        savePembayaran(jmlBayar, paymentMethod)
                    } else {
                        Toast.makeText(context, "Jumlah pembayaran tidak valid", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onNegativeClick() {

                }
            })

            dialog.jmlTagihan = tagihanModel.bill()
            dialog.showDialog()
        }
    }

    fun startListening(){
        adapter.startListening()
    }

    fun stopListening(){
        adapter.stopListening()
    }


    fun savePembayaran(jmlBayar: Int, paymentMethod: PaymentMethod){
        val data: HashMap<String, Any> = HashMap()
        data["tanggal"] = Date()
        data["uid"] = this.wargaData.uid()
        data["jmlBayar"] = jmlBayar
        data["previous_meter"] = wargaData.meterPaid()
        data["paid_meter"] = tagihanModel.runningMeter()
        data["usage"] = tagihanModel.usage()
        data["payment_method"] = paymentMethod.ordinal
        log("btnPembayaran click $data")

        paymentModel.add(data, object : PaymentModel.SaveListener{
            override fun onSaveCompleted(doc: DocumentReference) {
                wargaData.update("paid_meter", tagihanModel.runningMeter())
                if (paymentListener != null) {
                    paymentListener!!.onSaveCompleted()
                }

            }

            override fun onSaveFailure(exception: Exception) {
                log("Save data failure $exception")
            }
        })
    }

    interface PaymentListener{
        fun onSaveCompleted()
    }


}