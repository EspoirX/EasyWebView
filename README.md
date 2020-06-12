# EasyWebView

##使用说明

在使用 EasyWeb 之前记得先配置一下上下文，你可以把这个操作放在 Application 中。

```kotlin
  CacheConfig.instance.context = this
```
CacheConfig 是一个单例，作用是用来配置一些关于缓存相关的配置，下面会说到。因为内部获取缓存文件夹的时候需要一个上下文，
所以这里就先让它获得一下上下文，不然在使用缓存功能的时候会空指针哦。

## 最简单的使用
```kotlin
    EasyWeb.with(this)
        .ready()
        .loadUrl("https://www.bilibili.com/")
```
EasyWeb 至少要通过调用 with 方法和 ready 方法才能发起一个 WebView 请求。上面是最简单的调用，with 传入的是当前的 Activity。
当然，你也可以这样分开写：
```kotlin
    val easyWeb = EasyWeb.with(this)
        .ready()
    easyWeb.loadUrl("https://www.bilibili.com/")
```

## 指定加载布局
```kotlin
    EasyWeb.with(this)
        .setWebParent(webViewLayout)
        .ready()
        .loadUrl("https://www.bilibili.com/")
```
调用 setWebParent 方法可以指定 WebView 的父布局，传入的是一个 ViewGroup，setWebParent 第二个参数是 LayoutParams，默认宽高都是
MATCH_PARENT

## 生命周期管理
```kotlin
    EasyWeb.with(this)
        .setWebParent(webViewLayout)
        .lifecycle(this@MainActivity.lifecycle)
        .ready()
        .loadUrl("https://www.bilibili.com/")
```
通过 lifecycle 方法传入当前 Activity 的 lifecycle ，即可自动管理 WebView 的生命周期，内部实现了 onResume，onPause 和
onDestroy 时的处理，再也不用担心内存泄露。

## WebView 缓存
```kotlin
    EasyWeb.with(this)
        .setWebParent(webViewLayout)
        .lifecycle(this@MainActivity.lifecycle)
        .setWebCacheMode(WebCacheMode.DEFAULT)
        .ready()
        .loadUrl("https://www.bilibili.com/")
```
通过 setWebCacheMode 方法即可快速实现 WebView 的缓存机制，WebCacheMode 是一个枚举，提供三种模式，NOCACHE，DEFAULT，CACHE_RES。
分别代表着不使用缓存，使用 WebView 自带的缓存机制，以及使用自定义的缓存机制。

1. WebView 自带的缓存机制 , 即通过 WebSettings 配置 WebView 自带的缓存实现。具体的代码逻辑在 BaseWebViewSetting 这个类里面。
2. 自定义的缓存机制，主要是缓存网页中的资源文件，通过拦截 shouldInterceptRequest 方法，然后使用 OkHttp 把资源下载下来，EasyWeb
对这些文件使用了三级缓存，并通过 Lru 算法管理，即 LruCache，DiskLruCache。

缓存配置可以通过 CacheConfig 这个类，它是一个单例。如果你想缓存指定的文件，或者想某几个 url 不缓存，你可以这样配置：
```kotlin
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
```

## 与 JS 的交互

```kotlin
    EasyWeb.with(this)
        .setWebParent(webViewLayout)
        .lifecycle(this@MainActivity.lifecycle)
        .setWebCacheMode(WebCacheMode.DEFAULT)
        .ready()
        .addJsInterface(JsInterfaceTest(), "WebViewJavascriptBridge")
        .loadUrl("https://www.bilibili.com/")

    //当然也可以这样子
    val easyWeb = EasyWeb.with(this@MainActivity)
        .setWebParent(webViewLayout)
        .lifecycle(this@MainActivity.lifecycle)
        .setWebCacheMode(WebCacheMode.DEFAULT)
        .ready()
        .loadUrl(url)

    easyWeb?.addJsInterface(JsInterfaceTest(), "WebViewJavascriptBridge")

    class JsInterfaceTest : BaseJavascriptInterface() {
        @JavascriptInterface
        fun callAndroidMethod(param: String) {
            Toast.makeText(activity, "我是js里面传过来的参数:$param", Toast.LENGTH_SHORT).show()
        }
    }
```

与 Js 的交互，比如 Js 调用客户端的方法，可以通过 addJsInterface 方法实现，addJsInterface 方法需要在 ready 方法后
调用。该方法有两个参数，一个是 object，一个是 name，想必不需要过多的解析，object 需要继承 BaseJavascriptInterface 这个
类，为什么？因为继承了 BaseJavascriptInterface，你就能很方便的拿到当前的 Activity 和 WebView 哦：

```kotlin
open class BaseJavascriptInterface {
    var activity: Activity? = null
    var webView: IProxyWebView? = null
}
```

那么在开发时，如果想要拿到某个 js 回调怎么办？你可以通过 getJsInterface 方法去实现：
```kotlin
   val easyWeb = EasyWeb.with(this)
        .setWebParent(webViewLayout)
        .lifecycle(this@MainActivity.lifecycle)
        .setWebCacheMode(WebCacheMode.DEFAULT)
        .ready()
        .addJsInterface(JsInterfaceTest(), "WebViewJavascriptBridge")
        .loadUrl("https://www.bilibili.com/")

    val jsObj = easyWeb?.getJsInterface("WebViewJavascriptBridge")
    Log.i("MainActivity", "jsObj = $jsObj")
```

## 长按事件
```kotlin
 EasyWeb.with(this@MainActivity)
    .setWebParent(webViewLayout)
    .lifecycle(this@MainActivity.lifecycle)
    .setWebCacheMode(WebCacheMode.DEFAULT)
    .setOnWebViewLongClick(object : OnWebViewLongClick {
        override fun onClick(type: Int, hitTestResult: Any?): Boolean {
            return false
        }

        override fun onClickWebImage(imgUrl: String?): Boolean {
            return false
        }
    })
    .ready()
    .loadUrl("https://www.bilibili.com/")
```
通过设置 setOnWebViewLongClick 方法，实现 OnWebViewLongClick 接口即可实现 WebView 的长按事件，该接口有两个方法，
onClickWebImage，是长按图片的时候的回调，你可以在这里实现下载图片等逻辑，onClick，就是除了长按图片之外的其他长按类型
事件，你可以通过两个参数去区分具体是什么类型。

## WebChromeClient 与 setWebViewClient
```kotlin
     EasyWeb.with(this@MainActivity)
        .setWebParent(webViewLayout)
        .lifecycle(this@MainActivity.lifecycle)
        .setWebCacheMode(WebCacheMode.DEFAULT)
        .setWebChromeClient(object : WebChromeClient() {

        })
        .setWebViewClient(object : WebViewClient() {

        })
        .ready()
        .loadUrl("https://www.bilibili.com/")
```
设置 WebChromeClient 与 setWebViewClient 很简单，就是通过 setWebChromeClient 和 setWebViewClient 两个方法，跟
使用 WebView 时一样，但 EasyWeb 内部通过代理已经实现好了两个默认的 WebChromeClient 和 WebViewClient，并且把一些重定向
等问题解决了，用户不需要去管理，只需要跟正常使用一样使用 WebChromeClient 和 WebViewClient 即可。

## 配置 WebSettings
```kotlin
    EasyWeb.with(this@MainActivity)
        .setWebParent(webViewLayout)
        .setWebViewSetting(MyWebSetting())
        .ready()
        .loadUrl("https://www.bilibili.com/")

    class MyWebSetting : BaseWebViewSetting() {
        override fun setUpSetting(webSettings: WebSettings?) {
            //...
        }
    }
```
通过 setWebViewSetting 方法，即可配置自己的 WebSettings，你只需要继承 BaseWebViewSetting 这个类，并且在方法 setUpSetting 中
拿到 webSettings 对象，即可实现你自己的 WebSettings，当然，EasyWeb 默认的 WebSettings 在 DefaultWebSettings 这个类中
配置，并且已经移除了有风险的隐藏接口：
```kotlin
    removeJavascriptInterface("searchBoxJavaBridge_")
    removeJavascriptInterface("accessibility")
    removeJavascriptInterface("accessibilityTraversal")
```

## 添加加载出错时的界面
```kotlin
   val errorView = TextView(this)
    errorView.layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )
    errorView.gravity = Gravity.CENTER
    errorView.text = "出错了"

    EasyWeb.with(this@MainActivity)
        .setWebParent(webViewLayout)
        .addErrorView(errorView)
        .ready()
        .loadUrl("https://www.bilibili.com/")
```
通过 addErrorView 即可添加你的网页加载失败时显示的界面。addErrorView 第二个参数是一个 view 的 id，作用是当点击这个 view 时，
会触发 reload 操作。如果不传，或者传 -1，那么点击整个 view 是会触发 reload 操作。
addErrorView 除了传入 view 之外，还可以传入 layoutId，和 url。

## IUrlLoader 和 IJsLoader
```kotlin
    EasyWeb.with(this@MainActivity)
        .setWebParent(webViewLayout)
        .setUrlLoader(MyUrlLooader())
        .setJsLoader(MyJsLooader())
        .ready()
        .loadUrl("https://www.bilibili.com/")
```
在调用 WebView 的 loadUrl 或者其他加载网页的方法时，很多时候需要做一些其他操作，所以 EasyWeb 提供了两个接口专门去隔离这些操作，
让你可以在调用实际的 loadUrl 前做一些自己的逻辑，并且达到解耦的效果。通过 setUrlLoader 和 setJsLoader 方法配置即可。
可以通过查看代码中的 IUrlLoader 和 IJsLoader 文件查看这两个接口的定义以及默认的实现。

# 其他方法
EasyWeb 除了以上一些配置，通过 EasyWeb 对象你也可以获得很多东西，具体可以查看 EasyWeb 这个类的实现哦。

EasyWeb 是参考了很多优秀的 WebView 库之后产生的，感谢这些开源库。期待你的意见，喜欢就点个 star 把。^_^