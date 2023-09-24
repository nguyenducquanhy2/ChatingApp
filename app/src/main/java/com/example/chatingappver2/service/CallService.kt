package com.example.chatingappver2.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.app.TaskStackBuilder
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.chatingappver2.model.UserProfile
import com.example.chatingappver2.R
import com.example.chatingappver2.ui.activity.VideoCallActivity
import com.example.chatingappver2.ui.activity.VoiceCallActivity
import com.example.chatingappver2.BroadcastReceiver.myBroadCast
import com.example.chatingappver2.myapplication.MyApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.stringee.StringeeClient
import com.stringee.call.StringeeCall
import com.stringee.call.StringeeCall2
import com.stringee.exception.StringeeError
import com.stringee.listener.StatusListener
import com.stringee.listener.StringeeConnectionListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import java.util.Date

class CallService :Service() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        var client: StringeeClient? = null
        val voiceCallMap:MutableMap<String, StringeeCall> = mutableMapOf()
        val videoCallMap:MutableMap<String,StringeeCall2> = mutableMapOf()
    }

    private val currentUser = FirebaseAuth.getInstance().currentUser!!
    private val TAG: String="VideoCallService"
    private val KEY_SID:String="SK.0.dnq249fnTeu0O5r2i9buNlGe1nw8knBC"
    private val KEY_SECRET:String="THlLNzlwWDFLSThtb3ZaY2hIblRLclRFUzh2c3pLR1E="
    private var TOKEN:String? = null
    private val DECLINE=-1
    private val ANSWER=1
    private val NOTIFICATION_ID=101

    private var voiceCall:StringeeCall? =null
    private var videoCall:StringeeCall2? =null
    private var notificationManagerCompat:NotificationManagerCompat?=null


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent?.extras==null){
            connectStringee()
        }
        else{
            val action=intent.getIntExtra("action",0)
            handlerEnventClick(action)
        }
        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun connectStringee() {
        Log.d(TAG, "connectStringee: ")
        initStringeeClient()
        connectStringeeClient()
    }

    private fun handlerEnventClick(input:Int){

       if(input==DECLINE){
           if (voiceCall!=null){
               voiceCall?.reject(object : StatusListener(){
                   override fun onSuccess() {
                   }
               })

           }else{
               videoCall?.reject(object : StatusListener(){
                   override fun onSuccess() {
                   }
               })
           }
           notificationManagerCompat?.cancel(NOTIFICATION_ID)
       }

    }

    private fun connectStringeeClient() {
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
                        voiceCall=p0
                        videoCall=null
                        voiceCallMap[p0.callId] = p0
                        if (client!!.isConnected){
                            val fullnameCallFrom= getFullnameOfFromCall(p0.from)
                            sendNotify(fullnameCallFrom,p0.callId,"VoiceCallActivity")

                        }
                    }
                }
            }

            override fun onIncomingCall2(p0: StringeeCall2?) {
                CoroutineScope(Dispatchers.Main).launch {
                    if (p0 != null) {
                        voiceCall=null
                        videoCall=p0
                        videoCallMap[p0.callId] = p0
                        if (client!!.isConnected){
                            val fullnameCallFrom= getFullnameOfFromCall(p0.from)
                            sendNotify(fullnameCallFrom,p0.callId,"VideoCallActivity")
                        }
                    }
                }
            }

            override fun onConnectionError(p0: StringeeClient?, p1: StringeeError?) {
                CoroutineScope(Dispatchers.Main).launch {
                    if (p0 != null) {
                        connectStringee()
                    }
                }
            }

            override fun onRequestNewToken(p0: StringeeClient?) {
                if (p0 != null) {
                    Log.e(TAG, "onRequestNewToken")
                    connectStringee()
                }
            }

            override fun onCustomMessage(p0: String?, p1: JSONObject?) {
                Log.e(TAG, "onCustomMessage :")
            }

            override fun onTopicMessage(p0: String?, p1: JSONObject?) {
                Log.e(TAG, "onTopicMessage :")
            }
        })

    }

    private fun sendNotify(callingFrom: String, callId:String,CallingTaget:String) {

        val remoteViews = RemoteViews(packageName, R.layout.layout_notify_incomming_call)

        val resultIntent:Intent = createIntent(CallingTaget,callId)

        // Create the TaskStackBuilder
        val resultPendingIntent= createPendingIntent(resultIntent)

        remoteViews.setTextViewText(R.id.tvFullNameNotification, callingFrom)

        remoteViews.setOnClickPendingIntent(R.id.btnNotificationDecline,createPendingIntentBroadCast(-1))

        val notify = createNotify(remoteViews,resultPendingIntent)

        notificationManagerCompat = NotificationManagerCompat.from(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManagerCompat?.notify(NOTIFICATION_ID, notify)
        }
    }

    private fun createPendingIntentBroadCast(action: Int): PendingIntent? {
        val intent=Intent(this, myBroadCast::class.java)
        intent.putExtra("action",action)

        return PendingIntent.getBroadcast(this,action,intent,PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun createNotify(remoteViews: RemoteViews, resultPendingIntent: PendingIntent?): Notification {
        return NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_chat)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setCustomContentView(remoteViews)
            .setSound(null)
            .setAutoCancel(true)
            .setTimeoutAfter(15000L)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setContentIntent(resultPendingIntent)
            .build()
    }
    private fun createPendingIntent(resultIntent: Intent): PendingIntent? {
        return TaskStackBuilder.create(this)
            .addNextIntentWithParentStack(resultIntent)
            .getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    private fun createIntent(callingTaget: String,callId:String): Intent {
        val resultIntent:Intent
        if (callingTaget == "VoiceCallActivity"){
            resultIntent= Intent(this, VoiceCallActivity::class.java)
        }
        else{
            resultIntent= Intent(this, VideoCallActivity::class.java)
        }

        resultIntent.putExtra("isComingCall",true)
        resultIntent.putExtra("callId",callId)
        resultIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        return resultIntent
    }


    suspend fun getFullnameOfFromCall(idProfile: String):String {
        val database= FirebaseDatabase.getInstance()
        val job=database.reference.child("profile").child(idProfile)
            .get()

        val themProfile=createUserProfile(job.await())
        return themProfile.fullname
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