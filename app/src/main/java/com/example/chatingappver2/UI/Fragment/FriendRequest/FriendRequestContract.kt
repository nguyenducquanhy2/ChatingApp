package com.example.chatingappver2.UI.Fragment.FriendRequest

import com.example.chatingappver2.Model.FriendRequest


interface FriendRequestContract {
    interface view {
        fun cancelFriendRequestFail()
        fun cancelFriendRequestSucces(idSender: String)
        fun removeItemOfListByIndex(i: Int)
        fun removeOnListRequest( i: Int)
        fun setFrienRequest(friendRequest: FriendRequest)
    }
    interface presenter {
        fun acceptFriendRequest(friendRequest: FriendRequest)
        fun deleteFriendRequest(idSender: String)
        fun findIndexNeedRemoveInList(idSender: String, list: List<FriendRequest>)
        fun getListFrienRequestOnDB()
    }

}