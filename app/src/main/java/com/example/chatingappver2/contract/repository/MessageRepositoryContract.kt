package com.example.chatingappver2.contract.repository

import android.net.Uri
import com.example.chatingappver2.model.Message
import com.example.chatingappver2.model.UserProfile

interface MessageRepositoryContract {


    interface MessageFinishListener {
        fun addMessage(message: Message) {}
        fun getCurrentMessages(): MutableList<Message>
        fun messageValueChange(messageChange: Message, index: Int) {}
        fun removeMsgForMe(count: Int) {}
        fun onCancelled(error: Exception) {}
        fun setInformationAccount(accountFocus: UserProfile?) {}
        fun setReceiverProfile(receiverProfile: UserProfile)
        fun setSenderProfile(senderProfile: UserProfile)
        fun sendMsgSuccess()
        fun sendImgSuccess()
        fun sendImgFail()
    }

    fun getMessage(
        sendRoom: String,
        receiverProfile: UserProfile,
        senderProfile: UserProfile,
        instance: MessageFinishListener
    )

    fun submitSendMsg(
        msg: String,
        urlImg: String,
        receiverProfile: UserProfile,
        senderProfile: UserProfile,
        receiveRoom: String,
        sendRoom: String,
        instance: MessageFinishListener
    )

    fun setReceiverProfile(idReceiver: String, instance: MessageFinishListener)
    fun setSenderProfile(instance: MessageFinishListener)
    fun sendImg(
        uriImg: Uri,
        sendRoom: String,
        receiverProfile: UserProfile,
        senderProfile: UserProfile,
        receiveRoom: String,
        instance: MessageFinishListener
    )

    fun unSendMsgForEveryOne(
        keyMsg: String,
        sendRoom: String,
        receiverProfile: UserProfile,
        senderProfile: UserProfile,
        receiveRoom: String
    )

    fun unsendMsgForYou(keyMsg: String,sendRoom:String)

}