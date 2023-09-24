package com.example.chatingappver2.ui.activity

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.chatingappver2.R
import com.example.chatingappver2.contract.activity.SignInContract
import com.example.chatingappver2.presenter.activity.SignInPresenter
import com.example.chatingappver2.ui.base.activity.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_sign_in.Button
import kotlinx.android.synthetic.main.activity_sign_in.edtEmailLogin
import kotlinx.android.synthetic.main.activity_sign_in.edtxtPassword
import kotlinx.android.synthetic.main.activity_sign_in.tvForgotPassword
import kotlinx.android.synthetic.main.activity_sign_in.tvSignUp
import java.lang.Exception

@AndroidEntryPoint
class SignInActivity :BaseActivity(), View.OnClickListener,
    SignInContract.View {
    private val TAG: String = "SignInActivity"

    private val presenter by lazy {
        SignInPresenter(
            this,
            authRepository
        )
    }

    override fun getLayoutID(): Int =R.layout.activity_sign_in

    override fun onCreateActivity() {
        supportActionBar!!.title = "Login"
        setOnNetworkConnectedListener(this)
        registerClickListener()

        if (!isNetworkState){
            onNetworkDisconnected()
            dismissLoadingProgress()
            return
        }

        presenter.checkCurrentlySigned()
    }

    override fun onClick(view: View) {
        when (view.id) {
            Button.id -> {
                login()
            }

            tvForgotPassword.id -> {
                startActivity(Intent(this, ForgotPasswordActivity::class.java))
            }

            tvSignUp.id -> {
                startActivity(Intent(this, SignUpActivity::class.java))
            }
        }

    }

    private fun login() {
        val email: String = edtEmailLogin.text.toString()
        val password: String = edtxtPassword.text.toString()


        if (!isNetworkState){
            onNetworkDisconnected()
            return
        }

        showLoadingProgress()
        presenter.login(email, password)
    }


    override fun notifyEmailWrong() {
        dismissLoadingProgress()
        Toast.makeText(this, "The email address is badly formatted", Toast.LENGTH_SHORT).show()
    }

    override fun notifyPasswordWrong() {
        dismissLoadingProgress()
        Toast.makeText(this, "The password is invalid or empty", Toast.LENGTH_SHORT).show()
    }

    override fun notifySignInFail(exception: Exception) {
        dismissLoadingProgress()
        Log.e(TAG, "notifySignInFail: $exception")
        Toast.makeText(this, "Email or Password is wrong!", Toast.LENGTH_SHORT).show()
    }

    override fun changeHomeActivity() {
        dismissLoadingProgress()
        startActivity(Intent(this, HomeActivity::class.java))
    }

    override fun changeVerifiActivity() {
        dismissLoadingProgress()
        startActivity(Intent(this, VerificationActivity::class.java))
    }

    override fun changeUpdateProfile() {
        dismissLoadingProgress()
        startActivity(Intent(this, ProfileCreateActivity::class.java))
    }

    private fun registerClickListener() {
        Button.setOnClickListener(this)
        tvForgotPassword.setOnClickListener(this)
        tvSignUp.setOnClickListener(this)
    }

    override fun onNetworkConnected() {
        Toast.makeText(this, "Network connected", Toast.LENGTH_SHORT).show()
    }

    override fun onNetworkDisconnected() {
        Toast.makeText(this, "Network not connected", Toast.LENGTH_SHORT).show()
    }
}