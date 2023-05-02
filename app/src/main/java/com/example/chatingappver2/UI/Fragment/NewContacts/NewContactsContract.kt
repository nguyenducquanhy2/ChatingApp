package com.example.chatingappver2.UI.Fragment.NewContacts

import com.example.chatingappver2.Model.UserProfile

interface NewContactsContract {

    interface view {
        fun addItemToList(userProfile: UserProfile)
        fun notifySendFriendRequestFail()
        fun notifySendFriendRequestSuccess(idUserReceiveRequest: String)
        fun removeItemOfListByIndex(count: Int)
        fun setNewUserProfileList(list: MutableList<UserProfile>)
        fun setOldUserProfileList()
    }
    interface presenter {
        fun fillterData(ListUser: List<UserProfile>, FindvalueInput: String)
        fun findIndexRemove(idUserReceiveRequest: String?, ListUser: List<UserProfile>)
        fun getNewUser()
        fun sendRequestAddFriend(idUserReceiveRequest: String)
    }


}