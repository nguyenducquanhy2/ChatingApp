package com.example.chatingappver2.UI.Dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import com.example.chatingappver2.R

object NotifyInComingCallDialog {

    fun logoutDialog(context: Context): Dialog {

        val dialog = Dialog(context,R.style.NewDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_notify_incoming_call)
        dialog.setCancelable(false)
        val window = dialog.window
        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT)
        window.setGravity(Gravity.TOP)
//        window.setBackgroundDrawable(ColorDrawable(0))
        window.setBackgroundDrawableResource(android.R.color.transparent);
//        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
//        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
//        window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        return dialog

    }
}