package com.nekobitlz.voice_editor.view.custom

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.OvershootInterpolator
import androidx.appcompat.widget.AppCompatImageButton

class VoiceRecordButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleRes: Int = 0,
) : AppCompatImageButton(context, attrs, defStyleRes) {

    var listener: VoiceRecordButtonClickListener? = null
    var movingAvailable = false

    private var oldX = 0f
    private var oldY = 0f
    private var newX = 0f
    private var newY = 0f
    private var dX = 0f
    private var dY = 0f

    private var resetX = -1f
    private var resetY = -1f

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (resetX == -1f || resetY == -1f) {
            resetX = x
            resetY = y
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean = when (event.action) {
        MotionEvent.ACTION_DOWN -> {
            listener?.onButtonDown()
            oldX = event.x
            oldY = event.y
            true
        }
        MotionEvent.ACTION_UP -> {
            listener?.onButtonUp()
            animate()
                .x(resetX)
                .y(resetY)
                .setInterpolator(OvershootInterpolator())
                .duration = 300
            true
        }
        MotionEvent.ACTION_MOVE -> {
            listener?.onMove()
            if (movingAvailable) {
                newX = event.x
                newY = event.y
                dX = newX - oldX
                dY = newY - oldY
                x = x.plus(dX)
                y = y.plus(dY)
            }
            movingAvailable
        }
        else -> super.onTouchEvent(event)
    }
}
