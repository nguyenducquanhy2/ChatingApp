package com.example.chatingappver2.contract.activity

import android.content.ContentResolver
import android.net.Uri
import com.example.chatingappver2.model.Message
import com.example.chatingappver2.model.UserProfile

interface ChatActivityContract {
    interface view{
        fun setInformationAccount(accountFocus: UserProfile?)
        fun sendMsgFail()
        fun sendMsgSuccess()
        fun addMessage(message: Message)

        fun removeMessageForEveryone()
        fun sendImgSuccess()
        fun sendImgFail()

        fun getCurrentMessages(): MutableList<Message>

        fun removeMsgForMe(count:Int)

        fun messageValueChange(messageChange:Message,index:Int)

    }

    interface presenter{
        fun setAcountForcus(idReceiver: String)
        fun submitSendMsg(msg:String,urlImg: String)
        fun getMessages()
        fun sendImg(uriImg: Uri)
        fun getImageUriAndSending(contentResolver: ContentResolver,uriImg: Uri)

        fun unsendMsgForEveryOne(keyMsg:String)
        fun unsendMsgForYou(keyMsg:String)

    }

}