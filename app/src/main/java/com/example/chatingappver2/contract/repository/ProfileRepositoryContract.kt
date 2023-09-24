package com.example.chatingappver2.contract.repository

import android.net.Uri
import com.example.chatingappver2.model.UserProfile

interface ProfileRepositoryContract {

    interface GetProfileFinishListener {
        fun loadSuccess(result: UserProfile)
        fun loadError(message: String)
    }

    fun loadHeaderProfile(instance: GetProfileFinishListener){}


    fun notifyOnLineOrOfflineOnProfile(sateNotify: Boolean){}
    fun notifyOnlineOrOfflineForEveryone(sateNotify: Boolean){}

    interface SubmitProfileFinishListener {
        fun submitSuccess()
        fun submitError(message: String)
    }

    fun submitProfile(
        uri: Uri,
        fullname: String,
        dateChoose: String,
        submitProfileFinishListener: SubmitProfileFinishListener
    ){}

    interface UpdateProfileFinishListener{
        fun onSuccess()
    }
    fun updateProfileInCurrentContacts(value: String, path: String,instance:UpdateProfileFinishListener)
    fun updateImg(uri: Uri,instance:UpdateProfileFinishListener)
    fun updateMyProfileByPlace(value: String, path: String,instance:UpdateProfileFinishListener)
}