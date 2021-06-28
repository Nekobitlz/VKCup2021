package com.nekobitlz.vkcup.commons

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView

fun View.visible() {
    if (!isVisible()) {
        visibility = View.VISIBLE
    }
}

fun View.gone() {
    if (!isGone()) {
        visibility = View.GONE
    }
}

fun View.hide() {
    if (!isHidden()) {
        visibility = View.INVISIBLE
    }
}

fun View.setVisible(condition: Boolean) {
    when {
        condition -> visible()
        else -> gone()
    }
}

fun View.setVisibleOrHide(condition: Boolean) {
    when {
        condition -> visible()
        else -> hide()
    }
}

fun View.toggleVisibility() {
    if (isGone()) {
        visible()
    } else {
        gone()
    }
}

fun View.isVisible() = visibility == View.VISIBLE

fun View.isGone() = visibility == View.GONE

fun View.isHidden() = visibility == View.INVISIBLE

fun TextView.setTextOrHide(text: String?) {
    if (text.isNullOrEmpty()) {
        hide()
    } else {
        setText(text)
        visible()
    }
}

fun TextView.setTextOrGone(text: String?) {
    if (text.isNullOrEmpty()) {
        gone()
    } else {
        setText(text)
        visible()
    }
}

fun SimpleDraweeView.setImageOrHide(url: String?) {
    if (url.isNullOrEmpty()) {
        hide()
    } else {
        setImageURI(url)
        visible()
    }
}

fun SimpleDraweeView.setImageOrGone(url: String?) {
    if (url.isNullOrEmpty()) {
        gone()
    } else {
        setImageURI(url)
        visible()
    }
}

fun <ViewT : View> View.bindView(idRes: Int): Lazy<ViewT> {
    return lazyUnsychronized {
        findViewById<ViewT>(idRes)
    }
}

val View.layoutInflater: LayoutInflater
    get() = LayoutInflater.from(context)