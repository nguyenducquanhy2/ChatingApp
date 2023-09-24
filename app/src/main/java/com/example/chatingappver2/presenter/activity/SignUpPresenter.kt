package com.example.chatingappver2.presenter.activity


import android.app.ProgressDialog
import android.content.Context
import android.util.Log
import com.example.chatingappver2.contract.activity.SignUpContract
import com.example.chatingappver2.contract.repository.AuthRepositoryContract
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException


class SignUpPresenter(
    var view: SignUpContract.view,
    var context: Context,
    private val repository: AuthRepositoryContract
) :
    SignUpContract.presenter, AuthRepositoryContract.SingUpFinishListener,
    AuthRepositoryContract.SendEmailFinishListener {
    private val TAG = "SignUpPresenter"
    private val progessDialog = ProgressDialog(context)

    override fun SignUp(email: String, password: String, confirmPassword: String) {

        if (validEmail(email) == false) {
            view.notifyEmailWrong()
            return
        }

        if (validPassword(password) == false) {
            view.notifyPasswordWrong()
            return
        }

        if (validPassword(confirmPassword) == false) {
            view.notifyConfirmPasswordWrong()
            return
        }

        if (checkPassAndConFirmPass(password, confirmPassword) == false) {
            view.notifyTwoPassNotEqual()
            return
        }

        createNewAccount(email, password)
    }




    override fun singUpSuccess() {
        progessDialog.dismiss()
        repository.sendEmailVerification(this)

    }

    override fun singUpError(exception: java.lang.Exception) {
        progessDialog.dismiss()
        try {
            throw exception
        } catch (FirebaseAuthUserCollisionException: FirebaseAuthUserCollisionException) {
            Log.e(TAG, "fun createNewAccount: $exception")
            view.notifyDuplicateUserAccount()
        } catch (e: Exception) {
            Log.e(TAG, "fun createNewAccount: $exception")
            view.notifySignUpFail()
        }
    }

    override fun sendEmailSuccess(email: String) {
        FirebaseAuth.getInstance().signOut()
        view.notifySendEmailVerification()
    }

    override fun sendEmailError(exception: java.lang.Exception) {
        Log.e(TAG, "fun createNewAccount: $exception")
    }

    private fun createNewAccount(email: String, password: String) {
        progessDialog.setMessage("Loading...")
        progessDialog.show()
        repository.singUp(email, password, this)
    }

    private fun validEmail(email: String): Boolean {
        if (email.isEmpty()) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    private fun validPassword(password: String): Boolean {
        if (password.isEmpty()) {
            return false
        } else {
            return password.length >= 6
        }
    }

    private fun checkPassAndConFirmPass(password: String, confirmPassword: String): Boolean {
        if (password.equals(confirmPassword)) {
            return true
        }
        return false
    }


}