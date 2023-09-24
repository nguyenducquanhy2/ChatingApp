package com.example.chatingappver2.contract.activity

import com.example.chatingappver2.model.UserProfile

interface VideoCallContract {
    interface view{
        fun setInformationOfThem(themProfile: UserProfile)
        fun executeInitCall()
    }
    interface presenter{
        fun loadInformationOfThem(idProfile:String)
        fun checkPermissionRequest()
    }


}