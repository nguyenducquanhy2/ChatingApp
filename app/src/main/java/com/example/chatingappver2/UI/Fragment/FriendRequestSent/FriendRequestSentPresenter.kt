package com.example.chatingappver2.UI.Fragment.FriendRequestSent

import android.content.Context
import android.util.Log
import com.example.chatingappver2.Model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*


class FriendRequestSentPresenter(
    private val view: FragmentFriendRequestSent,
    private val context: Context
) :
    FriendRequestSentContract.presenter {
    private val TAG: String = "FragmentFriendRequestSent"

    private val currentUser: FirebaseUser = FirebaseAuth.getInstance().getCurrentUser()!!
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()


    override fun friendRequestSent() {
        database.reference.child("SenderRequest").child(currentUser.uid)
            .addChildEventListener(object :
                ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    database.reference.child("profile").child(snapshot.key!!)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val createUser = createUserProfileByData(snapshot)
                                view.addItemFriendRequest(createUser)
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e(TAG, "onCancelled: ${error.message}")
                            }
                        })
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

    override fun cancelFriendRequest(idReceive: String) {

        database.reference.child("FriendRequest").child(idReceive).child(currentUser.uid)
            .setValue(null)
            .addOnCompleteListener { cancelFriendRequest ->
                if (cancelFriendRequest.isSuccessful) {
                    view.cancelFriendRequestSucces(idReceive)
                } else {
                    view.cancelFriendRequestFail()
                    Log.e(TAG, "cancelFrienfRequest fail:  " + cancelFriendRequest.exception)
                }

            }

        database.reference.child("SenderRequest").child(currentUser.uid).child(idReceive)
            .setValue(null)

    }

    override fun removeItemOfList(
        idReceive: String,
        listFriendRequestSent: MutableList<UserProfile>
    ) {
        var count = 0
        for (userProfile: UserProfile in listFriendRequestSent) {
            val idUser: String = userProfile.idUser
            if ((idUser == idReceive)) {
                view.removeitemOfList(count)
                return
            }
            count++
        }
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