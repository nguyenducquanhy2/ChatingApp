package com.example.chatingappver2.contract.repository

import java.lang.Exception

interface AuthRepositoryContract {

    interface SingUpFinishListener{
        fun singUpSuccess()
        fun singUpError(exception: Exception)
    }

    fun singUp(email: String, password: String,singUpFinishListener:SingUpFinishListener){}

    interface SingInFinishListener{
        fun singInSuccess()
        fun singInError(exception: Exception)

        fun validVerify(isContainsProfile:Boolean)
        fun inValidVerify()
    }

    fun signIn(email: String, password: String,singInFinishListener:SingInFinishListener){}
    fun checkVerification(singInFinishListener: SingInFinishListener){}

    interface SendEmailFinishListener{
        fun sendEmailSuccess(email:String)
        fun sendEmailError(exception: Exception)
    }
    fun sendEmailVerification(sendEmailFinishListener:SendEmailFinishListener){}

    interface ChangePassWordListener{
        fun changePasswordSuccess()
        fun changePasswordError(message: String)
        fun checkOldPass()
    }
    fun changePassword(oldPass: String, NewPass: String ,changePassWordListener:ChangePassWordListener)


    fun doResquestResetPass(email: String, instance:SendEmailFinishListener)

}