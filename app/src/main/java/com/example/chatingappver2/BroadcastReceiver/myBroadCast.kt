package com.example.chatingappver2.BroadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.chatingappver2.service.CallService

class myBroadCast :BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action= intent?.getIntExtra("action",0)
        val intentService=Intent(context, CallService::class.java)
        intentService.putExtra("action",action)
        context?.startService(intentService)
    }
}