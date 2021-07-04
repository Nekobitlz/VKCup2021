package com.nekobitlz.voice_editor.view.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import com.nekobitlz.voice_editor.R
import com.nekobitlz.voice_editor.utils.LinearInterpolation.interpolateArray
import com.nekobitlz.voice_editor.view.custom.WaveRepository.MAX_VALUE
import com.nekobitlz.voice_editor.view.custom.WaveRepository.waveData
import java.util.*

class WaveView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {
    private val DEFAULT_ITEM_COLOR = ContextCompat.getColor(context, R.color.record_button_activated)

    private val wavePath = Path()
    private val linePaint = Paint()
    private val itemWidth: Int
    private var originalData: IntArray = waveData
    private var measuredData: IntArray? = null

    init {
        val displayMetrics = context.resources.displayMetrics
        val itemWidthFromAttr = (TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            DEFAULT_ITEM_WIDTH_DP.toFloat(),
            displayMetrics
        ) + 0.5f).toInt()
        val itemColorFromAttr = DEFAULT_ITEM_COLOR

        itemWidth = itemWidthFromAttr
        linePaint.style = Paint.Style.STROKE
        linePaint.color = itemColorFromAttr
        linePaint.strokeWidth = itemWidthFromAttr.toFloat()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        val itemCount = (width - paddingStart - paddingEnd + itemWidth) / (itemWidth * 2)
        measuredData = interpolateArray(originalData, itemCount)

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        val measuredData = measuredData ?: return

        wavePath.reset()
        val measuredHeight = measuredHeight - paddingTop - paddingBottom
        var currentX = paddingStart
        for (data in measuredData) {
            val height = data.toFloat() / MAX_VALUE * measuredHeight
            val startY = measuredHeight.toFloat() / 2f - height / 2f + paddingTop
            val endY = startY + height
            wavePath.moveTo(currentX.toFloat(), startY)
            wavePath.lineTo(currentX.toFloat(), endY)
            currentX += itemWidth * 2
        }
        canvas.drawPath(wavePath, linePaint)
    }

    fun setWave(wave: List<Int>) {
        setWave(wave.toIntArray())
    }

    fun setWave(wave: IntArray) {
        originalData = wave
        invalidate()
    }

    companion object {
        private const val DEFAULT_ITEM_WIDTH_DP = 4
    }
}

object WaveRepository {

    const val MAX_VALUE = 32768
    private const val WAVE_LENGTH = 200

    val waveData: IntArray
        get() {
            val data = IntArray(WAVE_LENGTH)
            val r = Random()
            for (i in data.indices) {
                val value: Int = r.nextInt(MAX_VALUE + 1)
                data[i] = value
            }
            return data
        }
}
