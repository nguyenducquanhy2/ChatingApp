package com.example.chatingappver2.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.chatingappver2.model.FriendRequest
import com.example.chatingappver2.R
import com.example.chatingappver2.ui.base.adapter.BaseAdapterSearching
import kotlinx.android.synthetic.main.layout_friend_request.view.*


class AdapterFriendRequest  : BaseAdapterSearching<FriendRequest>() {
    var acceptClickListener:((CurrentFrientRequest: FriendRequest)->Unit)?=null
    var cancelClickListener:((idUserSendRequest: String)->Unit)?=null

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): FriendRequestViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_friend_request, parent, false)
        return FriendRequestViewHolder(view)
    }


    override fun getItemCount(): Int = listToShow.size

    override fun filterData(value: String) {
        if (value.isEmpty()) {
            changeListData(false)
            return
        }

        val oldListUser = getDefaultList()
        val newListUser: MutableList<FriendRequest> =
            oldListUser.filter { it.nameUserSendRequest.contains(value) } as MutableList<FriendRequest>

        if (newListUser.size != 0) {
            clearListFilter()
            addListFilter(newListUser)
            changeListData(true)
        } else {
            changeListData(false)
        }
    }

    override fun onBind(holder: BaseViewHolder, item: FriendRequest) {
        Glide.with(holder.itemView.imgProfileUserFriendRequest.context).asBitmap().
        load(item.urlAvatarUserSendRequest)
            .into(holder.itemView.imgProfileUserFriendRequest)
        holder.itemView.tvFullnameUserAccountFriendRequest.text = item.nameUserSendRequest

        holder.itemView.btnACceptFriendRequest.setOnClickListener{
            acceptClickListener?.invoke(item)
        }

        holder.itemView.btnDeleteFriendRequest.setOnClickListener {
            cancelClickListener?.invoke(item.idUserSendRequest)
        }
    }

    inner class FriendRequestViewHolder(itemView: View) : BaseViewHolder(itemView)


}