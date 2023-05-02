package com.example.chatingappver2.UI.Activity.chatActivity

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.example.chatingappver2.Model.Message
import com.example.chatingappver2.Model.UserProfile
import com.example.chatingappver2.Model.currentContacts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ChatActivityPresenter(
    private val view: ChatActivityContract.view,
    private val context: Context
) :
    ChatActivityContract.presenter {
    private var receiverProfile: UserProfile? = null
    private var senderProfile: UserProfile? = null
    private val TAG: String = "ChatActivityPresenter"
    private val currentContacts: MutableList<UserProfile?> = mutableListOf()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val formatter: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
    private var receiveRoom: String? = null

    private var sendRoom: String? = null
    private val senderMsg: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
    private val storageDatabase: FirebaseStorage = FirebaseStorage.getInstance()
    private val time: Date = Calendar.getInstance().time


    fun setAcountForcus(idReceiver: String) {

        sendRoom = senderMsg.uid + idReceiver
        receiveRoom = idReceiver + senderMsg.uid
        setReceiverProfile(idReceiver)
        setSenderProfile()


    }

    private fun setReceiverProfile(idReceiver: String) {
        database.reference.child("profile").child(idReceiver)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    val createUserProfile = createUserProfile(snapshot)
                    this@ChatActivityPresenter.receiverProfile = createUserProfile

                    view.setInformationAccount(createUserProfile)
                }

                override fun onCancelled(error: DatabaseError) {

                    Log.e(TAG, "onCancelled: " + error.message)
                }
            })
    }

    private fun setSenderProfile() {
        database.reference.child("profile").child(senderMsg.uid)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val createUserProfile = createUserProfile(snapshot)
                    this@ChatActivityPresenter.senderProfile = createUserProfile
                }

                // com.google.firebase.database.ValueEventListener
                override fun onCancelled(error: DatabaseError) {

                    Log.e(TAG, "onCancelled: " + error.message)
                }
            })
    }

    private fun createUserProfile(dataSnapshot: DataSnapshot): UserProfile {

        val map: Map<*, *> = dataSnapshot.value as Map<*, *>
        val dateOfBirth: String = map.get("dateOfBirth").toString()
        val email: String = map.get("email").toString()
        val fullname: String = map.get("fullname").toString()
        val idUser: String = map.get("idUser").toString()
        val theyIsActive: Boolean = map.get("theyIsActive").toString().toBoolean()
        val urlImgProfile: String = map.get("urlImgProfile").toString()

        return UserProfile(dateOfBirth, email, fullname, idUser, theyIsActive, urlImgProfile)
    }

    override fun submitSendMsg(msg: String, urlImg: String) {
        if (msg.trim().isEmpty()) {
            return
        }

        val keyMsg = database.reference.push().key.toString()
        val MESSAGE = createMessage(msg, urlImg)

        database.reference.child("chats").child(sendRoom!!).child("message").child(keyMsg)
            .setValue(MESSAGE)

        database.reference.child("chats").child(receiveRoom!!).child("message").child(keyMsg)
            .setValue(MESSAGE)

        sendNotifycationForThem(MESSAGE)
        view.sendMsgSuccess()
    }

    override fun getMessages() {

        database.reference.child("chats").child(sendRoom!!).child("message").orderByKey()
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val viewVar: ChatActivityContract.view
                    val message: Message = snapshot.getValue(Message::class.java) as Message
                    message.keyMsg = snapshot.key

                    view.addMessage(message)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {

                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "onCancelled: ${error.message}")
                }
            })
    }

    override fun sendImg(uriImg: Uri) {
        val date = Date().toString()

        storageDatabase.reference.child("imgChat").child(sendRoom!!).child(date).putFile(uriImg)
            .addOnCompleteListener { upLoadImage ->
                if (upLoadImage.isSuccessful) {
                    storageDatabase.reference.child("imgChat").child(sendRoom!!)
                        .child(date).downloadUrl
                        .addOnCompleteListener { getUrlImage ->
                            if (getUrlImage.isSuccessful) {
                                if (getUrlImage.isSuccessful()) {
                                    val uri = getUrlImage.result.toString()
                                    view.sendImgSuccess()
                                    submitSendMsg("photo", uri)

                                } else {
                                    view.sendImgFail()
                                }
                            } else {
                                Log.e(
                                    TAG,
                                    "submitProfileToFireBase: putImg " + getUrlImage.exception
                                )
                            }

                        }
                }


            }

    }

    override fun getImageUriAndSending(contentResolver: ContentResolver, uriImg: Uri) {
        val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uriImg)
        val createTempFile = File.createTempFile("temprentpk", ".png")
        val byteArrayOutputStream = ByteArrayOutputStream()
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        }
        val byteArray = byteArrayOutputStream.toByteArray()
        val fileOutputStream = FileOutputStream(createTempFile)
        fileOutputStream.write(byteArray)
        fileOutputStream.flush()
        fileOutputStream.close()
        val imageURI = Uri.fromFile(createTempFile)
        sendImg(imageURI)
    }

    private fun rotateImage(bitmap: Bitmap, i: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(i.toFloat())
        val createBitmap: Bitmap =
            Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.getWidth(),
                bitmap.getHeight(),
                matrix,
                true
            )
        bitmap.recycle()
        return createBitmap
    }

    private fun createMessage(Message: String, urlImg: String): Message {
        val idSender: String = senderMsg.uid
        val dateSend = formatter.format(time)
        return Message(idSender, Message, dateSend, urlImg)
    }

    private fun sendNotifycationForThem(message: Message) {
        val notifySender=createCurrentUser(receiverProfile!!,message)
        notifySender.notifyNewMsg=false

        val notifyReceiver=createCurrentUser(senderProfile!!,message)

        database.reference.child("currentContacts").child(senderMsg.uid)
            .child(receiverProfile!!.idUser)
            .setValue(notifySender)

        database.reference.child("currentContacts").child(receiverProfile!!.idUser)
            .child(senderMsg.uid)
            .setValue(notifyReceiver)

    }

    private fun createCurrentUser(userProfile: UserProfile,message: Message): currentContacts {


        val fullname: String = userProfile.fullname
        val urlImgProfile: String = userProfile.urlImgProfile
        val dateOfBirth: String = userProfile.dateOfBirth
        val email: String = userProfile.email
        val idUser: String = userProfile.idUser
        val theyIsActive: Boolean = userProfile.theyIsActive == true

        val lastSentDate: String = message.time
        val idUserLastSentMsg: String = senderMsg.uid
        val lastSentMsg: String = message.msgTxt
        val notifyNewMsg = true

        return currentContacts(
            dateOfBirth,
            email,
            fullname,
            idUser,
            theyIsActive,
            urlImgProfile,
            idUserLastSentMsg,
            lastSentDate,
            lastSentMsg,
            notifyNewMsg
        )
    }


}