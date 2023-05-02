package com.example.chatingappver2.UI.Fragment.NewContacts

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatingappver2.Model.UserProfile
import com.example.chatingappver2.R
import com.example.chatingappver2.UI.Adapter.adapterNewUser
import com.example.chatingappver2.UI.InterfaceAdapter.NewFriendOnClick
import com.example.finalprojectchatapplycation.Dialog.progressDialog
import kotlinx.android.synthetic.main.fragment_new_contacts.*
import kotlinx.android.synthetic.main.fragment_new_contacts.view.*

class FragmentNewContacts() : Fragment(), NewContactsContract.view, NewFriendOnClick {
    var adapterNewContact: adapterNewUser? = null
    private var dialog: Dialog? = null
    private val TAG = "FragmentNewContacts"
    private val listUserNotContact: MutableList<UserProfile> = mutableListOf()
    private val presenter by lazy { fragmentNewContactsPresenter(this) }

    override fun onCreateView(
        inflater: LayoutInflater,
        viewGroup: ViewGroup?,
        bundle: Bundle?
    ): View? {
        val viewFragment: View = inflater.inflate(R.layout.fragment_new_contacts, viewGroup, false)

        dialog = progressDialog.progressDialog(requireContext())

        dialog!!.show()
        initUI(viewFragment)
        return viewFragment
    }

    private fun initUI(viewFragment: View) {
        registerEvent(viewFragment)
        presenter.getNewUser()
        addFirstAdapterToRecycle(viewFragment)
        dialog!!.dismiss()
    }

    private fun addFirstAdapterToRecycle(viewFragment: View) {
        val layoutManager = LinearLayoutManager(requireContext())
        val decoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        viewFragment.recyClerNewContact.layoutManager = layoutManager
        viewFragment.recyClerNewContact.addItemDecoration(decoration)

        adapterNewContact = adapterNewUser(listUserNotContact, requireContext(), this)
        viewFragment.recyClerNewContact.adapter = adapterNewContact
    }

    private fun resetAdapterOfRecycle(list: MutableList<UserProfile>) {
        adapterNewContact = adapterNewUser(list, requireContext(), this)
        recyClerNewContact.adapter = adapterNewContact
    }

    private fun registerEvent(view: View) {
        view.btnHideKeyBoardNewContact.setOnClickListener {
            if (searchNewContact.isFocused) {
                hideKeyBoardMethod(requireActivity(), searchNewContact)
            }
        }

        view.searchNewContact.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, p1: Int, p2: Int, p3: Int) {
                presenter.fillterData(listUserNotContact, charSequence.toString())
            }

            override fun afterTextChanged(editable: Editable?) {
                if (editable.toString().isEmpty()) {
                    btnHideKeyBoardNewContact.visibility = View.GONE
                } else {
                    btnHideKeyBoardNewContact.visibility = View.VISIBLE
                }
            }
        })

    }

    private fun hideKeyBoardMethod(activity: Activity, view: View) {
        val inputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
        view.clearFocus()
    }

    override fun notifySendFriendRequestFail() {
        Toast.makeText(requireContext(), "Send friend request fail", Toast.LENGTH_SHORT).show()
    }

    override fun notifySendFriendRequestSuccess(idUserReceiveRequest: String) {
        presenter.findIndexRemove(idUserReceiveRequest, listUserNotContact)
        Toast.makeText(requireContext(), "Sent friend request!", Toast.LENGTH_SHORT).show()
    }

    override fun addItemToList(userProfile: UserProfile) {
        listUserNotContact.add(userProfile)
        adapterNewContact?.notifyDataSetChanged()
    }

    override fun setOldUserProfileList() {
        resetAdapterOfRecycle(listUserNotContact)
    }

    override fun setNewUserProfileList(newListUser: MutableList<UserProfile>) {
        resetAdapterOfRecycle(newListUser)
    }

    override fun removeItemOfListByIndex(i: Int) {
        listUserNotContact.removeAt(i)
        adapterNewContact?.notifyDataSetChanged()
    }


    override fun addFriendOnClickListener(idUserReceiveRequest: String) {
        presenter.sendRequestAddFriend(idUserReceiveRequest)
    }
}