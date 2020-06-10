package com.lzx.easywebview

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.lzx.easyweb.EasyWeb
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var url = "https://www.baidu.com/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val errorView = TextView(this)
        errorView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        errorView.gravity = Gravity.CENTER
        errorView.text = "出错了"

//        webViewLayout.postDelayed({
        EasyWeb.with(this)
            .setWebParent(webViewLayout)
            .debug(true)
            .ready()
            .loadUrl(url)
//        }, 10000)

    }
}