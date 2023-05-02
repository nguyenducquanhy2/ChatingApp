package com.example.chatingappver2.UI.Activity.ForgotPassword

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatingappver2.R
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : AppCompatActivity(), ForgotPassContract.view {

    private val presenter by lazy{ ForgotPassPresenter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        registerCliclListener()

    }

    private fun registerCliclListener() {
        btnSendRequestNewPass.setOnClickListener(object :OnClickListener{
            override fun onClick(v: View?) {
                val emailForgotPass=edtEmailForgotPass.text.toString()
                presenter.SendResquestResetPass(emailForgotPass)
            }
        })
    }

    override fun notifyEmailInvalid() {
        Toast.makeText(this, "The email address badly formatted", Toast.LENGTH_SHORT).show()
    }

    override fun notifySendEmailSuccess() {
        Toast.makeText(this, "Email sent..", Toast.LENGTH_SHORT).show()
    }

    override fun notifySendEmailFail() {
        Toast.makeText(this, "Email sending fail!!", Toast.LENGTH_SHORT).show()
    }


}