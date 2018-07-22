package com.deuxvelva.tirtawesi.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.deuxvelva.tirtawesi.R
import com.deuxvelva.tirtawesi.model.PaymentItemModel
import com.deuxvelva.tirtawesi.tools.log
import com.deuxvelva.tirtawesi.tools.simpleDate
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException


class PaymentListAdapter(response: FirestoreRecyclerOptions<PaymentItemModel>): FirestoreRecyclerAdapter<PaymentItemModel, PaymentListAdapter.PaymentHolder>(response){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.pembayaran_list_item, parent, false)
        return PaymentHolder(view)
    }

    override fun onBindViewHolder(holder: PaymentHolder, position: Int, model: PaymentItemModel) {
        holder.bind(model)
    }

    override fun onError(e: FirebaseFirestoreException) {
        log("error: $e")
    }


    class PaymentHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val tvTanggal: TextView = itemView.findViewById(R.id.tvPaymentDate)
        private val tvPembayaran: TextView = itemView.findViewById(R.id.tvPembayaran)
        private val tvPaymentStatus: TextView = itemView.findViewById(R.id.tvPaymentStatus)
        private val tvUsage: TextView = itemView.findViewById(R.id.tvUsage)

        fun bind(paymentItem: PaymentItemModel){
            tvTanggal.text = simpleDate(paymentItem.tanggal!!)
            tvUsage.text = "${paymentItem.usage!!} m3"
            tvPembayaran.text = "Rp. ${paymentItem.jmlBayar}"
            when(paymentItem.status) {
                null -> tvPaymentStatus.text = "Verifikasi"
                else -> tvPaymentStatus.text = paymentItem.status
            }

        }
    }
}
