package com.lzx.easyweb.js

import org.json.JSONObject

class Performance {
    var navigationStart: Long = 0
    var unloadEventStart: Long = 0
    var unloadEventEnd: Long = 0
    var redirectStart: Long = 0
    var redirectEnd: Long = 0
    var fetchStart: Long = 0
    var domainLookupStart: Long = 0
    var domainLookupEnd: Long = 0
    var connectStart: Long = 0
    var connectEnd: Long = 0
    var secureConnectionStart: Long = 0
    var requestStart: Long = 0
    var responseStart: Long = 0
    var responseEnd: Long = 0
    var domLoading: Long = 0
    var domInteractive: Long = 0
    var domContentLoadedEventStart: Long = 0
    var domContentLoadedEventEnd: Long = 0
    var domComplete: Long = 0
    var loadEventStart: Long = 0
    var loadEventEnd: Long = 0

    fun getWebRequestTime(): String = (responseEnd - requestStart).toString() + "ms"

    fun getWebDomCompleteTime(): String = (domComplete - domInteractive).toString() + "ms"

    fun getWebDomLoadedTime(): String =
        (domContentLoadedEventEnd - navigationStart).toString() + "ms"

    fun getWebDomTotalTime(): String = (loadEventEnd - navigationStart).toString() + "ms"

    companion object {
        @JvmStatic
        fun toPerformance(json: String?): Performance {
            val performance = Performance()
            try {
                if (json == null) {
                    return performance
                }
                val obj = JSONObject(json)
                performance.navigationStart = obj.getLong("navigationStart")
                performance.unloadEventStart = obj.getLong("unloadEventStart")
                performance.unloadEventEnd = obj.getLong("unloadEventEnd")
                performance.redirectStart = obj.getLong("redirectStart")
                performance.redirectEnd = obj.getLong("redirectEnd")
                performance.fetchStart = obj.getLong("fetchStart")
                performance.domainLookupStart = obj.getLong("domainLookupStart")
                performance.domainLookupEnd = obj.getLong("domainLookupEnd")
                performance.connectStart = obj.getLong("connectStart")
                performance.connectEnd = obj.getLong("connectEnd")
                performance.secureConnectionStart = obj.getLong("secureConnectionStart")
                performance.requestStart = obj.getLong("requestStart")
                performance.responseStart = obj.getLong("responseStart")
                performance.responseEnd = obj.getLong("responseEnd")
                performance.domLoading = obj.getLong("domLoading")
                performance.domInteractive = obj.getLong("domInteractive")
                performance.domContentLoadedEventStart = obj.getLong("domContentLoadedEventStart")
                performance.domContentLoadedEventEnd = obj.getLong("domContentLoadedEventEnd")
                performance.domComplete = obj.getLong("domComplete")
                performance.loadEventStart = obj.getLong("loadEventStart")
                performance.loadEventEnd = obj.getLong("loadEventEnd")
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return performance
        }
    }


}