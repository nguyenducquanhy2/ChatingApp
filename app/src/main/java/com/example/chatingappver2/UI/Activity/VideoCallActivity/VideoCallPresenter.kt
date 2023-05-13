package com.example.chatingappver2.UI.Activity.VideoCallActivity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class VideoCallPresenter(private val view: VideoCallContract.view, private val context: Context) :
    VideoCallContract.presenter {


    override fun checkPermissionRequest() {
        val Listpermissions: MutableList<String> = mutableListOf()
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Listpermissions.add(Manifest.permission.RECORD_AUDIO)
        }

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Listpermissions.add(Manifest.permission.CAMERA)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Listpermissions.add(Manifest.permission.BLUETOOTH_CONNECT)
            }
        }
        if (Listpermissions.size > 0) {
            ActivityCompat.requestPermissions(context as VideoCallActivity, Listpermissions.toTypedArray(), 0)
        } else {
            view.executeInitCall()
        }
    }
}