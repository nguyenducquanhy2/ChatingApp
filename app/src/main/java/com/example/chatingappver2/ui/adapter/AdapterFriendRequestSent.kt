package com.example.chatingappver2.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.chatingappver2.model.UserProfile
import com.example.chatingappver2.R
import com.example.chatingappver2.ui.base.adapter.BaseAdapterSearching
import kotlinx.android.synthetic.main.item_friend_request_sent.view.btnCancelFriendRequestSent
import kotlinx.android.synthetic.main.item_friend_request_sent.view.imgProfileUser
import kotlinx.android.synthetic.main.item_friend_request_sent.view.tvFullnameUserAccountFriendRequestSent
import kotlin.jvm.internal.Intrinsics

class AdapterFriendRequestSent : BaseAdapterSearching<UserProfile>() {
    lateinit var revokeFriendRequest:(idUser:String)->Unit


    override fun filterData(value: String) {
        if (value.isEmpty()) {
            changeListData(false)
            return
        }

        val oldListUser = getDefaultList()
        val newListUser: MutableList<UserProfile> =
            oldListUser.filter { it.fullname.contains(value) } as MutableList<UserProfile>

        if (newListUser.size != 0) {
            clearListFilter()
            addListFilter(newListUser)
            changeListData(true)
        } else {
            changeListData(false)
        }
    }

    override fun onBind(holder: BaseViewHolder, item: UserProfile) {
        holder.itemView.tvFullnameUserAccountFriendRequestSent.text=item.fullname
        Glide.with(holder.itemView.imgProfileUser.context).asBitmap().load(item.urlImgProfile)
            .into(holder.itemView.imgProfileUser)

        holder.itemView.btnCancelFriendRequestSent.setOnClickListener {
            revokeFriendRequest(item.idUser)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendRequestSentViewHolder {
        Intrinsics.checkNotNullParameter(parent, "parent")
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_friend_request_sent, parent, false)
        Intrinsics.checkNotNullExpressionValue(view, "view")
        return FriendRequestSentViewHolder(view)
    }

    inner class FriendRequestSentViewHolder(itemView: View) : BaseViewHolder(itemView)


}