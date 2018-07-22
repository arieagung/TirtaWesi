package com.deuxvelva.tirtawesi.dialog

import android.app.Dialog
import android.content.Context
import android.widget.*
import com.deuxvelva.tirtawesi.R
import com.deuxvelva.tirtawesi.enum.PaymentMethod

class DialogKonfirmasiPembayaran(var mDialogActivity: Context, var listener: Listener){
    private var mDialog: Dialog? = null
    lateinit var mBtnPositive: Button
    lateinit var mBtnNegative: Button
    lateinit var mEtJmlBayar: EditText
    lateinit var mOptCash: RadioButton
    lateinit var mOptTransfer: RadioButton
    lateinit var mOptPaymentMethod: RadioGroup
    lateinit var mTvJmlTagihan: TextView

    var jmlTagihan: Int = 0

    fun showDialog(){
        if (mDialog == null) {
            mDialog = Dialog(mDialogActivity, R.style.CustomDialogTheme)
        }
        mDialog!!.setContentView(R.layout.dialog_konfirmasi_pembayaran)
        mDialog!!.show()

        mTvJmlTagihan = mDialog!!.findViewById(R.id.tvJmlTagihan)
        mTvJmlTagihan.text = "Rp. $jmlTagihan"

        mEtJmlBayar = mDialog!!.findViewById(R.id.etJumlahBayar)
        mOptPaymentMethod = mDialog!!.findViewById(R.id.paymentOptions)

        mOptCash = mDialog!!.findViewById(R.id.paymentCash)
        mOptTransfer = mDialog!!.findViewById(R.id.paymentTransfer)

        mBtnPositive = mDialog!!.findViewById(R.id.btnSend)
        mBtnNegative = mDialog!!.findViewById(R.id.btnCancel)
        mBtnPositive.setOnClickListener {
            
            var jmlBayar = 0
            if (mEtJmlBayar.text.toString() != "" ) {
                jmlBayar = mEtJmlBayar.text.toString().toInt()
            }
            val paymentMethod = when (mOptPaymentMethod.checkedRadioButtonId){
                mOptCash.id -> PaymentMethod.CASH
                mOptTransfer.id -> PaymentMethod.TRANSFER
                else -> PaymentMethod.CASH
            }
            listener.onPositiveClick(jmlBayar, paymentMethod)
            mDialog!!.dismiss()
        }

        mBtnNegative.setOnClickListener{
            mDialog!!.dismiss()
            listener.onNegativeClick()
        }
    }


    interface Listener{
        fun onPositiveClick(jmlBayar:Int, paymentMethod: PaymentMethod)
        fun onNegativeClick()
    }
}