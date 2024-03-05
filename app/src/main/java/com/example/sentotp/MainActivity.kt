package com.example.sentotp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var enterPhoneNo: AppCompatEditText
    lateinit var sentOtpButton: AppCompatButton

    lateinit var enterVerifyOtp: AppCompatEditText
    lateinit var verifyOtpButton: AppCompatButton

    private var verificationID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        enterPhoneNo = findViewById(R.id.enterPhoneNumber_editText)
        sentOtpButton = findViewById(R.id.sendOtp_button)

        enterVerifyOtp = findViewById(R.id.enterOtp_editText)
        verifyOtpButton = findViewById(R.id.verifyOtp_button)

        firebaseAuth = FirebaseAuth.getInstance()

        // Sent Otp
        sentOtpButton.setOnClickListener {
            sendOtp()
        }
        // verifyOtp
        verifyOtpButton.setOnClickListener {
            verifyOtp()
        }
    }
    private fun sendOtp(){
        val phoneAuthentication = PhoneAuthOptions.newBuilder()
            .setPhoneNumber("+91${enterPhoneNo.text}")
            .setActivity(this)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    Toast.makeText(this@MainActivity, "Verification Successful", Toast.LENGTH_SHORT).show()
                }

                override fun onVerificationFailed(exception: FirebaseException) {
                    Toast.makeText(this@MainActivity, "Verification Failed ${exception.message}", Toast.LENGTH_SHORT).show()
                }

                override fun onCodeSent(verificationId: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(verificationId, p1)
                    verificationID = verificationId
                    Toast.makeText(this@MainActivity, "Otp is sent", Toast.LENGTH_SHORT).show()
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(phoneAuthentication)
    }

    private fun verifyOtp(){
        val verifyOtp = enterVerifyOtp.text.toString()
        val userPhoneCredential = PhoneAuthProvider.getCredential(verificationID, verifyOtp)

        firebaseAuth.signInWithCredential(userPhoneCredential).addOnSuccessListener {
            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, HomeActivity::class.java))
        }.addOnFailureListener {
            Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
        }
    }
}