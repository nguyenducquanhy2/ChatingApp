package com.example.chatingappver2.ui.fragment

import android.app.Dialog
import android.view.View
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatingappver2.model.UserProfile
import com.example.chatingappver2.R
import com.example.chatingappver2.contract.fragment.NewContactsContract
import com.example.chatingappver2.presenter.fragment.NewContactsPresenter
import com.example.chatingappver2.ui.base.fragment.BaseFragment
import com.example.chatingappver2.ui.adapter.AdapterNewUser
import com.example.finalprojectchatapplycation.Dialog.ProgressDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_new_contacts.*
import kotlinx.android.synthetic.main.fragment_new_contacts.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentNewContacts : BaseFragment(), NewContactsContract.view {
    private var adapterNewContact: AdapterNewUser? = null


    private val presenter by lazy { NewContactsPresenter(this, accountRepository) }

    override fun getLayoutID(): Int = R.layout.fragment_new_contacts

    override fun onViewReady(view: View) {
        showLoadingProgress()
        initUI(view)
        dismissLoadingProgress()
    }

    override fun notifySendFriendRequestFail() {
        Toast.makeText(requireContext(), "Send friend request fail", Toast.LENGTH_SHORT).show()
    }

    override fun notifySendFriendRequestSuccess(idUserReceiveRequest: String) {
        val position=adapterNewContact?.getListShowing()?.indexOfFirst { it.idUser == idUserReceiveRequest }
        position?.let {
            removeItemOfListByIndex(it)
        }
        Toast.makeText(requireContext(), "Sent friend request!", Toast.LENGTH_SHORT).show()
    }

    override fun addItemToList(userProfile: UserProfile) {
        adapterNewContact?.addItemToListUser(userProfile)
    }

    override fun onNetworkConnected() {
        Toast.makeText(context, "Network connected", Toast.LENGTH_SHORT).show()
    }

    override fun onNetworkDisconnected() {
        Toast.makeText(context, "Network not connected", Toast.LENGTH_SHORT).show()
    }

    private fun removeItemOfListByIndex(position: Int) {
        adapterNewContact?.doShowListRemoveAt(position)
    }

    private fun addFriendOnClickListener(idUserReceiveRequest: String) {
        if (!isNetworkConnected()){
            onNetworkDisconnected()
            return
        }
        presenter.sendRequestAddFriend(idUserReceiveRequest)
    }

    private fun initUI(viewFragment: View) {
        registerEvent(viewFragment)
        addFirstAdapterToRecycle(viewFragment)

        if (!isNetworkConnected()){
            onNetworkDisconnected()
            return
        }

        presenter.getNewUser()
    }

    private fun addFirstAdapterToRecycle(viewFragment: View) {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        val decoration = DividerItemDecoration(requireContext(), linearLayoutManager.orientation)

        adapterNewContact = AdapterNewUser()
        adapterNewContact?.newFriendOnClick = { idUser: String ->
            addFriendOnClickListener(idUser)
        }
        viewFragment.recyClerNewContact.apply {
            layoutManager = linearLayoutManager
            addItemDecoration(decoration)
            adapter = adapterNewContact
        }
    }


    private fun registerEvent(view: View) {
        view.btnHideKeyBoardNewContact.setOnClickListener {
            if (searchNewContact.isFocused) {
                searchNewContact.setText("")
                hideKeyBoardMethod(requireActivity(), searchNewContact)
            }
        }

        view.searchNewContact.doAfterTextChanged {
            searchJob?.cancel()
            searchJob = CoroutineScope(Dispatchers.Main).launch {
                delay(200)
                // filter by string value
                adapterNewContact?.filterData(view.searchNewContact.text.toString())
                if (view.searchNewContact.text.toString().isEmpty()) {
                    btnHideKeyBoardNewContact.visibility = View.GONE
                } else {
                    btnHideKeyBoardNewContact.visibility = View.VISIBLE
                }
            }
        }

    }

}