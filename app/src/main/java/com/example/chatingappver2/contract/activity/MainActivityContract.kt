package com.example.chatingappver2.contract.activity

import com.example.chatingappver2.model.UserProfile

interface MainActivityContract {
    interface view{
        fun SignOutSuccess()
        fun SignOutFail()
        fun setHeaderProfile(userProfile: UserProfile)

    }
    interface presenter{
        fun logout()
        fun loadHeaderProfile()

        fun setNotifityOnline()
    }
}