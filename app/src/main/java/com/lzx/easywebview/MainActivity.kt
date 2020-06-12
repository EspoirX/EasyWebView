package com.lzx.easywebview

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.lzx.easyweb.EasyWeb
import com.lzx.easyweb.cache.CacheConfig
import com.lzx.easyweb.cache.WebCacheMode
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var url = "https://github.com/"
    private var easyWeb: EasyWeb? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        CacheConfig.instance.context = this

        val errorView = TextView(this)
        errorView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        errorView.gravity = Gravity.CENTER
        errorView.text = "出错了"


        easyWeb = EasyWeb.with(this@MainActivity)
            .setWebParent(webViewLayout)
            .debug(true)
            .lifecycle(this@MainActivity.lifecycle)
            .setWebCacheMode(WebCacheMode.CACHE_RES)
            .ready()
            .loadUrl(url)
    }

    override fun onBackPressed() {
        if (easyWeb?.goBack() == false) {
            super.onBackPressed()
        }
    }
}