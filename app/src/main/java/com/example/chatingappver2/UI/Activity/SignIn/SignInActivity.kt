package com.example.chatingappver2.UI.Activity.SignIn

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatingappver2.Model.UserProfile
import com.example.chatingappver2.R
import com.example.chatingappver2.UI.Activity.ForgotPassword.ForgotPasswordActivity
import com.example.chatingappver2.UI.Activity.MainActivity.HomeActivity
import com.example.chatingappver2.UI.Activity.ProfileCreate.ProfileCreateActivity
import com.example.chatingappver2.UI.Activity.SignUp.SignUpActivity
import com.example.chatingappver2.UI.Activity.Verification.VerificationActivity
import com.example.finalprojectchatapplycation.Dialog.progressDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_in.btnSignIn
import kotlinx.android.synthetic.main.activity_sign_in.edtEmailLogin
import kotlinx.android.synthetic.main.activity_sign_in.edtxtPassword
import kotlinx.android.synthetic.main.activity_sign_in.tvForgotPassword
import kotlinx.android.synthetic.main.activity_sign_in.tvSignUp


class SignInActivity() : AppCompatActivity(), View.OnClickListener,
    SignInContract.view {
    private var dialog: Dialog? = null
    private val TAG: String = "SignInActivity"
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser
    private val presenter by lazy {
        SigninPresenter(
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
        when (view.id) {
            btnSignIn.id -> {
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
        val valueOf: String = edtEmailLogin.getText().toString()
        val valueOf2: String = edtxtPassword.getText().toString()
        var dialog: Dialog? = dialog
        if (dialog == null) {
            dialog = null
        }
        dialog!!.show()
        presenter.Login(valueOf, valueOf2)
    }

    override fun onStop() {
        //notifyOffline()
        Log.e(TAG, "onStop: " )
        super.onStop()
    }

    override fun onDestroy() {
        //onStop()
        Log.e(TAG, "onDestroy: " )
        super.onDestroy()
    }

    private fun notifyOfflineForEveryone() {

        database.reference.child("currentContacts").get().addOnCompleteListener { task->
            if (task.isSuccessful){
                for (itemCurrentContacts in task.result.children){
                    if (itemCurrentContacts.key!= currentUser!!.uid){
                        database.reference.child( "currentContacts").child(itemCurrentContacts.key.toString())
                            .child(currentUser.uid).child("theyIsActive")
                            .setValue(false).addOnCompleteListener { UpdateProfile->
                                if (UpdateProfile.isSuccessful){

                                    Log.d(TAG, "notifyOnlineForEveryone: Success")
                                }else{

                                    Log.e(TAG, "notifyOnlineForEveryone: fail")
                                }
                            }
                    }
                }
            }
        }
    }



    private fun notifyOffline() {
        notifyOfflineOnProfile()
        notifyOfflineForEveryone()
    }

    private fun notifyOfflineOnProfile() {
        database.reference.child("profile").child(currentUser!!.uid).child("theyIsActive")
            .setValue(false).addOnCompleteListener { setOnlineOnProfile->
                if (setOnlineOnProfile.isSuccessful){
                    Log.d(TAG, "notifyOfflineOnProfile: Success")
                }
                else{
                    Log.e(TAG, "notifyOfflineOnProfile: Fail")
                }

            }
    }
    private fun createUserProfile(snapshot: DataSnapshot): UserProfile {
        val resultMap = snapshot.value as Map<String, Any>

        val theyIsActive: Boolean = resultMap["theyIsActive"].toString().toBoolean()
        val fullname: String = resultMap["fullname"].toString()
        val urlImgProfile: String = resultMap["urlImgProfile"].toString()
        val dateOfBirth: String = resultMap["dateOfBirth"].toString()
        val email: String = resultMap["email"].toString()
        val idUser: String = resultMap["idUser"].toString()

        return UserProfile(dateOfBirth, email, fullname, idUser, theyIsActive, urlImgProfile)
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