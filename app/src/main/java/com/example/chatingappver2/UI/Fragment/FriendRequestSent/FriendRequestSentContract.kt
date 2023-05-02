package com.example.chatingappver2.UI.Fragment.FriendRequestSent

import com.example.chatingappver2.Model.UserProfile

interface FriendRequestSentContract {
    interface view {
        fun addItemFriendRequest(userProfile: UserProfile)
        fun cancelFriendRequestFail()
        fun cancelFriendRequestSucces(str: String)
        fun removeitemOfList(count: Int)
    }

    interface presenter {
        fun cancelFriendRequest(str: String)
        fun  friendRequestSent()
        fun removeItemOfList(str: String, listFriendRequestSent: MutableList<UserProfile>)
    }


}