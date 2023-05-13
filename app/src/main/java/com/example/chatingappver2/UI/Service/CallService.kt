package com.example.chatingappver2.UI.Service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.chatingappver2.UI.Activity.VideoCallActivity.VideoCallActivity
import com.example.chatingappver2.UI.Activity.VoiceCallActivity.VoiceCallActivity
import com.google.firebase.auth.FirebaseAuth
import com.stringee.StringeeClient
import com.stringee.call.StringeeCall
import com.stringee.call.StringeeCall2
import com.stringee.exception.StringeeError
import com.stringee.listener.StatusListener
import com.stringee.listener.StringeeConnectionListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.Date

class CallService :Service() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        var client: StringeeClient? = null
        val voiceCallMap:MutableMap<String, StringeeCall> = mutableMapOf()
        val videoCallMap:MutableMap<String,StringeeCall2> = mutableMapOf()
    }

    private val mContext:Context=this
    private val currentUser = FirebaseAuth.getInstance().currentUser!!
    private val TAG: String="VideoCallService"
    private val KEY_SID:String="SK.0.y6Tonp4PIM2kpxgeWtaq2osSexgmaPBN"
    private val KEY_SECRET:String="VmVKSGx0N1pwSHBoaHFneW9UaXZES00yRTNaWE1xcw=="
    private var TOKEN:String? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        CoroutineScope(Dispatchers.Default).launch {
            Log.d(TAG, "onStartCommand: ")
            connectStringee()
        }

        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun connectStringee() {
        Log.d(TAG, "connectStringee: ")
        initStringeeClient()
        val userId=currentUser.uid
        if (client!!.isConnected){
            client?.disconnect()
        }
        //TOKEN="eyJjdHkiOiJzdHJpbmdlZS1hcGk7dj0xIiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiJTSy4wLnk2VG9ucDRQSU0ya3B4Z2VXdGFxMm9zU2V4Z21hUEJOLTE2ODMzNzI5NjgiLCJpc3MiOiJTSy4wLnk2VG9ucDRQSU0ya3B4Z2VXdGFxMm9zU2V4Z21hUEJOIiwiZXhwIjoxNjgzNDU5MzY4LCJ1c2VySWQiOiJ0ZXN0In0.w9aVABQBS6l2nIV_8n7dArZvCYWBhLdJ5grB_nrYpN0"
        TOKEN=genAccessToken(KEY_SID,KEY_SECRET,3600,userId)
        client!!.registerPushToken(TOKEN,object : StatusListener(){
            override fun onSuccess() {}
        })

        client?.connect(TOKEN)
    }

    private fun initStringeeClient() {
        Log.d(TAG, "initStringeeClient: ")
        client = StringeeClient(this)
        client!!.setConnectionListener(object : StringeeConnectionListener {
            @SuppressLint("SetTextI18n")
            override fun onConnectionConnected(p0: StringeeClient?, isReconnecting: Boolean) {
                CoroutineScope(Dispatchers.Main).launch {
                    Log.d(TAG, "onConnectionConnected: ")
                    if (p0 != null) {
                        Log.d(TAG, "connected : ${p0.userId}" )
                    }
                }

            }

            override fun onConnectionDisconnected(p0: StringeeClient?, p1: Boolean) {
                CoroutineScope(Dispatchers.Main).launch {
                    if (p0 != null) {
                        Log.d(TAG, "disconnection: ${p0.userId}" )

                    }
                }

            }

            override fun onIncomingCall(p0: StringeeCall?) {
                CoroutineScope(Dispatchers.Main).launch {
                    if (p0 != null) {
                        voiceCallMap[p0.callId] = p0
                        if (client!!.isConnected){
                            val intent= Intent(mContext, VoiceCallActivity::class.java)
                            intent.putExtra("isComingCall",true)
                            intent.putExtra("callId",p0.callId)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
                            startActivity(intent)
                        }
                    }
                }

            }

            override fun onIncomingCall2(p0: StringeeCall2?) {
                CoroutineScope(Dispatchers.Main).launch {
                    if (p0 != null) {
                        videoCallMap[p0.callId] = p0
                        if (client!!.isConnected){
                            withContext(coroutineContext){
                                val intent=Intent(mContext, VideoCallActivity::class.java)
                                intent.putExtra("isComingCall",true)
                                intent.putExtra("callId",p0.callId)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
                                startActivity(intent)
                            }

                        }
                    }
                }
            }

            override fun onConnectionError(p0: StringeeClient?, p1: StringeeError?) {
                CoroutineScope(Dispatchers.Main).launch {
                    if (p0 != null) {
                        Log.e(TAG, "connect error: ${p1}" )
                    }
                }

            }

            override fun onRequestNewToken(p0: StringeeClient?) {
                if (p0 != null) {
                    Log.d(TAG, "onRequestNewToken: ${p0.userId}")
                }
            }

            override fun onCustomMessage(p0: String?, p1: JSONObject?) {

            }

            override fun onTopicMessage(p0: String?, p1: JSONObject?) {

            }
        })

    }

    private fun genAccessToken(keySid: String, keySecret: String, expireInSecond: Int, userId:String): String? {
        try {
            val algorithmHS: Algorithm =
                Algorithm.HMAC256(keySecret)
            val headerClaims: MutableMap<String, Any> =
                HashMap()
            headerClaims["typ"] = "JWT"
            headerClaims["alg"] = "HS256"
            headerClaims["cty"] = "stringee-api;v=1"
            val exp = System.currentTimeMillis() + (expireInSecond * 2)
            return JWT.create().withHeader(headerClaims)
                .withClaim("jti", keySid + "-" + System.currentTimeMillis())
                .withClaim("iss", keySid)
                .withExpiresAt(Date(exp))
                .withClaim("userId", userId)
                .sign(algorithmHS)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }

}