package com.example.chatingappver2.UI.Dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.view.WindowManager
import com.example.chatingappver2.R

object moreDialogMessage {

    fun moreDialogMessage(context: Context): Dialog {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_more_message)
        dialog.setCancelable(false)
        val window = dialog.window
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)

        window!!.setBackgroundDrawable(ColorDrawable(0))

        return dialog
    }
}