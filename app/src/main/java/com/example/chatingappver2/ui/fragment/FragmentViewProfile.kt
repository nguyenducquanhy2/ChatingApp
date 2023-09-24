package com.example.chatingappver2.ui.fragment

import android.app.Dialog
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.chatingappver2.model.UserProfile
import com.example.chatingappver2.R
import com.example.chatingappver2.contract.fragment.FragViewProfileContract
import com.example.chatingappver2.presenter.fragment.FragViewProfilePresenter
import com.example.chatingappver2.ui.base.fragment.BaseFragment
import com.example.chatingappver2.ui.activity.ShowImgFullScreenActivity
import com.example.finalprojectchatapplycation.Dialog.ProgressDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_view_profile.*
import kotlinx.android.synthetic.main.fragment_view_profile.view.*
@AndroidEntryPoint
class FragmentViewProfile : BaseFragment(), FragViewProfileContract.View {

    private val TAG: String="FragmentViewProfile"
    private var urlImg: String? = null

    private val presenter: FragViewProfilePresenter by lazy { FragViewProfilePresenter(this,profileRepository) }

    override fun getLayoutID(): Int =R.layout.fragment_view_profile

    override fun onViewReady(view: View) {
        showLoadingProgress()

        information()
        registerEventCliclListener(view)

        dismissLoadingProgress()
    }

    override fun onNetworkConnected() {
        Toast.makeText(context, "Network connected", Toast.LENGTH_SHORT).show()
    }

    override fun onNetworkDisconnected() {
        Toast.makeText(context, "Network not connected", Toast.LENGTH_SHORT).show()
    }

    private fun registerEventCliclListener(view: View) {
        view.imtAvataProfile.setOnClickListener {
            val intent = Intent(requireContext(), ShowImgFullScreenActivity::class.java)
            intent.putExtra("urlImg", urlImg)
            requireContext().startActivity(intent)
        }
    }

    private fun information() {
        presenter.getInformation()
    }

    override fun setInformation(userProfile: UserProfile) {
        dismissLoadingProgress()
        urlImg = userProfile.urlImgProfile
        Glide.with(requireContext()).asBitmap().load(userProfile.urlImgProfile)
            .into(imtAvataProfile)
        InforFullName.text = userProfile.fullname
        InforEmail.text = userProfile.email
        InforBirthDay.text = userProfile.dateOfBirth
    }

    override fun getInforError(message: String) {
        dismissLoadingProgress()
        Log.e(TAG, "getInforError: " )
    }


}