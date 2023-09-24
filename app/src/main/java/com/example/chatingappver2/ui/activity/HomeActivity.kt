package com.example.chatingappver2.ui.activity


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.chatingappver2.model.UserProfile
import com.example.chatingappver2.R
import com.example.chatingappver2.ui.dialog.LogoutDialog
import com.example.chatingappver2.ui.fragment.FragmentChangePassword
import com.example.chatingappver2.ui.fragment.FriendRequestFragment
import com.example.chatingappver2.ui.fragment.FragmentFriendRequestSent
import com.example.chatingappver2.ui.fragment.FragmentHome
import com.example.chatingappver2.ui.fragment.FragmentNewContacts
import com.example.chatingappver2.ui.fragment.FragmentUpdateProfile
import com.example.chatingappver2.ui.fragment.FragmentViewProfile
import com.example.chatingappver2.service.CallService
import com.example.chatingappver2.contract.activity.MainActivityContract
import com.example.chatingappver2.presenter.activity.MainActivityPresenter
import com.example.chatingappver2.ui.base.activity.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.drawer_layout
import kotlinx.android.synthetic.main.activity_home.nav_view
import kotlinx.android.synthetic.main.nav_header_home.view.BirthDayHeader
import kotlinx.android.synthetic.main.nav_header_home.view.fullnameHeader
import kotlinx.android.synthetic.main.nav_header_home.view.imageViewHeader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : BaseActivity(), MainActivityContract.view {

    private var countUserBackPress=1

    private val TAG: String = "HomeActivity"
    val presenter by lazy { MainActivityPresenter(this,profileRepository) }
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var intentCallService:Intent

    override fun getLayoutID(): Int =R.layout.activity_home

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateActivity() {
        presenter.loadHeaderProfile()
        presenter.setNotifityOnline()
        setDefaulFragment()
        setToggleToActionBar()
        registerItemClickNavView()
        hideNavBar()
        //connectStringee()

        callSerVice()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun callSerVice() {
        checkRequestPermission()
        intentCallService=Intent(this, CallService::class.java)

        startService(intentCallService)
    }

    override fun onRestart() {
        Log.e(TAG, "onRestart: " )
        if (CallService.client?.isConnected==false){
            stopService(intentCallService)
            intentCallService=Intent(this, CallService::class.java)
            startService(intentCallService)
        }
        super.onRestart()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkRequestPermission() {
        CoroutineScope(Dispatchers.Main).launch {
            val Listpermissions: MutableList<String> = mutableListOf()
            if (ContextCompat.checkSelfPermission(
                    this@HomeActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Listpermissions.add(Manifest.permission.POST_NOTIFICATIONS)
            }

            if (Listpermissions.size > 0) {
                ActivityCompat.requestPermissions(
                    this@HomeActivity,
                    Listpermissions.toTypedArray(),
                    0
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)


        var isGranted = false
        if (grantResults.isNotEmpty()) {
            for (item in grantResults) {
                if (item != PackageManager.PERMISSION_GRANTED) {
                    isGranted = false
                    break
                } else {
                    isGranted = true
                }

            }
        }

        if (requestCode == 0) {
            if (!isGranted) {
                Toast.makeText(this, "You need allow permission.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onNetworkConnected() {
        Toast.makeText(this, "Network connected", Toast.LENGTH_SHORT).show()
    }

    override fun onNetworkDisconnected() {
        Toast.makeText(this, "Network not connected", Toast.LENGTH_SHORT).show()
    }

    private fun setDefaulFragment() {
        supportActionBar?.title ="Messages"
        val fragmentHome = FragmentHome()
        replateFragment(fragmentHome)
    }

    private fun registerItemClickNavView() {

        nav_view.getHeaderView(0).setOnClickListener {
            supportActionBar?.title ="My Profile"
            countUserBackPress=0
            val fragmentViewProfile = FragmentViewProfile()
            replateFragment(fragmentViewProfile)
            drawer_layout.close()
        }

        nav_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.logout -> {
                    showDialogLogout()
                }

                R.id.nav_UpdateProfile -> {
                    supportActionBar?.title ="Update Profile"
                    countUserBackPress=0
                    val fragmentUpdateProfile = FragmentUpdateProfile()
                    replateFragment(fragmentUpdateProfile)
                    drawer_layout.close()
                }

                R.id.nav_view_Home -> {
                    countUserBackPress=1
                    setDefaulFragment()
                    drawer_layout.close()
                }

                R.id.nav_change_password -> {
                    supportActionBar?.title ="Change Password"
                    countUserBackPress=0
                    val fragmentChangePassword = FragmentChangePassword()
                    replateFragment(fragmentChangePassword)
                    drawer_layout.close()
                }

                R.id.nav_newContact -> {
                    supportActionBar?.title ="Add Friend"
                    countUserBackPress=0
                    val fragmentNewContacts = FragmentNewContacts()
                    replateFragment(fragmentNewContacts)
                    drawer_layout.close()
                }

                R.id.nav_Friend_Request -> {
                    supportActionBar?.title ="Friend Request"
                    countUserBackPress=0
                    val friendRequestFragment = FriendRequestFragment()
                    replateFragment(friendRequestFragment)
                    drawer_layout.close()
                }

                R.id.nav_Friend_Request_Sent -> {
                    supportActionBar?.title ="Friend Request Sent"
                    countUserBackPress=0
                    val fragmentFriendRequestSent = FragmentFriendRequestSent()
                    replateFragment(fragmentFriendRequestSent)
                    drawer_layout.close()
                }
            }
            return@setNavigationItemSelectedListener true
        }


    }

    override fun onBackPressed() {
        countUserBackPress++
        if (countUserBackPress==1){
            setDefaulFragment()
            return
        }
        countUserBackPress=1
        showDialogLogout()
    }

    private fun showDialogLogout() {
        val dialogLogout = LogoutDialog(this)
        dialogLogout.logoutEvent={
            presenter.logout()
        }
        dialogLogout.show()
    }

    private fun replateFragment(fragment: Fragment) {
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(R.id.content_frame, fragment)
        fragmentTransition.commit()
    }

    private fun setToggleToActionBar() {
        toggle = ActionBarDrawerToggle(this, drawer_layout, R.string.open, R.string.close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun SignOutSuccess() {
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun SignOutFail() {
        Log.e(TAG, "SignOutFail: " )
    }

    override fun setHeaderProfile(userProfile: UserProfile) {
        val viewHeader = nav_view.getHeaderView(0)
        Glide.with(this@HomeActivity).asBitmap().load(userProfile.urlImgProfile)
            .into(viewHeader.imageViewHeader)
        viewHeader.BirthDayHeader.text = "Birth Day: ${userProfile.dateOfBirth}"
        viewHeader.fullnameHeader.text = "Full name: ${userProfile.fullname}"

    }
}