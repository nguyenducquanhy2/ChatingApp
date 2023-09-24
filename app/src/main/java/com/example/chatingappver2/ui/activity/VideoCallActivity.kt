package com.example.chatingappver2.ui.activity

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.chatingappver2.model.UserProfile
import com.example.chatingappver2.R
import com.example.chatingappver2.service.CallService
import com.example.chatingappver2.contract.activity.VideoCallContract
import com.example.chatingappver2.presenter.activity.VideoCallPresenter
import com.stringee.call.StringeeCall2
import com.stringee.common.StringeeAudioManager
import com.stringee.listener.StatusListener
import com.stringee.video.StringeeVideoTrack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_calling.tvStatusCall
import kotlinx.android.synthetic.main.activity_video_call.LayoutInforProfileOfThem
import kotlinx.android.synthetic.main.activity_video_call.btnAllowVideoCAllActivity
import kotlinx.android.synthetic.main.activity_video_call.btnAudioVideoCAllActivity
import kotlinx.android.synthetic.main.activity_video_call.btnCammeraVideoCAllActivity
import kotlinx.android.synthetic.main.activity_video_call.btnCancelVideoCAllActivity
import kotlinx.android.synthetic.main.activity_video_call.btnDeclineVideoCAllActivity
import kotlinx.android.synthetic.main.activity_video_call.btnMicVideoCAllActivity
import kotlinx.android.synthetic.main.activity_video_call.btnSwapCamera
import kotlinx.android.synthetic.main.activity_video_call.imgAvataVideoCAllActivity
import kotlinx.android.synthetic.main.activity_video_call.imgLocalVideoCAll
import kotlinx.android.synthetic.main.activity_video_call.imgRemoveVideoCAll
import kotlinx.android.synthetic.main.activity_video_call.layoutHandleInCallingVideoCAllActivity
import kotlinx.android.synthetic.main.activity_video_call.layoutVideoCAllActivityMain
import kotlinx.android.synthetic.main.activity_video_call.tvFullnameVideoCAllActivity
import kotlinx.android.synthetic.main.activity_video_call.tvStatusCallVideoCAllActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
@AndroidEntryPoint
class VideoCallActivity : AppCompatActivity(), VideoCallContract.view {


    private var call: StringeeCall2? = null
    private var isComingCall: Boolean? = null
    private var to: String = ""
    private var callid = ""
    private lateinit var mSignalLingState: StringeeCall2.SignalingState
    private lateinit var mMediaConnected: StringeeCall2.MediaState
    private lateinit var audioManager: StringeeAudioManager
    private var isSpeaker: Boolean = true
    private var isMicOn: Boolean = true
    private var isCamera: Boolean = true

    private val presenter by lazy { VideoCallPresenter(this, this) }

    //override var LogBug: String="ViewInforActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call)
        CoroutineScope(Dispatchers.Default).launch {
            getValueFromIntent()

            presenter.checkPermissionRequest()
        }
        CoroutineScope(Dispatchers.Main).launch {
            registerEventView()
        }
    }

    private fun registerEventView() {
        btnAudioVideoCAllActivity.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {

                isSpeaker = !isSpeaker
                audioManager.setSpeakerphoneOn(isSpeaker)

                if (isSpeaker) {
                    btnAudioVideoCAllActivity.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@VideoCallActivity,
                            R.drawable.outspeaker
                        )
                    )
                } else {
                    btnAudioVideoCAllActivity.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@VideoCallActivity,
                            R.drawable.headphone
                        )
                    )
                }
            }
        }

        btnMicVideoCAllActivity.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                call?.mute(isMicOn)
                isMicOn = !isMicOn

                if (isMicOn) {
                    btnMicVideoCAllActivity.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@VideoCallActivity,
                            R.drawable.turnonmic
                        )
                    )
                } else {
                    btnMicVideoCAllActivity.setImageDrawable(
                        ContextCompat.getDrawable(this@VideoCallActivity, R.drawable.ic_mutemic)
                    )
                }
            }
        }

        btnCancelVideoCAllActivity.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                call?.hangup(object : StatusListener() {
                    override fun onSuccess() {}
                })
                audioManager.stop()
                finish()
            }
        }

        btnDeclineVideoCAllActivity.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                if (call != null) {
                    call!!.reject(object : StatusListener() {
                        override fun onSuccess() {}
                    })
                    audioManager.stop()
                    finish()
                }
            }
        }
        btnAllowVideoCAllActivity.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                if (call != null) {
                    call!!.answer(object : StatusListener() {
                        override fun onSuccess() {}
                    })
                }

                imgLocalVideoCAll.visibility=View.VISIBLE
                layoutHandleInCallingVideoCAllActivity.visibility = View.GONE
                layoutVideoCAllActivityMain.visibility = View.VISIBLE
            }
        }
        btnSwapCamera.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                call?.switchCamera(object : StatusListener() {
                    override fun onSuccess() {}
                })
            }
        }

        btnCammeraVideoCAllActivity.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                isCamera = !isCamera
                call?.enableVideo(isCamera)

                if (isCamera) {
                    btnCammeraVideoCAllActivity.setImageDrawable(
                        ContextCompat.getDrawable(this@VideoCallActivity, R.drawable.ic_showcam)
                    )
                    imgLocalVideoCAll.visibility = View.VISIBLE
                } else {
                    btnCammeraVideoCAllActivity.setImageDrawable(
                        ContextCompat.getDrawable(this@VideoCallActivity, R.drawable.hidecam)
                    )
                    imgLocalVideoCAll.visibility = View.GONE
                }
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
            call = CallService.videoCallMap[callid]
            if (call == null) {
                finish()
                return
            }
            imgLocalVideoCAll.visibility=View.GONE
            layoutHandleInCallingVideoCAllActivity.visibility = View.VISIBLE
            layoutVideoCAllActivityMain.visibility = View.GONE
            presenter.loadInformationOfThem(call!!.from)

        } else {
            call = StringeeCall2(
                CallService.client,
                CallService.client?.userId, to)
            layoutHandleInCallingVideoCAllActivity.visibility = View.GONE
            layoutVideoCAllActivityMain.visibility = View.VISIBLE
            presenter.loadInformationOfThem(call!!.to)
            imgLocalVideoCAll.visibility=View.VISIBLE
        }

        audioManager = StringeeAudioManager(this)
        audioManager.start { p0, p1 -> }

        call!!.isVideoCall = true

        audioManager.setSpeakerphoneOn(true)


        call?.setCallListener(object : StringeeCall2.StringeeCallListener {
            override fun onSignalingStateChange(
                p0: StringeeCall2?,
                p1: StringeeCall2.SignalingState?,
                p2: String?,
                p3: Int,
                p4: String?
            ) {
                CoroutineScope(Dispatchers.Main).launch {
                    mSignalLingState = p1!!
                    when (mSignalLingState) {
                        StringeeCall2.SignalingState.CALLING -> {
                            tvStatusCallVideoCAllActivity.text = "Calling"
                        }

                        StringeeCall2.SignalingState.ANSWERED -> {
                            LayoutInforProfileOfThem.visibility=View.GONE
                            tvStatusCallVideoCAllActivity.text = "Starting"
                        }

                        StringeeCall2.SignalingState.BUSY -> {
                            audioManager.stop()
                            tvStatusCallVideoCAllActivity.text = "Busy"
                            finish()
                        }

                        StringeeCall2.SignalingState.RINGING -> {
                            tvStatusCallVideoCAllActivity.text = "Ringing"
                        }

                        StringeeCall2.SignalingState.ENDED -> {
                            audioManager.stop()
                            tvStatusCallVideoCAllActivity.text = "Ended"
                            finish()
                        }

                    }
                }

            }

            override fun onError(p0: StringeeCall2?, p1: Int, p2: String?) {
                CoroutineScope(Dispatchers.Main).launch {
                    finish()
                    audioManager.stop()
                }
            }

            override fun onHandledOnAnotherDevice(
                p0: StringeeCall2?,
                p1: StringeeCall2.SignalingState?,
                p2: String?
            ) {

            }

            override fun onMediaStateChange(p0: StringeeCall2?, p1: StringeeCall2.MediaState?) {
                mMediaConnected = p1!!

                if (mMediaConnected == StringeeCall2.MediaState.CONNECTED) {
                    if (mSignalLingState == StringeeCall2.SignalingState.ANSWERED) {

                        tvStatusCall.text = "Started"
                    }
                } else {
                    tvStatusCall.text = "Retry to connect"
                }
            }

            override fun onLocalStream(p0: StringeeCall2?) {
                CoroutineScope(Dispatchers.Main).launch {
                    imgLocalVideoCAll.removeAllViews()
                    imgLocalVideoCAll.addView(p0?.localView)
                    p0?.renderLocalView(true)
                }
            }

            override fun onRemoteStream(p0: StringeeCall2?) {
                CoroutineScope(Dispatchers.Main).launch {
                    imgRemoveVideoCAll.removeAllViews()
                    imgRemoveVideoCAll.addView(p0?.remoteView)
                    p0?.renderRemoteView(false)

                }
            }

            override fun onVideoTrackAdded(p0: StringeeVideoTrack?) {}

            override fun onVideoTrackRemoved(p0: StringeeVideoTrack?) {}

            override fun onCallInfo(p0: StringeeCall2?, p1: JSONObject?) {}

            override fun onTrackMediaStateChange(
                p0: String?,
                p1: StringeeVideoTrack.MediaType?,
                p2: Boolean
            ) {

            }
        })

        if (isComingCall == true) {
            call!!.ringing(object : StatusListener() {
                override fun onSuccess() {}
            })

        } else {
            call?.makeCall(object : StatusListener() {
                override fun onSuccess() {}
            })
        }


    }

    private fun getValueFromIntent() {
        if (intent == null) return
        isComingCall = intent.getBooleanExtra("isComingCall", false)
        to = intent.getStringExtra("to").toString()
        callid = intent.getStringExtra("callId").toString()
    }

    override fun executeInitCall() {
        CoroutineScope(Dispatchers.Main).launch {
            initCall()

        }
    }

    override fun setInformationOfThem(themProfile: UserProfile) {
        Glide.with(this).asBitmap().load(themProfile.urlImgProfile)
            .into(imgAvataVideoCAllActivity)
        tvFullnameVideoCAllActivity.text=themProfile.fullname
    }

}