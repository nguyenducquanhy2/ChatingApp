package com.example.chatingappver2.UI.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapplication.Ui.InterfaceAdapter.MessageOnClick
import com.example.chatingappver2.Model.Message
import com.example.chatingappver2.R
import com.example.chatingappver2.UI.Activity.ShowFullScreenImg.ShowImgFullScreenActivity
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

class MessageAdapter(
    private var messages: MutableList<Message>,
    private var context: Context,
    private val MessageOnClick: MessageOnClick
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private val ITEM_RECEIVE: Int = 2
    private val ITEM_SENT: Int = 1
    private val MSG_IMAGE: String = "photo"

    private val senderAccount: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

    override fun getItemViewType(i: Int): Int {
        val idSender = messages[i].idSender
        return if (idSender == senderAccount.uid) {
            ITEM_SENT
        } else {
            ITEM_RECEIVE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): RecyclerView.ViewHolder {
        if (i == ITEM_SENT) {
            val view = LayoutInflater.from(context).inflate(R.layout.send_mesg, parent, false)
            return SentMsgHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.receive_mesg, parent, false)
            return ReceiveMsgHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        val message = messages[i]
        holder.setIsRecyclable(false)

        if ((holder is SentMsgHolder)) {
            setValueMsgSend(holder ,message)

        } else {
            setValueMsgReceive(holder as ReceiveMsgHolder,message)
        }
    }

    private fun setValueMsgReceive(viewHolderReceive: MessageAdapter.ReceiveMsgHolder, message: Message) {

        if ((message.msgTxt == MSG_IMAGE)) {
            viewHolderReceive.itemView.layoutReceiveMsg.visibility = View.GONE
            viewHolderReceive.itemView.imgReceive.visibility = View.VISIBLE

            Glide.with(context).asBitmap().load(message.urlImg)
                .into(viewHolderReceive.itemView.imgReceive)

            ImgEventClickShowFullScreen(viewHolderReceive.itemView.imgReceive, message.urlImg)

            viewHolderReceive.itemView.imgReceive.setOnClickListener{
                MessageOnClick.ImageMessageOnClickListener(message.urlImg)
            }

            viewHolderReceive.itemView.imgReceive.setOnLongClickListener {
                val imageViewReceive = viewHolderReceive.itemView.imgReceive
                MessageOnClick.msgOnLongClickListener(imageViewReceive,message.msgTxt,message.urlImg,message.keyMsg!!,false)
                return@setOnLongClickListener true
            }

        }
        setTimeReceive(viewHolderReceive, message.time)
        viewHolderReceive.itemView.tvReceive.text =
            message.msgTxt

        viewHolderReceive.itemView.setOnLongClickListener {
            val textViewReceive = viewHolderReceive.itemView.tvReceive


            MessageOnClick.msgOnLongClickListener(textViewReceive,message.msgTxt,"",message.keyMsg!!,false)
            return@setOnLongClickListener true
        }
    }

    private fun setValueMsgSend(viewHolderSend: MessageAdapter.SentMsgHolder, message: Message) {

        if ((message.msgTxt == MSG_IMAGE)) {
            viewHolderSend.itemView.layoutMsg.visibility = View.GONE
            viewHolderSend.itemView.imgSend.visibility = View.VISIBLE

            Glide.with(context).asBitmap().load(message.urlImg).into(viewHolderSend.itemView.imgSend)

            viewHolderSend.itemView.imgSend.setOnClickListener{
                MessageOnClick.ImageMessageOnClickListener(message.urlImg)
            }

            viewHolderSend.itemView.imgSend.setOnLongClickListener {
                val imageViewSend = viewHolderSend.itemView.imgSend
                MessageOnClick.msgOnLongClickListener(imageViewSend,message.msgTxt,message.urlImg,message.keyMsg!!,true)

                return@setOnLongClickListener true
            }
        }
        setTimeSend(viewHolderSend, message.time)
        viewHolderSend.itemView.tvMsgSend.text = message.msgTxt
        viewHolderSend.itemView.setOnLongClickListener {
            val textViewSend = viewHolderSend.itemView.tvMsgSend
            MessageOnClick.msgOnLongClickListener(textViewSend,message.msgTxt,"",message.keyMsg!!,true)
            return@setOnLongClickListener true
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
            val intent = Intent(context, ShowImgFullScreenActivity::class.java)
            intent.putExtra("urlImg", urlImg)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = messages.size

    inner class SentMsgHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    inner class ReceiveMsgHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

}