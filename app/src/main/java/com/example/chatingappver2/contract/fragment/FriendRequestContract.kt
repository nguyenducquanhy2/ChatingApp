package com.example.chatingappver2.contract.fragment

import com.example.chatingappver2.model.FriendRequest


interface FriendRequestContract {
    interface view {
        fun cancelFriendRequestFail()
        fun cancelFriendRequestSucces(idSender: String)
        fun removeItemOfListByIndex(i: Int)
        fun removeOnListRequest(idUserSendRequest: String)
        fun setFrienRequest(friendRequest: FriendRequest)
    }
    interface presenter {
        fun acceptFriendRequest(friendRequest: FriendRequest)
        fun deleteFriendRequest(idSender: String)
        fun getListFrienRequestOnDB()
    }

}