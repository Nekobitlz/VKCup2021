package com.nekobitlz.taxi.view.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nekobitlz.taxi.databinding.ItemAddressBinding
import com.nekobitlz.vkcup.commons.layoutInflater

class ChooseAddressAdapter(private val onClick: (ChooseAddressItem) -> Unit) :
    ListAdapter<ChooseAddressItem, ChooseAddressViewHolder>(ChooseAddressDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseAddressViewHolder {
        val binding = ItemAddressBinding.inflate(parent.layoutInflater, parent, false)
        return ChooseAddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChooseAddressViewHolder, position: Int) {
        holder.bind(getItem(position), onClick)
    }
}

object ChooseAddressDiffUtil : DiffUtil.ItemCallback<ChooseAddressItem>() {
    override fun areItemsTheSame(oldItem: ChooseAddressItem, newItem: ChooseAddressItem): Boolean {
        return oldItem.place == oldItem.place
    }

    override fun areContentsTheSame(
        oldItem: ChooseAddressItem,
        newItem: ChooseAddressItem
    ): Boolean = oldItem == newItem
}

class ChooseAddressViewHolder(binding: ItemAddressBinding) : RecyclerView.ViewHolder(binding.root) {

    private val tvPlace = binding.tvPlace
    private val tvAddress = binding.tvAddress

    fun bind(item: ChooseAddressItem, onClick: (ChooseAddressItem) -> Unit) {
        tvPlace.text = item.place
        tvAddress.text = item.address
        itemView.setOnClickListener { onClick(item) }
    }
}