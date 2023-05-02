package com.example.chatingappver2.UI.Fragment.ChangePassword

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.chatapplication.Ui.Fragment.ChangePassword.ChangePasswordContract
import com.example.chatingappver2.R
import com.example.finalprojectchatapplycation.Dialog.progressDialog
import kotlinx.android.synthetic.main.fragment_change_password.*
import kotlinx.android.synthetic.main.fragment_change_password.view.*


class FragmentChangePassword : Fragment(), ChangePasswordContract.view,
    View.OnClickListener {
    private var dialog: Dialog? = null
    private val presenter by lazy { ChangePasswordPresenter(this) }

    public override fun onCreateView(
        inflater: LayoutInflater,
        viewGroup: ViewGroup?,
        bundle: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_change_password, viewGroup, false)
        val requireContext: Context = requireContext()
        dialog = progressDialog.progressDialog(requireContext)
        registerClickListener(view)

        return view
    }

    private fun registerClickListener(view: View?) {
        view?.btnUpdatePassword?.setOnClickListener(this)
        view?.LayoutChangePassworFragment?.setOnClickListener(this)
    }

     override fun OldPassInvalid() {
        dialog!!.dismiss()
        Toast.makeText(requireContext(), "Old password is invalid.", Toast.LENGTH_SHORT).show()
    }

     override fun NewPassInvalid() {

        dialog!!.dismiss()
        Toast.makeText(requireContext(), "New password is invalid.", Toast.LENGTH_SHORT).show()
    }

     override fun NewPassNotEqual() {

        dialog!!.dismiss()
        Toast.makeText(requireContext(), "Two password isn't contains", Toast.LENGTH_SHORT).show()
    }

     override fun changePassSucces() {

        dialog!!.dismiss()
        Toast.makeText(requireContext(), "Change Password success!", Toast.LENGTH_SHORT).show()
    }

    override fun OldPasswordNotContans() {
        dialog!!.dismiss()
        Toast.makeText(requireContext(),"The password is invalid or the user does not have a password.",Toast.LENGTH_SHORT).show()
    }

    override fun changePassFail() {
        dialog!!.dismiss()
        Toast.makeText(requireContext(), "Change Password Fail!", Toast.LENGTH_SHORT).show()
    }

     override fun onClick(view: View) {
         when(view.id){
             R.id.btnUpdatePassword->{
                 val valueOf: String =edtOldPass.text.toString()
                 val valueOf2: String =edtNewPass.text.toString()
                 val valueOf3: String =edtConfirmNewPass.getText().toString()

                 dialog!!.show()
                 presenter.changePass(valueOf, valueOf2, valueOf3)
             }
             R.id.LayoutChangePassworFragment->{

                 if (edtOldPass.isFocused) {
                     hideSoftKeyboard(requireActivity(), edtOldPass)
                     return
                 }

                 if (edtNewPass.isFocused) {
                     hideSoftKeyboard(requireActivity(), edtNewPass)
                     return
                 }

                 if (edtConfirmNewPass.isFocused) {
                     hideSoftKeyboard(requireActivity(), edtConfirmNewPass)
                     return
                 }

             }
         }

    }


    private fun hideSoftKeyboard(activity: Activity, view:View) {
        val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
        view.clearFocus()
    }

}