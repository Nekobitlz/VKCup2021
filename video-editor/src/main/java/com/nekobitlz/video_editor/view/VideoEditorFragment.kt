package com.nekobitlz.video_editor.view

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import androidx.fragment.app.Fragment
import com.nekobitlz.video_editor.R
import com.nekobitlz.video_editor.databinding.FragmentVideoEditorBinding

class VideoEditorFragment : Fragment(R.layout.fragment_video_editor) {

    private var _binding: FragmentVideoEditorBinding? = null
    private val binding: FragmentVideoEditorBinding
        get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentVideoEditorBinding.bind(view)
        val uri = arguments?.getParcelable(PARAM_VIDEO_URI) as Uri?
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        binding.toolbar.findViewById<View>(R.id.btn_save).setOnClickListener {
            activity?.onBackPressed()
        }
        binding.videoView.apply {
            setVideoURI(uri)
            setMediaController(null)
            requestFocus(0)
            start()
        }
        binding.btnPlay.setOnClickListener {
            val videoView = binding.videoView
            if (videoView.isPlaying) {
                videoView.pause()
            } else {
                videoView.start()
            }
        }

    }

    companion object {

        private const val PARAM_VIDEO_URI = "PARAM_VIDEO_URI"

        fun newInstance(videoUri: Uri): VideoEditorFragment = VideoEditorFragment().apply {
            arguments = Bundle().apply {
                putParcelable(PARAM_VIDEO_URI, videoUri)
            }
        }
    }
}