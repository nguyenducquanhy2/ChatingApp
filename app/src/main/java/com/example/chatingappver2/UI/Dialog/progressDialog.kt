package com.example.finalprojectchatapplycation.Dialog

import android.app.Dialog
import android.content.Context
import kotlin.jvm.internal.Intrinsics
import android.view.LayoutInflater

import android.view.ViewGroup
import android.graphics.drawable.ColorDrawable
import com.example.chatingappver2.R

object progressDialog {

    fun progressDialog(context: Context):Dialog{
        val dialog = Dialog(context!!)
        dialog.setContentView(
            LayoutInflater.from(context).inflate(R.layout.dialog_progress, null as ViewGroup?)
        )
        dialog.setCancelable(false)
        val window = dialog.window
        Intrinsics.checkNotNull(window)
        window!!.setBackgroundDrawable(ColorDrawable(0))
        return dialog
    }



}