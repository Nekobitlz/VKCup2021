package com.nekobitlz.video_editor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nekobitlz.video_editor.view.WelcomeEditorFragment

class VideoEditorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_VoiceEditor)
        setContentView(R.layout.activity_container)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, WelcomeEditorFragment(), null)
                .commit()
        }
    }
}