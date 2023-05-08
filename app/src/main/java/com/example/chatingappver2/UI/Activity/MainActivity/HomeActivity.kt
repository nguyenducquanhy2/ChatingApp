package com.example.chatingappver2.UI.Activity.MainActivity


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.bumptech.glide.Glide
import com.example.chatingappver2.Model.UserProfile
import com.example.chatingappver2.R
import com.example.chatingappver2.UI.Activity.CallingActivity
import com.example.chatingappver2.UI.Activity.VideoCallActivity
import com.example.chatingappver2.UI.Dialog.LogoutDialog
import com.example.chatingappver2.UI.Fragment.ChangePassword.FragmentChangePassword
import com.example.chatingappver2.UI.Fragment.FriendRequest.FriendRequestFragment
import com.example.chatingappver2.UI.Fragment.FriendRequestSent.FragmentFriendRequestSent
import com.example.chatingappver2.UI.Fragment.HomeFragment.FragmentHome
import com.example.chatingappver2.UI.Fragment.NewContacts.FragmentNewContacts
import com.example.chatingappver2.UI.Fragment.UpdateProfile.FragmentUpdateProfile
import com.example.chatingappver2.UI.Fragment.ViewProfile.FragmentViewProfile
import com.example.finalprojectchatapplycation.Dialog.progressDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.stringee.StringeeClient
import com.stringee.call.StringeeCall
import com.stringee.call.StringeeCall2
import com.stringee.exception.StringeeError
import com.stringee.listener.StatusListener
import com.stringee.listener.StringeeConnectionListener
import kotlinx.android.synthetic.main.activity_home.drawer_layout
import kotlinx.android.synthetic.main.activity_home.nav_view
import kotlinx.android.synthetic.main.layout_logout.btnAllowLogout
import kotlinx.android.synthetic.main.layout_logout.btnCancelLogout
import kotlinx.android.synthetic.main.nav_header_home.view.BirthDayHeader
import kotlinx.android.synthetic.main.nav_header_home.view.fullnameHeader
import kotlinx.android.synthetic.main.nav_header_home.view.imageViewHeader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.Date


class HomeActivity : AppCompatActivity(), MainActivityContract.view {
    private val KEY_SID:String="SK.0.y6Tonp4PIM2kpxgeWtaq2osSexgmaPBN"
    private val KEY_SECRET:String="VmVKSGx0N1pwSHBoaHFneW9UaXZES00yRTNaWE1xcw=="

    private var TOKEN:String? = null

    companion object {
        @SuppressLint("StaticFieldLeak")
        var client: StringeeClient? = null
        val voiceCallMap:MutableMap<String, StringeeCall> = mutableMapOf()
        val videoCallMap:MutableMap<String,StringeeCall2> = mutableMapOf()
    }

    private val currentUser = FirebaseAuth.getInstance().currentUser!!
    private val TAG: String = "HomeActivity"
    val presenter by lazy { MainActivityPresenter(this, this) }
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        dialog = progressDialog.progressDialog(this)
        presenter.loadHeaderProfile()
        setDefaulFragment()
        setToggleToActionBar()
        registerItemClickNavView()
        hideNavBar()
        connectStringee()
    }

    private fun connectStringee() {
        initStringeeClient()
        val userId=currentUser.uid
        if (client!!.isConnected){
            client?.disconnect()

        }
        //TOKEN="eyJjdHkiOiJzdHJpbmdlZS1hcGk7dj0xIiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiJTSy4wLnk2VG9ucDRQSU0ya3B4Z2VXdGFxMm9zU2V4Z21hUEJOLTE2ODMzNzI5NjgiLCJpc3MiOiJTSy4wLnk2VG9ucDRQSU0ya3B4Z2VXdGFxMm9zU2V4Z21hUEJOIiwiZXhwIjoxNjgzNDU5MzY4LCJ1c2VySWQiOiJ0ZXN0In0.w9aVABQBS6l2nIV_8n7dArZvCYWBhLdJ5grB_nrYpN0"
        TOKEN=genAccessToken(KEY_SID,KEY_SECRET,3600,userId)
        client!!.registerPushToken(TOKEN,object : StatusListener(){
            override fun onSuccess() {

            }
        })

        client?.connect(TOKEN)
    }

    private fun initStringeeClient() {
        client= StringeeClient(this)
        client!!.setConnectionListener(object : StringeeConnectionListener {
            @SuppressLint("SetTextI18n")
            override fun onConnectionConnected(p0: StringeeClient?, isReconnecting: Boolean) {
                CoroutineScope(Dispatchers.Main).launch {
                    if (p0 != null) {
                        Log.d(TAG, "connected : ${p0.userId}" )
                    }
                }

            }

            override fun onConnectionDisconnected(p0: StringeeClient?, p1: Boolean) {
                CoroutineScope(Dispatchers.Main).launch {
                    if (p0 != null) {
                        Log.d(TAG, "disconnection: ${p0.userId}" )
                    }
                }

            }

            override fun onIncomingCall(p0: StringeeCall?) {
                CoroutineScope(Dispatchers.Main).launch {
                    if (p0 != null) {
                        voiceCallMap.put(p0.callId, p0)
                        if (client!!.isConnected){
                            val intent= Intent(this@HomeActivity, CallingActivity::class.java)
                            intent.putExtra("isComingCall",true)
                            intent.putExtra("callId",p0.callId)

                            startActivity(intent)
                        }
                    }
                }

            }

            override fun onIncomingCall2(p0: StringeeCall2?) {
                CoroutineScope(Dispatchers.Main).launch {
                    if (p0 != null) {
                        videoCallMap.put(p0.callId, p0)
                        if (client!!.isConnected){
                            val intent=Intent(this@HomeActivity, VideoCallActivity::class.java)
                            intent.putExtra("isComingCall",true)
                            intent.putExtra("callId",p0.callId)
                            startActivity(intent)
                        }
                    }
                }
            }

            override fun onConnectionError(p0: StringeeClient?, p1: StringeeError?) {
                CoroutineScope(Dispatchers.Main).launch {
                    if (p0 != null) {
                        Log.e(TAG, "connect error: ${p1}" )
                    }
                }

            }

            override fun onRequestNewToken(p0: StringeeClient?) {
                if (p0 != null) {
                    Log.d(TAG, "onRequestNewToken: ${p0.userId}")
                }
            }

            override fun onCustomMessage(p0: String?, p1: JSONObject?) {

            }

            override fun onTopicMessage(p0: String?, p1: JSONObject?) {

            }
        })

    }

    fun genAccessToken(keySid: String, keySecret: String, expireInSecond: Int,userId:String): String? {
        try {
            val algorithmHS: Algorithm =
                Algorithm.HMAC256(keySecret)
            val headerClaims: MutableMap<String, Any> =
                HashMap()
            headerClaims["typ"] = "JWT"
            headerClaims["alg"] = "HS256"
            headerClaims["cty"] = "stringee-api;v=1"
            val exp = System.currentTimeMillis() + (expireInSecond * 2)
            return JWT.create().withHeader(headerClaims)
                .withClaim("jti", keySid + "-" + System.currentTimeMillis())
                .withClaim("iss", keySid)
                .withExpiresAt(Date(exp))
                .withClaim("userId", userId)
                .sign(algorithmHS)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
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
        val FragmentHome = FragmentHome()
        replateFragment(FragmentHome)
    }

    private fun registerItemClickNavView() {
        nav_view.getHeaderView(0).imageViewHeader.setOnClickListener {
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
                    val FragmentUpdateProfile = FragmentUpdateProfile()
                    replateFragment(FragmentUpdateProfile)
                    drawer_layout.close()
                }

                R.id.nav_view_Home -> {
                    val FragmentHome = FragmentHome()
                    replateFragment(FragmentHome)
                    drawer_layout.close()
                }

                R.id.nav_change_password -> {
                    val FragmentChangePassword = FragmentChangePassword()
                    replateFragment(FragmentChangePassword)
                    drawer_layout.close()
                }

                R.id.nav_newContact -> {
                    val FragmentNewContact = FragmentNewContacts()
                    replateFragment(FragmentNewContact)
                    drawer_layout.close()
                }

                R.id.nav_Friend_Request -> {
                    val FriendRequestFragment = FriendRequestFragment()
                    replateFragment(FriendRequestFragment)
                    drawer_layout.close()
                }

                R.id.nav_Friend_Request_Sent -> {
                    val FragmentFriendRequestSent = FragmentFriendRequestSent()
                    replateFragment(FragmentFriendRequestSent)
                    drawer_layout.close()
                }
            }

            return@setNavigationItemSelectedListener true
        }


    }

    override fun onBackPressed() {

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
        dialog.dismiss()
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