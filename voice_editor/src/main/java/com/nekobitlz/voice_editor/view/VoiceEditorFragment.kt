package com.nekobitlz.voice_editor.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nekobitlz.voice_editor.R
import com.nekobitlz.voice_editor.databinding.FragmentVoiceEditorBinding
import com.nekobitlz.voice_editor.view.adapter.VoiceEditorAdapter
import com.nekobitlz.voice_editor.view.custom.VoiceEditorItem
import com.nekobitlz.voice_editor.view.viewmodel.ErrorEvent
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
        viewModel.errorEvent.observe(viewLifecycleOwner) {
            val textRes = when (it) {
                ErrorEvent.NoiseSuppressionNotSupported -> R.string.noise_suppression_not_supported
                ErrorEvent.RoboticVoiceNotSupported -> R.string.robotic_voice_not_supported
            }
            Toast.makeText(context, textRes, Toast.LENGTH_LONG).show()
        }
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
        val items = listOf(
            VoiceEditorItem(
                R.id.id_remove_noise,
                R.drawable.icon_remove_noise,
                R.string.remove_noise
            ),
            VoiceEditorItem(
                R.id.id_add_robotic_voice,
                R.drawable.icon_add_robotic_voice,
                R.string.add_robotic_voice
            ),
            VoiceEditorItem(
                R.id.id_add_sound_effect,
                R.drawable.icon_add_sound_effect,
                R.string.add_sound_effect
            ),
        )
        binding.actionsBottomPanel.rvActions.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = VoiceEditorAdapter(items) {
                it.isSelected = !it.isSelected
                when (it.id) {
                    R.id.id_remove_noise -> viewModel.onToggleNoiseClick(it.isSelected)
                    R.id.id_add_robotic_voice -> viewModel.onToggleRoboticVoiceClick(it.isSelected)
                    R.id.id_add_sound_effect -> viewModel.onAddSoundEffectClick(it.isSelected)
                }
                adapter?.notifyItemChanged(items.indexOf(it))
            }
        }
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