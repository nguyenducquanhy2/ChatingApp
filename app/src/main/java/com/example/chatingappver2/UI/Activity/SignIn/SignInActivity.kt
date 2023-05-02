package com.example.chatingappver2.UI.Activity.SignIn

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatingappver2.R
import com.example.chatingappver2.UI.Activity.ForgotPassword.ForgotPasswordActivity
import com.example.chatingappver2.UI.Activity.MainActivity.HomeActivity
import com.example.chatingappver2.UI.Activity.ProfileCreate.ProfileCreateActivity
import com.example.chatingappver2.UI.Activity.SignUp.SignUpActivity
import com.example.chatingappver2.UI.Activity.Verification.VerificationActivity
import com.example.finalprojectchatapplycation.Dialog.progressDialog
import kotlinx.android.synthetic.main.activity_sign_in.*


class SignInActivity() : AppCompatActivity(), View.OnClickListener,
    com.example.chatingappver2.UI.Activity.SignIn.SignInContract.view {
    private var dialog: Dialog? = null
    private val TAG: String = "SignInActivity"
    private val presenter by lazy {
        com.example.chatingappver2.UI.Activity.SignIn.SigninPresenter(
            this,
            this
        )
    }


    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_sign_in)
        supportActionBar!!.title = "Login"
        dialog = progressDialog.progressDialog(this)
        presenter.checkCurrentlySigned()
        registerClickListener()
    }

    private fun registerClickListener() {

        btnSignIn.setOnClickListener(this)
        tvForgotPassword.setOnClickListener(this)
        tvSignUp.setOnClickListener(this)
    }

     override fun onClick(view: View) {
        when(view.id){
            btnSignIn.id->{
                login()
            }
            tvForgotPassword.id->{
                startActivity(Intent(this, ForgotPasswordActivity::class.java))
            }
            tvSignUp.id->{
                startActivity(Intent(this, SignUpActivity::class.java))
            }
        }

    }

    private fun login() {
        val valueOf: String = edtEmailLogin.getText().toString()
        val valueOf2: String =edtxtPassword.getText().toString()
        var dialog: Dialog? = dialog
        if (dialog == null) {
            dialog = null
        }
        dialog!!.show()
        presenter.Login(valueOf, valueOf2)
    }


     override fun notifyEmailWrong() {
        var dialog: Dialog? = dialog
        if (dialog == null) {
            dialog = null
        }
        dialog!!.dismiss()
        Toast.makeText(this, "The email address is badly formatted", Toast.LENGTH_SHORT).show()
    }


     override fun notifyPasswordWrong() {
        var dialog: Dialog? = dialog
        if (dialog == null) {
            dialog = null
        }
        dialog!!.dismiss()
        Toast.makeText(this, "The password is invalid or empty", Toast.LENGTH_SHORT).show()
    }

     override fun notifySignInFail() {
        var dialog: Dialog? = dialog
        if (dialog == null) {
            dialog = null
        }
        dialog!!.dismiss()
        Toast.makeText(this, "Email or Password is wrong!", Toast.LENGTH_SHORT).show()
    }

     override fun changeHomeActivity() {
        var dialog: Dialog? = dialog
        if (dialog == null) {
            dialog = null
        }
        dialog!!.dismiss()
        startActivity(Intent(this, HomeActivity::class.java))
    }

     override fun changeVerifiActivity() {
        var dialog: Dialog? = dialog
        if (dialog == null) {
            dialog = null
        }
        dialog!!.dismiss()
        startActivity(Intent(this, VerificationActivity::class.java))
    }


     override fun changeUpdateProfile() {
        var dialog: Dialog? = dialog
        if (dialog == null) {
            dialog = null
        }
        dialog!!.dismiss()
        startActivity(Intent(this, ProfileCreateActivity::class.java))
    }
}