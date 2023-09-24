package com.example.chatingappver2.ui.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.chatingappver2.R
import com.example.chatingappver2.contract.fragment.UpdateProfileContract
import com.example.chatingappver2.model.UserProfile
import com.example.chatingappver2.presenter.fragment.UpdateProfilePresenter
import com.example.chatingappver2.ui.activity.HomeActivity
import com.example.chatingappver2.ui.base.fragment.BaseFragment
import com.example.finalprojectchatapplycation.Dialog.ProgressDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.nav_view
import kotlinx.android.synthetic.main.fragment_update_profile.*
import kotlinx.android.synthetic.main.fragment_update_profile.view.*
import kotlinx.android.synthetic.main.nav_header_home.view.BirthDayHeader
import kotlinx.android.synthetic.main.nav_header_home.view.fullnameHeader
import kotlinx.android.synthetic.main.nav_header_home.view.imageViewHeader
import java.text.SimpleDateFormat
import java.util.Calendar

@AndroidEntryPoint
class FragmentUpdateProfile : BaseFragment(),
    UpdateProfileContract.View,
    View.OnClickListener {

    private var uriImg: Uri? = null
    private val REQUEST_CODE = 123
    private val presenter by lazy {
        UpdateProfilePresenter(
            this,
            requireContext(),
            profileRepository
        )
    }

    override fun getLayoutID(): Int = R.layout.fragment_update_profile

    override fun onViewReady(view: View) {
        registerClickListener(view)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.LayoutUpdateProfileFragment -> {
                if (edtFullNameUpdateProfile.isFocused) {
                    hideKeyBoardMethod(requireActivity(), edtFullNameUpdateProfile)
                }
            }

            R.id.btnChooseDateUpdateProfile -> {
                openDialogDatePicker()
            }

            R.id.btnUpDateProfile -> {
                submitProfile()
            }

            R.id.imgInformationUserUpdate -> {
                chooseImgFormDevice()
            }

        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == REQUEST_CODE) && (resultCode == -1) && (data != null)) {
            uriImg = data.data
            imgInformationUserUpdate.setImageURI(uriImg)
        }
    }

    override fun submitSuccess() {
        dismissLoadingProgress()

        if (!isNetworkConnected()){
            onNetworkDisconnected()
            return
        }

        presenter.loadHeaderProfile()
        Toast.makeText(requireContext(), "Update profile success!", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("SetTextI18n")
    override fun setHeaderProfile(userProfile: UserProfile) {
        val homeActivity = activity as HomeActivity
        val viewHeader = homeActivity.nav_view?.getHeaderView(0)

        viewHeader?.let {
            Glide.with(requireActivity()).asBitmap().load(userProfile.urlImgProfile)
                .into(it.imageViewHeader)
            it.BirthDayHeader.text = "Birth Day: ${userProfile.dateOfBirth}"
            it.fullnameHeader.text = "Full name: ${userProfile.fullname}"
        }
    }

    override fun submitFail() {
        dismissLoadingProgress()
        Toast.makeText(requireContext(), "Update profile fail!", Toast.LENGTH_SHORT).show()
    }

    override fun fullNameEmpty() {
        dismissLoadingProgress()
        Toast.makeText(requireContext(), "place full name mustn't empty!", Toast.LENGTH_SHORT)
            .show()
    }

    override fun dateInValid() {
        dismissLoadingProgress()
        Toast.makeText(requireContext(), "Date mustn't empty", Toast.LENGTH_SHORT).show()
    }

    override fun onNetworkConnected() {
        Toast.makeText(context, "Network connected", Toast.LENGTH_SHORT).show()
    }

    override fun onNetworkDisconnected() {
        Toast.makeText(context, "Network not connected", Toast.LENGTH_SHORT).show()
    }

    private fun registerClickListener(view: View) {

        view.imgInformationUserUpdate.setOnClickListener(this)
        view.btnUpDateProfile.setOnClickListener(this)
        view.btnChooseDateUpdateProfile.setOnClickListener(this)
        view.LayoutUpdateProfileFragment.setOnClickListener(this)
    }

    private fun submitProfile() {
        val fullname = edtFullNameUpdateProfile.text.toString()
        val dateOfBirth = tvDateUpdateProfile.text.toString()

        if (!isNetworkConnected()){
            onNetworkDisconnected()
            return
        }
        showLoadingProgress()
        presenter.submitProfileToFireBase(uriImg, fullname, dateOfBirth)
    }

    private fun chooseImgFormDevice() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = "android.intent.action.GET_CONTENT"
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE)
    }

    @SuppressLint("SimpleDateFormat")
    private fun openDialogDatePicker() {
        val calendar: Calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(), { p0, year, month, date ->
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
                calendar.set(year, month, date)
                val format: String = simpleDateFormat.format(calendar.time)
                tvDateUpdateProfile.text = format
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DATE)
        ).show()
    }

}