package com.example.chatingappver2.UI.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatingappver2.Model.UserProfile
import com.example.chatingappver2.R
import com.example.chatingappver2.UI.InterfaceAdapter.NewFriendOnClick
import kotlinx.android.synthetic.main.layout_addfriend.view.*
import kotlin.jvm.internal.Intrinsics

class adapterNewUser(
    private val listUser: MutableList<UserProfile>,
    private val context: Context,
    private val newFriendOnClick: NewFriendOnClick
) : RecyclerView.Adapter<adapterNewUser.userViewHolder>() {
    private val TAG = "adapterUser"

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): userViewHolder {
        Intrinsics.checkNotNullParameter(parent, "parent")
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_addfriend, parent, false)
        Intrinsics.checkNotNullExpressionValue(view, "view")
        return userViewHolder(view)
    }

    override fun onBindViewHolder(holder: userViewHolder, position: Int) {

        val currentUserContacts = listUser[position]

        Glide.with(context).asBitmap().load(currentUserContacts.urlImgProfile)
            .into(holder.itemView.imgProfileUserAddFriend)

        holder.itemView.tvFullnameUserAccountAddFriend.text = currentUserContacts.fullname
        holder.itemView.btnRequestAddFriend.setOnClickListener {
            val idUser = currentUserContacts.idUser
            newFriendOnClick.addFriendOnClickListener(idUser)
        }
    }

    override fun getItemCount(): Int = listUser.size

    inner class userViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

}