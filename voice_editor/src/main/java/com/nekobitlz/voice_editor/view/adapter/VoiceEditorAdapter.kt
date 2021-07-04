package com.nekobitlz.voice_editor.view.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nekobitlz.vkcup.commons.layoutInflater
import com.nekobitlz.vkcup.commons.setVisible
import com.nekobitlz.voice_editor.databinding.ItemEditorActionBinding
import com.nekobitlz.voice_editor.view.custom.VoiceEditorItem

class VoiceEditorAdapter(
    private val list: List<VoiceEditorItem>,
    private val onClick: (item: VoiceEditorItem) -> Unit
) : RecyclerView.Adapter<VoiceEditorViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoiceEditorViewHolder {
        val binding = ItemEditorActionBinding.inflate(parent.layoutInflater, parent, false)
        return VoiceEditorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VoiceEditorViewHolder, position: Int) {
        holder.bind(list[position], onClick)
    }

    override fun getItemCount(): Int = list.size
}

class VoiceEditorViewHolder(
    binding: ItemEditorActionBinding
) : RecyclerView.ViewHolder(binding.root) {

    private val container = binding.container
    private val ivIcon = binding.ivIcon
    private val tvName = binding.tvName
    private val ivSelected = binding.ivSelected

    fun bind(item: VoiceEditorItem, onClick: (item: VoiceEditorItem) -> Unit) {
        ivIcon.setImageResource(item.icon)
        tvName.setText(item.title)
        ivSelected.setVisible(item.isSelected)
        container.setOnClickListener {
            onClick(item)
        }
    }
}