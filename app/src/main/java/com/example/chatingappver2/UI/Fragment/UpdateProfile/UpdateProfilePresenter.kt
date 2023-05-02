package com.example.chatingappver2.UI.Fragment.UpdateProfile

import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.DatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class UpdateProfilePresenter(
    private val view: UpdateProfileContract.view,
    private val context: Context
) :
    UpdateProfileContract.presenter {
    private val TAG: String = "UpdateProfilePresenter"
    private val calendar: Calendar = Calendar.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val database = FirebaseDatabase.getInstance()
    private val reference: DatabaseReference = database.reference
    private val storage = FirebaseStorage.getInstance()

    override fun submitProfileToFireBase(uri: Uri?, fullname: String, dateChoose: String) {

        if (fullname.isNotEmpty() && !inValidDate(dateChoose) && (uri != null)) {
            updateThreePlace(uri, fullname, dateChoose)
            return
        }

        if (fullname.isNotEmpty() && !inValidDate(dateChoose)) {
            updateFullnameAndDate(fullname, dateChoose)
            return
        }

        if (!inValidDate(dateChoose) && (uri != null)) {
            updateImgAndDate(uri, dateChoose)
            return
        }

        if (fullname.isNotEmpty() && (uri != null)) {
            updateFullnameAndImg(fullname, uri)
            return
        }

        if (uri != null) {
            updateImg(uri)
            return
        }
        if (fullname.isNotEmpty()) {
            updateFullname(fullname)
            return
        }

        if (inValidDate(dateChoose)) {
            updateDate(dateChoose)
            return
        }

    }

    override fun openDialogDatePicker() {
        DatePickerDialog(
            context, object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(p0: DatePicker?, year: Int, month: Int, date: Int) {
                    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
                    calendar.set(year, month, date)
                    val format: String = simpleDateFormat.format(calendar.time)
                    view.setTextViewDate(format)
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DATE)
        ).show()
    }


    private fun updateProfileInCurrentContacts(value: String, path: String) {
        database.reference.child("currentContacts").get()
            .addOnCompleteListener { currentUserTask ->

                if (currentUserTask.isSuccessful) {

                    for (itemCurrentUser in currentUserTask.result.children) {

                        if (itemCurrentUser.key != currentUser!!.uid) {

                            database.reference.child("currentContacts")
                                .child(itemCurrentUser.key!!).child(currentUser.uid)
                                .child("dateOfBirth").get()
                                .addOnCompleteListener { contains ->

                                    if (contains.result.value != null && contains.isSuccessful) {
                                        database.reference.child("currentContacts")
                                            .child(itemCurrentUser.key!!).child(currentUser.uid)
                                            .child(path)
                                            .setValue(value)
                                            .addOnCompleteListener { updateCurrentUser ->
                                                if (updateCurrentUser.isSuccessful) {
                                                    Log.d(TAG, "updateMyProfileOn $path is success" )
                                                }
                                                else {
                                                    Log.e(TAG, "updateMyProfileOn $path is fail ${updateCurrentUser.exception}" )
                                                }

                                            }
                                    }

                                }

                        }


                    }
                }


            }
    }

    private fun updateDate(DateOfBirth: String) {

        CoroutineScope(IO).launch {
            updateMyProfileByPlace(DateOfBirth, "dateOfBirth")
        }

        CoroutineScope(IO).launch {
            updateProfileInCurrentContacts(DateOfBirth, "dateOfBirth")
        }
    }

    private fun updateFullname(fullname: String) {

        CoroutineScope(IO).launch {
            updateMyProfileByPlace(fullname, "fullname")
        }

        CoroutineScope(IO).launch {
            updateProfileInCurrentContacts(fullname, "fullname")

        }
    }

    private fun updateImg(uri: Uri) {

        storage.reference.child("ImgProfile").child(currentUser!!.uid).delete()

        storage.reference.child("ImgProfile").child(currentUser.uid).putFile(uri)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storage.reference.child("ImgProfile").child(currentUser.uid).downloadUrl
                        .addOnCompleteListener { urlImage ->
                            if (urlImage.isSuccessful) {
                                val newUrlAvatar = urlImage.result.toString()
                                CoroutineScope(IO).launch {
                                    updateMyProfileByPlace(newUrlAvatar, "urlImgProfile")
                                }
                                CoroutineScope(IO).launch {
                                    updateProfileInCurrentContacts(newUrlAvatar, "urlImgProfile")

                                }

                            }
                        }
                } else {
                    Log.e(TAG, "submitProfileToFireBase: putImg " + task.exception)
                }
            }
    }

    private fun updateMyProfileByPlace(value: String, path: String) {
        database.reference.child("profile").child(currentUser!!.uid)
            .child(path)
            .setValue(value)
            .addOnCompleteListener { updateValueOnProfile ->
                if (updateValueOnProfile.isSuccessful) {
                    view.submitSuccess()
                }
                else {
                    Log.e(TAG, "updateMyProfileOn $path is fail ${updateValueOnProfile.exception}", )
                }

            }
    }

    private fun updateImgAndDate(uri: Uri, dateChoose: String) {
        updateImg(uri)
        updateDate(dateChoose)
    }

    private fun updateFullnameAndImg(fullname: String, uri: Uri) {
        updateImg(uri)
        updateFullname(fullname)
    }

    private fun updateFullnameAndDate(fullname: String, dateChoose: String) {
        updateFullname(fullname)
        updateDate(dateChoose)
    }

    private fun updateThreePlace(uri: Uri, fullname: String, dateChoose: String) {
        updateImg(uri)
        updateFullname(fullname)
        updateDate(dateChoose)
    }
    private fun inValidDate(value: String): Boolean {
        return value?.trim().toString() == "yyyy/mm/dd"
    }

}
