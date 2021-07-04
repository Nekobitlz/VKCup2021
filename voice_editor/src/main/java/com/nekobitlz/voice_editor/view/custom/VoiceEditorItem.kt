package com.nekobitlz.voice_editor.view.custom

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes

data class VoiceEditorItem(
    @IdRes val id: Int,
    @DrawableRes val icon: Int,
    @StringRes val title: Int,
    var isSelected: Boolean = false,
)