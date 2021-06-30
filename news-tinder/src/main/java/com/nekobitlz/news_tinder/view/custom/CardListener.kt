package com.nekobitlz.news_tinder.view.custom

import android.view.View

interface CardListener {
    fun onLeftSwipe(position: Int, model: Any)
    fun onRightSwipe(position: Int, model: Any)
    fun onItemShow(position: Int, model: Any)
    fun onSwipeCancel(view: View, model: Any)
    fun onSwipeCompleted()
    fun onLeftMove(view: View)
    fun onRightMove(view: View)
}