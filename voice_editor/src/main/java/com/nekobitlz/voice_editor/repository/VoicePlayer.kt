package com.nekobitlz.voice_editor.repository

import android.media.MediaPlayer
import android.media.PlaybackParams
import android.media.audiofx.NoiseSuppressor
import android.os.Build
import androidx.annotation.RequiresApi
import com.nekobitlz.voice_editor.utils.VoiceLogger.logDebug


class VoicePlayer {
    private var mediaPlayer: MediaPlayer? = null
    private var noiseSuppressor: NoiseSuppressor? = null

    private var noiseEnabled: Boolean = false
    private var roboticEnabled: Boolean = false

    fun startPlaying(fileName: String, onComplete: () -> Unit) {
        logDebug("Start playing")
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(fileName)
            }
            try {
                mediaPlayer?.setOnCompletionListener {
                    releasePlayer()
                    onComplete()
                }
                mediaPlayer?.prepare()
                logDebug("Started")
            } catch (e: Exception) {
                logDebug(e.message ?: "ise")
            }
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                addRoboticPitch()
            }
            mediaPlayer?.start()
            noiseSuppressor = mediaPlayer?.audioSessionId?.let {
                NoiseSuppressor.create(it).apply {
                    enabled = noiseEnabled
                }
            }
        } catch (e: Exception) {
            logDebug(e.message ?: "ise")
        }
    }

    private fun releasePlayer() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        noiseSuppressor?.release()
        noiseSuppressor = null
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun addRoboticPitch() {
        val params = PlaybackParams()
        params.pitch = if (roboticEnabled) PITCH_SIZE else 1.0f
        mediaPlayer?.playbackParams = params
    }

    fun toggleNoise(isEnabled: Boolean, onError: () -> Unit) {
        if (NoiseSuppressor.isAvailable()) {
            noiseEnabled = isEnabled
            noiseSuppressor?.enabled = isEnabled
        } else {
            onError()
        }
    }

    fun pause() {
        mediaPlayer?.pause()
        logDebug("Paused")
    }

    fun toggleRobotVoice(isEnabled: Boolean, onError: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            roboticEnabled = isEnabled
            addRoboticPitch()
        } else {
            onError()
        }
    }

    fun release() {
        releasePlayer()
    }

    companion object {
        private const val PITCH_SIZE = 2f
    }
}