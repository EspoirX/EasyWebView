package com.lzx.easyweb.js

interface IJsInterface {
    fun addJsInterfaces(maps: Map<String, BaseJavascriptInterface>): IJsInterface

    fun addJsInterface(obj: BaseJavascriptInterface, name: String): IJsInterface

    fun getJsInterface(name: String): BaseJavascriptInterface?

    fun removeJsInterface(name: String): IJsInterface

    fun clearJsInterface(): IJsInterface
}