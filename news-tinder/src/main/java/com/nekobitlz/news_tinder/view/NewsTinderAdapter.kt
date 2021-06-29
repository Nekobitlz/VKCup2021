package com.nekobitlz.news_tinder.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.nekobitlz.news_tinder.R
import com.nekobitlz.news_tinder.repository.NewsTinderCard
import com.nekobitlz.news_tinder.databinding.CardNewsTinderBinding
import com.nekobitlz.news_tinder.view.custom.CardContainerAdapter
import com.nekobitlz.vkcup.commons.*

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
            ivAvatar.setImageOrGone(model.avatarUrl)
            ivBackground.setImageURI(model.imageUrl)
            if (model.imageUrl != null) {
                tvTitle.textSize = 21f
                tvTitle.maxLines = 4
                ivBackground.hierarchy?.setPlaceholderImage(R.color.colorGreyWhite)
            } else {
                tvTitle.textSize = 16f
                tvTitle.maxLines = Int.MAX_VALUE
                ivBackground.hierarchy?.setPlaceholderImage(R.color.colorTurquoise)
            }
            topGradient.setVisible(model.imageUrl != null)
            bottomGradient.setVisible(model.imageUrl != null)
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