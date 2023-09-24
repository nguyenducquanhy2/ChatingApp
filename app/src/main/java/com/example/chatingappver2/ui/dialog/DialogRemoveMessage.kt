package com.example.chatingappver2.ui.dialog

import android.content.Context
import android.view.View
import android.widget.Toast
import com.example.chatingappver2.R
import com.example.chatingappver2.ui.base.dialog.BaseBottomDialog
import kotlinx.android.synthetic.main.layout_delete_message.removeForYou
import kotlinx.android.synthetic.main.layout_delete_message.unsendEveryOne

class DialogRemoveMessage(private val context: Context, private val isMyMessage: Boolean) :
    BaseBottomDialog(context) {

    lateinit var unsendMsgForEveryOne:()->Unit
    lateinit var unsendMsgForYou:()->Unit

    override fun getLayoutID(): Int = R.layout.layout_delete_message

    override fun isCancelableDialog(): Boolean = true

    override fun setupViews() {
        if (isMyMessage) {
            this.unsendEveryOne.visibility = View.VISIBLE
        } else {
            this.unsendEveryOne.visibility = View.GONE
        }
    }

    override fun setupListeners() {
        this.unsendEveryOne.setOnClickListener {
            Toast.makeText(context, "unsend", Toast.LENGTH_SHORT).show()
            unsendMsgForEveryOne()
            dismiss()
        }

        this.removeForYou.setOnClickListener {
            unsendMsgForYou()
            dismiss()
        }
    }
}