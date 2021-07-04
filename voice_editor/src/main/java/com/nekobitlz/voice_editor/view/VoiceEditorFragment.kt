package com.nekobitlz.voice_editor.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nekobitlz.voice_editor.R
import com.nekobitlz.voice_editor.databinding.FragmentVoiceEditorBinding
import com.nekobitlz.voice_editor.view.viewmodel.VoiceEditorState
import com.nekobitlz.voice_editor.view.viewmodel.VoiceEditorViewModel

class VoiceEditorFragment : Fragment(R.layout.fragment_voice_editor) {

    private var _binding: FragmentVoiceEditorBinding? = null
    private val binding: FragmentVoiceEditorBinding
        get() = _binding!!

    private lateinit var viewModel: VoiceEditorViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentVoiceEditorBinding.bind(view)
        val fileName = arguments?.getString(PARAM_FILE_NAME)
        val wave = arguments?.getIntArray(PARAM_WAVE)!!
        viewModel = ViewModelProvider(this).get(VoiceEditorViewModel::class.java)
        viewModel.state.observe(viewLifecycleOwner, {
            when (it) {
                VoiceEditorState.Play -> handlePlayState()
                VoiceEditorState.Pause -> handlePauseState()
            }
        })
        if (savedInstanceState == null) {
            viewModel.onPlayRequest(fileName)
        }
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        binding.btnPlay.isActivated = true
        binding.btnPlay.setOnClickListener {
            viewModel.onPlayButtonClick(fileName)
        }
        binding.waveView.setWave(wave)
    }

    private fun handlePlayState() {
        binding.apply {
            btnPlay.setImageResource(R.drawable.pause_48)
        }
    }

    private fun handlePauseState() {
        binding.apply {
            btnPlay.setImageResource(R.drawable.play_48)
        }
    }

    companion object {
        private const val PARAM_FILE_NAME = "PARAM_FILE_NAME"
        private const val PARAM_WAVE = "PARAM_WAVE"

        fun newInstance(fileName: String, wave: IntArray) = VoiceEditorFragment().apply {
            arguments = Bundle().apply {
                putString(PARAM_FILE_NAME, fileName)
                putIntArray(PARAM_WAVE, wave)
            }
        }
    }
}