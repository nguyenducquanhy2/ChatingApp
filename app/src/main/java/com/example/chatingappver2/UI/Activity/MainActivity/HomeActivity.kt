package com.example.chatingappver2.UI.Activity.MainActivity


import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.chatingappver2.Model.UserProfile
import com.example.chatingappver2.R
import com.example.chatingappver2.UI.Dialog.LogoutDialog
import com.example.chatingappver2.UI.Fragment.ChangePassword.FragmentChangePassword
import com.example.chatingappver2.UI.Fragment.FriendRequest.FriendRequestFragment
import com.example.chatingappver2.UI.Fragment.FriendRequestSent.FragmentFriendRequestSent
import com.example.chatingappver2.UI.Fragment.HomeFragment.FragmentHome
import com.example.chatingappver2.UI.Fragment.NewContacts.FragmentNewContacts
import com.example.chatingappver2.UI.Fragment.UpdateProfile.FragmentUpdateProfile
import com.example.chatingappver2.UI.Fragment.ViewProfile.FragmentViewProfile
import com.example.chatingappver2.UI.Service.CallService
import com.example.finalprojectchatapplycation.Dialog.progressDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_home.drawer_layout
import kotlinx.android.synthetic.main.activity_home.nav_view
import kotlinx.android.synthetic.main.layout_logout.btnAllowLogout
import kotlinx.android.synthetic.main.layout_logout.btnCancelLogout
import kotlinx.android.synthetic.main.nav_header_home.view.BirthDayHeader
import kotlinx.android.synthetic.main.nav_header_home.view.fullnameHeader
import kotlinx.android.synthetic.main.nav_header_home.view.imageViewHeader


class HomeActivity : AppCompatActivity(), MainActivityContract.view {
    private var countUserBackPress=1

    private val currentUser = FirebaseAuth.getInstance().currentUser!!
    private val TAG: String = "HomeActivity"
    val presenter by lazy { MainActivityPresenter(this, this) }
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var dialog: Dialog
    private lateinit var intentCallService:Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        dialog = progressDialog.progressDialog(this)

        presenter.loadHeaderProfile()
        presenter.setNotifityOnline()
        setDefaulFragment()
        setToggleToActionBar()
        registerItemClickNavView()
        hideNavBar()
        //connectStringee()

        CallSerVice()

    }
    private fun CallSerVice() {
        intentCallService=Intent(this, CallService::class.java)
        startService(intentCallService)
    }
    override fun onStop() {
        Log.e(TAG, "onStop: ", )

        super.onStop()
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
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")

    }
    private fun hideNavBar() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }
    private fun setDefaulFragment() {
        supportActionBar?.title ="Messages"
        val FragmentHome = FragmentHome()
        replateFragment(FragmentHome)
    }
    private fun registerItemClickNavView() {
        nav_view.getHeaderView(0).imageViewHeader.setOnClickListener {
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
                    val FragmentUpdateProfile = FragmentUpdateProfile()
                    replateFragment(FragmentUpdateProfile)
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
                    val FragmentChangePassword = FragmentChangePassword()
                    replateFragment(FragmentChangePassword)
                    drawer_layout.close()
                }

                R.id.nav_newContact -> {
                    supportActionBar?.title ="Add Friend"
                    countUserBackPress=0
                    val FragmentNewContact = FragmentNewContacts()
                    replateFragment(FragmentNewContact)
                    drawer_layout.close()
                }

                R.id.nav_Friend_Request -> {
                    supportActionBar?.title ="Friend Request"
                    countUserBackPress=0
                    val FriendRequestFragment = FriendRequestFragment()
                    replateFragment(FriendRequestFragment)
                    drawer_layout.close()
                }

                R.id.nav_Friend_Request_Sent -> {
                    supportActionBar?.title ="Friend Request Sent"
                    countUserBackPress=0
                    val FragmentFriendRequestSent = FragmentFriendRequestSent()
                    replateFragment(FragmentFriendRequestSent)
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
        val dialogLogout = LogoutDialog.logoutDialog(this)

        val btnCancel = dialogLogout.btnCancelLogout
        val btnLogout = dialogLogout.btnAllowLogout

        btnCancel.setOnClickListener {
            dialogLogout.dismiss()
        }

        btnLogout.setOnClickListener {
            presenter.logout()
            dialogLogout.dismiss()
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
//        dialog.dismiss()
        finish()
    }
    override fun SignOutFail() {
        dialog.dismiss()
    }
    override fun setHeaderProfile(value: UserProfile, currentUser: FirebaseUser) {
        val viewHeader = nav_view.getHeaderView(0)
        Glide.with(this@HomeActivity).asBitmap().load(value.urlImgProfile)
            .into(viewHeader.imageViewHeader)
        viewHeader.BirthDayHeader.text = "Birth Day: ${value.dateOfBirth}"
        viewHeader.fullnameHeader.text = "Full name: ${value.fullname}"

    }


}