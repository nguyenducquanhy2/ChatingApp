package com.example.chatingappver2.presenter.activity

import android.util.Patterns
import com.example.chatingappver2.contract.activity.SignInContract
import com.example.chatingappver2.contract.repository.AuthRepositoryContract
import com.example.chatingappver2.database.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import java.lang.Exception


class SignInPresenter(
    private val view: SignInContract.View,
    private val repository: AuthRepository
) :
    SignInContract.Presenter, AuthRepositoryContract.SingInFinishListener {

    private var auth = FirebaseAuth.getInstance()


    override fun checkCurrentlySigned() {
        auth.currentUser ?: return
        checkVerification()
    }

    // com.example.chatapplication.Ui.Activity.SignIn.SignInContract.presenter
    override fun login(email: String, password: String) {

        if (!validEmail(email)) {
            view.notifyEmailWrong()

        }

        if (!validPassword(password)) {
            view.notifyPasswordWrong()
            return
        }

        repository.signIn(email, password, this)
//        SignIn(email, password)
    }

    override fun singInSuccess() {
        checkVerification()
    }

    override fun singInError(exception: Exception) {
        view.notifySignInFail(exception)
    }

    override fun validVerify(isContainsProfile: Boolean) {
        if (isContainsProfile) {
            view.changeHomeActivity()
        } else {
            view.changeUpdateProfile()
        }
    }

    override fun inValidVerify() {
        view.changeVerifiActivity()
    }


    private fun checkVerification() {
        repository.checkVerification(this)
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