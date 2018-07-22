package com.deuxvelva.tirtawesi.model

import com.google.firebase.auth.FirebaseAuth

class AuthModel {
    val mAuth = FirebaseAuth.getInstance()

    fun uid(): String? {
        return when(mAuth.currentUser) {
            null -> null
            else -> mAuth!!.currentUser!!.uid
        }
    }
}