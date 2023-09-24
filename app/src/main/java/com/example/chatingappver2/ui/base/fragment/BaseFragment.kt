package com.example.chatingappver2.ui.base.fragment

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.BaseAdapter
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatingappver2.BroadcastReceiver.OnNetworkConnectedListener
import com.example.chatingappver2.database.repository.AccountRepository
import com.example.chatingappver2.database.repository.AuthRepository
import com.example.chatingappver2.database.repository.ProfileRepository
import com.example.chatingappver2.ui.adapter.AdapterNewUser
import com.example.chatingappver2.ui.base.activity.BaseActivity
import com.example.finalprojectchatapplycation.Dialog.ProgressDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_new_contacts.view.recyClerNewContact
import kotlinx.coroutines.Job
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseFragment : Fragment(), OnNetworkConnectedListener {

    @Inject
    lateinit var accountRepository: AccountRepository

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var profileRepository: ProfileRepository



    protected var searchJob: Job? = null
    private var progressDialog: Dialog? = null

    @LayoutRes
    abstract fun getLayoutID(): Int

    abstract fun onViewReady(view: View)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(getLayoutID(), container, false)
        onViewReady(rootView)
        return rootView
    }

    fun isNetworkConnected(): Boolean {
        if (activity is BaseActivity) {
            return (activity as BaseActivity).isNetworkState
        }
        return false
    }

    fun showLoadingProgress() {
        progressDialog?.takeIf { it.isShowing }?.dismiss()

        if (progressDialog==null)
            progressDialog = ProgressDialog(requireContext())

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
        requireActivity().window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }
}