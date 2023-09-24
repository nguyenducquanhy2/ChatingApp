package com.example.chatingappver2.database.repository

import android.net.Uri
import android.util.Log
import com.example.chatingappver2.model.UserProfile
import com.example.chatingappver2.contract.repository.ProfileRepositoryContract
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileRepository(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase,
    private val storage: StorageReference
) :
    ProfileRepositoryContract {
    private val TAG: String = "ProfileRepository"

    override fun loadHeaderProfile(instance: ProfileRepositoryContract.GetProfileFinishListener) {

        database.reference.child("profile").child(auth.currentUser?.uid!!)
            .addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val result = createUserProfile(snapshot)
                    instance.loadSuccess(result)
                }

                override fun onCancelled(error: DatabaseError) {
                    instance.loadError(error.message)
                }
            })

    }

    override fun submitProfile(
        uri: Uri,
        fullname: String,
        dateChoose: String,
        submitProfileFinishListener: ProfileRepositoryContract.SubmitProfileFinishListener
    ) {
        val currentUser = auth.currentUser
        val deleteReference = storage.child("ImgProfile").child(currentUser!!.uid)
        deleteReference.delete()

        val storageReference = storage.child("ImgProfile").child(currentUser.uid)


        storageReference.putFile(uri).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                storageReference.downloadUrl.addOnCompleteListener { urlImage ->
                    if (urlImage.isSuccessful) {
                        val urlImg = urlImage.result.toString()
                        val profile = UserProfile(fullname, urlImg, dateChoose)
                        profile.email = currentUser.email.toString()
                        profile.idUser = currentUser.uid

                        database.reference.child("profile").child(currentUser.uid)
                            .setValue(profile).addOnCompleteListener { task1 ->
                                if (task1.isSuccessful) {
                                    submitProfileFinishListener.submitSuccess()
                                } else {
                                    task1.exception?.message?.let {
                                        submitProfileFinishListener.submitError(
                                            it
                                        )
                                    }
                                    Log.e(
                                        TAG,
                                        "submitProfileToFireBase: putProfile ${task1.exception}"
                                    )
                                }
                            }

                    }

                }


            } else {
                Log.e(TAG, "submitProfileToFireBase: putImg ${task.exception}")
            }

        }

    }

    override fun updateProfileInCurrentContacts(
        value: String,
        path: String,
        instance: ProfileRepositoryContract.UpdateProfileFinishListener
    ) {
        database.reference.child("currentContacts").get()
            .addOnCompleteListener { currentUserTask ->

                if (currentUserTask.isSuccessful) {

                    for (itemCurrentUser in currentUserTask.result.children) {

                        if (itemCurrentUser.key != auth.currentUser?.uid) {

                            database.reference.child("currentContacts")
                                .child(itemCurrentUser.key!!).child(auth.currentUser?.uid!!)
                                .child("dateOfBirth").get()
                                .addOnCompleteListener { contains ->

                                    if (contains.result.value != null && contains.isSuccessful) {
                                        database.reference.child("currentContacts")
                                            .child(itemCurrentUser.key!!).child(auth.currentUser?.uid!!)
                                            .child(path)
                                            .setValue(value)
                                            .addOnCompleteListener { updateCurrentUser ->
                                                if (updateCurrentUser.isSuccessful) {
                                                    Log.d(TAG, "updateMyProfileOn $path is success")
                                                } else {
                                                    Log.e(
                                                        TAG,
                                                        "updateMyProfileOn $path is fail ${updateCurrentUser.exception}"
                                                    )
                                                }

                                            }
                                    }

                                }

                        }


                    }
                }


            }
    }

    override fun updateImg(
        uri: Uri,
        instance: ProfileRepositoryContract.UpdateProfileFinishListener
    ) {
        storage.child("ImgProfile").child(auth.currentUser?.uid!!).delete()

        storage.child("ImgProfile").child(auth.currentUser?.uid!!).putFile(uri)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storage.child("ImgProfile").child(auth.currentUser?.uid!!).downloadUrl
                        .addOnCompleteListener { urlImage ->
                            if (urlImage.isSuccessful) {
                                val newUrlAvatar = urlImage.result.toString()
                                CoroutineScope(Dispatchers.IO).launch {
                                    updateMyProfileByPlace(newUrlAvatar, "urlImgProfile", instance)
                                }
                                CoroutineScope(Dispatchers.IO).launch {
                                    updateProfileInCurrentContacts(
                                        newUrlAvatar,
                                        "urlImgProfile",
                                        instance
                                    )

                                }

                            }
                        }
                } else {
                    Log.e(TAG, "submitProfileToFireBase: putImg " + task.exception)
                }
            }
    }

    override fun updateMyProfileByPlace(
        value: String,
        path: String,
        instance: ProfileRepositoryContract.UpdateProfileFinishListener
    ) {
        database.reference.child("profile").child(auth.currentUser?.uid!!)
            .child(path)
            .setValue(value)
            .addOnCompleteListener { updateValueOnProfile ->
                if (updateValueOnProfile.isSuccessful) {
                    instance.onSuccess()
                } else {
                    Log.e(TAG, "updateMyProfileOn $path is fail ${updateValueOnProfile.exception}")
                }

            }
    }

    override fun notifyOnLineOrOfflineOnProfile(sateNotify: Boolean) {
        auth.currentUser?.let {
            database.reference.child("profile").child(it.uid).child("theyIsActive")
                .setValue(sateNotify).addOnCompleteListener { setOnlineOnProfile ->
                    if (setOnlineOnProfile.isSuccessful) {
                        Log.d(TAG, "notifyOfflineOnProfile: Success")
                    } else {
                        Log.e(TAG, "notifyOfflineOnProfile: Fail")
                    }

                }
        }
    }

    override fun notifyOnlineOrOfflineForEveryone(sateNotify: Boolean) {
        database.reference.child("currentContacts").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (dataSnapshot in task.result.children) {
                        if (dataSnapshot.key != auth.currentUser?.uid) {
                            val refCurrentUser =
                                database.reference.child("currentContacts/${dataSnapshot.key.toString()}/${auth.currentUser?.uid}")
                            refCurrentUser.get().addOnCompleteListener { contains ->
                                if (contains.result.value != null && contains.isSuccessful) {
                                    refCurrentUser.child("theyIsActive")
                                        .setValue(sateNotify)
                                        .addOnCompleteListener { updateTheyActiveForCurrentUser ->
                                            if (updateTheyActiveForCurrentUser.isSuccessful && sateNotify == false) {
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