package com.example.chatingappver2.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatingappver2.model.Message
import com.example.chatingappver2.R
import com.example.chatingappver2.ui.base.adapter.BaseAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.receive_mesg.view.imgReceive
import kotlinx.android.synthetic.main.receive_mesg.view.layoutReceiveMsg
import kotlinx.android.synthetic.main.receive_mesg.view.tvReceive
import kotlinx.android.synthetic.main.receive_mesg.view.tvTimeReceive
import kotlinx.android.synthetic.main.send_mesg.view.imgSend
import kotlinx.android.synthetic.main.send_mesg.view.layoutMsg
import kotlinx.android.synthetic.main.send_mesg.view.tvMsgSend
import kotlinx.android.synthetic.main.send_mesg.view.tvTimeSend
import java.text.SimpleDateFormat
import java.util.Calendar

class MessageAdapter :
    BaseAdapter<Message>() {
    companion object{
        private const val ITEM_RECEIVE: Int = 2
        private const val ITEM_SENT: Int = 1
        private const val MSG_IMAGE: String = "photo"
    }

    lateinit var msgOnLongClickListener:(view:View, message:String, urlImage: String, keyMsg:String, isMyMessage:Boolean)->Unit
    lateinit var imageMessageOnClickListener:(urlImage:String)->Unit
    lateinit var imageShowFullScreen:(urlImage:String)->Unit

    private val senderAccount: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

    override fun getItemViewType(i: Int): Int {
        val idSender = listDefault[i].idSender
        return if (idSender == senderAccount.uid) {
            ITEM_SENT
        } else {
            ITEM_RECEIVE
        }
    }
    override fun onBind(holder: BaseViewHolder, item: Message) {
        holder.setIsRecyclable(false)

        if ((holder.itemViewType ==ITEM_SENT)) {
            (holder as SentMsgHolder).bindingView(item)

        } else {
            (holder as ReceiveMsgHolder).bindingView(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return if (viewType == ITEM_SENT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.send_mesg, parent, false)
            SentMsgHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.receive_mesg, parent, false)
            ReceiveMsgHolder(view)
        }
    }

    private fun setTimeSend(MsgHolder: RecyclerView.ViewHolder, time: String) {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val currentTime = simpleDateFormat.format(Calendar.getInstance().time)

        val parseTime = simpleDateFormat.parse(time)

        val TimeMsg = simpleDateFormat.format(parseTime)

        val spitTime = time.split(" ")
        if ((TimeMsg == currentTime)) {
            MsgHolder.itemView.tvTimeSend.text = spitTime[1]
        } else {
            MsgHolder.itemView.tvTimeSend.text = spitTime[0]
        }
    }

    private fun setTimeReceive(receiveMsgHolder: RecyclerView.ViewHolder, time: String) {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val currentTime = simpleDateFormat.format(Calendar.getInstance().time)

        val parseTime = simpleDateFormat.parse(time)

        val TimeMsg = simpleDateFormat.format(parseTime)

        val spitTime = time.split(" ")

        if ((TimeMsg == currentTime)) {
            receiveMsgHolder.itemView.tvTimeReceive.text = spitTime[1]
        } else {
            receiveMsgHolder.itemView.tvTimeReceive.text = spitTime[0]
        }
    }

    private fun ImgEventClickShowFullScreen(imageView: ImageView, urlImg: String) {
        imageView.setOnClickListener {
            imageShowFullScreen(urlImg)
        }
    }

    inner class SentMsgHolder(itemView: View) : BaseViewHolder(itemView){
        fun bindingView( message: Message) {

            if ((message.msgTxt == MSG_IMAGE)) {
                itemView.layoutMsg.visibility = View.GONE
                itemView.imgSend.visibility = View.VISIBLE

                Glide.with(itemView.context).asBitmap().load(message.urlImg).into(itemView.imgSend)

                itemView.imgSend.setOnClickListener{
                    imageMessageOnClickListener(message.urlImg)
                }

                itemView.imgSend.setOnLongClickListener {
                    val imageViewSend = itemView.imgSend
                    msgOnLongClickListener(imageViewSend,message.msgTxt,message.urlImg,message.keyMsg!!,true)

                    return@setOnLongClickListener true
                }
            }

            setTimeSend(this, message.time)
            itemView.tvMsgSend.text = message.msgTxt

            itemView.setOnLongClickListener {
                val textViewSend = itemView.tvMsgSend
                msgOnLongClickListener(textViewSend,message.msgTxt,"",message.keyMsg!!,true)
                return@setOnLongClickListener true
            }

        }
    }

    inner class ReceiveMsgHolder(itemView: View) : BaseViewHolder(itemView){
        fun bindingView(message: Message){
            if ((message.msgTxt == MSG_IMAGE)) {
                itemView.layoutReceiveMsg.visibility = View.GONE
                itemView.imgReceive.visibility = View.VISIBLE

                Glide.with(itemView.context).asBitmap().load(message.urlImg)
                    .into(itemView.imgReceive)

                ImgEventClickShowFullScreen(itemView.imgReceive, message.urlImg)

                itemView.imgReceive.setOnClickListener{
                    imageMessageOnClickListener(message.urlImg)
                }

                itemView.imgReceive.setOnLongClickListener {
                    val imageViewReceive = itemView.imgReceive
                    msgOnLongClickListener(imageViewReceive,message.msgTxt,message.urlImg,message.keyMsg!!,false)
                    return@setOnLongClickListener true
                }

            }
            setTimeReceive(this, message.time)
            itemView.tvReceive.text =
                message.msgTxt

            itemView.setOnLongClickListener {
                val textViewReceive = itemView.tvReceive


                msgOnLongClickListener(textViewReceive,message.msgTxt,"",message.keyMsg!!,false)
                return@setOnLongClickListener true
            }
        }
    }

}