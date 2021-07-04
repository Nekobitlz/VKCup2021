package com.nekobitlz.voice_editor.repository

import android.media.MediaPlayer
import android.media.audiofx.NoiseSuppressor
import com.nekobitlz.voice_editor.utils.VoiceLogger


class VoicePlayer {
    private var mediaPlayer: MediaPlayer? = null
    private var noiseSuppressor: NoiseSuppressor? = null

    fun startPlaying(fileName: String, onComplete: () -> Unit) {
        VoiceLogger.logDebug("Start playing")
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(fileName)
            }
            try {
                mediaPlayer?.setOnCompletionListener {
                    mediaPlayer?.stop()
                    mediaPlayer?.release()
                    mediaPlayer = null
                    noiseSuppressor?.release()
                    noiseSuppressor = null
                    onComplete()
                }
                mediaPlayer?.prepare()
                VoiceLogger.logDebug("Started")
            } catch (e: Exception) {
                VoiceLogger.logDebug(e.message ?: "ise")
            }
        }
        try {
            mediaPlayer?.start()
            noiseSuppressor = mediaPlayer?.audioSessionId?.let { NoiseSuppressor.create(it) }
        } catch (e: Exception) {
            VoiceLogger.logDebug(e.message ?: "ise")
        }
    }

    fun toggleNoise(isEnabled: Boolean, onError: () -> Unit) {
        if (NoiseSuppressor.isAvailable()) {
            noiseSuppressor?.enabled = isEnabled
        } else {
            onError()
        }
    }

    fun pause() {
        mediaPlayer?.pause()
        VoiceLogger.logDebug("Paused")
    }

}