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
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import kotlin.system.measureNanoTime

class MainActivityPresenter(
    private val view: MainActivityContract.view,
    private val context: Context
) :
    MainActivityContract.presenter {
    private val TAG: String = "MainActivityPresenter"
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser!!
    private val database = FirebaseDatabase.getInstance()

    override fun logout() {
        notifyOffline()
        view.SignOutSuccess()
    }

    private fun notifyOffline() {
        notifyOnOffOnProfile(false)
        notifyOnOffForEveryone(false)
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
        notifyOnline()
    }


    private fun notifyOnline() {
        notifyOnOffOnProfile(true)
        notifyOnOffForEveryone(true)
    }

    private fun notifyOnOffOnProfile(isOnline: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            database.reference.child("profile").child(currentUser.uid)
                .child("theyIsActive").setValue(isOnline)
                .addOnCompleteListener { setOnlineOnProfile ->
                    if (setOnlineOnProfile.isSuccessful) {
                        Log.d(TAG, "notifyOnlineOnProfile: Success")
                    } else {
                        Log.e(TAG, "notifyOnlineOnProfile: Fail")
                    }
                }
        }
    }

    private fun notifyOnOffForEveryone(isOnline: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            val time= measureNanoTime {
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
                                                .setValue(isOnline)
                                        }

                                    }


                                }


                            }
                        }
                    }
            }
            Log.e(TAG,"Time execute: "+showElapsedTime(time))
        }
    }

    private fun showElapsedTime(time :Long):String {
        val date = Date(time)
        val formatter: DateFormat = SimpleDateFormat("HH:mm:ss")
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        return formatter.format(date)
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