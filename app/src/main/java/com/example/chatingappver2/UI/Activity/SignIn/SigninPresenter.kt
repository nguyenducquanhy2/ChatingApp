package com.example.chatingappver2.UI.Activity.SignIn

import android.content.Context
import android.util.Log
import android.util.Patterns
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SigninPresenter(private val view: SignInContract.view, private var context: Context) :
    SignInContract.presenter {

    private val TAG: String = "SigninPresenter"
    private var auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()


     override fun checkCurrentlySigned() {
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser == null) {
            return
        }
        checkVerification()
    }

    // com.example.chatapplication.Ui.Activity.SignIn.SignInContract.presenter
     override fun Login(email: String, password: String) {

        if (!validEmail(email)) {
            view.notifyEmailWrong()

        }

        if (!validPassword(password)) {
            view.notifyPasswordWrong()
            return
        }

        SignIn(email, password)
    }

    private fun SignIn(str: String, str2: String) {
        auth.signInWithEmailAndPassword(str, str2).addOnCompleteListener { taskSignIn ->
            if (taskSignIn.isSuccessful) {
                checkVerification()
            } else {
                view.notifySignInFail()
            }

        }
    }

    private fun checkVerification() {

        if (auth.currentUser?.isEmailVerified == true) {
            val child: DatabaseReference = database.reference.child("profile")
            child.child(auth.currentUser!!.uid).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (task.result.exists()) {
                            view.changeHomeActivity()
                        } else {
                            view.changeUpdateProfile()
                        }
                    } else {
                        Log.e(TAG, "checkVerification:  ")
                    }
                }
        } else {
            view.changeVerifiActivity()
        }

    }

     override fun validEmail(email: String): Boolean {
        val str: String = email
        if (str.isEmpty()) {
            return false
        }
        return Patterns.EMAIL_ADDRESS.matcher(str).matches()
    }

     override fun validPassword(password: String): Boolean {
        return !(password.isEmpty()) && password.length >= 6
    }


}