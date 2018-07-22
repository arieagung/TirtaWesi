package com.deuxvelva.tirtawesi.model

import com.deuxvelva.tirtawesi.controller.FirestoreController
import com.deuxvelva.tirtawesi.tools.log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.SetOptions

data class WargaModel(var listener: Listener? = null){

    var docSnapshot: DocumentSnapshot? = null
    var userDocRef: DocumentReference? = null

    init {

        userDocRef = FirestoreController().docRef("Warga", AuthModel().uid()!!)

        userDocRef!!.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                log("Listen failed $firebaseFirestoreException")
            } else {
                if (listener != null) {
                    listener!!.onDataChanged()
                }
                if (documentSnapshot != null && documentSnapshot.exists()){
                    docSnapshot = documentSnapshot
//                    log ("WargaDoc ${docSnapshot!!["uid"]}")
                }
            }
        }

    }

    fun userExists(): Boolean{
        return when(docSnapshot) {
            null -> false
            else -> true
        }
    }
    fun docReference():DocumentReference{
        return docSnapshot!!.reference
    }

    fun update(key: String, value: Any){
        val data = HashMap<String, Any>()
        data[key] = value
        docReference().set(data, SetOptions.merge())
    }

    fun update(data: HashMap<String, Any>){
        docReference().set(data, SetOptions.merge())
    }

    fun add(data: HashMap<String, Any>, listener: WargaModel.SaveListener){
        val wargaCollection = FirestoreController().colRef("Warga")

        wargaCollection!!.add(data)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        listener.onSaveCompleted(task.result)
                    }
                }
                .addOnFailureListener { err ->
                    listener.onSaveFailure(err)
                }

    }

    fun kavling(): String{
        return when(docSnapshot){
            null -> "-"
            else -> docSnapshot!!.get("kav").toString()
        }
    }

    fun nama(): String{
        return when(docSnapshot){
            null -> "-"
            else -> docSnapshot!!.getString("name")!!
        }
    }

    fun phone(): String{
        return when(docSnapshot){
            null -> "-"
            else -> docSnapshot!!.getString("phone")!!
        }
    }

    fun uid(): String{
        return when(docSnapshot){
            null -> ""
            else -> docSnapshot!!.getString("uid")!!
        }
    }

    fun meterPaid(): Int{
        return when {
            docSnapshot == null || docSnapshot!!.getLong("paid_meter") == null -> 0
            else -> docSnapshot!!.getLong("paid_meter")!!.toInt()
        }
    }

    interface Listener{
        fun onDataChanged()
    }

    interface SaveListener{
        fun onSaveCompleted(doc: DocumentReference)
        fun onSaveFailure(exception: Exception)
    }
}