package com.example.chatingappver2.database.repository

import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import com.example.chatingappver2.contract.repository.MessageRepositoryContract
import com.example.chatingappver2.firebase.ChildEventListenerImp
import com.example.chatingappver2.model.CurrentContacts
import com.example.chatingappver2.model.Message
import com.example.chatingappver2.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class MessageRepository(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase,
    private val storageReference: StorageReference
) : MessageRepositoryContract {

    private val formatter: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
    private val time: Date = Calendar.getInstance().time


    override fun getMessage(
        sendRoom: String,
        receiverProfile: UserProfile,
        senderProfile: UserProfile,
        instance: MessageRepositoryContract.MessageFinishListener
    ) {

        database.reference.child("chats").child(sendRoom).child("message").orderByKey()
            .addChildEventListener(object : ChildEventListenerImp {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val message: Message = snapshot.getValue(Message::class.java) as Message
                    message.keyMsg = snapshot.key

                    instance.addMessage(message)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val messageChange: Message = snapshot.getValue(Message::class.java)!!
                    messageChange.keyMsg = snapshot.key
                    val messages = instance.getCurrentMessages()

                    if (messages[messages.lastIndex].keyMsg == messageChange.keyMsg) {
                        instance.messageValueChange(messageChange, messages.lastIndex)
                        val msgSenderRemove = "You unsent a message"
                        val msgReceiverRemove = "${receiverProfile?.fullname} unsent a message"
                        for (item in (messages.lastIndex - 1) downTo 1) {
                            if (messages[item].msgTxt != msgSenderRemove && messages[item].msgTxt != msgReceiverRemove) {
                                sendNotifycationForThem(
                                    messages[item],
                                    receiverProfile,
                                    senderProfile
                                )
                                break
                            }

                        }

                    }

                    var count = 0
                    for (item in messages) {
                        if (item.keyMsg == messageChange.keyMsg) {
                            instance.messageValueChange(messageChange, count)
                            break
                        }
                        count++
                    }

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val messageRemove: Message = snapshot.getValue(Message::class.java)!!
                    messageRemove.keyMsg = snapshot.key
                    val messages = instance.getCurrentMessages()

                    if (messages[messages.lastIndex].keyMsg == messageRemove.keyMsg) {
                        instance.removeMsgForMe(messages.lastIndex)
                        val msgSenderRemove = "You unsent a message"
                        val msgReceiverRemove = "${receiverProfile?.fullname} unsent a message"
                        for (item in (messages.lastIndex - 1) downTo 1) {
                            if (messages[item].msgTxt != msgSenderRemove && messages[item].msgTxt != msgReceiverRemove) {
                                sendNotifycationForThem(
                                    messages[item],
                                    receiverProfile,
                                    senderProfile
                                )
                                break
                            }

                        }
                        return
                    }

                    var count = 0
                    for (item in messages) {
                        if (item.keyMsg == messageRemove.keyMsg) {
                            instance.removeMsgForMe(count)
                            break
                        }
                        count++
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    instance.onCancelled(error.toException())
                }
            })
    }

    override fun setReceiverProfile(
        idReceiver: String,
        instance: MessageRepositoryContract.MessageFinishListener
    ) {
        database.reference.child("profile").child(idReceiver)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    val createUserProfile = createUserProfile(snapshot)
                    instance.setReceiverProfile(createUserProfile)

                    instance.setInformationAccount(createUserProfile)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    override fun submitSendMsg(
        msg: String,
        urlImg: String,
        receiverProfile: UserProfile,
        senderProfile: UserProfile,
        receiveRoom: String,
        sendRoom: String,
        instance: MessageRepositoryContract.MessageFinishListener
    ) {
        val keyMsg = database.reference.push().key.toString()
        val message = createMessage(msg, urlImg)

        database.reference.child("chats").child(sendRoom).child("message").child(keyMsg)
            .setValue(message)

        database.reference.child("chats").child(receiveRoom).child("message").child(keyMsg)
            .setValue(message)

        sendNotifycationForThem(message, receiverProfile, senderProfile)
        instance.sendMsgSuccess()
    }

    override fun setSenderProfile(instance: MessageRepositoryContract.MessageFinishListener) {
        database.reference.child("profile").child(auth.currentUser?.uid!!)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val createUserProfile = createUserProfile(snapshot)
                    instance.setSenderProfile(createUserProfile)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    override fun sendImg(
        uriImg: Uri,
        sendRoom: String,
        receiverProfile: UserProfile,
        senderProfile: UserProfile,
        receiveRoom: String,
        instance: MessageRepositoryContract.MessageFinishListener
    ) {
        val date = Date().toString()

        storageReference.child("imgChat").child(sendRoom).child(date).putFile(uriImg)
            .addOnCompleteListener { upLoadImage ->
                if (upLoadImage.isSuccessful) {
                    storageReference.child("imgChat").child(sendRoom)
                        .child(date).downloadUrl
                        .addOnCompleteListener { getUrlImage ->
                            if (getUrlImage.isSuccessful) {
                                val uri = getUrlImage.result.toString()
                                instance.sendImgSuccess()
                                submitSendMsg(
                                    "photo", uri,
                                    receiverProfile,
                                    senderProfile,
                                    receiveRoom,
                                    sendRoom,
                                    instance
                                )

                            } else {
                                instance.sendImgFail()
                            }
                        }
                }
            }
    }

    override fun unSendMsgForEveryOne(
        keyMsg: String,
        sendRoom: String,
        receiverProfile: UserProfile,
        senderProfile: UserProfile,
        receiveRoom: String
    ) {
        val msgSenderRemove = "You unsent a message"
        val msgReceiverRemove = "${senderProfile.fullname} unsent a message"

        database.reference.child("chats").child(receiveRoom).child("message").child(keyMsg)
            .setValue(createMsgNull(msgReceiverRemove))

        database.reference.child("chats").child(sendRoom).child("message").child(keyMsg)
            .setValue(createMsgNull(msgSenderRemove))
    }

    private fun createMsgNull(msgRemove: String): Message {
        val keyMsg = database.reference.push().key.toString()
        return createMessage(msgRemove, "")
    }

    override fun unsendMsgForYou(keyMsg: String, sendRoom: String) {
        database.reference.child("chats").child(sendRoom).child("message").child(keyMsg)
            .removeValue()
    }

    private fun sendNotifycationForThem(
        message: Message,
        receiverProfile: UserProfile,
        senderProfile: UserProfile
    ) {
        val notifySender = createCurrentUser(receiverProfile, message)
        notifySender.notifyNewMsg = false

        val notifyReceiver = createCurrentUser(senderProfile, message)

        auth.currentUser?.uid?.let {
            database.reference.child("currentContacts").child(it)
                .child(receiverProfile.idUser)
                .setValue(notifySender)

            database.reference.child("currentContacts").child(receiverProfile.idUser)
                .child(it)
                .setValue(notifyReceiver)
        }


    }

    private fun createCurrentUser(userProfile: UserProfile, message: Message): CurrentContacts {


        val fullname: String = userProfile.fullname
        val urlImgProfile: String = userProfile.urlImgProfile
        val dateOfBirth: String = userProfile.dateOfBirth
        val email: String = userProfile.email
        val idUser: String = userProfile.idUser
        val theyIsActive: Boolean = userProfile.theyIsActive == true

        val lastSentDate: String = message.time
        val idUserLastSentMsg: String = auth.currentUser?.uid!!
        val lastSentMsg: String = message.msgTxt
        val notifyNewMsg = true

        return CurrentContacts(
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

        val idSender: String = auth.currentUser?.uid!!
        val dateSend = formatter.format(time)
        return Message(idSender, Message, dateSend, urlImg)
    }

}