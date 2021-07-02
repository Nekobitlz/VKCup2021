package com.nekobitlz.voice_editor.view.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nekobitlz.voice_editor.repository.VoicePlayer
import com.nekobitlz.voice_editor.repository.VoiceRecorder

class VoiceEditorViewModel(application: Application) : AndroidViewModel(application) {

    private val voiceRecorder = VoiceRecorder(application.applicationContext)
    private val voicePlayer = VoicePlayer()

    private val _state = MutableLiveData<VoiceEditorState>().apply {
        value = VoiceEditorState.Start
    }
    val state: LiveData<VoiceEditorState>
        get() = _state

    fun onButtonUp() {
        if (_state.value is VoiceEditorState.Recording) {
            voiceRecorder.stopRecording()
            _state.value = VoiceEditorState.Recorded
        }
    }

    fun onButtonDown() {
        if (_state.value is VoiceEditorState.Start) {
            voiceRecorder.startRecording()
            _state.value = VoiceEditorState.Recording
        } else if (_state.value is VoiceEditorState.Recorded) {
            voicePlayer.startPlaying(voiceRecorder.currentFileName ?: return)
        }
    }

    fun onPermissionGranted() {
        onButtonDown()
    }

    fun onNewRecordRequest() {
        _state.value = VoiceEditorState.Start
    }
}

sealed class VoiceEditorState {
    object Start : VoiceEditorState()
    object Recording : VoiceEditorState()
    object Recorded : VoiceEditorState()
}