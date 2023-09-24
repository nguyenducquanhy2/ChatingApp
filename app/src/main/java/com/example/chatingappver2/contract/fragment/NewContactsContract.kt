package com.example.chatingappver2.contract.fragment

import com.example.chatingappver2.model.UserProfile

interface NewContactsContract {

    interface view {
        fun addItemToList(userProfile: UserProfile)
        fun notifySendFriendRequestFail()
        fun notifySendFriendRequestSuccess(idUserReceiveRequest: String)
    }
    interface presenter {
        fun getNewUser()
        fun sendRequestAddFriend(idUserReceiveRequest: String)
    }


}