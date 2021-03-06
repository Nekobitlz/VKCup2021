package com.nekobitlz.voice_editor.view.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nekobitlz.vkcup.commons.SingleLiveData
import com.nekobitlz.voice_editor.repository.VoiceRecorder
import java.lang.Exception

class VoiceRecorderViewModel(application: Application) : AndroidViewModel(application) {

    private val voiceRecorder = VoiceRecorder(application.applicationContext)

    private val _state = MutableLiveData<VoiceRecorderState>().apply {
        value = VoiceRecorderState.Start
    }
    val state: LiveData<VoiceRecorderState>
        get() = _state

    private val _openEditorEvent = SingleLiveData<OpenPhotoEditorEvent>()
    val openEditorEvent: LiveData<OpenPhotoEditorEvent>
        get() = _openEditorEvent

    private val _updateTimerEvent = MutableLiveData<String>()
    val updateTimerEvent: LiveData<String>
        get() = _updateTimerEvent

    private var startTime: Long = 0

    fun onButtonUp() {
        if (_state.value is VoiceRecorderState.Recording) {
            voiceRecorder.stopRecording()
            _state.value = VoiceRecorderState.Recorded
        }
    }

    fun onButtonDown() {
        if (_state.value is VoiceRecorderState.Start) {
            try {
                voiceRecorder.startRecording()
                _state.value = VoiceRecorderState.Recording
            } catch (e: Exception) {
                _state.value = VoiceRecorderState.Error
            }
        } else if (_state.value is VoiceRecorderState.Recorded) {
            if (voiceRecorder.audioWave.isNotEmpty()) {
                _openEditorEvent.value = OpenPhotoEditorEvent(
                    voiceRecorder.currentFileName ?: return,
                    voiceRecorder.audioWave.toIntArray()
                )
            }
        }
    }

    fun onPermissionGranted() {
        onButtonDown()
    }

    fun onNewRecordRequest() {
        _state.value = VoiceRecorderState.Start
    }

    fun onNextTime() {
        val millis: Long = System.currentTimeMillis() - startTime
        var seconds = (millis / 1000).toInt()
        val minutes = seconds / 60
        seconds %= 60
        _updateTimerEvent.value = String.format("%d:%02d", minutes, seconds)
    }

    fun onStartTimer() {
        startTime = System.currentTimeMillis()
    }
}

sealed class VoiceRecorderState {
    object Start : VoiceRecorderState()
    object Recording : VoiceRecorderState()
    object Recorded : VoiceRecorderState()
    object Error : VoiceRecorderState()
}

class OpenPhotoEditorEvent(
    val fileName: String,
    val wave: IntArray
)

class UpdateWaveEvent(
    val wave: List<Int>
)