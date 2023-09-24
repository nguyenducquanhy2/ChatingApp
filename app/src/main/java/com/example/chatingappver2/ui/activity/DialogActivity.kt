package com.example.chatingappver2.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatingappver2.R
import com.example.chatingappver2.ui.dialog.NotifyInComingCallDialog
import kotlinx.android.synthetic.main.layout_notify_incoming_call.btnCancelIncomingCall
import kotlinx.android.synthetic.main.layout_notify_incoming_call.btnReplyIncomingCall

class DialogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog)

        supportActionBar?.title =""

        val dialogIncomingCall = NotifyInComingCallDialog(this)

        dialogIncomingCall.btnReplyIncomingCall.setOnClickListener {

            dialogIncomingCall.dismiss()
            Toast.makeText(this, "dialogIncomingCall", Toast.LENGTH_SHORT).show()
            finish()
        }
        dialogIncomingCall.btnCancelIncomingCall.setOnClickListener {
            dialogIncomingCall.dismiss()
            Toast.makeText(this, "dialogIncomingCall", Toast.LENGTH_SHORT).show()
            finish()
        }

        dialogIncomingCall.show()

    }
}