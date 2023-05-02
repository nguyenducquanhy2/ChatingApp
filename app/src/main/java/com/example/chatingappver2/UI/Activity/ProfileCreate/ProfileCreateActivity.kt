package com.example.chatingappver2.UI.Activity.ProfileCreate

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatingappver2.R
import com.example.chatingappver2.UI.Activity.MainActivity.HomeActivity
import com.example.finalprojectchatapplycation.Dialog.progressDialog
import kotlinx.android.synthetic.main.activity_profile_create.*


class ProfileCreateActivity : AppCompatActivity(),OnClickListener, CreateProfileContract.view {
    private val REQUEST_CODE: Int=111
    private val presenter by lazy { PresenterCreateProfile(this,this) }
    private var uriImg:Uri?=null
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_create)
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
        val FullName:String=edtFullNameProfile.text.toString()
        val Date:String=tvDate.text.toString()
        dialog= progressDialog.progressDialog(this)
        uriImg?.let {
            dialog.show()
            presenter.submitProfileToFireBase(it,FullName,Date ) }
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
        dialog.dismiss()
        Toast.makeText(this, "Update profile success!", Toast.LENGTH_SHORT).show()
        val intent=Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun submitFail() {
        dialog.dismiss()
        Toast.makeText(this, "Update profile fail!", Toast.LENGTH_SHORT).show()
    }

    override fun fullNameEmpty() {
        dialog.dismiss()
        Toast.makeText(this, "place full name mustn't empty!", Toast.LENGTH_SHORT).show()
    }

    override fun setTextViewDate(value:String) {
        tvDate.text=value
    }

    override fun dateInValid() {
        dialog.dismiss()
        Toast.makeText(this, "Date mustn't empty", Toast.LENGTH_SHORT).show()
    }


}