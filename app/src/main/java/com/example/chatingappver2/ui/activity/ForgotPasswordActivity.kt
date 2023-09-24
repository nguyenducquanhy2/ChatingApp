package com.example.chatingappver2.ui.activity

import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import com.example.chatingappver2.R
import com.example.chatingappver2.contract.activity.ForgotPassContract
import com.example.chatingappver2.presenter.activity.ForgotPassPresenter
import com.example.chatingappver2.ui.base.activity.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_forgot_password.*

@AndroidEntryPoint
class ForgotPasswordActivity : BaseActivity(),
    ForgotPassContract.View {

    private val presenter by lazy{ ForgotPassPresenter(this, authRepository) }

    override fun getLayoutID(): Int =R.layout.activity_forgot_password

    override fun onCreateActivity() {
        registerCliclListener()
    }

    override fun onNetworkConnected() {
        Toast.makeText(this, "Network connected", Toast.LENGTH_SHORT).show()
    }

    override fun onNetworkDisconnected() {
        Toast.makeText(this, "Network not connected", Toast.LENGTH_SHORT).show()
    }

    private fun registerCliclListener() {
        btnSendRequestNewPass.setOnClickListener(object :OnClickListener{
            override fun onClick(v: View?) {
                val emailForgotPass=edtEmailForgotPass.text.toString()
                if (!isNetworkState){
                    onNetworkDisconnected()
                    return
                }
                showLoadingProgress()
                presenter.sendResquestResetPass(emailForgotPass)
            }
        })
    }

    override fun notifyEmailInvalid() {
        dismissLoadingProgress()
        Toast.makeText(this, "The email address badly formatted", Toast.LENGTH_SHORT).show()
    }

    override fun notifySendEmailSuccess() {
        dismissLoadingProgress()
        Toast.makeText(this, "Email sent..", Toast.LENGTH_SHORT).show()
    }

    override fun notifySendEmailFail() {
        dismissLoadingProgress()
        Toast.makeText(this, "Email sending fail!!", Toast.LENGTH_SHORT).show()
    }


}