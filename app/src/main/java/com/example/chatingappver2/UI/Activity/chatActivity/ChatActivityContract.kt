package com.example.chatingappver2.UI.Activity.chatActivity

import android.content.ContentResolver
import android.net.Uri
import com.example.chatingappver2.Model.Message
import com.example.chatingappver2.Model.UserProfile

interface ChatActivityContract {
    interface view{
        fun setInformationAccount(accountFocus: UserProfile?)
        fun sendMsgFail()
        fun sendMsgSuccess()
        fun addMessage(message: Message)
        fun removeMessageForMe()
        fun removeMessageForEveryone()
        fun sendImgSuccess()
        fun sendImgFail()
    }

    interface presenter{
        fun submitSendMsg(msg:String,urlImg: String)
        fun getMessages()
        fun sendImg(uriImg: Uri)
        fun getImageUriAndSending(contentResolver: ContentResolver,uriImg: Uri)
    }

}