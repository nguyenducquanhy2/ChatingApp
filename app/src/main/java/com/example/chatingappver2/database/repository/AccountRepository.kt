package com.example.chatingappver2.database.repository

import android.util.Log
import com.example.chatingappver2.model.CurrentContacts
import com.example.chatingappver2.contract.repository.AccountRepositoryContract
import com.example.chatingappver2.firebase.ChildEventListenerImp
import com.example.chatingappver2.model.FriendRequest
import com.example.chatingappver2.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AccountRepository(private val auth: FirebaseAuth, private val database: FirebaseDatabase) :
    AccountRepositoryContract {
    private val TAG: String = "AccountRepository"

    override fun getCurrentContact(instance: AccountRepositoryContract.GetContactFinishListener) {
        auth.currentUser?.let {
            database.reference.child("currentContacts").child(it.uid)
                .addChildEventListener(object :
                    ChildEventListenerImp {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        val currentContact: CurrentContacts = createCurrentContact(snapshot)
                        instance.currentContactsAdd(currentContact)
                    }

                    override fun onChildChanged(
                        snapshot: DataSnapshot,
                        previousChildName: String?
                    ) {
                        val currentContactChange = createCurrentContact(snapshot)
                        instance.currentContactsChange(currentContactChange)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        instance.getCurrentError(error.message)
                    }
                })
        }
    }

    override suspend fun getUserNotContact(instance: AccountRepositoryContract.GetUserNotContactFinishListener) {
        val job1 = getListUserNeedHide()

        database.reference.child("profile").addChildEventListener(object : ChildEventListenerImp {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (!job1.containsKey(snapshot.key) && snapshot.key != auth.currentUser?.uid) {
                    val newUserNotContact = createUserProfileByData(snapshot)
                    instance.userNotContactsAdd(newUserNotContact)
                }
            }
        })

    }

    override fun sendRequestAddFriend(
        idUserReceiveRequest: String,
        instance: AccountRepositoryContract.SendFriendRequestFinishListener
    ) {
        database.reference.child("profile").child(auth.currentUser?.uid!!).get()
            .addOnCompleteListener { getUrlAvatarSender ->
                if (getUrlAvatarSender.isSuccessful) {
                    val createUserProfileByData = createUserProfileByData(getUrlAvatarSender.result)

                    val friendRequest =
                        createFriendRequest(
                            idUserReceiveRequest,
                            createUserProfileByData,
                            auth.currentUser!!.uid
                        )

                    sendFriendRequest(
                        idUserReceiveRequest,
                        friendRequest,
                        instance
                    )

                } else {
                    //Log.e(TAG, "getUrlAvatar fail: " + getUrlAvatarSender.exception)
                }

            }
    }

    override fun getListFriendRequest(instance: AccountRepositoryContract.GetListFriendRequest) {
        auth.currentUser?.uid?.let {
            database.reference.child("FriendRequest").child(it)
                .addChildEventListener(object : ChildEventListenerImp {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        val createFriendRequest = createFriendRequest(snapshot)
                        instance.friendRequestsAdd(createFriendRequest)
                    }

                    override fun onChildRemoved(snapshot: DataSnapshot) {
                        val createFriendRequest = createFriendRequest(snapshot)
                        instance.friendRequestsRemove(createFriendRequest.idUserSendRequest)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        instance.friendRequestsError(error.message)
                    }
                })
        }
    }

    override fun acceptFriendRequest(friendRequest: FriendRequest) {
        updateFriendRequest(friendRequest)
        CoroutineScope(Dispatchers.Default).launch { getUserProfile(friendRequest) }
    }

    override fun deleteFriendRequest(
        idSender: String,
        instance: AccountRepositoryContract.CancelRequestFinishListener
    ) {
        auth.currentUser?.uid?.let { currentUserUid ->
            database.reference.child("FriendRequest").child(currentUserUid).child(idSender)
                .setValue(null)
                .addOnCompleteListener { sendRequestAddFriend ->
                    if (sendRequestAddFriend.isSuccessful) {
                        instance.cancelRequestSuccess(idSender)
                    } else {
                        sendRequestAddFriend.exception?.message?.let { message ->
                            instance.cancelRequestError(
                                message
                            )
                        }
                        Log.e(TAG, "cancelFriendRequest fail:  " + sendRequestAddFriend.exception)
                    }

                }

            database.reference.child("SenderRequest").child(idSender).child(currentUserUid)
                .setValue(null)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "cancelFriendRequest: success")
                    } else {
                        Log.d(TAG, "cancelFriendRequest: fail")
                    }
                }
        }

    }

    override fun getListFriendRequestSent(instance: AccountRepositoryContract.ListFriendRequestSent) {
        auth.currentUser?.uid?.let {
            database.reference.child("SenderRequest").child(it)
                .addChildEventListener(object :
                    ChildEventListenerImp {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                        database.reference.child("profile").child(snapshot.key!!)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val createUser = createUserProfileByData(snapshot)
                                    instance.friendRequestsAdd(createUser)
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    instance.friendRequestsError(error.message)
                                }
                            })

                    }

                    override fun onCancelled(error: DatabaseError) {
                        instance.friendRequestsError(error.message)
                    }
                })
        }

    }

    override fun revokeFriendRequest(
        idReceive: String,
        instance: AccountRepositoryContract.RevokeFriendRequestFinishListener
    ) {
        auth.currentUser?.uid?.let {
            database.reference.child("FriendRequest").child(idReceive).child(it)
                .setValue(null)
                .addOnCompleteListener { cancelFriendRequest ->
                    if (cancelFriendRequest.isSuccessful) {
                        instance.revokeFriendRequestSuccess(idReceive)
                    } else {
                        cancelFriendRequest.exception?.message?.let { it1 ->
                            instance.revokeFriendRequestError(
                                it1
                            )
                        }
                    }

                }

            database.reference.child("SenderRequest").child(it).child(idReceive)
                .setValue(null)
        }

    }

    private suspend fun getUserProfile(friendRequest: FriendRequest) {
        val userSender = database.reference.child("profile").child(friendRequest.idUserSendRequest)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "getUserProfile: get userSender success")
                } else {
                    Log.d(TAG, "getUserProfile: get userSender fail")
                }
            }

        val userReceive =
            database.reference.child("profile").child(friendRequest.idUserReceiveRequest)
                .get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "getUserProfile: get userSender success")
                    } else {
                        Log.d(TAG, "getUserProfile: get userSender fail")
                    }
                }

        val userProfileSender = createCurrentUserOnData(userSender.await())
        val userProfileReceive = createCurrentUserOnData(userReceive.await())

        setRelationContacts(userProfileSender, userProfileReceive)

    }

    private fun updateFriendRequest(friendRequest: FriendRequest) {
        database.reference.child("FriendRequest")
            .child(friendRequest.idUserReceiveRequest)
            .child(friendRequest.idUserSendRequest).setValue(null)

        database.reference.child("SenderRequest").child(friendRequest.idUserSendRequest)
            .child(friendRequest.idUserReceiveRequest).setValue(null)
    }

    private fun setRelationContacts(
        UserProfileSender: UserProfile,
        UserProfileReceive: UserProfile
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            launch {
                database.reference.child("currentContacts")
                    .child(UserProfileSender.idUser).child(UserProfileReceive.idUser)
                    .setValue(UserProfileReceive)
            }

            launch {
                database.reference.child("currentContacts")
                    .child(UserProfileReceive.idUser).child(UserProfileSender.idUser)
                    .setValue(UserProfileSender)
            }
        }
    }

    private fun sendFriendRequest(
        idUserReceiveRequest: String,
        friendRequest: FriendRequest,
        sendFriendRequestFinishListener: AccountRepositoryContract.SendFriendRequestFinishListener
    ) {

        database.reference.child("FriendRequest").child(idUserReceiveRequest)
            .child(auth.currentUser?.uid!!)
            .setValue(friendRequest)
            .addOnCompleteListener { sendRequestAddFriend ->
                if (sendRequestAddFriend.isSuccessful) {
                    sendFriendRequestFinishListener.SendFriendRequestSuccess(idUserReceiveRequest)

                } else {
                    sendRequestAddFriend.exception?.message?.let {
                        sendFriendRequestFinishListener.SendFriendRequestError(
                            it
                        )
                    }
                    //Log.e(TAG, "sendFriendRequest fail:  " + sendRequestAddFriend.getException())
                }

            }

        database.reference.child("SenderRequest").child(auth.currentUser?.uid!!)
            .child(idUserReceiveRequest)
            .setValue("Request...").addOnCompleteListener {
//                if (task.isSuccessful()) {
//                    //Log.d(TAG, "SenderRequest: success")
//                } else {
//                    //Log.d(TAG, "SenderRequest: fail")
//                }
            }
    }

    private suspend fun getListUserNeedHide(): MutableMap<String, Int> {
        val mapListUserContainsContacts: MutableMap<String, Int> = mutableMapOf()
        val currentUser = auth.currentUser!!

        val listCurrentUser = database.reference.child("currentContacts").child(currentUser.uid)
            .get()

        val listSenderRequest = database.reference.child("SenderRequest").child(currentUser.uid)
            .get()

        val listFriendRequest = database.reference.child("FriendRequest").child(currentUser.uid)
            .get()

        for (itemCurrentUser in listCurrentUser.await().children) {
            mapListUserContainsContacts[itemCurrentUser.key!!] = 1
        }

        for (itemSenderRequest in listSenderRequest.await().children) {
            mapListUserContainsContacts[itemSenderRequest.key!!] = 1
        }

        for (itemFriendRequest in listFriendRequest.await().children) {
            mapListUserContainsContacts[itemFriendRequest.key!!] = 1
        }

        return mapListUserContainsContacts
    }


    companion object {
        fun createCurrentContact(dataSnapshot: DataSnapshot): CurrentContacts {
            val map: Map<*, *> = dataSnapshot.value as Map<*, *>

            val fullname: String = map["fullname"].toString()
            val urlImgProfile: String = map["urlImgProfile"].toString()
            val dateOfBirth: String = map["dateOfBirth"].toString()
            val email: String = map["email"].toString()
            val idUser: String = map["idUser"].toString()
            val lastSentDate: String = map["lastSentDate"].toString()
            val theyIsActive: Boolean = map["theyIsActive"].toString().toBoolean()

            val idUserLastSentMsg: String = map["idUserLastSentMsg"].toString()
            val lastSentMsg: String = map["lastSentMsg"].toString()
            val notifyNewMsg: Boolean = map["notifyNewMsg"].toString().toBoolean()
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

        fun createFriendRequest(
            idUserReceiveRequest: String,
            userProfile: UserProfile,
            currentUserUid: String
        ): FriendRequest {
            val accept = false
            val urlAvatarUserSendRequest: String = userProfile.urlImgProfile
            val nameUserSendRequest: String = userProfile.fullname
            val idUserSendRequest: String = currentUserUid

            return FriendRequest(
                accept,
                idUserReceiveRequest,
                idUserSendRequest,
                nameUserSendRequest,
                urlAvatarUserSendRequest
            )
        }

        fun createUserProfileByData(dataSnapshot: DataSnapshot): UserProfile {
            val map: Map<*, *> = dataSnapshot.value as Map<*, *>
            val dateOfBirth: String = map["dateOfBirth"].toString()
            val email: String = map["email"].toString()
            val fullname: String = map["fullname"].toString()
            val idUser: String = map["idUser"].toString()
            val theyIsActive: Boolean = map["theyIsActive"].toString().toBoolean()
            val urlImgProfile: String = map["urlImgProfile"].toString()

            return UserProfile(dateOfBirth, email, fullname, idUser, theyIsActive, urlImgProfile)
        }

        fun createCurrentUserOnData(dataSnapshot: DataSnapshot): UserProfile {

            val map: Map<*, *> = dataSnapshot.value as Map<*, *>
            val dateOfBirth: String = map["dateOfBirth"].toString()
            val email: String = map["email"].toString()
            val fullname: String = map["fullname"].toString()
            val idUser: String = map["idUser"].toString()
            val theyIsActive: Boolean = map["theyIsActive"].toString().toBoolean()
            val urlImgProfile: String = map["urlImgProfile"].toString()

            return UserProfile(dateOfBirth, email, fullname, idUser, theyIsActive, urlImgProfile)
        }

        fun createFriendRequest(dataSnapshot: DataSnapshot): FriendRequest {

            val map: Map<*, *> = dataSnapshot.value as Map<*, *>
            val urlAvatarUserSendRequest = map["urlAvatarUserSendRequest"].toString()
            val nameUserSendRequest = map["nameUserSendRequest"].toString()
            val accept: Boolean = map["accept"].toString().toBoolean()
            val idUserReceiveRequest: String = map["idUserReceiveRequest"].toString()
            val idUserSendRequest: String = map["idUserSendRequest"].toString()

            return FriendRequest(
                accept,
                idUserReceiveRequest,
                idUserSendRequest,
                nameUserSendRequest,
                urlAvatarUserSendRequest
            )
        }
    }


}