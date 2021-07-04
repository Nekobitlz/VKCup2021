package com.nekobitlz.voice_editor.repository

import android.content.Context
import android.media.MediaRecorder
import android.os.Environment
import android.os.Handler
import android.os.Looper
import com.nekobitlz.voice_editor.utils.VoiceLogger.logDebug
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.max

class VoiceRecorder(private val context: Context) {

    var currentFileName: String? = null
        private set
    var audioWave = ArrayList<Int>(WAVE_MAX_VALUES)
        private set
    var audioWaveDisplayed = ArrayList<Int>(WAVE_MAX_VALUES)
        private set
    private var recorder: MediaRecorder? = null
    private val currentFormat = 1
    private val outputFormats = intArrayOf(
        MediaRecorder.OutputFormat.MPEG_4,
        MediaRecorder.OutputFormat.THREE_GPP
    )
    private val fileExts = arrayOf(VOICE_RECORDER_FILE_EXT_MP4, VOICE_RECORDER_FILE_EXT_3GP)
    private val handler = Handler(Looper.myLooper()!!)
    private var timerInterval: Long = WAVE_TIMER_START_INTERVAL
    private var audioWaveChanged = false
    private var skipSamplesRate = 1
    private var recordingSampleIdx = 0
    private var recordingSample = 0

    private val waveUpdateRunnable = object : Runnable {
        override fun run() {
            if (recorder == null) {
                handler.removeCallbacks(this)
                return
            }
            val maxAmplitude = abs(recorder!!.maxAmplitude)
            var db = if (maxAmplitude == 0) -45.0 else 18.0 * log10(maxAmplitude.toDouble() / 32768)
            if (db < -45.0) {
                db = -45.0
            }
            logDebug("max atr value = $maxAmplitude db $db")
            val power = ((db + 45.0) * 32768.0 / 45.0).toInt()
            audioWaveDisplayed.add(power)
            if (audioWaveDisplayed.size >= WAVE_MAX_VALUES_DISPLAYED) {
                audioWaveDisplayed.removeAt(0)
            }
            audioWaveChanged = true
            recordingSample = power.coerceAtLeast(recordingSample)
            ++recordingSampleIdx
            if (recordingSampleIdx % skipSamplesRate == 0) {
                audioWave.add(recordingSample)
                recordingSample = 0
                if (audioWave.size >= WAVE_MAX_VALUES) {
                    for (i in 0 until WAVE_MAX_VALUES / 2) {
                        audioWave[i] = max(audioWave[2 * i], audioWave[2 * i + 1])
                    }
                    audioWave.subList(WAVE_MAX_VALUES / 2, audioWave.size).clear()
                    skipSamplesRate *= 2
                }
            }
            handler.postDelayed(this, timerInterval)
        }
    }

    @Throws(IllegalStateException::class)
    fun startRecording() {
        currentFileName = getFilename()
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION)
            setOutputFormat(outputFormats[currentFormat])
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(currentFileName)
            setOnErrorListener(errorListener)
            setOnInfoListener(infoListener)
        }
        try {
            recorder?.prepare()
            recorder?.start()
            handler.postDelayed(waveUpdateRunnable, timerInterval)
        } catch (e: IllegalStateException) {
            logDebug(e.message ?: "ise")
            throw IllegalStateException()
        } catch (e: IOException) {
            logDebug(e.message ?: "io")
            throw IllegalStateException()
        }
    }

    fun stopRecording() {
        recorder?.stop()
        recorder?.reset()
        recorder?.release()
        recorder = null
    }

    private fun getFilename(): String {
        val filepath: String = context.getExternalFilesDir(Environment.DIRECTORY_PODCASTS)?.path
            ?: Environment.getExternalStorageDirectory().path
        logDebug(filepath)
        val file = File(filepath, VOICE_RECORDER_FOLDER)
        if (!file.exists()) {
            file.mkdirs()
        }
        return file.absolutePath.toString() + "/" + System.currentTimeMillis() + fileExts[currentFormat]
    }

    private val errorListener = MediaRecorder.OnErrorListener { mr, what, extra ->
        logDebug("Error: $what, $extra")
    }

    private val infoListener = MediaRecorder.OnInfoListener { mr, what, extra ->
        logDebug("Warning: $what, $extra")
    }

    companion object {
        private const val VOICE_RECORDER_FILE_EXT_3GP = ".3gp"
        private const val VOICE_RECORDER_FILE_EXT_MP4 = ".mp4"
        private const val VOICE_RECORDER_FOLDER = "VoiceRecorder"
        private const val WAVE_TIMER_START_INTERVAL = 100L
        private const val WAVE_MAX_VALUES = 100
        private const val WAVE_MAX_VALUES_DISPLAYED = 2000
    }
}