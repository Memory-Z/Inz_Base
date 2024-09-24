package com.inz.base.base

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class BaseRvAdapter<T, VH : RecyclerView.ViewHolder>(val context: Context) :
    RecyclerView.Adapter<VH>() {

    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(context)

    protected var dataList: MutableList<T>? = null

    abstract fun onCreateVH(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): VH

    abstract fun onBindVH(holder: VH, position: Int)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater = LayoutInflater.from(parent.context)
        return onCreateVH(inflater, parent, viewType)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        onBindVH(holder, position)
    }

    override fun getItemCount(): Int {
        return dataList?.size ?: 0
    }

    fun getItemByPosition(position: Int): T? {
        if (isInDataRange(position)) {
            return this.dataList?.get(position)
        }
        return null
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshDataList(dataList: List<T>?) {
        this.dataList?.clear()
        this.dataList = mutableListOf<T>()
            .apply {
                dataList?.let {
                    addAll(it)
                }
            }
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun loadMoreDataList(dataList: List<T>) {
        this.dataList?.addAll(dataList)
        notifyDataSetChanged()
    }

    fun updateItemByPosition(data: T, position: Int) {
        if (isInDataRange(position)) {
            this.dataList?.set(position, data)
            notifyItemChanged(position)
        }
    }

    fun removeItemByPosition(position: Int) {
        if (isInDataRange(position)) {
            this.dataList?.removeAt(position)
            notifyItemChanged(position)
        }
    }

    fun isInDataRange(position: Int): Boolean {
        this.dataList?.let {
            if (position >= 0 && position < it.size) {
                return true
            }
        }
        return false
    }

    abstract inner class BaseRvHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(data: T)
    }
}