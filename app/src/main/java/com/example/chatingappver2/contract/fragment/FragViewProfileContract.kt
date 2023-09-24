package com.example.chatingappver2.contract.fragment

import com.example.chatingappver2.model.UserProfile

interface FragViewProfileContract {
    interface View{
        fun setInformation(userProfile: UserProfile)
        fun getInforError(message:String)
    }

    interface Presenter{
        fun getInformation()
    }

}