package com.nekobitlz.voice_editor.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.nekobitlz.vkcup.commons.gone
import com.nekobitlz.vkcup.commons.lazyUnsychronized
import com.nekobitlz.vkcup.commons.visible
import com.nekobitlz.voice_editor.R
import com.nekobitlz.voice_editor.databinding.FragmentVoiceEditorBinding
import com.nekobitlz.voice_editor.repository.VoiceRecorder
import com.nekobitlz.voice_editor.utils.VoiceLogger.logDebug
import java.lang.ref.WeakReference


class VoiceEditorFragment : Fragment(R.layout.fragment_voice_editor) {

    private var _binding: FragmentVoiceEditorBinding? = null
    private val binding: FragmentVoiceEditorBinding
        get() = _binding!!

    private var startTime: Long = 0
    private val timerHandler: Handler = Handler(Looper.myLooper()!!)
    private val timer: Runnable = object : Runnable {
        override fun run() {
            val millis: Long = System.currentTimeMillis() - startTime
            var seconds = (millis / 1000).toInt()
            val minutes = seconds / 60
            seconds %= 60
            binding.tvTimer.text = String.format("%d:%02d", minutes, seconds)
            timerHandler.postDelayed(this, 500)
        }
    }
    private val voiceRecorder by lazyUnsychronized {
        VoiceRecorder(WeakReference(requireContext()))
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentVoiceEditorBinding.bind(view)
        binding.btnVoiceRecord.setOnTouchListener(OnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.RECORD_AUDIO
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ),
                            REQUEST_PERMISSION
                        )
                    } else {
                        logDebug("Start Recording")
                        startRecording()
                    }
                    return@OnTouchListener true
                }
                MotionEvent.ACTION_UP -> {
                    logDebug("stop Recording")
                    stopRecording()
                    return@OnTouchListener true
                }
            }
            false
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    logDebug("Start Recording with permission")
                    startRecording()
                }
                return
            }
        }
    }

    private fun startRecording() {
        voiceRecorder.startRecording()
        startTimer()
        binding.btnVoiceRecord.isActivated = true
    }

    private fun stopRecording() {
        voiceRecorder.stopRecording()
        stopTimer()
        binding.btnVoiceRecord.isActivated = false
    }

    private fun startTimer() {
        startTime = System.currentTimeMillis()
        timerHandler.postDelayed(timer, 0)
        binding.tvTimer.visible()
        binding.tvRecordHint.gone()
    }

    private fun stopTimer() {
        timerHandler.removeCallbacks(timer)
    }

    companion object {
        private const val REQUEST_PERMISSION = 101
    }
}