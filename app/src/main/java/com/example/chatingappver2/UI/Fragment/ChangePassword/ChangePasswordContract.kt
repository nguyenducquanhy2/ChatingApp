package com.example.chatapplication.Ui.Fragment.ChangePassword

interface ChangePasswordContract {

    interface view{
        fun OldPassInvalid()
        fun NewPassInvalid()
        fun NewPassNotEqual()
        fun changePassSucces()
        fun OldPasswordNotContans()
        fun changePassFail()
    }

    interface presenter{
        fun changePass(oldPass:String,NewPass:String,ConfirmPass:String)

    }

}