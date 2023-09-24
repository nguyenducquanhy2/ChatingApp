package com.example.chatingappver2.presenter.activity

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.example.chatingappver2.model.Message
import com.example.chatingappver2.model.UserProfile
import com.example.chatingappver2.model.CurrentContacts
import com.example.chatingappver2.contract.activity.ChatActivityContract
import com.example.chatingappver2.contract.repository.MessageRepositoryContract
import com.example.chatingappver2.database.repository.MessageRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class ChatActivityPresenter(
    private val view: ChatActivityContract.view,
    private val repository: MessageRepository
) :
    ChatActivityContract.presenter, MessageRepositoryContract.MessageFinishListener {
    private val TAG: String = "ChatActivityPresenter"

    private var receiverProfile: UserProfile? = null
    private var senderProfile: UserProfile? = null
    private var receiveRoom: String? = null
    private var sendRoom: String? = null

    override fun setAcountForcus(idReceiver: String) {
        val senderMsg = FirebaseAuth.getInstance().currentUser!!

        sendRoom = senderMsg.uid + idReceiver
        receiveRoom = idReceiver + senderMsg.uid
        setReceiverProfile(idReceiver)
        setSenderProfile()
    }

    override fun setInformationAccount(accountFocus: UserProfile?) {
        view.setInformationAccount(accountFocus)
    }

    private fun setReceiverProfile(idReceiver: String) {
        repository.setReceiverProfile(idReceiver, this)
    }

    override fun setReceiverProfile(receiverProfile: UserProfile) {
        this@ChatActivityPresenter.receiverProfile = receiverProfile
        if (this@ChatActivityPresenter.senderProfile != null && this@ChatActivityPresenter.receiverProfile != null){
            getMessages()
        }
    }

    override fun setSenderProfile(senderProfile: UserProfile) {
        this@ChatActivityPresenter.senderProfile = senderProfile
        if (this@ChatActivityPresenter.senderProfile != null && this@ChatActivityPresenter.receiverProfile != null){
            getMessages()
        }
    }

    override fun sendMsgSuccess() {
        view.sendMsgSuccess()
    }

    override fun sendImgSuccess() {
        view.sendImgSuccess()
    }

    override fun sendImgFail() {
        view.sendImgFail()
    }

    private fun setSenderProfile() {
        repository.setSenderProfile(this)
    }

    override fun submitSendMsg(msg: String, urlImg: String) {

        if (msg.trim().isEmpty()) return

        if (sendRoom == null) return

        if (receiverProfile == null) return

        if (senderProfile == null) return

        if (receiveRoom == null) return

        repository.submitSendMsg(
            msg,
            urlImg,
            receiverProfile!!,
            senderProfile!!,
            receiveRoom!!,
            sendRoom!!, this
        )

    }

    override fun addMessage(message: Message) {
        view.addMessage(message)
    }

    override fun getCurrentMessages(): MutableList<Message> {
        return view.getCurrentMessages()
    }

    override fun messageValueChange(messageChange: Message, index: Int) {
        view.messageValueChange(messageChange, index)
    }

    override fun removeMsgForMe(count: Int) {
        view.removeMsgForMe(count)
    }

    override fun onCancelled(error: Exception) {
        Log.e(TAG, "onCancelled: ${error.message}")
    }

    override fun getMessages() {
        if (sendRoom == null) return

        if (receiverProfile == null) return

        if (senderProfile == null) return

        repository.getMessage(sendRoom!!, receiverProfile!!, senderProfile!!, this)
    }

    override fun sendImg(uriImg: Uri) {
        if (sendRoom == null) return

        if (receiverProfile == null) return

        if (senderProfile == null) return

        if (receiveRoom == null) return

        repository.sendImg(
            uriImg,
            sendRoom!!,
            receiverProfile!!,
            senderProfile!!,
            receiveRoom!!,
            this
        )
    }

    override fun getImageUriAndSending(contentResolver: ContentResolver, uriImg: Uri) {
        val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uriImg)
        val createTempFile = File.createTempFile("temprentpk", ".png")
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val fileOutputStream = FileOutputStream(createTempFile)
        fileOutputStream.write(byteArray)
        fileOutputStream.flush()
        fileOutputStream.close()
        val imageURI = Uri.fromFile(createTempFile)
        sendImg(imageURI)
    }

    override fun unsendMsgForEveryOne(keyMsg: String) {
        if (keyMsg.isEmpty()) return

        if (sendRoom == null) return

        if (receiverProfile == null) return

        if (senderProfile == null) return

        if (receiveRoom == null) return

        repository.unSendMsgForEveryOne(
            keyMsg,
            sendRoom!!,
            receiverProfile!!,
            senderProfile!!,
            receiveRoom!!
        )
    }


    override fun unsendMsgForYou(keyMsg: String) {
        if (sendRoom == null) return
        repository.unsendMsgForYou(keyMsg, sendRoom!!)
    }


}