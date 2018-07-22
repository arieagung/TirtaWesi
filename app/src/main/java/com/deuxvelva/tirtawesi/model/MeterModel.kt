package com.deuxvelva.tirtawesi.model

import com.deuxvelva.tirtawesi.controller.FirestoreController
import com.deuxvelva.tirtawesi.tools.log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.SetOptions
import java.util.*
import kotlin.collections.HashMap

data class MeterModel(var listener: DataListener? = null){

    private var wargaData: WargaModel = WargaModel()
    private var meterDoc: DocumentSnapshot? = null
    private val LAST_METER_FIELD = "last_update_meter"
    private val LAST_METER_DATE_FIELD = "last_meter_update_date"
    var bottomThreshold: Int = 0

    init {
        log ("MeterModel ${wargaData.uid()}")

        wargaData.listener = object : WargaModel.Listener{
                override fun onDataChanged() {
                    bottomThreshold = wargaData.meterPaid()
                    meterDoc = wargaData.docSnapshot
                    if (listener != null) {
                        listener!!.onDataChanged(lastMeter())
                    }
                }
            }

    }

    private fun setDocumentSnapshot(){

        val meterRef = FirestoreController().docRef("Meteran", wargaData.uid())
        meterRef.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                log("Listen update lastMeter failed $firebaseFirestoreException")
            } else {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    meterDoc = documentSnapshot
                    if (listener != null) {
                        listener!!.onDataChanged(lastMeter())
                    }

                } else {
                    log("Current lastMeter data null")
                }
            }
        }
    }

    fun lastMeter(): Int{
        return when (meterDoc){
            null -> 0
            else -> meterDoc!!.getLong(LAST_METER_FIELD)!!.toInt()
        }
    }

    fun lastUpdate(): Date? {
        return when (meterDoc){
            null -> null
            else -> meterDoc!!.getDate(LAST_METER_DATE_FIELD)
        }
    }

    fun updateMeter(meter: Int, date: Date){
        if (meterDoc != null) {
            val data: HashMap<String, Any> = HashMap()
            val docRef = meterDoc!!.reference

            data[LAST_METER_FIELD] = meter
            data[LAST_METER_DATE_FIELD] = date
            docRef.set(data, SetOptions.merge())
        }
    }

    interface UpdateListener{
        fun onUpdateComplete(task: Task<Void>)
        fun onUpdateFailure(exception: Exception)
    }

    interface DataListener{
        fun onDataChanged(lastMeter: Int)
    }
}