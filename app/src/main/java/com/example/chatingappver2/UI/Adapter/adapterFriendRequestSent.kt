package com.example.chatingappver2.UI.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatingappver2.Model.UserProfile
import com.example.chatingappver2.R
import com.example.chatingappver2.UI.InterfaceAdapter.FrienRequestSentOnClick
import kotlinx.android.synthetic.main.layout_friend_request_sent.view.*
import kotlin.jvm.internal.Intrinsics

class adapterFriendRequestSent(
    private val listFriendRequestSent: List<UserProfile>,
    private val context: Context,
    private val frienRequestSentOnClick: FrienRequestSentOnClick
) : RecyclerView.Adapter<adapterFriendRequestSent.FriendRequestSentViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): FriendRequestSentViewHolder {
        Intrinsics.checkNotNullParameter(parent, "parent")
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.layout_friend_request_sent, parent, false)
        Intrinsics.checkNotNullExpressionValue(view, "view")
        return FriendRequestSentViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendRequestSentViewHolder, i: Int) {

        val CurrentFriendRequestSent = listFriendRequestSent.get(i)
        Glide.with(context).asBitmap().load(CurrentFriendRequestSent.urlImgProfile)
            .into(holder.itemView.imgProfileUserFriendRequestSent)

        holder.itemView.btnCancelFriendRequestSent.setOnClickListener {
            frienRequestSentOnClick.cancelFriendRequestOnClickListener(CurrentFriendRequestSent.idUser)
        }
    }

    override fun getItemCount(): Int {
        return listFriendRequestSent.size
    }

    inner class FriendRequestSentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}


}