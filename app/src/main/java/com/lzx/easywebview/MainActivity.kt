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

    //    private var url = "file:///android_asset/demo.html"
    private var url = "https://www.bilibili.com/"
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
        EasyWeb.with(this@MainActivity)
            .setWebParent(webViewLayout)
            .ready()
            .loadUrl("https://www.bilibili.com/")

        easyWeb?.addJsInterface(JsInterfaceTest(), "WebViewJavascriptBridge")


        //添加指定缓存文件
        CacheConfig.instance
            .addCacheFile("html")
            .addCacheFile("js")
            .addCacheFile("css")
            .addCacheFile("jpg")
            .addCacheFile("png")
            .addCacheFile("gif")
        //添加缓存白名单
        CacheConfig.instance
            .addIgnoreUrl("baidu")
            .addIgnoreUrl("taobao")
            .addIgnoreUrl("wxpay")
            .addIgnoreUrl("alipay")

    }

    override fun onBackPressed() {
        if (easyWeb?.goBack() == false) {
            super.onBackPressed()
        }
    }


}