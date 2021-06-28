package com.nekobitlz.news_tinder.repository

import com.vk.sdk.api.groups.dto.GroupsGroupFull
import com.vk.sdk.api.newsfeed.dto.NewsfeedNewsfeedItem
import com.vk.sdk.api.users.dto.UsersUserFull

data class NewsItem(
    val newsfeedItemWallpost: NewsfeedNewsfeedItem.NewsfeedItemWallpost,
    val user: UsersUserFull? = null,
    val group: GroupsGroupFull? = null,
)