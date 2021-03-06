package com.nekobitlz.news_tinder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nekobitlz.news_tinder.view.NewsTinderFragment

class NewsTinderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, NewsTinderFragment(), null)
                .commit()
        }
    }
}