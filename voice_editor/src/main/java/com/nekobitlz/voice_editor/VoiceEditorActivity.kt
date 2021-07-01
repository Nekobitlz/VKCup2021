package com.nekobitlz.voice_editor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nekobitlz.voice_editor.view.VoiceEditorFragment

class VoiceEditorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, VoiceEditorFragment(), null)
                .commit()
        }
    }
}