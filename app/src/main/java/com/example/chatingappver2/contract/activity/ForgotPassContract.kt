package com.example.chatingappver2.contract.activity

interface ForgotPassContract {

    interface View{
        fun notifyEmailInvalid()
        fun notifySendEmailSuccess()
        fun notifySendEmailFail()
    }

    interface Presenter{
        fun sendResquestResetPass(email:String)
        fun validEmail(email:String):Boolean
    }
}