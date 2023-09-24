package com.example.chatingappver2.database.repository

import android.util.Log
import com.example.chatingappver2.contract.repository.AuthRepositoryContract
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AuthRepository(private val auth: FirebaseAuth, private val database: FirebaseDatabase) :
    AuthRepositoryContract {

    private val TAG: String = "AuthRepository"

    override fun singUp(
        email: String,
        password: String,
        singUpFinishListener: AuthRepositoryContract.SingUpFinishListener
    ) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                singUpFinishListener.singUpSuccess()
            } else {
                //handle signUp Fail
                singUpFinishListener.singUpError(task.exception!!)
            }
        }
    }

    override fun signIn(
        email: String,
        password: String,
        singInFinishListener: AuthRepositoryContract.SingInFinishListener
    ) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { taskSignIn ->
            if (taskSignIn.isSuccessful) {
                singInFinishListener.singInSuccess()
            } else {
                taskSignIn.exception?.let { singInFinishListener.singInError(it) }
            }
        }
    }

    override fun checkVerification(singInFinishListener: AuthRepositoryContract.SingInFinishListener) {
        if (auth.currentUser?.isEmailVerified == true) {
            checkProfile(singInFinishListener)
        } else {
            singInFinishListener.inValidVerify()
        }
    }

    override fun sendEmailVerification(sendEmailFinishListener: AuthRepositoryContract.SendEmailFinishListener) {
        val currentUser = auth.currentUser!!
        currentUser.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val email = currentUser.email
                email?.let {
                    sendEmailFinishListener.sendEmailSuccess(it)
                }
            } else {
                task.exception?.let { sendEmailFinishListener.sendEmailError(it) }
            }
        }
    }

    override fun changePassword(
        oldPass: String,
        NewPass: String,
        changePassWordListener: AuthRepositoryContract.ChangePassWordListener
    ) {
        //check old pass
        auth.currentUser?.email?.let {email->
            auth.signInWithEmailAndPassword(email, oldPass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    updateNewPass(NewPass, changePassWordListener)
                } else {
                    changePassWordListener.checkOldPass()
                }
            }
        }
    }

    override fun doResquestResetPass(
        email: String,
        instance: AuthRepositoryContract.SendEmailFinishListener
    ) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                instance.sendEmailSuccess(email)
            } else {
                try {
                    throw task.exception!!

                } catch (e: Exception) {
                    instance.sendEmailError(e)
                }
            }

        }
    }

    private fun updateNewPass(
        newPass: String,
        changePassWordListener: AuthRepositoryContract.ChangePassWordListener
    ) {
        auth.currentUser?.updatePassword(newPass)?.addOnCompleteListener { taskUpdatePass ->
            if (taskUpdatePass.isSuccessful) {
                changePassWordListener.changePasswordSuccess()
            } else {
                changePassWordListener.changePasswordError(
                    taskUpdatePass.exception?.message ?: ""
                )
            }
        }
    }

    private fun checkProfile(singInFinishListener: AuthRepositoryContract.SingInFinishListener) {
        val child: DatabaseReference = database.reference.child("profile")
        child.child(auth.currentUser!!.uid).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    if (task.result.exists()) {
                        singInFinishListener.validVerify(true)
                    } else {
                        singInFinishListener.validVerify(false)
                    }

                } else {
                    Log.e(TAG, "checkVerification:  ")
                }
            }
    }


}