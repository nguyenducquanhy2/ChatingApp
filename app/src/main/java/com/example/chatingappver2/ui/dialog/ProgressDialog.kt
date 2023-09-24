package com.example.finalprojectchatapplycation.Dialog

import android.app.Dialog
import android.content.Context
import kotlin.jvm.internal.Intrinsics
import android.view.LayoutInflater

import android.view.ViewGroup
import android.graphics.drawable.ColorDrawable
import com.example.chatingappver2.R
import com.example.chatingappver2.ui.base.dialog.BaseAlertDialog

class ProgressDialog(context: Context):BaseAlertDialog(context) {

    override fun getLayoutID(): Int =R.layout.dialog_progress

    override fun isCancelableDialog(): Boolean =false

}