package com.nekobitlz.taxi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nekobitlz.taxi.view.TaxiFragment
import org.osmdroid.config.Configuration

class TaxiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().userAgentValue = packageName

        setContentView(R.layout.activity_container)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, TaxiFragment(), null)
                .commit()
        }
    }
}