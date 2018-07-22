package com.deuxvelva.tirtawesi.model

import android.support.v7.widget.RecyclerView
import com.deuxvelva.tirtawesi.controller.FirestoreController
import com.deuxvelva.tirtawesi.tools.getMonthFromDate
import com.deuxvelva.tirtawesi.tools.log
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import android.view.View
import android.widget.TextView
import com.deuxvelva.tirtawesi.R
import com.deuxvelva.tirtawesi.tools.simpleDate
import java.util.*
import kotlin.collections.HashMap
import android.view.LayoutInflater
import android.view.ViewGroup




data class PaymentModel(var dataListener: DataListener? = null){
    private var mPaymentRef: CollectionReference? = FirestoreController().colRef("Invoice")
    private var mInvoiceList: List<DocumentSnapshot>? = null

    var query: Query? = null

    init {
        val wargaData = WargaModel()
        query = FirestoreController().mFireStore
                .collection("Invoice")
                .whereEqualTo("uid", wargaData.uid())
                .orderBy("tanggal", Query.Direction.DESCENDING)
        if (mPaymentRef != null) {

            mPaymentRef!!.whereEqualTo("uid", wargaData.uid())
                    .orderBy("tanggal", Query.Direction.DESCENDING)
                    .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                        if (querySnapshot != null && !querySnapshot.documents.isEmpty()) {

                            mInvoiceList = querySnapshot.documents
                            if (dataListener!=null) {
                                dataListener!!.onDataChanged()
                            }
                        }
                    }
        }
    }

    fun add(data: HashMap<String, Any>, listener: SaveListener){
        if (mPaymentRef != null) {
            mPaymentRef!!.add(data)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            listener.onSaveCompleted(task.result)
                        }
                    }
                    .addOnFailureListener { err ->
                        listener.onSaveFailure(err)
                    }
        }
    }

    fun lastPayment(): DocumentSnapshot?{
        return if (mInvoiceList == null) {
            null
        } else {
            mInvoiceList!![0]
        }
    }

    fun lastPaidUsage(): Int{
        val invoiceDoc = lastPayment()
        return when {
            invoiceDoc?.getLong("paid_usage") == null -> 0
            else -> invoiceDoc.getLong("paid_usage")!!.toInt()
        }
    }

    fun month(): Int{
        val invoiceDoc = lastPayment()
        return when (invoiceDoc) {
            null -> 0
            else -> getMonthFromDate(invoiceDoc.getDate("tanggal")!!)
        }
    }

    fun lastPaidDate():Date? {
        val invoiceDoc = lastPayment()
        return when (invoiceDoc) {
            null -> null
            else -> invoiceDoc.getDate("tanggal")!!
        }
    }

    interface SaveListener{
        fun onSaveCompleted(doc: DocumentReference)
        fun onSaveFailure(exception: Exception)
    }

    interface DataListener{
        fun onDataChanged()
    }
}