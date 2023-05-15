package com.example.chatingappver2.UI.Fragment.NewContacts

import android.util.Log
import com.example.chatingappver2.Model.FriendRequest
import com.example.chatingappver2.Model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class fragmentNewContactsPresenter(private val view: NewContactsContract.view) :
    NewContactsContract.presenter {
    private val TAG: String = "fragmentNewContactsPresenter"
    private val currentUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()


    override fun getNewUser() {
        CoroutineScope(Dispatchers.Default).launch { getUserNotContact() }
    }

    private suspend fun getListUserNeedHide(): MutableMap<String, Int> {
        val mapListUserContainsContacts: MutableMap<String, Int> = mutableMapOf()

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


    private suspend fun getUserNotContact() {
        val job1=  getListUserNeedHide()

        database.reference.child("profile").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if ((job1.containsKey(snapshot.key) == false)&&snapshot.key!=currentUser.uid) {
                    val newUserNotContact = createUserProfileByData(snapshot)
                    view.addItemToList(newUserNotContact)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    // com.example.chatapplication.Ui.Fragment.NewContacts.NewContactsContract.presenter
    override fun sendRequestAddFriend(idUserReceiveRequest: String) {

        database.reference.child("profile").child(currentUser.uid).get()
            .addOnCompleteListener { getUrlAvatarSender ->
                if (getUrlAvatarSender.isSuccessful) {
                    val createUserProfileByData = createUserProfileByData(getUrlAvatarSender.result)
                    val friendRequest =
                        createFriendRequest(idUserReceiveRequest, createUserProfileByData)
                    sendFriendRequest(idUserReceiveRequest, friendRequest)

                } else {
                    Log.e(TAG, "getUrlAvatar fail: " + getUrlAvatarSender.exception)
                }

            }
    }

    // com.example.chatapplication.Ui.Fragment.NewContacts.NewContactsContract.presenter
    override fun fillterData(ListUser: List<UserProfile>, FindvalueInput: String) {
        if (FindvalueInput.isEmpty()) {
            view.setOldUserProfileList()
            return
        }
        val newListUser: MutableList<UserProfile> = mutableListOf()

        for (userProfile in ListUser) {
            val fullname: String = userProfile.fullname
            if (fullname.contains(FindvalueInput)) {
                newListUser.add(userProfile)
            }
        }

        if (newListUser.size != 0) {
            view.setNewUserProfileList(newListUser)
        } else {
            view.setOldUserProfileList()
        }
    }

    // com.example.chatapplication.Ui.Fragment.NewContacts.NewContactsContract.presenter
    override fun findIndexRemove(idUserReceiveRequest: String?, ListUser: List<UserProfile>) {
        var count = 0
        for (userProfile in ListUser) {
            if (userProfile.idUser == idUserReceiveRequest) {
                view.removeItemOfListByIndex(count)
                break
            }
            count++
        }
    }

    private fun sendFriendRequest(idUserReceiveRequest: String, friendRequest: FriendRequest) {
        val child: DatabaseReference =
            database.reference.child("FriendRequest").child(idUserReceiveRequest)
        val firebaseUser: FirebaseUser = currentUser
        database.reference.child("FriendRequest").child(idUserReceiveRequest).child(currentUser.uid)
            .setValue(friendRequest)
            .addOnCompleteListener { sendRequestAddFriend ->
                if (sendRequestAddFriend.isSuccessful) {
                    view.notifySendFriendRequestSuccess(idUserReceiveRequest)

                } else {
                    view.notifySendFriendRequestFail()
                    Log.e(TAG, "sendFriendRequest fail:  " + sendRequestAddFriend.getException())
                }

            }

        database.reference.child("SenderRequest").child(currentUser.uid).child(idUserReceiveRequest)
            .setValue("Request...").addOnCompleteListener { task ->
                if (task.isSuccessful()) {
                    Log.d(TAG, "SenderRequest: success")
                } else {
                    Log.d(TAG, "SenderRequest: fail")
                }
            }
    }

    private fun createFriendRequest(
        idUserReceiveRequest: String,
        userProfile: UserProfile
    ): FriendRequest {
        val accept = false
        val urlAvatarUserSendRequest: String = userProfile.urlImgProfile
        val nameUserSendRequest: String = userProfile.fullname
        val idUserSendRequest: String = currentUser.uid

        return FriendRequest(
            accept,
            idUserReceiveRequest,
            idUserSendRequest,
            nameUserSendRequest,
            urlAvatarUserSendRequest
        )
    }

    private fun createUserProfileByData(dataSnapshot: DataSnapshot): UserProfile {
        val map: Map<*, *> = dataSnapshot.value as Map<*, *>
        val dateOfBirth: String = map.get("dateOfBirth").toString()
        val email: String = map.get("email").toString()
        val fullname: String = map.get("fullname").toString()
        val idUser: String = map.get("idUser").toString()
        val theyIsActive: Boolean = map.get("theyIsActive").toString().toBoolean()
        val urlImgProfile: String = map.get("urlImgProfile").toString()

        return UserProfile(dateOfBirth, email, fullname, idUser, theyIsActive, urlImgProfile)
    }


}