package com.example.chatingappver2.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.chatingappver2.model.CurrentContacts
import com.example.chatingappver2.R
import com.example.chatingappver2.ui.base.adapter.BaseAdapterSearching
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.layout_user.view.iconSeen
import kotlinx.android.synthetic.main.layout_user.view.imgIconOnline
import kotlinx.android.synthetic.main.layout_user.view.imgProfileUserNewContacts
import kotlinx.android.synthetic.main.layout_user.view.tvFullnameUserAccount
import kotlinx.android.synthetic.main.layout_user.view.tvLastDateSent
import kotlinx.android.synthetic.main.layout_user.view.tvLastMesage
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.jvm.internal.Intrinsics


class AdapterCurrentUser :
    BaseAdapterSearching<CurrentContacts>() {
    private val TAG: String = "adapterUser"
    var clickItemCurrentUser: ((idUserReceive: String) -> Unit)? = null

    override fun getItemCount(): Int = listToShow.size

    override fun onBind(holder: BaseViewHolder, item: CurrentContacts) {
        (holder as UserViewHolder).onBind(item)
    }

    override fun filterData(value: String) {
        if (value.isEmpty()) {
            changeListData(false)
            return
        }
        val oldListUser = getDefaultList()
        val newListUser: MutableList<CurrentContacts> =
            oldListUser.filter { it.fullname.contains(value) } as MutableList<CurrentContacts>

        if (newListUser.size != 0) {
            clearListFilter()
            addListFilter(newListUser)
            changeListData(true)
        } else {
            changeListData(false)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_user, parent, false)
        return UserViewHolder(view)
    }


    private fun setTvLastMesage(currentcontacts: CurrentContacts, itemView: View) {
        if (currentcontacts.lastSentMsg == "null") {
            itemView.tvLastMesage.text = ""
            return
        }

        if (currentcontacts.idUserLastSentMsg == currentcontacts.idUser) {
            itemView.tvLastMesage.text = currentcontacts.lastSentMsg
        } else {
            itemView.tvLastMesage.text = "You: " + currentcontacts.lastSentMsg
        }
    }

    private fun setTvLastSentDate(view: View, timelastMSg: String) {
        if ((timelastMSg == "null")) {
            view.tvLastDateSent.text = ""
            view.iconSeen.visibility = View.GONE
            return
        }

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")

        val currentTime = simpleDateFormat.format(Calendar.getInstance().time)

        val SpitTime = timelastMSg.split(" ")

        if ((SpitTime[0] == currentTime)) {
            view.tvLastDateSent.text = SpitTime[1]
        } else {
            view.tvLastDateSent.text = SpitTime[0]
        }

    }

    private fun notifySeenMessage(currentcontacts: CurrentContacts) {
        val currentUser = FirebaseAuth.getInstance().currentUser!!
        val firebaseDatabase = FirebaseDatabase.getInstance()

        firebaseDatabase.reference.child("currentContacts").child(currentUser.uid)
            .child(currentcontacts.idUser).child("notifyNewMsg").setValue(false)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val idUser: String = currentcontacts.idUser
                    clickItemCurrentUser?.invoke(idUser)
                    Log.d(TAG, "sendNotifycationForThem: success")

                } else {
                    Log.e(TAG, "sendNotifycationForThem: fail")
                }
            }
    }

    inner class UserViewHolder(private val itemView: View) : BaseViewHolder(itemView) {

        fun onBind(currentcontacts: CurrentContacts) {
            Glide.with(itemView.context).asBitmap().load(currentcontacts.urlImgProfile)
                .into(itemView.imgProfileUserNewContacts)
            itemView.tvFullnameUserAccount.text = currentcontacts.fullname
            val view: View = itemView

            setTvLastSentDate(view, currentcontacts.lastSentDate)

            checkActive(currentcontacts.theyIsActive!!)
            setTvLastMesage(currentcontacts, itemView)

            if (Intrinsics.areEqual(currentcontacts.notifyNewMsg, true as Any?)) {
                itemView.tvLastMesage.setTypeface(itemView.tvLastMesage.typeface, Typeface.BOLD)
                itemView.tvFullnameUserAccount.setTypeface(
                    itemView.tvFullnameUserAccount.typeface,
                    Typeface.BOLD
                )

            } else {
                itemView.tvLastMesage.setTypeface(itemView.tvLastMesage.typeface, Typeface.NORMAL)
                itemView.tvFullnameUserAccount.setTypeface(
                    itemView.tvFullnameUserAccount.typeface,
                    Typeface.NORMAL
                )

            }

            itemView.setOnClickListener {
                try {
                    notifySeenMessage(currentcontacts)
                } catch (e: IndexOutOfBoundsException) {
                    Log.e(TAG, "initAdapterForRecycle: ${e.message}")
                    Toast.makeText(itemView.context, "IndexOutOfBoundsException: ${e.message}", Toast.LENGTH_SHORT).show()
                }

            }

        }

        private fun checkActive(bool: Boolean) {
            if (bool == true) {
                itemView.imgIconOnline.setVisibility(View.VISIBLE)
            } else {
                itemView.imgIconOnline.setVisibility(View.INVISIBLE)
            }
        }
    }

}