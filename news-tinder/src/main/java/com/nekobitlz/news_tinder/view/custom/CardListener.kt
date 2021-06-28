package com.nekobitlz.news_tinder.view.custom

interface CardListener {
    fun onLeftSwipe(position: Int, model: Any)
    fun onRightSwipe(position: Int, model: Any)
    fun onItemShow(position: Int, model: Any)
    fun onSwipeCancel(position: Int, model: Any)
    fun onSwipeCompleted()
}