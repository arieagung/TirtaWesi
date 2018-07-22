package com.deuxvelva.tirtawesi

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.deuxvelva.tirtawesi.tools.log
import com.deuxvelva.tirtawesi.controller.MeterController
import com.deuxvelva.tirtawesi.controller.PaymentController
import com.deuxvelva.tirtawesi.controller.TagihanController
import com.deuxvelva.tirtawesi.controller.WargaController
import com.deuxvelva.tirtawesi.model.TagihanModel
import com.deuxvelva.tirtawesi.model.WargaModel
import java.util.concurrent.TimeUnit
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.FirebaseException

import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.firestore.*
import com.joanzapata.iconify.IconDrawable
import com.joanzapata.iconify.fonts.FontAwesomeIcons
import com.firebase.ui.auth.AuthUI
import java.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.firebase.ui.auth.IdpResponse
import android.content.Intent

class MainActivity : AppCompatActivity() {

    public var verifying = false
//    lateinit var phoneNumber: String
    private val RC_SIGN_IN = 123
    private val VERIFY_DURATION: Long = 60
    lateinit var mVerificationId: String
    lateinit var mResendToken: PhoneAuthProvider.ForceResendingToken
    lateinit var mAuth: FirebaseAuth
    lateinit var mFireStore: FirebaseFirestore
    lateinit var mCurrentUser: FirebaseUser

    lateinit var mTagihan: TagihanController
    lateinit var mMeter: MeterController
    lateinit var mWarga: WargaController
    private var mPayment: PaymentController? = null
    private var actionLogout: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
        FirebaseFirestore.setLoggingEnabled(true)

        mFireStore = FirebaseFirestore.getInstance()

        val fireStoreSettings:FirebaseFirestoreSettings = FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build()
        mFireStore.firestoreSettings = fireStoreSettings


        btnLogin.setOnClickListener {
            setLoginPageEnable(false)
            loginFlow()
        }

        btnVerify.setOnClickListener {
            val code = "123456"// etVerifyCode.text.toString()
            val credential = PhoneAuthProvider.getCredential(mVerificationId, code)
            verifying = false
            setVerificationPageEnable(false)
            signInWithPhoneAuthCredential(credential)
        }


        mAuth.currentUser?.getIdToken(true)

        if (mAuth.currentUser != null) {
            login()
        } else {
            setLoginPageVisibility(true)
            setLoginPageEnable(true)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                login()
            } else {
                if (response == null) {
                    setLoginPageVisibility(true)
                    setLoginPageEnable(true)
                }
            }
        }
    }

    private fun loginFlow(){
        val providers = Arrays.asList(
                AuthUI.IdpConfig.PhoneBuilder().build()
        )
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)
                        .build(),
                RC_SIGN_IN)
    }

    private fun login(){
        if (WargaModel().userExists()) {
            mCurrentUser = mAuth.currentUser!!

            setLoginPageVisibility(false)
            setLoginPageEnable(false)
            loadMainData()
            setMainPageVisibility(true)
        } else {
            Toast.makeText(applicationContext, "Anda tidak terdaftar sebagai pengguna", Toast.LENGTH_LONG).show()
            logout()
        }
    }

    override fun onStart() {
        super.onStart()
        if (verifying) {

        }

        if (mAuth.currentUser != null && mPayment != null) {
            mPayment!!.startListening()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        actionLogout = menu.findItem(R.id.action_logout)
        actionLogout!!
                .icon = IconDrawable(this, FontAwesomeIcons.fa_sign_out)
                .colorRes(R.color.material_cyan_100)
                .actionBarSize()

        if (mAuth.currentUser !=null) {
            actionLogout!!.isVisible = true
        }
        return true
    }

    override fun onStop() {
        super.onStop()
        if (mAuth.currentUser != null && mPayment != null) {
            mPayment!!.stopListening()
        }
    }


    private fun initPembayaranController(){
        mPayment = PaymentController(
                context = this,
                btnPembayaran = btnKonfirmasiPembayaran,
                rvPaymentList = rvPaymentList)

        mPayment!!.startListening()

    }

    private fun initWargaController(){
        mWarga = WargaController(
                tvKavling = tvNoRumah,
                tvName = tvNamaPemilik,
                tvMeterPaid = tvPenggunaanTerbayar
        )
    }

    private fun initMeterClass(){
        mMeter = MeterController(
                context = applicationContext,
                btnUpdateMeter = btnUpdateMeter,
                meterView = meterView,
                tvLastUpdate = tvLastMeterUpdateLabel
        )
    }

    private fun initTagihanClass(){
        mTagihan = TagihanController(
                tvBulanTagihan = tvBulanTagihan,
                tvPemakaian = tvPemakaian,
                tvTagihan = tvTagihan)
    }


    private fun verifyPhone(phoneNumber: String){
        val mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                verifying = false
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                verifying = false

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }
            }

            override fun onCodeSent(verificationId: String?,
                                    token: PhoneAuthProvider.ForceResendingToken) {
                mVerificationId = verificationId!!
                mResendToken = token
                setVerificationPageVisibility(true)
            }
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                VERIFY_DURATION,
                TimeUnit.SECONDS,
                this,
                mCallbacks
        )
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId){
            R.id.action_logout -> {
                logout()
            }
        }
        return true
    }

    private fun logout(){
        mAuth.signOut()
        if (actionLogout!=null) {
            actionLogout!!.isVisible = false
        }
        setMainPageVisibility(false)
        setLoginPageVisibility(true)
        setLoginPageEnable(true)
    }


    private fun loadMainData(){
        initWargaController()
        initTagihanClass()
        initMeterClass()
        initPembayaranController()
    }

    private fun setMainPageVisibility(visible:Boolean){
        if (visible)
            mainViewGroup.visibility = View.VISIBLE
        else {
            mainViewGroup.visibility = View.GONE
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        log("signInWithCredential:success")
                        setVerificationPageVisibility(false)
                        setLoginPageVisibility(false)

                        mCurrentUser = task.result.user
                        loadMainData()

                    } else {
                        // Sign in failed, display a message and update the UI
                        log("signInWithCredential:failure")
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            setVerificationPageEnable(true)
                            setVerificationPageVisibility(true)
                            Toast.makeText(this, "Wrong code", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
    }

    private fun setLoginPageVisibility(visible: Boolean){
        var visibility = View.VISIBLE

        if (visible) {
            setVerificationPageVisibility(false)
        } else {
            visibility = View.GONE
        }

        loginViewGroup.visibility = visibility
    }

    private fun setLoginPageEnable(enable: Boolean){
        btnLogin.isEnabled= enable
    }

    private fun setVerificationPageVisibility(visible: Boolean){
        var visibility = View.VISIBLE

        if (visible) {
            setLoginPageVisibility(false)
        } else {
            visibility = View.GONE
        }

        tvVerifyLabel.visibility = visibility
        etVerifyCode.visibility = visibility
        btnVerify.visibility = visibility
    }

    private fun setVerificationPageEnable(enable: Boolean){
        etVerifyCode.isEnabled = enable
        btnVerify.isEnabled= enable
    }
}

