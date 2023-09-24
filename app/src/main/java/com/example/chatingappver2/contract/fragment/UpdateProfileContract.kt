package com.example.chatingappver2.contract.fragment

import android.net.Uri
import com.example.chatingappver2.model.UserProfile


interface UpdateProfileContract {
    interface View {
        fun dateInValid()
        fun fullNameEmpty()
        fun submitFail()
        fun submitSuccess()
        fun setHeaderProfile(userProfile: UserProfile)
    }

    interface Presenter {
        fun loadHeaderProfile()
        fun submitProfileToFireBase(uri: Uri?, str: String, str2: String)
    }


}