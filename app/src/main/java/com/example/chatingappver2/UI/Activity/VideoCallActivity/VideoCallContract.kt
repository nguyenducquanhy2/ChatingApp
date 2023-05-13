package com.example.chatingappver2.UI.Activity.VideoCallActivity

interface VideoCallContract {
    interface view{

        fun executeInitCall()
    }
    interface presenter{

        fun checkPermissionRequest()
    }


}