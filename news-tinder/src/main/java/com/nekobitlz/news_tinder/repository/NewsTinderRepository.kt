package com.nekobitlz.news_tinder.repository

import com.vk.api.sdk.VK
import com.vk.sdk.api.newsfeed.NewsfeedService
import com.vk.sdk.api.newsfeed.dto.NewsfeedNewsfeedItem
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlin.math.abs

class NewsTinderRepository {

    fun getNews(): Observable<List<NewsItem>> = Observable.fromCallable {
        VK.executeSync(NewsfeedService().newsfeedGetRecommended())
    }
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .map { response ->
            response.items
                ?.filterIsInstance<NewsfeedNewsfeedItem.NewsfeedItemWallpost>()
                ?.map { post ->
                    NewsItem(
                        post,
                        response.profiles?.find { it.id == post.sourceId },
                        response.groups?.find { it.id == abs(post.sourceId ?: 0) }
                    )
                }
        }
}