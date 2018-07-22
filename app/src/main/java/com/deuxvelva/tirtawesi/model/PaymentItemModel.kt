package com.deuxvelva.tirtawesi.model

import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class PaymentItemModel {

    var tanggal: Date? = null
    var jmlBayar: Long? = 0
    var usage: Long? = 0
    var status: String? = null
    var paid_meter: Long? = 0

    constructor()

    constructor(
            tanggal: Date,
            jmlBayar: Long,
            usage: Long,
            status: String,
            paid_meter: Long)
}