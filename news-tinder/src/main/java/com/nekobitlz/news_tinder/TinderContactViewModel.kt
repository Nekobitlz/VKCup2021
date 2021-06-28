package com.nekobitlz.news_tinder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nekobitlz.news_tinder.repository.NewsTinderRepository

class TinderContactViewModel : ViewModel() {

    private val stream = MutableLiveData<TinderContactModel>()

    private val repository = NewsTinderRepository()

    val modelStream: LiveData<TinderContactModel>
        get() = stream

    private var currentIndex = 0

    private var data = listOf<TinderContactCardModel>()
    private val topCard
        get() = if (data.isNotEmpty()) data[currentIndex % data.size] else null
    private val bottomCard
        get() = if (data.isNotEmpty()) data[(currentIndex + 1) % data.size] else null

    init {
        repository.getNews().map { items ->
            items.map {
                TinderContactCardModel(
                    if (it.user == null) it.group?.name else it.user.firstName + " " + it.user.lastName,
                    it.newsfeedItemWallpost.text,
                    it.newsfeedItemWallpost.date,
                    it.newsfeedItemWallpost.attachments
                        ?.firstOrNull { it.photo != null }
                        ?.photo
                        ?.sizes
                        ?.lastOrNull()
                        ?.url,
                    it.user?.photo50 ?: it.group?.photo50,
                    it.newsfeedItemWallpost.likes?.count ?: 0,
                    it.newsfeedItemWallpost.comments?.count ?: 0,
                    it.newsfeedItemWallpost.reposts?.count ?: 0,
                )
            }
        }.subscribe {
            data = it
            updateCards()
        }
    }

    fun swipe() {
        currentIndex += 1
        updateCards()
    }

    private fun updateCards() {
        if (topCard != null && bottomCard != null) {
            stream.value = TinderContactModel(
                cardTop = topCard!!,
                cardBottom = bottomCard!!
            )
        }
    }

}