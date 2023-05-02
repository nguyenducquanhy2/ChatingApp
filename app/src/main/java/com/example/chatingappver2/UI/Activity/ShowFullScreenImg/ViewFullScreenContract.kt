package com.example.chatingappver2.UI.Activity.ShowFullScreenImg

import android.widget.ImageView

interface ViewFullScreenContract {
    interface view{

    }
    interface presenter{
        fun saveImage(ImageView: ImageView)
    }
}