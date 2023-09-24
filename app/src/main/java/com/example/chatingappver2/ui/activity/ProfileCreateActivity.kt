package com.example.chatingappver2.ui.activity

import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import com.example.chatingappver2.BroadcastReceiver.OnNetworkConnectedListener
import com.example.chatingappver2.R
import com.example.chatingappver2.contract.activity.CreateProfileContract
import com.example.chatingappver2.presenter.activity.PresenterCreateProfile
import com.example.chatingappver2.ui.base.activity.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_profile_create.*

@AndroidEntryPoint
class ProfileCreateActivity : BaseActivity(),OnClickListener,
    CreateProfileContract.view , OnNetworkConnectedListener {

    private val REQUEST_CODE: Int=111
    private val presenter by lazy { PresenterCreateProfile(this,this,profileRepository) }
    private var uriImg:Uri?=null

    override fun getLayoutID(): Int =R.layout.activity_profile_create

    override fun onCreateActivity() {
        registerClickListener()
    }


    private fun registerClickListener() {
        imgInformationUser.setOnClickListener(this)
        btnSetupProfile.setOnClickListener(this)
        btnChooseDate.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.imgInformationUser->{
                chooseImgFormGraly()
            }
            R.id.btnSetupProfile->{

                submitProfile()
            }
            R.id.btnChooseDate->{
                presenter.openDialogDatePicker()
            }

        }
    }

    private fun submitProfile() {
        val fullname:String=edtFullNameProfile.text.toString()
        val date:String=tvDate.text.toString()

        uriImg?.let {
            showLoadingProgress()
            presenter.submitProfileToFireBase(it,fullname,date ) }
    }

    private fun chooseImgFormGraly() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==REQUEST_CODE&&resultCode== RESULT_OK&&data!=null){
            uriImg=data.data
            imgInformationUser.setImageURI(uriImg)
        }

    }

    override fun submitSuccess() {
        dismissLoadingProgress()
        Toast.makeText(this, "Update profile success!", Toast.LENGTH_SHORT).show()
        val intent=Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun submitFail() {
        dismissLoadingProgress()
        Toast.makeText(this, "Update profile fail!", Toast.LENGTH_SHORT).show()
    }

    override fun fullNameEmpty() {
        dismissLoadingProgress()
        Toast.makeText(this, "place full name mustn't empty!", Toast.LENGTH_SHORT).show()
    }

    override fun setTextViewDate(value:String) {
        tvDate.text=value
    }

    override fun dateInValid() {
        dismissLoadingProgress()
        Toast.makeText(this, "Date mustn't empty", Toast.LENGTH_SHORT).show()
    }

    override fun onNetworkConnected() {
        Toast.makeText(this, "Network connected", Toast.LENGTH_SHORT).show()
    }

    override fun onNetworkDisconnected() {
        Toast.makeText(this, "Network not connected", Toast.LENGTH_SHORT).show()
    }


}