package com.example.chatingappver2.ui.fragment

import android.app.Dialog
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatingappver2.model.CurrentContacts
import com.example.chatingappver2.R
import com.example.chatingappver2.contract.fragment.HomeContract
import com.example.chatingappver2.presenter.fragment.HomeFragmentPresenter
import com.example.chatingappver2.ui.base.fragment.BaseFragment
import com.example.chatingappver2.ui.activity.ChatActivity
import com.example.chatingappver2.ui.adapter.AdapterCurrentUser
import com.example.finalprojectchatapplycation.Dialog.ProgressDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.refreshLayout
import kotlinx.android.synthetic.main.fragment_home.searchCurrentContact
import kotlinx.android.synthetic.main.fragment_home.view.btnHideKeyBoard
import kotlinx.android.synthetic.main.fragment_home.view.recyClerCurrentContact
import kotlinx.android.synthetic.main.fragment_home.view.refreshLayout
import kotlinx.android.synthetic.main.fragment_home.view.searchCurrentContact
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentHome : BaseFragment(), HomeContract.View {

    private val TAG: String = "FragmentHome"
    private var adapterCurrentContact: AdapterCurrentUser? = null

    private lateinit var viewFragment: View

    private val presenter: HomeFragmentPresenter by lazy {
        HomeFragmentPresenter(
            this,
            accountRepository
        )
    }

    override fun getLayoutID(): Int = R.layout.fragment_home

    override fun onViewReady(view: View) {
        viewFragment = view
        hideNavBar()
        registerEvent()
        initAdapterForRecycle()

    }

    override fun currentContactsAdd(currentContacts: CurrentContacts) {
        adapterCurrentContact?.addItemToListUser(currentContacts)
    }

    override fun currentContactsChange(currentContacts: CurrentContacts) {
        adapterCurrentContact?.addItemToListUser(currentContacts)
    }

    override fun getCurrentError(message: String) {
        Log.e(TAG, "onCancelled: $message")
    }

    override fun onNetworkConnected() {
        Toast.makeText(context, "Network connected", Toast.LENGTH_SHORT).show()
    }

    override fun onNetworkDisconnected() {
        Toast.makeText(context, "Network not connected", Toast.LENGTH_SHORT).show()
    }

    private fun initRecycler() {
        if (!isNetworkConnected()){
            onNetworkDisconnected()
            return
        }
        presenter.getCurrentContact()
    }

    private fun initAdapterForRecycle() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), linearLayoutManager.orientation)

        adapterCurrentContact = AdapterCurrentUser()
        adapterCurrentContact?.showOldList()
        adapterCurrentContact?.clickItemCurrentUser = { idUserReceive ->

                changeToChatActivity(idUserReceive)


        }

        viewFragment.recyClerCurrentContact.apply {
            addItemDecoration(dividerItemDecoration)
            layoutManager = linearLayoutManager
            adapter = adapterCurrentContact
        }
        initRecycler()

    }

    private fun registerEvent() {
        viewFragment.refreshLayout.setOnRefreshListener {
            refreshRecyclerView()
            refreshLayout.isRefreshing = false
        }

        viewFragment.searchCurrentContact.doAfterTextChanged {
            searchJob?.cancel()
            searchJob = CoroutineScope(Dispatchers.Main).launch {
                delay(200)
                // filter by string value
                adapterCurrentContact?.filterData(viewFragment.searchCurrentContact.text.toString())
                if (viewFragment.searchCurrentContact.toString().isEmpty()) {
                    viewFragment.btnHideKeyBoard.visibility = View.INVISIBLE
                } else {
                    viewFragment.btnHideKeyBoard.visibility = View.VISIBLE
                }
            }
        }

        viewFragment.btnHideKeyBoard.setOnClickListener {
            if (searchCurrentContact.isFocused) {
                searchCurrentContact.setText("")
                hideKeyBoardMethod(requireActivity(), searchCurrentContact)
            }
        }

    }

    private fun refreshRecyclerView() {
        adapterCurrentContact?.refreshListUser()
        initRecycler()
    }

    private fun changeToChatActivity(idUserReceive: String) {
        val intent = Intent(context, ChatActivity::class.java)
        intent.putExtra("idUserReceive", idUserReceive)
        context?.startActivity(intent)
        activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }


}