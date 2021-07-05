package com.nekobitlz.video_editor.view

import android.R.attr
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.nekobitlz.video_editor.R
import com.nekobitlz.video_editor.databinding.FragmentWelcomeEditorBinding


class WelcomeEditorFragment : Fragment(R.layout.fragment_welcome_editor) {

    private var _binding: FragmentWelcomeEditorBinding? = null
    private val binding: FragmentWelcomeEditorBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*activity?.window?.let {
            it.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            it.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            it.statusBarColor = ContextCompat.getColor(requireContext(), R.color.welcome_background_grey)
        }*/
        /*val windowInsetController = ViewCompat.getWindowInsetsController(activity?.window?.decorView ?: return)
        windowInsetController?.isAppearanceLightStatusBars = true
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.welcome_background_grey)*/
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentWelcomeEditorBinding.bind(view)
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        binding.btnPickVideo.setOnClickListener {
            val intent = Intent()
            intent.type = "video/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, getString(R.string.choose_video)),
                REQUEST_TAKE_GALLERY_VIDEO
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TAKE_GALLERY_VIDEO && resultCode == RESULT_OK) {
            val video = data?.data ?: return
            parentFragmentManager.beginTransaction()
                .replace(id, VideoEditorFragment.newInstance(video))
                .addToBackStack(null)
                .commit()
        }
    }

    companion object {
        private const val REQUEST_TAKE_GALLERY_VIDEO = 101
    }
}