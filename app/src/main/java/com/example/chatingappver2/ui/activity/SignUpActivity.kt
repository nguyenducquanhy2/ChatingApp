package com.example.chatingappver2.ui.activity

import android.content.Intent
import android.widget.Toast
import com.example.chatingappver2.R
import com.example.chatingappver2.contract.activity.SignUpContract
import com.example.chatingappver2.presenter.activity.SignUpPresenter
import com.example.chatingappver2.ui.base.activity.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_sign_up.Button
import kotlinx.android.synthetic.main.activity_sign_up.edtConfirmPassword
import kotlinx.android.synthetic.main.activity_sign_up.edtEmailSignUp
import kotlinx.android.synthetic.main.activity_sign_up.edtxtPassword

@AndroidEntryPoint
class SignUpActivity : BaseActivity(), SignUpContract.view {

    private val presenter by lazy { SignUpPresenter(this, this, authRepository) }

    override fun getLayoutID(): Int =R.layout.activity_sign_up

    override fun onCreateActivity() {
        supportActionBar?.title = "SignUp"
        registerClickListener()
    }

    private fun registerClickListener() {
        Button.setOnClickListener {
            val email: String = edtEmailSignUp.text.toString()
            val password: String = edtxtPassword.text.toString()
            val confirmPassword: String = edtConfirmPassword.text.toString()

            if (!isNetworkState){
                onNetworkDisconnected()
                return@setOnClickListener
            }
            showLoadingProgress()
            presenter.SignUp(email, password, confirmPassword)
        }
    }

    override fun notifyEmailWrong() {
        dismissLoadingProgress()
        Toast.makeText(this, "The email address is badly formatted! ", Toast.LENGTH_SHORT).show()
    }

    override fun notifyPasswordWrong() {
        dismissLoadingProgress()
        Toast.makeText(this, "The password is invalid or empty!", Toast.LENGTH_SHORT).show()
    }

    override fun notifySignUpFail() {
        dismissLoadingProgress()
        Toast.makeText(this, "SignUp is fail!", Toast.LENGTH_SHORT).show()
    }

    override fun notifyConfirmPasswordWrong() {
        dismissLoadingProgress()
        Toast.makeText(this, "The confirm password is invalid or empty! ", Toast.LENGTH_SHORT)
            .show()
    }

    override fun notifyTwoPassNotEqual() {
        Toast.makeText(this, "Two place password is not equal!", Toast.LENGTH_SHORT).show()
    }

    override fun notifySendEmailVerification() {
        dismissLoadingProgress()
        Toast.makeText(this, "Email verification sending to you", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, VerificationActivity::class.java))
        finish()
    }

    override fun notifyDuplicateUserAccount() {
        dismissLoadingProgress()
        Toast.makeText(this, "Duplicate user account!", Toast.LENGTH_SHORT).show()
    }

    override fun onNetworkConnected() {
        Toast.makeText(this, "Network connected", Toast.LENGTH_SHORT).show()
    }

    override fun onNetworkDisconnected() {
        Toast.makeText(this, "Network not connected", Toast.LENGTH_SHORT).show()
    }

}