package com.example.chatingappver2.ui.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.chatingappver2.model.UserProfile
import com.example.chatingappver2.R
import com.example.chatingappver2.ui.base.activity.BaseActivity
import com.example.finalprojectchatapplycation.Dialog.ProgressDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_view_infor.*
import kotlinx.android.synthetic.main.activity_view_infor.view.btnBackViewInforActivity
import kotlinx.android.synthetic.main.activity_view_infor.view.titleActivityViewInfor
@AndroidEntryPoint
class ViewInforActivity : BaseActivity() {

    private lateinit var urlImg:String
    private var accountFocus: UserProfile? = null


    override fun getLayoutID(): Int = R.layout.activity_view_infor

    override fun onCreateActivity() {
        showLoadingProgress()
        getDataFromBundle()
        getInformation()
        registerEventCliclListener()
        dismissLoadingProgress()
    }

    override fun onNetworkConnected() {
        Toast.makeText(this, "Network connected", Toast.LENGTH_SHORT).show()
    }

    override fun onNetworkDisconnected() {
        Toast.makeText(this, "Network not connected", Toast.LENGTH_SHORT).show()
    }

    private fun getDataFromBundle() {
        val bundle =intent.getBundleExtra("dataFromChatActivity")

        accountFocus = if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU){
            bundle!!.getSerializable("accountForcus", UserProfile::class.java)
        }else{
            bundle!!.getSerializable("accountForcus") as UserProfile
        }
    }

    private fun registerEventCliclListener() {
        toolBarActivityViewProfile.btnBackViewInforActivity.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        imtAvataProfileViewInforActivity.setOnClickListener{
            val intent= Intent(this, ShowImgFullScreenActivity::class.java)
            intent.putExtra("urlImg",urlImg)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getInformation() {
        urlImg = accountFocus!!.urlImgProfile
        toolBarActivityViewProfile.titleActivityViewInfor.text= "Profile"
        Glide.with(this).asBitmap().load(urlImg).into(imtAvataProfileViewInforActivity)
        InforFullNameViewInforActivity.text = accountFocus!!.fullname
        InforEmailViewInforActivity.text = accountFocus!!.email
        InforBirthDayViewInforActivity.text = accountFocus!!.dateOfBirth
    }

}