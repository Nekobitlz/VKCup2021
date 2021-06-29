package com.nekobitlz.vkcup

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nekobitlz.news_tinder.NewsTinderActivity
import com.nekobitlz.vkcup.databinding.ActivityMainBinding
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toSecond.setOnClickListener {
            if (VK.isLoggedIn()) {
                toNewsTinder()
            } else {
                VK.login(this, arrayListOf(VKScope.FRIENDS, VKScope.WALL))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object: VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                toNewsTinder()
            }

            override fun onLoginFailed(errorCode: Int) {
                Toast.makeText(baseContext, "Login Failed", Toast.LENGTH_LONG).show()
            }
        }
        if (data == null || !VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun toNewsTinder() {
        val myIntent = Intent(this@MainActivity, NewsTinderActivity::class.java)
        startActivity(myIntent)
    }

}