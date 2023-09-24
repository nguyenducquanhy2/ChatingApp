package com.example.chatingappver2.ui.base.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.view.WindowManager

abstract class BaseDialog(context: Context) : Dialog(context) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(getLayoutID())
        setCancelable(isCancelableDialog())
        setupWindowFeatures()
        setupViews()
        setupListeners()
    }

    abstract fun getLayoutID(): Int

    open fun isCancelableDialog(): Boolean = false

    open fun setupViews(){}

    open fun setupListeners(){}

    private fun setupWindowFeatures() {
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window?.setBackgroundDrawable(ColorDrawable(0))
    }
}