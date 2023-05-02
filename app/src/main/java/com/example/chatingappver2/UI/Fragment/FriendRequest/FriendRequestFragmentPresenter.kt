package com.example.chatingappver2.UI.Fragment.FriendRequest

import android.content.Context
import android.util.Log
import com.example.chatingappver2.Model.FriendRequest
import com.example.chatingappver2.Model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class FriendRequestFragmentPresenter(
    private val view: FriendRequestContract.view,
    private val context: Context
) : FriendRequestContract.presenter {
    val TAG: String = "FriendRequestFragmentPresenter"

    private val currentUser: FirebaseUser = FirebaseAuth.getInstance().getCurrentUser()!!
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val listFriendRequest: MutableList<FriendRequest?> = mutableListOf()


    override fun acceptFriendRequest(friendRequest: FriendRequest) {
        UpdateFriendRequest(friendRequest)
        CoroutineScope(Dispatchers.Default).launch { getUserProfile(friendRequest) }

    }

    override fun deleteFriendRequest(idSender: String) {

        database.getReference().child("FriendRequest").child(currentUser.uid).child(idSender)
            .setValue(null)
            .addOnCompleteListener { sendRequestAddFriend ->
                if (sendRequestAddFriend.isSuccessful()) {
                    view.cancelFriendRequestSucces(idSender)
                } else {
                    view.cancelFriendRequestFail()
                    Log.e(TAG, "cancelFrienfRequest fail:  " + sendRequestAddFriend.getException())
                }

            }

        database.getReference().child("SenderRequest").child(idSender).child(currentUser.uid)
            .setValue(null)
            .addOnCompleteListener { task ->
                if (task.isSuccessful()) {
                    Log.d(TAG, "cancelFrienfRequest: success")
                } else {
                    Log.d(TAG, "cancelFrienfRequest: fail")
                }
            }
    }


    override fun findIndexNeedRemoveInList(idSender: String, list: List<FriendRequest>) {
        var count: Int = 0
        for (friendRequest in listFriendRequest) {
            if (friendRequest?.idUserSendRequest.equals(idSender)) {
                view.removeItemOfListByIndex(count)
                return
            }
            count++
        }
    }

    override fun getListFrienRequestOnDB() {

        database.getReference().child("FriendRequest").child(currentUser.getUid())
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val createFriendRequest = createFriendRequest(snapshot)
                    listFriendRequest.add(createFriendRequest)
                    view.setFrienRequest(createFriendRequest)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val createFriendRequest = createFriendRequest(snapshot)
                    var count = 0
                    for (item in listFriendRequest) {
                        if (item?.idUserSendRequest == createFriendRequest.idUserSendRequest) {
                            break
                        }
                        count++
                    }
                    listFriendRequest.removeAt(count)
                    view.removeOnListRequest(count)
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

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

        val UserProfileSender = createCurrentUserOnData(userSender.await())
        val UserProfileReceive = createCurrentUserOnData(userReceive.await())

        SetRelationContacts(UserProfileSender, UserProfileReceive)

    }

    private fun UpdateFriendRequest(friendRequest: FriendRequest) {
        database.reference.child("FriendRequest")
            .child(friendRequest.idUserReceiveRequest)
            .child(friendRequest.idUserSendRequest).setValue(null)

        database.reference.child("SenderRequest").child(friendRequest.idUserSendRequest)
            .child(friendRequest.idUserReceiveRequest).setValue(null)
    }

    private fun SetRelationContacts(
        UserProfileSender: UserProfile,
        UserProfileReceive: UserProfile
    ) {

        database.reference.child("currentContacts")
            .child(UserProfileSender.idUser).child(UserProfileReceive.idUser)
            .setValue(UserProfileReceive).addOnCompleteListener { task ->
                if (task.isSuccessful) {

                }
            }

        database.reference.child("currentContacts")
            .child(UserProfileReceive.idUser).child(UserProfileSender.idUser)
            .setValue(UserProfileSender).addOnCompleteListener { task ->
                if (task.isSuccessful) {

                }
            }

    }

    private fun createCurrentUserOnData(dataSnapshot: DataSnapshot): UserProfile {

        val map: Map<*, *> = dataSnapshot.value as Map<*, *>
        val dateOfBirth: String = map.get("dateOfBirth").toString()
        val email: String = map.get("email").toString()
        val fullname: String = map.get("fullname").toString()
        val idUser: String = map.get("idUser").toString()
        val theyIsActive: Boolean = map.get("theyIsActive").toString().toBoolean()
        val urlImgProfile: String = map.get("urlImgProfile").toString()

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