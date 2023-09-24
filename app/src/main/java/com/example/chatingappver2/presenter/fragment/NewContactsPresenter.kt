package com.example.chatingappver2.presenter.fragment

import android.util.Log
import com.example.chatingappver2.model.FriendRequest
import com.example.chatingappver2.model.UserProfile
import com.example.chatingappver2.contract.fragment.NewContactsContract
import com.example.chatingappver2.contract.repository.AccountRepositoryContract
import com.example.chatingappver2.database.repository.AccountRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewContactsPresenter(
    private val view: NewContactsContract.view,
    private val repository: AccountRepository
) : NewContactsContract.presenter,
    AccountRepositoryContract.GetUserNotContactFinishListener,
    AccountRepositoryContract.SendFriendRequestFinishListener {
    private val TAG: String = "fragmentNewContactsPresenter"

    override fun getNewUser() {
        CoroutineScope(Dispatchers.Default).launch {
            repository.getUserNotContact(this@NewContactsPresenter)
        }
    }

    override fun sendRequestAddFriend(idUserReceiveRequest: String) {
        repository.sendRequestAddFriend(idUserReceiveRequest,this)
    }

    override fun userNotContactsAdd(userProfile: UserProfile) {
        view.addItemToList(userProfile)
    }

    override fun getError(message: String) {
        Log.e(TAG, "getError: $message")
    }

    override fun SendFriendRequestSuccess(idUserReceiveRequest: String) {
        view.notifySendFriendRequestSuccess(idUserReceiveRequest)
    }

    override fun SendFriendRequestError(message: String) {
        view.notifySendFriendRequestFail()
    }


}