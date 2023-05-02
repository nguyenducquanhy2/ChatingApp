package com.example.chatingappver2.UI.Fragment.UpdateProfile

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.chatingappver2.R
import com.example.finalprojectchatapplycation.Dialog.progressDialog
import kotlinx.android.synthetic.main.fragment_update_profile.*
import kotlinx.android.synthetic.main.fragment_update_profile.view.*

class FragmentUpdateProfile : Fragment(), UpdateProfileContract.view,
    View.OnClickListener {
    var dialog: Dialog? = null
    private var uriImg: Uri? = null
    private val REQUEST_CODE=123

    private val presenter by lazy { UpdateProfilePresenter(this, requireContext()) }
    override fun onCreateView(
        inflater: LayoutInflater,
        viewGroup: ViewGroup?,
        bundle: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_update_profile, viewGroup, false)

        dialog = progressDialog.progressDialog(requireContext())
        registerClickListener(view)
        return view
    }

    private fun registerClickListener(view: View) {

        view.imgInformationUserUpdate.setOnClickListener(this)
        view.btnUpDateProfile.setOnClickListener(this)
        view.btnChooseDateUpdateProfile.setOnClickListener(this)
        view.LayoutUpdateProfileFragment.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.LayoutUpdateProfileFragment -> {
                if (edtFullNameUpdateProfile.isFocused) {
                    hideKeyBoardMethod(requireActivity(), edtFullNameUpdateProfile)
                }
            }
            R.id.btnChooseDateUpdateProfile -> {
                presenter.openDialogDatePicker()
            }
            R.id.btnUpDateProfile -> {
                submitProfile()
            }
            R.id.imgInformationUserUpdate -> {
                chooseImgFormGraly()
            }

        }
    }
    private fun hideKeyBoardMethod(activity: Activity, view:View) {
        val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
        view.clearFocus()
    }



    private fun submitProfile() {
        val fullname=edtFullNameUpdateProfile.text.toString()
        val dateOfBirth=tvDateUpdateProfile.text.toString()
        dialog!!.show()
        presenter.submitProfileToFireBase(uriImg,fullname,dateOfBirth)
    }

    private fun chooseImgFormGraly() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = "android.intent.action.GET_CONTENT"
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == REQUEST_CODE) && (resultCode == -1) && (data != null)) {
            uriImg = data.data
            imgInformationUserUpdate.setImageURI(uriImg )
        }
    }

    override fun submitSuccess() {
        dialog!!.dismiss()
        Toast.makeText(requireContext(), "Update profile success!", Toast.LENGTH_SHORT).show()
    }

    override fun submitFail() {
        dialog!!.dismiss()
        Toast.makeText(requireContext(), "Update profile fail!", Toast.LENGTH_SHORT).show()
    }

     override fun fullNameEmpty() {

        dialog!!.dismiss()
        Toast.makeText(requireContext(), "place full name mustn't empty!", Toast.LENGTH_SHORT).show()
    }

    override fun setTextViewDate(value: String?) {
        tvDateUpdateProfile.text = value
    }

    override fun dateInValid() {
        dialog!!.dismiss()
        Toast.makeText(requireContext(), "Date mustn't empty", Toast.LENGTH_SHORT).show()
    }

}