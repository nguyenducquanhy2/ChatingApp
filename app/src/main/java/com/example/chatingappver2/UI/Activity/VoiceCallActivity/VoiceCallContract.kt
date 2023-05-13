package com.example.chatingappver2.UI.Activity.VoiceCallActivity

import com.example.chatingappver2.Model.UserProfile

interface VoiceCallContract {
    interface view{
        fun setInformationOfThem(themProfile: UserProfile)
        fun showElapsedTime(dateFormatted:String)
        fun executeInitCall()
    }
    interface presenter{
        fun loadInformationOfThem(idProfile:String)
        fun convertLongTimeDateFormat(time:Long)
        fun checkPermissionRequest()
    }


}