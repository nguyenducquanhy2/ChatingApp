package com.example.chatingappver2.contract.activity

import android.net.Uri

interface CreateProfileContract {
    interface view{
        fun submitSuccess()
        fun submitFail()
        fun fullNameEmpty()
        fun setTextViewDate(value:String)
        fun dateInValid()
    }

    interface presenter{
        fun submitProfileToFireBase(uri:Uri, fullname:String, dateChoose:String)
        fun openDialogDatePicker()
    }

}