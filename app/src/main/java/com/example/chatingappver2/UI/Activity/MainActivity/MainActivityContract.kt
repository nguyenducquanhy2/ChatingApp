package com.example.chatingappver2.UI.Activity.MainActivity

import com.example.chatingappver2.Model.UserProfile
import com.google.firebase.auth.FirebaseUser

interface MainActivityContract {
    interface view{
        fun SignOutSuccess()
        fun SignOutFail()
        fun setHeaderProfile(value: UserProfile, currentUser: FirebaseUser)

    }
    interface presenter{
        fun logout()
        fun loadHeaderProfile()
    }
}