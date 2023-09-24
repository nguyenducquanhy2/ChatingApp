package com.example.chatingappver2.contract.repository

import com.example.chatingappver2.model.CurrentContacts
import com.example.chatingappver2.model.FriendRequest
import com.example.chatingappver2.model.UserProfile

interface AccountRepositoryContract {

    interface GetContactFinishListener {
        fun currentContactsAdd(currentContacts: CurrentContacts)
        fun currentContactsChange(currentContacts: CurrentContacts)
        fun getCurrentError(message: String)
    }

    fun getCurrentContact(instance: GetContactFinishListener)

    interface GetUserNotContactFinishListener {
        fun userNotContactsAdd(userProfile: UserProfile)
        fun getError(message: String)
    }

    suspend fun getUserNotContact(instance: GetUserNotContactFinishListener)

    interface SendFriendRequestFinishListener {
        fun SendFriendRequestSuccess(idUserReceiveRequest: String)
        fun SendFriendRequestError(message:String)
    }

    fun sendRequestAddFriend(idUserReceiveRequest: String,instance:SendFriendRequestFinishListener)

    interface GetListFriendRequest {
        fun friendRequestsAdd(friendRequest: FriendRequest)
        fun friendRequestsRemove(idUserSendRequest: String)
        fun friendRequestsError(message: String)
    }
    fun getListFriendRequest(instance:GetListFriendRequest)


    fun acceptFriendRequest(friendRequest: FriendRequest)

    interface CancelRequestFinishListener {
        fun cancelRequestSuccess(idSender: String)
        fun cancelRequestError(message:String)
    }
    fun deleteFriendRequest(idSender: String,instance:CancelRequestFinishListener)

    interface ListFriendRequestSent {
        fun friendRequestsAdd( userProfile:  UserProfile)
        fun friendRequestsError(message: String)
    }

    fun getListFriendRequestSent(instance:ListFriendRequestSent)

    interface RevokeFriendRequestFinishListener {
        fun revokeFriendRequestSuccess(idReceive: String)
        fun revokeFriendRequestError(message:String)
    }
    fun revokeFriendRequest(idReceive: String,instance:RevokeFriendRequestFinishListener)

}