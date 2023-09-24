package com.example.chatingappver2.ui.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.example.chatingappver2.R
import com.example.chatingappver2.ui.base.dialog.BaseAlertDialog
import kotlinx.android.synthetic.main.layout_notify_incoming_call.btnCancelIncomingCall
import kotlinx.android.synthetic.main.layout_notify_incoming_call.btnReplyIncomingCall

class NotifyInComingCallDialog(context: Context): BaseAlertDialog(context) {

    override fun getLayoutID(): Int = R.style.NewDialog

    override fun isCancelableDialog(): Boolean =false

}