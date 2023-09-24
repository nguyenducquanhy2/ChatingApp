package com.example.chatingappver2.presenter.activity


import com.example.chatingappver2.contract.activity.ForgotPassContract
import com.example.chatingappver2.contract.repository.AuthRepositoryContract
import com.example.chatingappver2.database.repository.AuthRepository

class ForgotPassPresenter(
    private var view: ForgotPassContract.View,
    private val repository: AuthRepository
) : ForgotPassContract.Presenter, AuthRepositoryContract.SendEmailFinishListener {


    override fun sendResquestResetPass(email: String) {
        if (!validEmail(email)) {
            view.notifyEmailInvalid()
            return
        }

       repository.doResquestResetPass(email,this)
    }

    override fun validEmail(email: String): Boolean {
        return if (email.isEmpty()) {
            false
        } else {
            android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
    }

    override fun sendEmailSuccess(email: String) {
        view.notifySendEmailSuccess()
    }

    override fun sendEmailError(exception: java.lang.Exception) {
        view.notifySendEmailFail()
    }


}