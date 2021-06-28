package com.nekobitlz.vkcup.commons

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.nekobitlz.vkcup.vkcup.R

sealed class EmptyViewState {
    object None : EmptyViewState()
    object Loading : EmptyViewState()
    data class Error(val text: String? = null, val retryText: String?, val onRetryClick: (() -> Unit)? = null) : EmptyViewState()
}

class BaseEmptyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    protected val progressBar by bindView<ProgressBar>(R.id.progress_bar)
    protected val textView by bindView<TextView>(R.id.tv_text)
    protected val retryButton by bindView<Button>(R.id.btn_retry)

    var state: EmptyViewState = EmptyViewState.None
        set(value) = when (value) {
            EmptyViewState.None -> gone()
            EmptyViewState.Loading -> {
                visible()
                progressBar.visible()
                textView.gone()
                retryButton.gone()
            }
            is EmptyViewState.Error -> {
                visible()
                progressBar.gone()
                textView.visible()
                if (!value.text.isNullOrEmpty()) {
                    textView.text = value.text
                }
                if (!value.retryText.isNullOrEmpty()) {
                    retryButton.text = value.retryText
                }
                retryButton.setVisible(value.onRetryClick != null)
                retryButton.setOnClickListener {
                    value.onRetryClick?.invoke()
                }
            }
        }

    init {
        inflate(context, R.layout.base_empty_view, this)
        state = EmptyViewState.None
    }
}