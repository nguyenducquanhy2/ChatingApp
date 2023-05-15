package com.example.chatingappver2.UI.Activity.Verification

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatingappver2.R
import com.example.finalprojectchatapplycation.Dialog.progressDialog
import kotlinx.android.synthetic.main.activity_verification.btnSendEmailAgain

class VerificationActivity : AppCompatActivity(), VerificationContract.view {
    private lateinit var dialog: Dialog

    private val presenter by lazy { VerificationPresenter(this) }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_verification)
        registerListenser()
    }

    private fun registerListenser() {
        btnSendEmailAgain.setOnClickListener{
            dialog = progressDialog.progressDialog(this)
            dialog.show()
            presenter.EmailsendAgain()
        }
    }

    override fun exceptionSendVerifiEmail() {
        dialog.dismiss()
        Toast.makeText(this, "send email address is fail", Toast.LENGTH_SHORT).show()
    }

    override fun sentEmail(email: String) {
        dialog.dismiss()
        Toast.makeText(this, "$email sent.", Toast.LENGTH_SHORT).show()
    }

}