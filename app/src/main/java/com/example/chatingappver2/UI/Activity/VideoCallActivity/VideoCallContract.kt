package com.example.chatingappver2.UI.Activity.VideoCallActivity

import com.example.chatingappver2.Model.UserProfile

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