package com.nekobitlz.vkcup.commons.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nekobitlz.vkcup.commons.DummyViewHolder

open class BaseAdapter(
    diffUtil: DiffUtil.ItemCallback<BaseAdapterItem<RecyclerView.ViewHolder>> = DIFF_UTIL,
) : ListAdapter<BaseAdapterItem<RecyclerView.ViewHolder>, RecyclerView.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return currentList.find { it.viewType == viewType }?.createViewHolder(parent)
            ?: DummyViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        item.bind(holder)
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).viewType
    }

    fun indexOf(headerTypesItem: BaseAdapterItem<out RecyclerView.ViewHolder>): Int {
        return currentList.indexOf(headerTypesItem)
    }

    companion object {
        val DIFF_UTIL =
            object : DiffUtil.ItemCallback<BaseAdapterItem<RecyclerView.ViewHolder>>() {

                override fun areItemsTheSame(
                    old: BaseAdapterItem<RecyclerView.ViewHolder>,
                    new: BaseAdapterItem<RecyclerView.ViewHolder>,
                ): Boolean {
                    return old.viewType == new.viewType
                }

                override fun areContentsTheSame(
                    old: BaseAdapterItem<RecyclerView.ViewHolder>,
                    new: BaseAdapterItem<RecyclerView.ViewHolder>,
                ): Boolean {
                    return old == new
                }
            }
    }
}