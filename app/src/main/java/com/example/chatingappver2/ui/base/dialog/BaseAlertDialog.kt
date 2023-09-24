package com.example.chatingappver2.ui.base.dialog

import android.content.Context

abstract class BaseAlertDialog(context: Context):BaseDialog(context) {
    abstract override fun getLayoutID(): Int
}