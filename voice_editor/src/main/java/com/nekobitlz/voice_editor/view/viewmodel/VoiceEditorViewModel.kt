package com.nekobitlz.voice_editor.view.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nekobitlz.vkcup.commons.SingleLiveData
import com.nekobitlz.voice_editor.repository.VoicePlayer

class VoiceEditorViewModel(application: Application) : AndroidViewModel(application) {

    private val player = VoicePlayer()

    private val _state = MutableLiveData<VoiceEditorState>()
    val state: LiveData<VoiceEditorState>
        get() = _state

    private val _errorEvent = SingleLiveData<ErrorEvent>()
    val errorEvent: LiveData<ErrorEvent>
        get() = _errorEvent

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

    fun onToggleNoiseClick(isSelected: Boolean) {
        player.toggleNoise(isSelected, onError = {
            _errorEvent.value = ErrorEvent.NoiseSuppressionNotSupported
        })
    }

    fun onToggleRoboticVoiceClick(isSelected: Boolean) {
        player.toggleRobotVoice(isSelected, onError = {
            _errorEvent.value = ErrorEvent.RoboticVoiceNotSupported
        })
    }

    fun onAddSoundEffectClick(isSelected: Boolean) {

    }

    override fun onCleared() {
        super.onCleared()
        player?.release()
    }
}

sealed class VoiceEditorState {
    object Play : VoiceEditorState()
    object Pause : VoiceEditorState()
}

sealed class ErrorEvent {
    object NoiseSuppressionNotSupported : ErrorEvent()
    object RoboticVoiceNotSupported : ErrorEvent()
}