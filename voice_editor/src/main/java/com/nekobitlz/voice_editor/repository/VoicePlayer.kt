package com.nekobitlz.voice_editor.repository

import android.media.MediaPlayer
import android.media.audiofx.NoiseSuppressor
import com.nekobitlz.voice_editor.utils.VoiceLogger


class VoicePlayer {
    private var mediaPlayer: MediaPlayer? = null

    fun startPlaying(fileName: String, onComplete: () -> Unit) {
        VoiceLogger.logDebug("Start playing")
        mediaPlayer = MediaPlayer().apply {
            setDataSource(fileName)
        }
        try {
            mediaPlayer?.setOnCompletionListener {
                onComplete()
            }
            mediaPlayer?.prepare()
            mediaPlayer?.start()
            VoiceLogger.logDebug("Started")
        } catch (e: Exception) {
            VoiceLogger.logDebug(e.message ?: "ise")
        }
    }

    fun toggleNoise(isEnabled: Boolean) {
        val noiseSuppressor = NoiseSuppressor.create(mediaPlayer?.audioSessionId ?: return)
        noiseSuppressor.enabled = isEnabled
    }

    fun pause() {
        mediaPlayer?.pause()
    }

}