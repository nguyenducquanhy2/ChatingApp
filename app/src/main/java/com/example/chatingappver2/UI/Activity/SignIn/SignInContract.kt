package com.example.chatingappver2.UI.Activity.SignIn


 interface SignInContract {

     interface presenter {
        fun Login(str: String, str2: String)
        fun checkCurrentlySigned()
        fun validEmail(str: String): Boolean
        fun validPassword(str: String): Boolean
    }

     interface view {
        fun changeHomeActivity()
        fun changeUpdateProfile()
        fun changeVerifiActivity()
        fun notifyEmailWrong()
        fun notifyPasswordWrong()
        fun notifySignInFail()
    }
}