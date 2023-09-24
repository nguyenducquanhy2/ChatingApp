package com.example.chatingappver2.ui.base.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatingappver2.model.Message

abstract class BaseAdapter<T> :
    RecyclerView.Adapter<BaseAdapter<T>.BaseViewHolder>() {
    protected var listDefault: MutableList<T> = mutableListOf()

    abstract fun onBind(holder: BaseViewHolder, item: T)

    abstract override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item = listDefault[position]
        onBind(holder, item)
    }

//    override fun onBindViewHolder(
//        holder: BaseViewHolder,
//        position: Int,
//        payloads: MutableList<Any>
//    ) {
//        if (payloads.isEmpty()) {
//            val item = listDefault[position]
//            onBind(holder, item)
//        } else {
//            onItemChange(payloads)
//
//            // Cập nhật một phần của ViewHolder dựa trên payloads
//            for (payload in payloads) {
//                when (payload) {
//                    is String -> holder.textView.text = payload // Cập nhật chỉ text của TextView
//                    // Xử lý các trường hợp payload khác
//                }
//            }
//        }
//
//
//    }

    override fun getItemCount(): Int =listDefault.size

    abstract inner class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    @SuppressLint("NotifyDataSetChanged")
    fun addItemToListUser(list: T) {
        this.listDefault.add(list)
        notifyItemInserted(this.listDefault.lastIndex)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getDefaultList(): MutableList<T> {
        return listDefault
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearListUser() {
        listDefault.clear()
        notifyDataSetChanged()
    }

    fun removeItemAtIndex(position:Int){
        listDefault.removeAt(position)
//        notifyItemRemoved(position)
//        notifyItemRangeChanged(position,1)
       notifyDataSetChanged()
    }

    fun valueItemChange(messageChange: T, index: Int){
        this.listDefault[index]=messageChange
        notifyItemChanged(index)
    }

}