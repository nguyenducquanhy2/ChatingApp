package com.example.chatingappver2.ui.fragment

import android.view.View
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatingappver2.model.FriendRequest
import com.example.chatingappver2.R
import com.example.chatingappver2.contract.fragment.FriendRequestContract
import com.example.chatingappver2.presenter.fragment.FriendRequestFragmentPresenter
import com.example.chatingappver2.ui.base.fragment.BaseFragment
import com.example.chatingappver2.ui.adapter.AdapterFriendRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_friend_request.btnHideKeyBoard
import kotlinx.android.synthetic.main.fragment_friend_request.searchFriendRequest
import kotlinx.android.synthetic.main.fragment_friend_request.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FriendRequestFragment : BaseFragment(), FriendRequestContract.view {

    private var adapterFrienRequest: AdapterFriendRequest? = null

    private val presenter by lazy { FriendRequestFragmentPresenter(this, accountRepository) }

    override fun getLayoutID(): Int = R.layout.fragment_friend_request

    override fun onViewReady(view: View) {
        initUi(view)
        listFriendRequestOnDataBase()
    }

    override fun setFrienRequest(friendRequest: FriendRequest) {
        adapterFrienRequest?.addItemToListUser(friendRequest)
    }

    override fun removeOnListRequest(idUserSendRequest: String) {
        val position = adapterFrienRequest?.getListShowing()
            ?.indexOfFirst { it.idUserSendRequest == idUserSendRequest }
        position?.let {
            adapterFrienRequest?.doShowListRemoveAt(position)
        }
    }

    override fun cancelFriendRequestSucces(idSender: String) {
        val position =
            adapterFrienRequest?.getListShowing()?.indexOfFirst { it.idUserSendRequest == idSender }
        position?.let {
            removeItemOfListByIndex(it)
        }
    }

    override fun cancelFriendRequestFail() {
        Toast.makeText(requireContext(), "Delete Friend Request Fail", Toast.LENGTH_SHORT).show()
    }

    override fun removeItemOfListByIndex(i: Int) {
        adapterFrienRequest?.doShowListRemoveAt(i)
    }

    override fun onNetworkConnected() {
        Toast.makeText(context, "Network connected", Toast.LENGTH_SHORT).show()
    }

    override fun onNetworkDisconnected() {
        Toast.makeText(context, "Network not connected", Toast.LENGTH_SHORT).show()
    }

    private fun listFriendRequestOnDataBase() {
        if (!isNetworkConnected()){
            onNetworkDisconnected()
            return
        }
        presenter.getListFrienRequestOnDB()
    }

    private fun initUi(view: View) {
        registerEvent(view)
        addAdapterToRecycler(view)
    }

    private fun acceptClickListener(friendRequest: FriendRequest) {
        if (!isNetworkConnected()){
            onNetworkDisconnected()
            return
        }
        presenter.acceptFriendRequest(friendRequest)
    }

    private fun cancelClickListener(idUserSendRequest: String) {
        if (!isNetworkConnected()){
            onNetworkDisconnected()
            return
        }
        presenter.deleteFriendRequest(idUserSendRequest)
    }

    private fun addAdapterToRecycler(view: View) {
        createAdapter()
        val linearLayoutManager = LinearLayoutManager(requireContext())
        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), linearLayoutManager.orientation)

        view.recyClerFriendRequest?.apply {
            layoutManager = linearLayoutManager
            addItemDecoration(dividerItemDecoration)
            adapter = adapterFrienRequest
        }
    }

    private fun createAdapter() {
        adapterFrienRequest = AdapterFriendRequest()

        adapterFrienRequest?.acceptClickListener = {
            acceptClickListener(it)
        }

        adapterFrienRequest?.cancelClickListener = {
            cancelClickListener(it)
        }
    }

    private fun registerEvent(view: View) {
        view.searchFriendRequest.doAfterTextChanged {
            searchJob?.cancel()
            searchJob = CoroutineScope(Dispatchers.Main).launch {
                delay(200)
                // filter by string value
                adapterFrienRequest?.filterData(view.searchFriendRequest.text.toString())
                if (searchFriendRequest.text.toString().isEmpty()) {
                    btnHideKeyBoard.visibility = View.GONE
                } else {
                    btnHideKeyBoard.visibility = View.VISIBLE
                }
            }
        }
    }


}