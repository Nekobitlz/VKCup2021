package com.nekobitlz.voice_editor.repository

import android.media.MediaPlayer
import com.nekobitlz.voice_editor.utils.VoiceLogger


class VoicePlayer {
    private var mediaPlayer: MediaPlayer? = null

    fun startPlaying(fileName: String) {
        VoiceLogger.logDebug("Start playing")
        mediaPlayer = MediaPlayer().apply {
            setDataSource(fileName)
        }
        try {
            /*if (playerCompletion != null) {
                mediaPlayer.setOnCompletionListener(playerCompletion)
            }*/
            mediaPlayer?.prepare()
            mediaPlayer?.start()
            VoiceLogger.logDebug("Started")
        } catch (e: Exception) {
            VoiceLogger.logDebug(e.message ?: "ise")
        }
    }

}