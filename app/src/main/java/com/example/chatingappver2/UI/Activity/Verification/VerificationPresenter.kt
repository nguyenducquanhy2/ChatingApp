package com.example.chatingappver2.UI.Activity.Verification

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class VerificationPresenter(private val view: VerificationContract.view) :
    VerificationContract.presenter {
    private val TAG: String = "VerificationPresenter"
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val currentUser: FirebaseUser? = auth.currentUser

    override fun EmailsendAgain() {

        currentUser?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val email = currentUser?.email
                view.sentEmail(email!!)
            } else {
                try {
                    throw (task.exception!!)
                } catch (e: Exception) {
                    view.exceptionSendVerifiEmail()
                    Log.e(TAG, "EmailsendAgain: $e")
                }
            }
        }


    }
}


