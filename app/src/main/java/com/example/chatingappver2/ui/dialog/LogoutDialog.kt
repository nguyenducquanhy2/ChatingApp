package com.example.chatingappver2.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.view.WindowManager
import com.example.chatingappver2.R
import com.example.chatingappver2.ui.base.dialog.BaseDialog
import kotlinx.android.synthetic.main.layout_logout.btnAllowLogout
import kotlinx.android.synthetic.main.layout_logout.btnCancelLogout

class LogoutDialog(context: Context):BaseDialog(context) {
    lateinit var logoutEvent:()->Unit
    override fun getLayoutID(): Int =R.layout.layout_logout

    override fun isCancelableDialog(): Boolean =false

    override fun setupListeners() {
        val btnCancel = this.btnCancelLogout
        val btnLogout = this.btnAllowLogout
        btnCancel.setOnClickListener {
            dismiss()
        }

        btnLogout.setOnClickListener {
            logoutEvent()
            dismiss()
        }
    }
}