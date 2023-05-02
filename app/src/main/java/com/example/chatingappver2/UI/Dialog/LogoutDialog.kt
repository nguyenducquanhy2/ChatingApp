package com.example.chatingappver2.UI.Dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import com.example.chatingappver2.R

object LogoutDialog {
    fun logoutDialog(context: Context): Dialog {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(
            LayoutInflater.from(context).inflate(R.layout.layout_logout, null as ViewGroup?)
        )
        dialog.setCancelable(false)
        val window = dialog.window
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT)
        window!!.setBackgroundDrawable(ColorDrawable(0))

        return dialog
    }
}