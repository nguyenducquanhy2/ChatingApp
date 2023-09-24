package com.example.chatingappver2.ui.fragment

import android.view.View
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatingappver2.model.UserProfile
import com.example.chatingappver2.R
import com.example.chatingappver2.contract.fragment.FriendRequestSentContract
import com.example.chatingappver2.presenter.fragment.FriendRequestSentPresenter
import com.example.chatingappver2.ui.base.fragment.BaseFragment
import com.example.chatingappver2.ui.adapter.AdapterFriendRequestSent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_friend_request_sent.btnHideKeyBoard
import kotlinx.android.synthetic.main.fragment_friend_request_sent.edtSearch
import kotlinx.android.synthetic.main.fragment_friend_request_sent.view.edtSearch
import kotlinx.android.synthetic.main.fragment_friend_request_sent.view.rclvContact
import kotlinx.android.synthetic.main.fragment_friend_request_sent.view.refreshLayout
import kotlinx.android.synthetic.main.fragment_home.refreshLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentFriendRequestSent : BaseFragment(),
    FriendRequestSentContract.View {

    private var adapterFriendRequestSent: AdapterFriendRequestSent? = null
    private val presenter by lazy { FriendRequestSentPresenter(this, accountRepository) }

    override fun getLayoutID(): Int = R.layout.fragment_friend_request_sent

    override fun onViewReady(view: View) {
        setupRecycleView(view)
        registerEvent(view)
        presenter.friendRequestSent()
    }

    override fun addItemFriendRequest(userProfile: UserProfile) {
        adapterFriendRequestSent?.addItemToListUser(userProfile)
    }

    override fun revokeFriendRequestSuccess(idReceive: String) {
        val position = adapterFriendRequestSent?.getListShowing()?.indexOfFirst { it.idUser == idReceive }
        if (position != null) {
            removeItemOfList(position)
        }
        Toast.makeText(requireContext(), "Cancel Friend Request", Toast.LENGTH_SHORT).show()
    }

    override fun revokeFriendRequestFail() {
        Toast.makeText(requireContext(), "Cancel Friend Request Fail", Toast.LENGTH_SHORT).show()
    }

    override fun onNetworkConnected() {
        Toast.makeText(context, "Network connected", Toast.LENGTH_SHORT).show()
    }

    override fun onNetworkDisconnected() {
        Toast.makeText(context, "Network not connected", Toast.LENGTH_SHORT).show()
    }

    private fun registerEvent(view: View) {

        view.refreshLayout.setOnRefreshListener {
            adapterFriendRequestSent?.refreshListUser()
            presenter.friendRequestSent()
            refreshLayout.isRefreshing = false
        }

        view.edtSearch.doAfterTextChanged {
            searchJob?.cancel()
            searchJob = CoroutineScope(Dispatchers.Main).launch {
                delay(200)
                // filter by string value
                adapterFriendRequestSent?.filterData(edtSearch.text.toString())
                if (edtSearch.toString().isEmpty()) {
                    btnHideKeyBoard.visibility = View.INVISIBLE
                } else {
                    btnHideKeyBoard.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupRecycleView(view: View) {
        createAdapter()
        val linearLayoutManager = LinearLayoutManager(requireContext())
        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), linearLayoutManager.getOrientation())

        view.rclvContact.apply {
            layoutManager = linearLayoutManager
            addItemDecoration(dividerItemDecoration)
            adapter = adapterFriendRequestSent
        }
    }

    private fun createAdapter() {
        adapterFriendRequestSent = AdapterFriendRequestSent()

        adapterFriendRequestSent?.revokeFriendRequest = {
            revokeFriendRequest(it)
        }
    }

    private fun removeItemOfList(position: Int) {
        adapterFriendRequestSent?.doShowListRemoveAt(position)
    }

    private fun revokeFriendRequest(idReceive: String) {
        presenter.revokeFriendRequest(idReceive)
    }
}