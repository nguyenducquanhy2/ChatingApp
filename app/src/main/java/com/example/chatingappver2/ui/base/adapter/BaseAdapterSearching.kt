package com.example.chatingappver2.ui.base.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup

abstract class BaseAdapterSearching<T> :
    BaseAdapter<T>() {

    abstract fun filterData(value: String)
    protected var newList: MutableList<T> = mutableListOf()
    protected var listToShow: MutableList<T> = listDefault

    abstract override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item = listToShow[position]
        onBind(holder, item)
    }

    override fun getItemCount() = listToShow.size

    @SuppressLint("NotifyDataSetChanged")
    fun showOldList() {
        this.listToShow = this.listDefault
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun showFilterList() {
        this.listToShow = this.newList
        notifyDataSetChanged()
    }

    fun refreshListUser() {
        listDefault.clear()
        this.newList.clear()
        showOldList()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getListFilter(): MutableList<T> {
        return this.newList
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addListFilter(list: MutableList<T>) {
        this.newList.addAll(list)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearListFilter() {
        this.newList.clear()
        notifyDataSetChanged()
    }

    fun changeListData(isFilter: Boolean) {
        if (isFilter) {
            showFilterList()
        } else {
            showOldList()
        }
    }

    fun doShowListRemoveAt(position: Int) {
        this.listToShow.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getListShowing() = listToShow

}
