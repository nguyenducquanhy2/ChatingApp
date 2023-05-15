package com.example.chatingappver2.UI.Activity.MainActivity

import android.content.Context
import android.util.Log
import com.example.chatingappver2.Model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityPresenter(
    private val view: MainActivityContract.view,
    private val context: Context
) :
    MainActivityContract.presenter {
    private val TAG: String = "MainActivityPresenter"
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser!!
    private val database = FirebaseDatabase.getInstance()

    private fun notifyOnlineOrOfflineForEveryone(isOnline: Boolean) {

        CoroutineScope(Dispatchers.IO).launch {
            database.reference.child("currentContacts").get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (dataSnapshot in task.result.children) {
                            if (dataSnapshot.key != currentUser.uid) {
                                val refCurrentUser =
                                    database.reference.child("currentContacts/${dataSnapshot.key.toString()}/${currentUser.uid}")
                                refCurrentUser.get().addOnCompleteListener { contains ->
                                    if (contains.result.value != null && contains.isSuccessful) {
                                        refCurrentUser.child("theyIsActive")
                                            .setValue(isOnline).addOnCompleteListener { updateTheyActiveForCurrentUser->
                                                if(updateTheyActiveForCurrentUser.isSuccessful&&isOnline==false){
                                                    auth.signOut()
                                                }
                                            }
                                    }

                                }
                            }
                        }

                    }
                }
        }
    }

    override fun logout() {
        notifyOffline()
        view.SignOutSuccess()
    }

    private fun notifyOffline() {
        notifyOnLineOrOfflineOnProfile(false)
        notifyOnlineOrOfflineForEveryone(false)
    }

    private fun notifyOnLineOrOfflineOnProfile(isOnline: Boolean) {
        database.reference.child("profile").child(currentUser.uid).child("theyIsActive")
            .setValue(isOnline).addOnCompleteListener { setOnlineOnProfile ->
                if (setOnlineOnProfile.isSuccessful) {
                    Log.d(TAG, "notifyOfflineOnProfile: Success")
                } else {
                    Log.e(TAG, "notifyOfflineOnProfile: Fail")
                }

            }
    }

    override fun loadHeaderProfile() {

        database.reference.child("profile").child(currentUser.uid)
            .addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val result = createUserProfile(snapshot)
                    view.setHeaderProfile(result, currentUser)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "loadHeaderProfile: $error")
                }
            })


    }

    override fun setNotifityOnline() {
        notifyOnLineOrOfflineOnProfile(true)
        notifyOnlineOrOfflineForEveryone(true)
    }

    private fun createUserProfile(snapshot: DataSnapshot): UserProfile {
        val resultMap = snapshot.value as Map<String, Any>

        val theyIsActive: Boolean = resultMap["theyIsActive"].toString().toBoolean()
        val fullname: String = resultMap["fullname"].toString()
        val urlImgProfile: String = resultMap["urlImgProfile"].toString()
        val dateOfBirth: String = resultMap["dateOfBirth"].toString()
        val email: String = resultMap["email"].toString()
        val idUser: String = resultMap["idUser"].toString()

        return UserProfile(dateOfBirth, email, fullname, idUser, theyIsActive, urlImgProfile)
    }
}