package com.nekobitlz.voice_editor.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nekobitlz.vkcup.commons.gone
import com.nekobitlz.vkcup.commons.visible
import com.nekobitlz.voice_editor.R
import com.nekobitlz.voice_editor.databinding.FragmentVoiceEditorBinding
import com.nekobitlz.voice_editor.utils.VoiceLogger.logDebug
import com.nekobitlz.voice_editor.view.custom.VoiceRecordButtonClickListener
import com.nekobitlz.voice_editor.view.viewmodel.VoiceEditorState
import com.nekobitlz.voice_editor.view.viewmodel.VoiceEditorViewModel


class VoiceEditorFragment : Fragment(R.layout.fragment_voice_editor) {

    private var _binding: FragmentVoiceEditorBinding? = null
    private val binding: FragmentVoiceEditorBinding
        get() = _binding!!

    private lateinit var viewModel: VoiceEditorViewModel

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentVoiceEditorBinding.bind(view)
        viewModel = ViewModelProvider(this).get(VoiceEditorViewModel::class.java)
        viewModel.state.observe(viewLifecycleOwner, {
            when (it) {
                VoiceEditorState.Start -> handleStartState()
                VoiceEditorState.Recorded -> handleRecordedState()
                VoiceEditorState.Recording -> handleRecordingState()
            }
        })
        binding.btnBin.setOnClickListener {
            viewModel.onNewRecordRequest()
        }
        binding.tvRecordNew.setOnClickListener {
            viewModel.onNewRecordRequest()
        }
        binding.btnVoiceRecord.listener = object : VoiceRecordButtonClickListener {
            override fun onButtonDown() {
                if (isPermissionGranted()) {
                    requestPermissions()
                } else {
                    logDebug("Start Recording")
                    viewModel.onButtonDown()
                }
            }

            override fun onButtonUp() {
                logDebug("stop Recording")
                if (!matchesWithBin(binding.btnVoiceRecord)) {
                    viewModel.onButtonUp()
                } else {
                    viewModel.onNewRecordRequest()
                }
                val v = binding.btnBin
                if (v.scaleX > 1f || v.scaleY > 1f) {
                    v.scaleX = 1f
                    v.scaleY = 1f
                }
            }

            override fun onMove() {
                val v = binding.btnBin
                if (v.scaleX <= 1f || v.scaleY <= 1f) {
                    v.scaleX = 1.25f
                    v.scaleY = 1.25f
                }
            }

        }
    }

    private fun matchesWithBin(v: View): Boolean {
        val location = IntArray(2)
        val binBtn = binding.btnBin
        binBtn.getLocationInWindow(location)
        val binX = location[0]
        val binY = location[1]
        if (v.y + v.height * 2 / 3 >= binY - binBtn.height
            && v.y <= binY + binBtn.height
            && v.x + v.width * 2 / 3 >= binX - binBtn.width
            && v.x <= binX + binBtn.width
        ) {
            return true
        }
        return false
    }

    private fun handleRecordingState() {
        startTimer()
        binding.apply {
            btnBin.visible()
            btnVoiceRecord.apply {
                isActivated = true
                movingAvailable = true
            }
        }
    }

    private fun handleRecordedState() {
        stopTimer()
        binding.apply {
            tvRecordNew.visible()
            btnVoiceRecord.setImageDrawable(
                ContextCompat.getDrawable(requireContext(), R.drawable.play_48)
            )
        }
    }

    private fun handleStartState() {
        stopTimer()
        binding.apply {
            tvTimer.gone()
            tvRecordHint.visible()
            tvRecordNew.gone()
            btnBin.gone()
            btnVoiceRecord.apply {
                visible()
                isActivated = false
                setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(), R.drawable.microphone_alt_28
                    )
                )
                movingAvailable = false
            }
        }
    }

    private fun isPermissionGranted() = ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.RECORD_AUDIO
    ) != PackageManager.PERMISSION_GRANTED

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    logDebug("Start Recording with permission")
                    viewModel.onPermissionGranted()
                }
                return
            }
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            REQUEST_PERMISSION
        )
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