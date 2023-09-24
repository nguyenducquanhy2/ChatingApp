package com.example.chatingappver2.presenter.fragment

import android.util.Log
import com.example.chatingappver2.model.FriendRequest
import com.example.chatingappver2.model.UserProfile
import com.example.chatingappver2.contract.fragment.FriendRequestContract
import com.example.chatingappver2.contract.repository.AccountRepositoryContract
import com.example.chatingappver2.database.repository.AccountRepository
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class FriendRequestFragmentPresenter(
    private val view: FriendRequestContract.view,
    private val repository: AccountRepository
) : FriendRequestContract.presenter, AccountRepositoryContract.GetListFriendRequest,
    AccountRepositoryContract.CancelRequestFinishListener {
    val TAG: String = "FriendRequestFragmentPresenter"

    override fun acceptFriendRequest(friendRequest: FriendRequest) {
        repository.acceptFriendRequest(friendRequest)
    }

    override fun deleteFriendRequest(idSender: String) {
        repository.deleteFriendRequest(idSender, this)
    }

    override fun getListFrienRequestOnDB() {
        repository.getListFriendRequest(this)
    }

    override fun friendRequestsAdd(friendRequest: FriendRequest) {
        view.setFrienRequest(friendRequest)
    }

    override fun friendRequestsRemove(idUserSendRequest: String) {
        view.removeOnListRequest(idUserSendRequest)
    }

    override fun friendRequestsError(message: String) {
        Log.e(TAG, "friendRequestsError: $message")
    }

    override fun cancelRequestSuccess(idSender: String) {
        view.cancelFriendRequestSucces(idSender)
    }

    override fun cancelRequestError(message: String) {
        view.cancelFriendRequestFail()
    }


}