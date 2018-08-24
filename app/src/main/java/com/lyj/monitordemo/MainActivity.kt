package com.lyj.monitordemo

import android.content.Intent
import android.os.Bundle
import h.com.mylibrary.LibActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        text.setOnClickListener { view ->
            startActivity(Intent(this@MainActivity, LibActivity::class.java))
        }
    }
}
