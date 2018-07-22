package com.deuxvelva.tirtawesi.controller

import android.content.Context
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class FirestoreController (){

    var mFireStore:FirebaseFirestore

    init {
        FirebaseFirestore.setLoggingEnabled(true)

        mFireStore = FirebaseFirestore.getInstance()

        val fireStoreSettings: FirebaseFirestoreSettings = FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build()
        mFireStore.firestoreSettings = fireStoreSettings
    }

    fun docRef(collection:String, doc: String):DocumentReference{
        return mFireStore.collection(collection).document(doc)
    }

    fun colRef(collection:String): CollectionReference{
        return mFireStore.collection(collection)
    }
}