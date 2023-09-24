package com.example.chatingappver2.ui.base.dialog

import android.content.Context
import com.google.android.material.bottomsheet.BottomSheetDialog

abstract class BaseBottomDialog(context: Context) : BottomSheetDialog(context) {

    init {
        setCancelable(isCancelableDialog())
        setContentView(getLayoutID())
        setupViews()
        setupListeners()
    }

    abstract fun getLayoutID(): Int

    open fun isCancelableDialog(): Boolean = true

    open fun setupViews(){}

    open fun setupListeners(){}

}