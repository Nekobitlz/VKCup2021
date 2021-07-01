package com.nekobitlz.voice_editor.repository

import android.content.Context
import android.media.MediaRecorder
import android.os.Environment
import com.nekobitlz.voice_editor.utils.VoiceLogger.logDebug
import java.io.File
import java.io.IOException
import java.lang.ref.WeakReference

class VoiceRecorder(private val contextRef: WeakReference<Context>) {

    private val AUDIO_RECORDER_FILE_EXT_3GP = ".3gp"
    private val AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4"
    private val AUDIO_RECORDER_FOLDER = "AudioRecorder"
    private var recorder: MediaRecorder? = null
    private val currentFormat = 0
    private val output_formats = intArrayOf(
        MediaRecorder.OutputFormat.MPEG_4,
        MediaRecorder.OutputFormat.THREE_GPP
    )
    private val file_exts = arrayOf(AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP)

    fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(output_formats[currentFormat])
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(getFilename())
            setOnErrorListener(errorListener)
            setOnInfoListener(infoListener)
        }
        try {
            recorder?.prepare()
            recorder?.start()
        } catch (e: IllegalStateException) {
            logDebug(e.message ?: "ise")
        } catch (e: IOException) {
            logDebug(e.message ?: "io")
        }
    }

    fun stopRecording() {
        recorder?.stop()
        recorder?.reset()
        recorder?.release()
        recorder = null
    }

    private fun getFilename(): String {
        val filepath: String = contextRef.get()
            ?.getExternalFilesDir(Environment.DIRECTORY_PODCASTS)?.path
            ?: Environment.getExternalStorageDirectory().path
        logDebug(filepath)
        val file = File(filepath, AUDIO_RECORDER_FOLDER)
        if (!file.exists()) {
            file.mkdirs()
        }
        return file.absolutePath.toString() + "/" + System.currentTimeMillis() + file_exts[currentFormat]
    }

    private val errorListener = MediaRecorder.OnErrorListener { mr, what, extra ->
        logDebug("Error: $what, $extra")
    }

    private val infoListener = MediaRecorder.OnInfoListener { mr, what, extra ->
        logDebug("Warning: $what, $extra")
    }
}