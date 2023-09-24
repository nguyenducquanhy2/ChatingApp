package com.example.chatingappver2.ui.base.activity

import android.app.Activity
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.example.chatingappver2.BroadcastReceiver.OnNetworkConnectedListener
import com.example.chatingappver2.database.repository.AccountRepository
import com.example.chatingappver2.database.repository.AuthRepository
import com.example.chatingappver2.database.repository.MessageRepository
import com.example.chatingappver2.database.repository.ProfileRepository
import com.example.finalprojectchatapplycation.Dialog.ProgressDialog
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity(),OnNetworkConnectedListener {

    var LogBug: String="Base: "+this::class.java.simpleName

    @Inject
    lateinit var accountRepository: AccountRepository

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var profileRepository: ProfileRepository

    @Inject
    lateinit var messageRepository: MessageRepository

    private var progressDialog: Dialog? = null

    var isNetworkState: Boolean = false
    private var onNetworkConnectedListener: OnNetworkConnectedListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(LogBug, "onCreate: ")
        super.onCreate(savedInstanceState)
        isNetworkState = checkNetworkConnection()
        registerNetworkChangeReceiver()
        setContentView(getLayoutID())
        onCreateActivity()
    }

    override fun onStart() {
        Log.d(LogBug, "onStart: " )
        super.onStart()
    }

    override fun onResume() {
        Log.d(LogBug, "onResume: ")
        super.onResume()
    }

    override fun onPause() {
        Log.d(LogBug, "onPause: ")
        super.onPause()
        progressDialog = null
    }

    override fun onStop() {
        Log.d(LogBug, "onStop: ")
        super.onStop()
    }

    override fun onRestart() {
        Log.d(LogBug, "onRestart: ")
        super.onRestart()
    }

    override fun onDestroy() {
        Log.d(LogBug, "onDestroy: ")
        super.onDestroy()
        unregisterReceiver(networkChangeReceiver)
    }

    @LayoutRes
    abstract fun getLayoutID(): Int

    abstract fun onCreateActivity()


    fun setOnNetworkConnectedListener(listener: OnNetworkConnectedListener) {
        this.onNetworkConnectedListener = listener
    }

    fun showLoadingProgress() {
        progressDialog?.takeIf { it.isShowing }?.dismiss()

        if (progressDialog==null)
            progressDialog = ProgressDialog(this)

        progressDialog?.show()
    }

    fun dismissLoadingProgress() {
        progressDialog?.takeIf { it.isShowing }?.dismiss()
    }

    fun hideKeyBoardMethod(activity: Activity, view: View) {
        val inputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
        view.clearFocus()
    }

    fun hideNavBar() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }


    private fun registerNetworkChangeReceiver() {
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeReceiver, filter)
    }

    private val networkChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            isNetworkState = checkNetworkConnection()
            onNetworkConnectedListener?.run {
                if (isNetworkState) onNetworkConnected()
                else onNetworkDisconnected()
            }
        }
    }

    private fun checkNetworkConnection(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo?.isConnected == true
    }
}

