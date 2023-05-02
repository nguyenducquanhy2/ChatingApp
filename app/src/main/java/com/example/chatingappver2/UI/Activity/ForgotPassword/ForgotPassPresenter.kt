package com.example.chatingappver2.UI.Activity.ForgotPassword


import android.util.Log
import com.google.firebase.auth.FirebaseAuth

class ForgotPassPresenter(private var view: ForgotPassContract.view): ForgotPassContract.presenter {
    private val TAG: String="ForgotPassPresenter"
    val auth=FirebaseAuth.getInstance()


    override fun SendResquestResetPass(email: String) {
        if (validEmail(email)==false){
            view.notifyEmailInvalid()
            return
        }

        auth.sendPasswordResetEmail(email).addOnCompleteListener { task->
            if (task.isSuccessful){
                Log.d(TAG, "SendResquestResetPass: Email sent...")
            }
            else{
                try {
                    throw task.exception!!

                }
                catch (e:Exception){
                    view.notifySendEmailFail()
                    Log.e(TAG, "SendResquestResetPass: ${task.exception.toString()}")
                }
            }

        }


    }

    override fun validEmail(email: String):Boolean {
        if (email.isEmpty()) {
            return false
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
    }


}