package com.example.chatingappver2.UI.Activity.SignUp


import android.app.ProgressDialog
import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser


class SignUpPresenter(var view: SignUpContract.view, var context: Context):
    SignUpContract.presenter {
    private val auth=FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser
    private val TAG="SignUpPresenter"
    private val progessDialog=ProgressDialog(context)

    override fun validEmail(email: String):Boolean {
        if (email.isEmpty()) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    override fun validPassword(password: String):Boolean {
        if (password.isEmpty()){
            return false
        }
        else{
            return password.length>=6
        }
    }

    override fun checkPassAndConFirmPass(password: String, confirmPassword: String): Boolean {
        if (password.equals(confirmPassword)){
            return true
        }
        return false
    }

    override fun SignUp(email: String, password: String, confirmPassword: String) {

        if (validEmail(email)==false){
            view.notifyEmailWrong()
            return
        }

        if (validPassword(password)==false){
            view.notifyPasswordWrong()
            return
        }

        if (validPassword(confirmPassword)==false){
            view.notifyConfirmPasswordWrong()
            return
        }

        if (checkPassAndConFirmPass(password,confirmPassword)==false){
            view.notifyTwoPassNotEqual()
            return
        }

        createNewAccount(email, password)
    }

    private fun createNewAccount(email: String, password: String) {
        progessDialog.setMessage("Loading...")
        progessDialog.show()
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{task->
            if (task.isSuccessful){
                progessDialog.dismiss()
                SignUpSuccess()
            }
            else{
                //handle signUp Fail
                try {
                    progessDialog.dismiss()
                    throw task.exception!!
                }
                catch (FirebaseAuthUserCollisionException: FirebaseAuthUserCollisionException){
                    Log.e(TAG, "fun createNewAccount: "+task.exception.toString() )
                    view.notifyDuplicateUserAccount()
                }
                catch (e:Exception){
                    Log.e(TAG, "fun createNewAccount: "+task.exception.toString() )
                    view.notifySignUpFail()
                }
            }
        }
    }

    private fun SignUpSuccess() {
        currentUser= auth.currentUser!!
        currentUser.sendEmailVerification().addOnCompleteListener { task->
            if (task.isSuccessful){
                auth.signOut()
                view.notifySendEmailVerification()
            }
        }
    }



}