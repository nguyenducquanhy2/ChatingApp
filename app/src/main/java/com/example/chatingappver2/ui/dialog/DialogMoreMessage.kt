package com.example.chatingappver2.ui.dialog

import android.content.Context
import android.widget.Toast
import com.example.chatingappver2.R
import com.example.chatingappver2.ui.base.dialog.BaseAlertDialog
import kotlinx.android.synthetic.main.layout_more_message.forwardMessage
import kotlinx.android.synthetic.main.layout_more_message.removeMessage

class DialogMoreMessage(context: Context):BaseAlertDialog(context) {
    lateinit var showDialogRemoveMsg:()->Unit

    override fun getLayoutID(): Int = R.layout.layout_more_message

    override fun isCancelableDialog(): Boolean = false

    override fun setupListeners() {
        val btnForward = this.forwardMessage
        val btnRemove = this.removeMessage

        btnForward.setOnClickListener {
            Toast.makeText(context, "Forward", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        btnRemove.setOnClickListener {
            showDialogRemoveMsg()
            dismiss()
        }
    }




}