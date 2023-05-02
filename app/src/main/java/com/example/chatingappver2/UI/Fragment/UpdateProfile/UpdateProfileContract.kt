package com.example.chatingappver2.UI.Fragment.UpdateProfile

import android.net.Uri


interface UpdateProfileContract {
    interface view {
        fun dateInValid()
        fun fullNameEmpty()
        fun setTextViewDate(str: String?)
        fun submitFail()
        fun submitSuccess()
    }

    interface presenter {
        fun openDialogDatePicker()
        fun submitProfileToFireBase(uri: Uri?, str: String, str2: String)
    }


}