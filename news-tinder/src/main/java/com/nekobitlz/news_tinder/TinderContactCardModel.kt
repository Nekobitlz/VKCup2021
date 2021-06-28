package com.nekobitlz.news_tinder

data class TinderContactCardModel(
    val name: String?,
    val title: String?,
    val date: Int?,
    val imageUrl: String?,
    val avatarUrl: String?,
    val likeCount: Int,
    val commentCount: Int,
    val shareCount: Int,
) {

    fun getSaveCount(count: Int): String = if (count > 0) count.toString() else ""
}