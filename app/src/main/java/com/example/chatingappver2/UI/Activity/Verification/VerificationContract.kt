package com.example.chatingappver2.UI.Activity.Verification

interface VerificationContract {
    interface view{
        fun exceptionSendVerifiEmail()
        fun sentEmail(email:String)
    }
    interface presenter{
        fun EmailsendAgain()

    }

}