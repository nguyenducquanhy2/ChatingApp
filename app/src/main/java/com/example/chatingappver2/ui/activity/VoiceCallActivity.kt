package com.example.chatingappver2.ui.activity

import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.chatingappver2.model.UserProfile
import com.example.chatingappver2.R
import com.example.chatingappver2.service.CallService
import com.example.chatingappver2.contract.activity.VoiceCallContract
import com.example.chatingappver2.presenter.activity.VoiceCallPresenter
import com.stringee.call.StringeeCall
import com.stringee.common.StringeeAudioManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_calling.btnAnswer
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
@AndroidEntryPoint
class VoiceCallActivity : AppCompatActivity(), VoiceCallContract.view {

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

    private val presenter by lazy { VoiceCallPresenter(this,this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calling)
        CoroutineScope(Dispatchers.Default).launch {
            getValueFromIntent()
            presenter.checkPermissionRequest()
        }
        CoroutineScope(Dispatchers.Main).launch {
            registerEventView()
        }
    }

    private fun registerEventView() {
        btnAudio.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                isSpeaker=!isSpeaker
                audioManager.setSpeakerphoneOn(isSpeaker)
                if(isSpeaker){
                    btnAudio.setImageDrawable(ContextCompat.getDrawable(this@VoiceCallActivity,R.drawable.outspeaker))
                }else{
                    btnAudio.setImageDrawable(ContextCompat.getDrawable(this@VoiceCallActivity,R.drawable.headphone))
                }
            }
        }

        btnMic.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                isMicOn=!isMicOn
                call?.mute(!isMicOn)
                if(isMicOn){
                    btnMic.setImageDrawable(ContextCompat.getDrawable(this@VoiceCallActivity,R.drawable.turnonmic))
                }else{
                    btnMic.setImageDrawable(ContextCompat.getDrawable(this@VoiceCallActivity,R.drawable.ic_mutemic))
                }
            }
        }

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

        btnAnswer.setOnClickListener {
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
                CoroutineScope(Dispatchers.Main).launch {
                    initCall()
                }
            }
        }


    }

    private fun initCall() {
        if (isComingCall == true) {
            call= CallService.voiceCallMap[callid]
            if (call==null) {
                finish()
                return
            }
            setLayoutInCommingCall()

        } else {
            call = StringeeCall(CallService.client, CallService.client?.userId, to)
            setLayoutCalling()
        }

        audioManager= StringeeAudioManager(this)
        audioManager.start { p0, p1 -> }

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
                            val elapsedMillis: Long = SystemClock.elapsedRealtime() - countUpChronometer.base
                            presenter.convertLongTimeDateFormat(elapsedMillis)
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
                    phoneRinging=MediaPlayer.create(this@VoiceCallActivity, R.raw.phone_ringing);
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



    private fun setLayoutInCommingCall() {
        layoutHandleInCalling.visibility= View.VISIBLE
        layoutCallingMain.visibility= View.GONE
        presenter.loadInformationOfThem(call!!.from)
    }
    private fun setLayoutCalling() {
        layoutHandleInCalling.visibility= View.GONE
        layoutCallingMain.visibility= View.VISIBLE
        presenter.loadInformationOfThem(call!!.to)
    }

    private fun getValueFromIntent() {
        if (intent == null) return
        isComingCall = intent.getBooleanExtra("isComingCall", false)
        to = intent.getStringExtra("to").toString()
        callid= intent.getStringExtra("callId").toString()
    }

    override fun setInformationOfThem(themProfile: UserProfile) {
        Glide.with(this).asBitmap().load(themProfile.urlImgProfile)
            .into(imgAvataCallingActivity)
        tvFullnameCallingActivity.text=themProfile.fullname
    }

    override fun showElapsedTime(dateFormatted: String) {
        Toast.makeText(this, dateFormatted, Toast.LENGTH_SHORT).show()
    }

    override fun executeInitCall() {
        CoroutineScope(Dispatchers.Main).launch {
            initCall()
        }
    }


}