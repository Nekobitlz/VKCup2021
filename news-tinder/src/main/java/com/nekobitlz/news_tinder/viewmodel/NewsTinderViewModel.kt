package com.nekobitlz.news_tinder.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nekobitlz.news_tinder.repository.NewsTinderCard
import com.nekobitlz.news_tinder.repository.NewsTinderRepository

class NewsTinderViewModel : ViewModel() {

    private val repository = NewsTinderRepository()

    private val _models = MutableLiveData<List<NewsTinderCard>>()
    val models: LiveData<List<NewsTinderCard>>
        get() = _models

    init {
        repository.getNews().map { items ->
            items.map {
                NewsTinderCard(
                    it.newsfeedItemWallpost.postId,
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
            _models.postValue(it)
        }
    }
}