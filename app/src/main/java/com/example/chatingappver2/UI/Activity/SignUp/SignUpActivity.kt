package com.example.chatingappver2.UI.Activity.SignUp

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatingappver2.R
import com.example.chatingappver2.UI.Activity.Verification.VerificationActivity
import com.example.finalprojectchatapplycation.Dialog.progressDialog
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity(), SignUpContract.view {

    private val presenter by lazy { SignUpPresenter(this, this) }
    private var progessDialog: Dialog? = null


    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_sign_up)
        progessDialog = progressDialog.progressDialog(this)
        registerClickListener()
    }

    private fun registerClickListener() {
        btnSignUp.setOnClickListener {
            val email: String=edtEmailSignUp.text.toString()
            val password: String=edtxtPassword.text.toString()
            val confirmPassword: String=edtConfirmPassword.text.toString()
            presenter.SignUp(email,password,confirmPassword)
        }
    }

    override fun notifyEmailWrong() {
        Toast.makeText(this, "The email address is badly formatted! ", Toast.LENGTH_SHORT).show()
    }

    override fun notifyPasswordWrong() {
        Toast.makeText(this, "The password is invalid or empty!", Toast.LENGTH_SHORT).show()
    }

    override fun notifySignUpFail() {
        Toast.makeText(this, "SignUp is fail!", Toast.LENGTH_SHORT).show()
    }

    override fun notifyConfirmPasswordWrong() {
        Toast.makeText(this, "The confirm password is invalid or empty! ", Toast.LENGTH_SHORT).show()
    }

    override fun notifyTwoPassNotEqual() {
        Toast.makeText(this, "Two place password is not equal!", Toast.LENGTH_SHORT).show()
    }

    override fun notifySendEmailVerification() {
        val signUpActivity = this
        Toast.makeText(signUpActivity, "Email verification sending to you", Toast.LENGTH_SHORT).show()
        startActivity(Intent(signUpActivity, VerificationActivity::class.java))
        finish()
    }

    override fun notifyDuplicateUserAccount() {
        Toast.makeText(this, "Duplicate user account!", Toast.LENGTH_SHORT).show()
    }



}