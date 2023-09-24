package com.example.chatingappver2.contract.activity

interface SignUpContract {
    interface view{
        fun notifyEmailWrong()
        fun notifyPasswordWrong()
        fun notifySignUpFail()
        fun notifyConfirmPasswordWrong()
        fun notifyTwoPassNotEqual()
        fun notifySendEmailVerification()
        fun notifyDuplicateUserAccount()

    }

    interface presenter{
        fun SignUp(email:String,password:String,confirmPassword:String)


    }
}