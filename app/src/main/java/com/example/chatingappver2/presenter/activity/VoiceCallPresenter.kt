package com.example.chatingappver2.presenter.activity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.chatingappver2.model.UserProfile
import com.example.chatingappver2.contract.activity.VoiceCallContract
import com.example.chatingappver2.ui.activity.VoiceCallActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

class VoiceCallPresenter(private val view: VoiceCallContract.view, private val context: Context) :
    VoiceCallContract.presenter {
    override fun loadInformationOfThem(idProfile: String) {
        val database= FirebaseDatabase.getInstance()
        database.reference.child("profile").child(idProfile)
            .get().addOnCompleteListener{task->
                if (task.isSuccessful){
                    val themProfile=createUserProfile(task.result)
                    view.setInformationOfThem(themProfile)
                }
            }
    }

    override fun convertLongTimeDateFormat(time: Long) {
        val date = Date(time)
        val formatter: DateFormat = SimpleDateFormat("HH:mm:ss")
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val dateFormatted = formatter.format(date)
        view.showElapsedTime(dateFormatted)
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

    override fun checkPermissionRequest() {
        val Listpermissions:MutableList<String> = mutableListOf()
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
            Listpermissions.add(Manifest.permission.RECORD_AUDIO)
        }
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Listpermissions.add(Manifest.permission.BLUETOOTH_CONNECT)
            }
        }
        if (Listpermissions.size>0){
            ActivityCompat.requestPermissions(context as VoiceCallActivity, Listpermissions.toTypedArray(),0)
        }else{
            view.executeInitCall()
        }
    }
}