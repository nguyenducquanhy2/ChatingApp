package com.example.chatingappver2.contract.fragment

import com.example.chatingappver2.model.UserProfile

interface FriendRequestSentContract {
    interface View {
        fun addItemFriendRequest(userProfile: UserProfile)
        fun revokeFriendRequestFail()
        fun revokeFriendRequestSuccess(idReceive: String)
        //fun removeItemOfList(position: Int)
    }

    interface Presenter {
        fun revokeFriendRequest(idReceive: String)
        fun  friendRequestSent()
        //fun removeItemOfList(idReceive: String, listFriendRequestSent: MutableList<UserProfile>)
    }


}