package com.example.chatingappver2.UI.Activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.chatingappver2.Model.UserProfile
import com.example.chatingappver2.R
import com.example.chatingappver2.UI.Activity.ShowFullScreenImg.ShowImgFullScreenActivity
import com.example.finalprojectchatapplycation.Dialog.progressDialog
import kotlinx.android.synthetic.main.activity_view_infor.*

class viewInforActivity : AppCompatActivity() {
    private lateinit var dialog: Dialog
    private lateinit var urlImg:String
    private var accountFocus: UserProfile? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_infor)

        dialog = progressDialog.progressDialog(this)
        dialog.show()
        getDataFromBundle()
        getInformation()
        registerEventCliclListener()
        dialog.dismiss()

    }

    private fun getDataFromBundle() {
        val bundle =intent.getBundleExtra("dataFromChatActivity")

        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU){
            accountFocus= bundle!!.getSerializable("accountForcus", UserProfile::class.java)
        }else{
            accountFocus= bundle!!.getSerializable("accountForcus") as UserProfile
        }
    }

    private fun registerEventCliclListener() {
        imtAvataProfileViewInforActivity.setOnClickListener{
            val intent= Intent(this, ShowImgFullScreenActivity::class.java)
            intent.putExtra("urlImg",urlImg)
            startActivity(intent)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getInformation() {
        urlImg = accountFocus!!.urlImgProfile.toString()
        Glide.with(this).asBitmap().load(urlImg).into(imtAvataProfileViewInforActivity)
        InforFullNameViewInforActivity.text = accountFocus!!.fullname
        InforEmailViewInforActivity.text = accountFocus!!.email
        InforBirthDayViewInforActivity.text = accountFocus!!.dateOfBirth
    }

}