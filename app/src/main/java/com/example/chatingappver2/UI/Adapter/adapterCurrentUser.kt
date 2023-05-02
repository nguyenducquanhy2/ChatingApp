package com.example.chatingappver2.UI.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatingappver2.Model.currentContacts
import com.example.chatingappver2.R
import com.example.chatingappver2.UI.Activity.chatActivity.ChatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.layout_user.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.jvm.internal.Intrinsics


class adapterCurrentUser constructor(
    private val listUser: MutableList<currentContacts>,
    private val context: Context
) :
    RecyclerView.Adapter<adapterCurrentUser.userViewHolder?>() {
    private val TAG: String = "adapterUser"

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): userViewHolder {
        Intrinsics.checkNotNullParameter(parent, "parent")
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_user, parent, false)
        return userViewHolder(view)
    }


    override fun onBindViewHolder(holder: userViewHolder, i: Int) {
        val currentcontacts: currentContacts = listUser[i]

        Glide.with(context).asBitmap().load(currentcontacts.urlImgProfile).into(holder.itemView.imgProfileUserNewContacts)
        holder.itemView.tvFullnameUserAccount.text = currentcontacts.fullname
        val view: View = holder.itemView

        setTvLastSentDate(view, currentcontacts.lastSentDate)
        checkActive(currentcontacts.theyIsActive!!, holder)
        setTvLastMesage(currentcontacts, holder.itemView)

        if (Intrinsics.areEqual(currentcontacts.notifyNewMsg, true as Any?)) {
            holder.itemView.tvLastMesage.setTypeface(holder.itemView.tvLastMesage.typeface, Typeface.BOLD)
            holder.itemView.tvFullnameUserAccount.setTypeface(holder.itemView.tvFullnameUserAccount.typeface, Typeface.BOLD)

        } else {
            holder.itemView.tvLastMesage.setTypeface(holder.itemView.tvLastMesage.typeface, Typeface.NORMAL)
            holder.itemView.tvFullnameUserAccount.setTypeface(holder.itemView.tvFullnameUserAccount.typeface, Typeface.NORMAL)

        }

        holder.itemView.setOnClickListener{
            notifySeenMessage(currentcontacts)
        }
    }

    private fun setTvLastMesage(currentcontacts: currentContacts, itemView: View) {
        if (currentcontacts.lastSentMsg=="null"){
            itemView.tvLastMesage.text =""
            return
        }

        if (currentcontacts.idUserLastSentMsg == currentcontacts.idUser) {
            itemView.tvLastMesage.text = currentcontacts.lastSentDate
        } else {
            itemView.tvLastMesage.text = "You: " + currentcontacts.lastSentMsg
        }
    }

    private fun setTvLastSentDate(view: View, timelastMSg: String) {
        if ((timelastMSg == "null")) {
            view.tvLastDateSent.text =""
            view.iconSeen.visibility=View.GONE
            return
        }

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")

        val currentTime = simpleDateFormat.format(Calendar.getInstance().time)
        val parse: Date = simpleDateFormat.parse(timelastMSg)
//
//        val format2: String = simpleDateFormat.format(parse)

        val SpitTime =timelastMSg.split(" ")

        if ((SpitTime[0] == currentTime)) {
            view.tvLastDateSent.text =SpitTime[1]
        } else {
            view.tvLastDateSent.text =SpitTime[0]
        }

    }

    private fun notifySeenMessage(currentcontacts: currentContacts) {
        val currentUser = FirebaseAuth.getInstance().currentUser!!
        val firebaseDatabase = FirebaseDatabase.getInstance()

        firebaseDatabase.reference.child("currentContacts").child(currentUser.uid)
            .child(currentcontacts.idUser).child("notifyNewMsg").setValue(false)
            .addOnCompleteListener{task->
                if (task.isSuccessful) {
                    val idUser: String = currentcontacts.idUser
                    Intrinsics.checkNotNull(idUser)
                    changeToChatActivity(idUser)
                    Log.d(TAG, "sendNotifycationForThem: success")

                }
                else{
                    Log.e(TAG, "sendNotifycationForThem: fail")
                }
            }
    }

    private fun changeToChatActivity(idUserReceive: String) {
        val intent=Intent(context, ChatActivity::class.java)
        intent.putExtra("idUserReceive",idUserReceive)
        context.startActivity(intent)
    }

    private fun checkActive(bool: Boolean, userviewholder: userViewHolder) {
        if (bool == true) {
            userviewholder.itemView.imgIconOnline.setVisibility(View.VISIBLE)
        } else {
            userviewholder.itemView.imgIconOnline.setVisibility(View.INVISIBLE)
        }
    }


    override fun getItemCount(): Int = listUser.size

    inner class userViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

}