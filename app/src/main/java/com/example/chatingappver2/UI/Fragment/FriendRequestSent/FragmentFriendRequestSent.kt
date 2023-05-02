package com.example.chatingappver2.UI.Fragment.FriendRequestSent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatingappver2.Model.UserProfile
import com.example.chatingappver2.R
import com.example.chatingappver2.UI.Adapter.adapterFriendRequestSent
import com.example.chatingappver2.UI.InterfaceAdapter.FrienRequestSentOnClick
import kotlinx.android.synthetic.main.fragment_friend_request_sent.view.*


class FragmentFriendRequestSent constructor() : Fragment(), FriendRequestSentContract.view,

    FrienRequestSentOnClick {
    private var adapter: adapterFriendRequestSent? = null
    private val presenter by lazy { FriendRequestSentPresenter(this, requireContext()) }
    private var listFriendRequestSent: MutableList<UserProfile> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater,
        viewGroup: ViewGroup?,
        bundle: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_friend_request_sent, viewGroup, false)
        setupRecycleView(view)
        presenter.friendRequestSent()
        return view
    }

    private fun setupRecycleView(viewFragment: View) {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), linearLayoutManager.getOrientation())

        viewFragment.recyClerFriendRequestSent.layoutManager = linearLayoutManager
        viewFragment.recyClerFriendRequestSent.addItemDecoration(dividerItemDecoration)


        adapter = adapterFriendRequestSent(listFriendRequestSent, requireContext(), this)
        viewFragment.recyClerFriendRequestSent.adapter = adapter

    }

    override fun addItemFriendRequest(userProfile: UserProfile) {
        listFriendRequestSent.add(userProfile)
        adapter?.notifyDataSetChanged()
    }

    override fun cancelFriendRequestSucces(idReceive: String) {
        presenter.removeItemOfList(idReceive, listFriendRequestSent)
        Toast.makeText(requireContext(), "Cancel Friend Request", Toast.LENGTH_SHORT).show()
    }

    override fun cancelFriendRequestFail() {
        Toast.makeText(requireContext(), "Cancel Friend Request Fail", Toast.LENGTH_SHORT).show()
    }

    override fun removeitemOfList(i: Int) {
        listFriendRequestSent.removeAt(i)
        adapter?.notifyDataSetChanged()
    }

    override fun cancelFriendRequestOnClickListener(idReceive: String) {
        presenter.cancelFriendRequest(idReceive)
    }
}