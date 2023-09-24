package com.example.chatingappver2.contract.fragment

interface ChangePasswordContract {

    interface View{
        fun oldPassInvalid()
        fun newPassInvalid()
        fun newPassNotEqual()
        fun changePassSucces()
        fun oldPasswordNotContans()
        fun changePassFail(message: String)
    }

    interface Presenter{
        fun changePass(oldPass:String,NewPass:String,ConfirmPass:String)

    }

}