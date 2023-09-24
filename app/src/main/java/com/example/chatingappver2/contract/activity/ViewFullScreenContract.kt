package com.example.chatingappver2.contract.activity

import android.widget.ImageView

interface ViewFullScreenContract {
    interface view{

    }
    interface presenter{
        fun saveImage(ImageView: ImageView)
    }
}