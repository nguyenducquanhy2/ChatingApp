package com.example.chatingappver2.ui.activity

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatingappver2.BroadcastReceiver.OnNetworkConnectedListener
import com.example.chatingappver2.R
import com.example.chatingappver2.contract.activity.VerificationContract
import com.example.chatingappver2.database.repository.AuthRepository
import com.example.chatingappver2.presenter.activity.VerificationPresenter
import com.example.chatingappver2.ui.base.activity.BaseActivity
import com.example.finalprojectchatapplycation.Dialog.ProgressDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_verification.btnSendEmailAgain
import javax.inject.Inject

@AndroidEntryPoint
class VerificationActivity : BaseActivity(), VerificationContract.view ,
    OnNetworkConnectedListener {

    private val presenter by lazy { VerificationPresenter(this, authRepository) }


    override fun getLayoutID(): Int=R.layout.activity_verification

    override fun onCreateActivity() {

        registerListenser()
    }

    private fun registerListenser() {
        btnSendEmailAgain.setOnClickListener {
            showLoadingProgress()
            presenter.EmailsendAgain()
        }
    }

    override fun exceptionSendVerifiEmail() {
        dismissLoadingProgress()
        Toast.makeText(this, "send email address is fail", Toast.LENGTH_SHORT).show()
    }

    override fun sentEmail(email: String) {
        dismissLoadingProgress()
        Toast.makeText(this, "$email sent.", Toast.LENGTH_SHORT).show()
    }
    override fun onNetworkConnected() {
        Toast.makeText(this, "Network connected", Toast.LENGTH_SHORT).show()
    }

    override fun onNetworkDisconnected() {
        Toast.makeText(this, "Network not connected", Toast.LENGTH_SHORT).show()
    }
}