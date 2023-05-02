package com.example.chatingappver2.UI.Fragment.FriendRequest

import FriendRequestOnClick
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatingappver2.Model.FriendRequest
import com.example.chatingappver2.R
import com.example.chatingappver2.UI.Adapter.adapterFriendRequest
import kotlinx.android.synthetic.main.fragment_friend_request.view.*


class FriendRequestFragment constructor() : Fragment(), FriendRequestContract.view,
    FriendRequestOnClick {

    private var adapterFrienRequest: adapterFriendRequest? = null
    private val presenter by lazy { FriendRequestFragmentPresenter(this, requireContext()) }
    private val listFriendRequest: MutableList<FriendRequest> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        viewGroup: ViewGroup?,
        bundle: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_friend_request, viewGroup, false)
        initUi(view)
        listFriendRequestOnDataBase()
        return view
    }

    private fun listFriendRequestOnDataBase() {
        presenter.getListFrienRequestOnDB()
    }

    private fun initUi(view: View) {
        val linearLayoutManager: LinearLayoutManager = LinearLayoutManager(requireContext())
        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), linearLayoutManager.orientation)
        view?.recyClerFriendRequest?.layoutManager = linearLayoutManager
        view?.recyClerFriendRequest?.addItemDecoration(dividerItemDecoration)
        adapterFrienRequest = adapterFriendRequest(listFriendRequest, requireContext(), this)
        view?.recyClerFriendRequest?.adapter = adapterFrienRequest

    }

    override fun setFrienRequest(friendRequest: FriendRequest) {
        listFriendRequest.add(friendRequest)
        var adapterfriendrequest: adapterFriendRequest? = adapterFrienRequest
        if (adapterfriendrequest == null) {
            adapterfriendrequest = null
        }
        adapterfriendrequest?.notifyDataSetChanged()
    }

    override fun removeOnListRequest( i: Int) {
        this.listFriendRequest.removeAt(i)
        adapterFrienRequest?.notifyDataSetChanged()
    }

    override fun cancelFriendRequestSucces(idSender: String) {
        presenter.findIndexNeedRemoveInList(idSender, listFriendRequest)
        Toast.makeText(requireContext(), "Delete Friend Request", Toast.LENGTH_SHORT).show()
    }

    override fun cancelFriendRequestFail() {
        Toast.makeText(requireContext(), "Delete Friend Request Fail", Toast.LENGTH_SHORT).show()
    }

    override fun removeItemOfListByIndex(i: Int) {
        listFriendRequest.removeAt(i)
        adapterFrienRequest?.notifyDataSetChanged()
    }

    override fun btnAcceptClickListener(friendRequest: FriendRequest) {
        presenter.acceptFriendRequest(friendRequest)
    }


    override fun btnDeleteClickListener(idUserSendRequest: String) {
        presenter.deleteFriendRequest(idUserSendRequest)
    }


}