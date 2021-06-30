package com.nekobitlz.news_tinder.view.custom

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.content.res.Resources
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageButton
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.nekobitlz.news_tinder.R
import com.nekobitlz.vkcup.commons.SimpleAnimatorListener

val Int.px: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()
val Int.dp: Int get() = (this / Resources.getSystem().displayMetrics.density).toInt()

fun View.pulse() {
    val animatorSet = AnimatorSet()
    val object1: ObjectAnimator = ObjectAnimator.ofFloat(this, View.SCALE_Y, 1f, 0.9f, 1f)
    val object2: ObjectAnimator = ObjectAnimator.ofFloat(this, View.SCALE_X, 1f, 0.9f, 1f)
    animatorSet.playTogether(object1, object2)
    animatorSet.duration = 150
    animatorSet.interpolator = AccelerateInterpolator()
    animatorSet.start()
}

fun ImageButton.pulse(@ColorRes iconColor: Int) {
    val imageList = ColorStateList.valueOf(ContextCompat.getColor(context, iconColor))
    animate().scaleX(1.3f)
        .scaleY(1.3f)
        .setDuration(150)
        .withStartAction {
            isActivated = true
            imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorWhite))
        }.withEndAction {
            animate().scaleX(1f)
                .scaleY(1f)
                .setDuration(150).withEndAction {
                    isActivated = false
                    imageTintList = imageList
                }.start()
        }
        .start()
}

fun View.pulseOnlyUp() {
    val animatorSet = AnimatorSet()
    val object1: ObjectAnimator = ObjectAnimator.ofFloat(this, View.SCALE_Y, 0.95f, 1f, 1f)
    val object2: ObjectAnimator = ObjectAnimator.ofFloat(this, View.SCALE_X, 0.95f, 1f, 1f)
    animatorSet.playTogether(object1, object2)
    animatorSet.duration = 250
    animatorSet.interpolator = AccelerateInterpolator()
    animatorSet.start()
}

fun View.scale(f: Float) {
    val animatorSet = AnimatorSet()
    val object1: ObjectAnimator = ObjectAnimator.ofFloat(this, View.SCALE_Y, 1f, 1f + f, 1f + f)
    val object2: ObjectAnimator = ObjectAnimator.ofFloat(this, View.SCALE_X, 1f, 1f + f, 1f + f)
    animatorSet.playTogether(object1, object2)
    animatorSet.duration = 250
    animatorSet.interpolator = AccelerateInterpolator()
    animatorSet.start()
}
