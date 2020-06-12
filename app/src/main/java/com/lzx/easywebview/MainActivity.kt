package com.lzx.easywebview

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.lzx.easyweb.EasyWeb
import com.lzx.easyweb.cache.CacheConfig
import com.lzx.easyweb.cache.WebCacheMode
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permission
import com.qw.soul.permission.bean.Permissions
import com.qw.soul.permission.callbcak.CheckRequestPermissionsListener
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var url = "https://github.com/"

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




        EasyWeb.with(this@MainActivity)
            .setWebParent(webViewLayout)
            .debug(true)
            .lifecycle(this@MainActivity.lifecycle)
            .setWebCacheMode(WebCacheMode.CACHE_RES)
            .ready()
            .loadUrl(url)
    }


}