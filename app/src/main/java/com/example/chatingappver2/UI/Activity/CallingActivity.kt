package com.example.chatingappver2.UI.Activity

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.chatingappver2.Model.UserProfile
import com.example.chatingappver2.R
import com.example.chatingappver2.UI.Activity.MainActivity.HomeActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.stringee.call.StringeeCall
import com.stringee.common.StringeeAudioManager
import kotlinx.android.synthetic.main.activity_calling.btnAllow
import kotlinx.android.synthetic.main.activity_calling.btnAudio
import kotlinx.android.synthetic.main.activity_calling.btnCancel
import kotlinx.android.synthetic.main.activity_calling.btnDecline
import kotlinx.android.synthetic.main.activity_calling.btnMic
import kotlinx.android.synthetic.main.activity_calling.countUpChronometer
import kotlinx.android.synthetic.main.activity_calling.imgAvataCallingActivity
import kotlinx.android.synthetic.main.activity_calling.layoutCallingMain
import kotlinx.android.synthetic.main.activity_calling.layoutHandleInCalling
import kotlinx.android.synthetic.main.activity_calling.tvFullnameCallingActivity
import kotlinx.android.synthetic.main.activity_calling.tvStatusCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

class CallingActivity : AppCompatActivity() {

    private var call: StringeeCall? = null
    private var isComingCall: Boolean? = null
    private var to: String = ""
    private var callid=""
    private lateinit var mSignalLingState: StringeeCall.SignalingState
    private lateinit var mMediaConnected: StringeeCall.MediaState
    private lateinit var audioManager: StringeeAudioManager
    private lateinit var phoneRinging: MediaPlayer
    private var isSpeaker:Boolean=false
    private var isMicOn:Boolean=true
    private var isCamOn=false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calling)
        getValueFromIntent()
        checkPermissionRequest()
        registerEventView()
    }

    private fun registerEventView() {
        btnAudio.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                isSpeaker=!isSpeaker
                audioManager.setSpeakerphoneOn(isSpeaker)
                if(isSpeaker){
                    btnAudio.setImageDrawable(ContextCompat.getDrawable(this@CallingActivity,R.drawable.outspeaker))
                }else{
                    btnAudio.setImageDrawable(ContextCompat.getDrawable(this@CallingActivity,R.drawable.headphone))
                }
            }
        }
        btnMic.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                isMicOn=!isMicOn
                call?.mute(!isMicOn)
                if(isMicOn){
                    btnMic.setImageDrawable(ContextCompat.getDrawable(this@CallingActivity,R.drawable.turnonmic))
                }else{
                    btnMic.setImageDrawable(ContextCompat.getDrawable(this@CallingActivity,R.drawable.ic_mutemic))
                }
            }
        }
//        btnCammeraCallingActivity.setOnClickListener {
//            CoroutineScope(Dispatchers.Main).launch {
//                isCamOn=!isCamOn
//                if(isCamOn){
//                    btnCammeraCallingActivity.setImageDrawable(ContextCompat.getDrawable(this@CallingActivity,R.drawable.hidecam))
//                }else{
//                    btnCammeraCallingActivity.setImageDrawable(ContextCompat.getDrawable(this@CallingActivity,R.drawable.showcam))
//                }
//            }
//        }
        btnCancel.setOnClickListener{
            CoroutineScope(Dispatchers.Main).launch {
                call?.hangup(object : com.stringee.listener.StatusListener() {
                    override fun onSuccess() {

                    }
                })
                audioManager.stop()
                finish()
            }
        }
        btnDecline.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                if (call != null) {
                    call!!.reject(object : com.stringee.listener.StatusListener(){
                        override fun onSuccess() {

                        }
                    })
                    audioManager.stop()
                    phoneRinging.stop()
                    audioManager.setSpeakerphoneOn(false)
                    finish()
                }
            }
        }
        btnAllow.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                if (call != null) {
                    call!!.answer(object : com.stringee.listener.StatusListener(){
                        override fun onSuccess() {

                        }
                    })
                }
                phoneRinging.stop()
                audioManager.setSpeakerphoneOn(false)
                layoutHandleInCalling.visibility= View.GONE
                layoutCallingMain.visibility= View.VISIBLE
            }
        }


    }

    private fun checkPermissionRequest() {
        val Listpermissions:MutableList<String> = mutableListOf()
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
            Listpermissions.add(Manifest.permission.RECORD_AUDIO)
        }
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Listpermissions.add(Manifest.permission.BLUETOOTH_CONNECT)
            }
        }
        if (Listpermissions.size>0){
            ActivityCompat.requestPermissions(this, Listpermissions.toTypedArray(),0)

        }else{
            initCall()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var isGranted = false
        if (grantResults.isNotEmpty()) {
            for (item in grantResults) {
                if (item != PackageManager.PERMISSION_GRANTED) {
                    isGranted = false
                    break
                } else {
                    isGranted = true
                }

            }
        }

        if (requestCode == 0) {
            if (!isGranted) {
                finish()
            } else {
                initCall()
            }
        }


    }

    private fun initCall() {
        if (isComingCall == true) {
            call= HomeActivity.voiceCallMap[callid]
            if (call==null) {
                finish()
                return
            }
            setLayoutInCommingCall()

        } else {
            call = StringeeCall(HomeActivity.client, HomeActivity.client?.userId, to)
            setLayoutCalling()
        }

        audioManager= StringeeAudioManager(this)
        audioManager.start(object: StringeeAudioManager.AudioManagerEvents{
            override fun onAudioDeviceChanged(
                p0: StringeeAudioManager.AudioDevice?,
                p1: MutableSet<StringeeAudioManager.AudioDevice>?
            ) {

            }
        } )

        call?.setCallListener(object : StringeeCall.StringeeCallListener {
            override fun onSignalingStateChange(
                p0: StringeeCall?,
                p1: StringeeCall.SignalingState?,
                p2: String?,
                p3: Int,
                p4: String?
            ) {
                CoroutineScope(Dispatchers.Main).launch {
                    mSignalLingState = p1!!
                    when (mSignalLingState) {
                        StringeeCall.SignalingState.CALLING -> {
                            tvStatusCall.text = "Calling"
                        }

                        StringeeCall.SignalingState.ANSWERED -> {
                            tvStatusCall.text = "Starting"
                        }

                        StringeeCall.SignalingState.BUSY -> {
                            audioManager.stop()
                            tvStatusCall.text = "Busy"
                            finish()
                        }

                        StringeeCall.SignalingState.RINGING -> {
                            tvStatusCall.text = "Ringing"
                        }

                        StringeeCall.SignalingState.ENDED -> {
                            countUpChronometer.visibility=View.GONE
                            tvStatusCall.visibility=View.VISIBLE
                            audioManager.stop()
                            countUpChronometer.stop()
                            showElapsedTime()
                            tvStatusCall.text = "Ended"
                            finish()
                        }
                    }
                }
            }

            override fun onError(p0: StringeeCall?, p1: Int, p2: String?) {
                audioManager.stop()
                finish()
            }

            override fun onHandledOnAnotherDevice(
                p0: StringeeCall?,
                p1: StringeeCall.SignalingState?,
                p2: String?
            ) {

            }

            override fun onMediaStateChange(p0: StringeeCall?, p1: StringeeCall.MediaState?) {
                CoroutineScope(Dispatchers.Main).launch {
                    mMediaConnected=p1!!

                    if (mMediaConnected == StringeeCall.MediaState.CONNECTED) {
                        if (mSignalLingState== StringeeCall.SignalingState.ANSWERED){
                            tvStatusCall.visibility = View.GONE
                            countUpChronometer.visibility=View.VISIBLE
                            countUpChronometer.base = SystemClock.elapsedRealtime()
                            countUpChronometer.start()
                        }
                    }
                    else{
                        tvStatusCall.text = "Retry to connect"
                    }
                }
            }

            override fun onLocalStream(p0: StringeeCall?) {

            }

            override fun onRemoteStream(p0: StringeeCall?) {

            }

            override fun onCallInfo(p0: StringeeCall?, p1: JSONObject?) {

            }
        })
        if (isComingCall == true) {
            call!!.ringing(object : com.stringee.listener.StatusListener(){
                override fun onSuccess() {
                    phoneRinging=MediaPlayer.create(this@CallingActivity, R.raw.phone_ringing);
                    phoneRinging.start()
                    audioManager.setSpeakerphoneOn(true)
                }
            })

        }
        else {
            audioManager.setSpeakerphoneOn(false)
            call?.makeCall(object : com.stringee.listener.StatusListener() {
                override fun onSuccess() {

                }
            })
        }
    }

    private fun showElapsedTime() {
        val elapsedMillis: Long = SystemClock.elapsedRealtime() - countUpChronometer.base
        val date = Date(elapsedMillis)
        val formatter: DateFormat = SimpleDateFormat("HH:mm:ss")
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val dateFormatted = formatter.format(date)
        Toast.makeText(this, dateFormatted, Toast.LENGTH_SHORT).show()
    }

    private fun setLayoutInCommingCall() {
        layoutHandleInCalling.visibility= View.VISIBLE
        layoutCallingMain.visibility= View.GONE
        loadInformationOfThem(call!!.from)
    }
    private fun setLayoutCalling() {
        layoutHandleInCalling.visibility= View.GONE
        layoutCallingMain.visibility= View.VISIBLE
        loadInformationOfThem(call!!.to)
    }

    private fun loadInformationOfThem(idProfile:String) {
        val database= FirebaseDatabase.getInstance()
        database.reference.child("profile").child(idProfile)
            .get().addOnCompleteListener{task->
            if (task.isSuccessful){
                val themProfile=createUserProfile(task.result)
                Glide.with(this).asBitmap().load(themProfile.urlImgProfile)
                    .into(imgAvataCallingActivity)
                tvFullnameCallingActivity.text=themProfile.fullname
            }
        }
    }

    private fun getValueFromIntent() {
        if (intent == null) return
        isComingCall = intent.getBooleanExtra("isComingCall", false)
        to = intent.getStringExtra("to").toString()
        callid= intent.getStringExtra("callId").toString()
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