package com.lzx.easyweb.code

import android.os.Build
import androidx.annotation.RequiresApi

interface IJsLoader {
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun loadJs(method: String?, callback: WebValueCallback<String?>?, vararg params: Any?)

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun loadJs(method: String?, callback: WebValueCallback<String?>?)

    fun loadJs(method: String?, vararg params: Any?)
}
