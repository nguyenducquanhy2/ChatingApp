package com.example.chatingappver2.ui.fragment

import android.app.Dialog
import android.view.View
import android.widget.Toast
import com.example.chatingappver2.contract.fragment.ChangePasswordContract
import com.example.chatingappver2.R
import com.example.chatingappver2.presenter.fragment.ChangePasswordPresenter
import com.example.chatingappver2.ui.base.fragment.BaseFragment
import com.example.finalprojectchatapplycation.Dialog.ProgressDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_change_password.*
import kotlinx.android.synthetic.main.fragment_change_password.view.*

@AndroidEntryPoint
class FragmentChangePassword : BaseFragment(),
    ChangePasswordContract.View,
    View.OnClickListener {


    private val presenter by lazy { ChangePasswordPresenter(this, authRepository) }

    override fun getLayoutID(): Int = R.layout.fragment_change_password

    override fun onViewReady(view: View) {
        showLoadingProgress()
        registerClickListener(view)
    }

    override fun oldPassInvalid() {
        dismissLoadingProgress()
        Toast.makeText(requireContext(), "Old password is invalid.", Toast.LENGTH_SHORT).show()
    }

    override fun newPassInvalid() {

        dismissLoadingProgress()
        Toast.makeText(requireContext(), "New password is invalid.", Toast.LENGTH_SHORT).show()
    }

    override fun newPassNotEqual() {

        dismissLoadingProgress()
        Toast.makeText(requireContext(), "Two password isn't contains", Toast.LENGTH_SHORT).show()
    }

    override fun changePassSucces() {

        dismissLoadingProgress()
        Toast.makeText(requireContext(), "Change Password success!", Toast.LENGTH_SHORT).show()
    }

    override fun oldPasswordNotContans() {
        dismissLoadingProgress()
        Toast.makeText(
            requireContext(),
            "The password is invalid or the user does not have a password.",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun changePassFail(message: String) {
        dismissLoadingProgress()
        Toast.makeText(requireContext(), "Change Password Fail!", Toast.LENGTH_SHORT).show()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnUpdatePassword -> {
                val oldPass: String = edtOldPass.text.toString()
                val newPass: String = edtNewPass.text.toString()
                val confirmPass: String = edtConfirmNewPass.getText().toString()

                if (!isNetworkConnected()){
                    onNetworkDisconnected()
                    return
                }

                showLoadingProgress()
                presenter.changePass(oldPass, newPass, confirmPass)
            }

            R.id.LayoutChangePassworFragment -> {

                if (edtOldPass.isFocused) {
                    hideKeyBoardMethod(requireActivity(), edtOldPass)
                    return
                }

                if (edtNewPass.isFocused) {
                    hideKeyBoardMethod(requireActivity(), edtNewPass)
                    return
                }

                if (edtConfirmNewPass.isFocused) {
                    hideKeyBoardMethod(requireActivity(), edtConfirmNewPass)
                    return
                }

            }
        }
    }

    override fun onNetworkConnected() {
        Toast.makeText(context, "Network connected", Toast.LENGTH_SHORT).show()
    }

    override fun onNetworkDisconnected() {
        Toast.makeText(context, "Network not connected", Toast.LENGTH_SHORT).show()
    }

    private fun registerClickListener(view: View?) {
        view?.btnUpdatePassword?.setOnClickListener(this)
        view?.LayoutChangePassworFragment?.setOnClickListener(this)
    }


}