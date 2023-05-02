package com.example.chatingappver2.UI.Activity.SignUp

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
        fun validEmail(email:String):Boolean
        fun validPassword(password:String):Boolean
        fun checkPassAndConFirmPass(password:String,confirmPassword:String):Boolean

    }
}