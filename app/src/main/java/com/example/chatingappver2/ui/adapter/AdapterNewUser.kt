package com.example.chatingappver2.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.chatingappver2.model.UserProfile
import com.example.chatingappver2.R
import com.example.chatingappver2.ui.base.adapter.BaseAdapterSearching
import kotlinx.android.synthetic.main.layout_addfriend.view.*
import kotlin.jvm.internal.Intrinsics

class AdapterNewUser: BaseAdapterSearching<UserProfile>() {
    private val TAG = "adapterUser"

    var newFriendOnClick: ((idUser:String)->Unit)?=null

    override fun onBind(holder: BaseViewHolder, item: UserProfile) {

        Glide.with(holder.itemView.imgProfileUserAddFriend.context).asBitmap()
            .load(item.urlImgProfile)
            .into(holder.itemView.imgProfileUserAddFriend)

        holder.itemView.tvFullnameUserAccountAddFriend.text = item.fullname
        holder.itemView.btnRequestAddFriend.setOnClickListener {
            val idUser = item.idUser
            newFriendOnClick?.invoke(idUser)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        Intrinsics.checkNotNullParameter(parent, "parent")
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_addfriend, parent, false)
        Intrinsics.checkNotNullExpressionValue(view, "view")
        return UserViewHolder(view)
    }

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

    override fun getItemCount(): Int = listToShow.size

    inner class UserViewHolder(itemView: View) : BaseViewHolder(itemView) {}

}