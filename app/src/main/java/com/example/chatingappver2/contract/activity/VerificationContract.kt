package com.example.chatingappver2.contract.activity

interface VerificationContract {
    interface view{
        fun exceptionSendVerifiEmail()
        fun sentEmail(email:String)
    }
    interface presenter{
        fun EmailsendAgain()

    }

}