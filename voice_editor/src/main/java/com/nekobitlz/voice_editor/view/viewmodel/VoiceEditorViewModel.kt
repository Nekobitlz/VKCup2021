package com.nekobitlz.voice_editor.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nekobitlz.voice_editor.repository.VoicePlayer

class VoiceEditorViewModel : ViewModel() {

    private val player = VoicePlayer()

    private val _state = MutableLiveData<VoiceEditorState>()
    val state: LiveData<VoiceEditorState>
        get() = _state

    fun onPlayRequest(fileName: String?) {
        startPlaying(fileName ?: return)
        _state.value = VoiceEditorState.Play
    }

    fun onPlayButtonClick(fileName: String?) {
        if (state.value is VoiceEditorState.Play) {
            player.pause()
            _state.value = VoiceEditorState.Pause
        } else {
            startPlaying(fileName ?: return)
            _state.value = VoiceEditorState.Play
        }
    }

    private fun startPlaying(fileName: String): Boolean {
        player.startPlaying(fileName) {
            _state.value = VoiceEditorState.Pause
        }
        return false
    }
}

sealed class VoiceEditorState {
    object Play : VoiceEditorState()
    object Pause : VoiceEditorState()
}