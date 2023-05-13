package com.example.chatingappver2.UI.Dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import com.example.chatingappver2.R

object NotifyInComingCallDialog {
    fun logoutDialog(context: Context): Dialog {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_notify_incoming_call)
        dialog.setCancelable(false)
        val window = dialog.window
        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT)
        window.setGravity(Gravity.TOP)
        window.setBackgroundDrawable(ColorDrawable(0))

        return dialog
    }
}