package com.example.chatingappver2.presenter.activity

import android.util.Log
import com.example.chatingappver2.contract.activity.VerificationContract
import com.example.chatingappver2.contract.repository.AuthRepositoryContract
import com.example.chatingappver2.database.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class VerificationPresenter(private val view: VerificationContract.view,private val repository: AuthRepository) :
    VerificationContract.presenter, AuthRepositoryContract.SendEmailFinishListener {
    private val TAG: String = "VerificationPresenter"

    override fun EmailsendAgain() {
        repository.sendEmailVerification(this)
    }

    override fun sendEmailSuccess(email: String) {
        view.sentEmail(email)
    }

    override fun sendEmailError(exception: java.lang.Exception) {
        try {
            throw (exception)
        } catch (e: Exception) {
            view.exceptionSendVerifiEmail()
            Log.e(TAG, "EmailsendAgain: $e")
        }
    }
}


