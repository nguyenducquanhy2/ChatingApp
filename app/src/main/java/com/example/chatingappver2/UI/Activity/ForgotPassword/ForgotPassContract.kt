package com.example.chatingappver2.UI.Activity.ForgotPassword

interface ForgotPassContract {
    interface view{
        fun notifyEmailInvalid()
        fun notifySendEmailSuccess()
        fun notifySendEmailFail()
    }

    interface presenter{
        fun SendResquestResetPass(email:String)
        fun validEmail(email:String):Boolean
    }
}