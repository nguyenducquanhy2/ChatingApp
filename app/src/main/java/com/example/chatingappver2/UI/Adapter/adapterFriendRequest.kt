package com.example.chatingappver2.UI.Adapter

import FriendRequestOnClick
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatingappver2.Model.FriendRequest
import com.example.chatingappver2.R
import kotlinx.android.synthetic.main.layout_friend_request.view.*


class adapterFriendRequest constructor(
    private val listFriendRequest: MutableList<FriendRequest>,
    private val context: Context,
    private val friendRequestOnClick: FriendRequestOnClick
) : RecyclerView.Adapter<adapterFriendRequest.FriendRequestViewHolder?>() {


    override fun onCreateViewHolder(parent: ViewGroup, i: Int): FriendRequestViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.layout_friend_request, parent, false)
        return FriendRequestViewHolder(view)
    }


    override fun onBindViewHolder(holder: FriendRequestViewHolder, i: Int) {
        val CurrentFrientRequest = listFriendRequest[i]
        Glide.with(context).asBitmap().load(CurrentFrientRequest.urlAvatarUserSendRequest)
            .into(holder.itemView.imgProfileUserFriendRequest)
        holder.itemView.tvFullnameUserAccountFriendRequest.text = CurrentFrientRequest.nameUserSendRequest

        holder.itemView.btnACceptFriendRequest.setOnClickListener{
            friendRequestOnClick.btnAcceptClickListener(CurrentFrientRequest)
        }

        holder.itemView.btnDeleteFriendRequest.setOnClickListener {
            friendRequestOnClick.btnDeleteClickListener(CurrentFrientRequest.idUserSendRequest)
        }

    }

    override fun getItemCount(): Int = listFriendRequest.size

    inner class FriendRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}


}