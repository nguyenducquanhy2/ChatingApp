package com.example.chatingappver2.contract.activity

import java.lang.Exception


interface SignInContract {


     interface View {
        fun changeHomeActivity()
        fun changeUpdateProfile()
        fun changeVerifiActivity()
        fun notifyEmailWrong()
        fun notifyPasswordWrong()
        fun notifySignInFail(exception: Exception)
    }

    interface Presenter {
        fun login(email: String, password: String)
        fun checkCurrentlySigned()
        fun validEmail(str: String): Boolean
        fun validPassword(str: String): Boolean

    }

}