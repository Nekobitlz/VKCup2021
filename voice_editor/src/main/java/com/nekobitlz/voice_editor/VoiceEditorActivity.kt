package com.nekobitlz.voice_editor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nekobitlz.voice_editor.view.VoiceRecorderFragment

class VoiceEditorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, VoiceRecorderFragment(), null)
                .commit()
        }
    }
}