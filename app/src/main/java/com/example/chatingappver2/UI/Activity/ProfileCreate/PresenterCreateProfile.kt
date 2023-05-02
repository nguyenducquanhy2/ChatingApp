package com.example.chatingappver2.UI.Activity.ProfileCreate

import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.DatePicker
import com.example.chatingappver2.Model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class PresenterCreateProfile(private var view: CreateProfileContract.view, private val context: Context):
    CreateProfileContract.presenter {
    private val TAG: String?="PresenterCreateProfile"
    private val currentUser=FirebaseAuth.getInstance().currentUser
    private val database=FirebaseDatabase.getInstance()
    private val reference=database.reference
    private val storage=FirebaseStorage.getInstance().reference
    private var calendar = Calendar.getInstance()

    override fun submitProfileToFireBase(uri: Uri, fullname: String, dateChoose:String) {
        if (fullname.isEmpty()){
            view.fullNameEmpty()
            return
        }

        if (validDate(dateChoose)==true){
            view.dateInValid()
            return
        }


        val deleteReference=storage.child("ImgProfile").child(currentUser!!.uid)
        deleteReference.delete()

        val storageReference=storage.child("ImgProfile").child(currentUser.uid)


        storageReference.putFile(uri).addOnCompleteListener{ task->
            if (task.isSuccessful){
                storageReference.downloadUrl.addOnCompleteListener{urlImage->
                    if (urlImage.isSuccessful) {
                        var urlImg=urlImage.result.toString()
                        var profile = UserProfile(fullname, urlImg,dateChoose)
                        profile.email= currentUser.email.toString()
                        profile.idUser=currentUser.uid

                        reference.child("profile").child(currentUser.uid)
                            .setValue(profile).addOnCompleteListener { task1 ->
                            if (task1.isSuccessful) {
                                view.submitSuccess()
                            } else {
                                view.submitFail()
                                Log.e(TAG, "submitProfileToFireBase: putProfile ${task1.exception}")
                            }
                        }

                    }

                }


            }
            else{
                Log.e(TAG, "submitProfileToFireBase: putImg ${task.exception}" )
            }

        }




    }

    private fun validDate(date: String): Boolean {
        return date.trim().equals("yyyy/mm/dd")
    }

    override fun openDialogDatePicker() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DATE)

        DatePickerDialog(context, object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(View: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                val formater = SimpleDateFormat("yyyy-MM-dd")
                calendar.set(year, month, dayOfMonth)
                val DateCheckIn: String = formater.format(calendar.time)
                view.setTextViewDate(DateCheckIn)
            }
        }, year, month, day).show()
    }


}