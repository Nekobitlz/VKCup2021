package com.nekobitlz.news_tinder.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.nekobitlz.news_tinder.repository.NewsTinderCard
import com.nekobitlz.news_tinder.databinding.CardNewsTinderBinding
import com.nekobitlz.news_tinder.view.custom.CardContainerAdapter
import com.nekobitlz.vkcup.commons.getTimeAgo
import com.nekobitlz.vkcup.commons.setImageOrGone
import com.nekobitlz.vkcup.commons.setTextOrGone
import com.nekobitlz.vkcup.commons.setTextOrHide

class NewsTinderAdapter(context: Context) : CardContainerAdapter() {

    var layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private var list: List<NewsTinderCard> = listOf()

    override fun getItem(position: Int) = list[position]

    override fun getView(position: Int): View {
        val binding = CardNewsTinderBinding.inflate(layoutInflater)
        val model = getItem(position)
        with(binding) {
            tvName.setTextOrHide(model.name)
            tvTitle.setTextOrGone(model.title)
            tvTime.text = getTimeAgo(tvName.resources, model.date?.toLong() ?: 0)
            ivBackground.setImageURI(model.imageUrl)
            ivAvatar.setImageOrGone(model.avatarUrl)
            tvLikeCount.text = model.getSaveCount(model.likeCount)
            tvCommentsCount.text = model.getSaveCount(model.commentCount)
            tvShareCount.text = model.getSaveCount(model.shareCount)
        }
        return binding.root
    }

    override fun getCount(): Int = list.size

    fun submitList(list: List<NewsTinderCard>) {
        this.list = list
        notifyAppendData()
    }
}