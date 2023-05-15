package com.example.chatingappver2.UI.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatingappver2.R
import com.example.chatingappver2.UI.Dialog.NotifyInComingCallDialog
import kotlinx.android.synthetic.main.layout_notify_incoming_call.btnCancelIncomingCall
import kotlinx.android.synthetic.main.layout_notify_incoming_call.btnReplyIncomingCall

class DialogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog)

//        window.addFlags(
//            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                    or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
//        )
//        window.addFlags(
//            (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
//        )
//        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)

        supportActionBar?.title =""



        val dialogIncomingCall = NotifyInComingCallDialog.logoutDialog(this)

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