package com.nekobitlz.vkcup.commons.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapterItem<T : RecyclerView.ViewHolder> {

    abstract val viewType: Int

    abstract fun bind(holder: T)
    abstract fun createViewHolder(parent: ViewGroup): T
}