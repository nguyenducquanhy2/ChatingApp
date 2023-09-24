package com.example.chatingappver2.presenter.fragment

import android.util.Log
import com.example.chatingappver2.model.UserProfile
import com.example.chatingappver2.contract.fragment.FriendRequestSentContract
import com.example.chatingappver2.contract.repository.AccountRepositoryContract
import com.example.chatingappver2.database.repository.AccountRepository
import com.example.chatingappver2.ui.fragment.FragmentFriendRequestSent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*


class FriendRequestSentPresenter(
    private val view: FragmentFriendRequestSent,
    private val repository: AccountRepository
) :
    FriendRequestSentContract.Presenter, AccountRepositoryContract.ListFriendRequestSent,
    AccountRepositoryContract.RevokeFriendRequestFinishListener {

    private val TAG: String = "FragmentFriendRequestSent"

    override fun friendRequestSent() {
        repository.getListFriendRequestSent(this)
    }

    override fun revokeFriendRequest(idReceive: String) {
        repository.revokeFriendRequest(idReceive, this)
    }


    override fun friendRequestsAdd(userProfile: UserProfile) {
        view.addItemFriendRequest(userProfile)
    }

    override fun friendRequestsError(message: String) {
        Log.e(TAG, "friendRequestsError: $message")
    }

    override fun revokeFriendRequestSuccess(idReceive: String) {
        view.revokeFriendRequestSuccess(idReceive)
    }

    override fun revokeFriendRequestError(message: String) {
        view.revokeFriendRequestFail()
    }

}