package com.example.chatapplication.Ui.InterfaceAdapter

import android.view.View

interface MessageOnClick {
    fun msgOnLongClickListener(view:View,message:String,urlImage: String,keyMsg:String,isMyMessage:Boolean)



    fun ImageMessageOnClickListener(urlImage:String)


}