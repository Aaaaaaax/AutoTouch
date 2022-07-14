package com.example.autotouch

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.Path
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class TouchAcsyService : AccessibilityService() {
    private val TAG = "AliAccessibilityService"
    private var thread: Thread? = null

    // 服务创建/启动时回调
    override fun onCreate() {
        Log.i("t", "=== accessibility create")
        val intent = Intent("accessibilityServerStart")
        sendBroadcast(intent)
        init()
    }

    //服务中断时的回调
    override fun onInterrupt() {
        Log.d(TAG, "===onInterrupt")
        thread?.interrupt()
    }

    //接收到系统发送AccessibilityEvent时的回调
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.d(TAG, "===onAccessibilityEvent")
    }

    private fun init() {
        thread = Thread {
            val receiver = myReciver()
            val filter = IntentFilter("click")
            registerReceiver(receiver,filter)
        }
        thread?.start()
    }

    private inner class myReciver: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            clickGesture(100f,250f)
            Log.i(TAG,"123123")
        }

    }

    // 使用手势模拟点击
    private fun clickGesture(x: Float, y: Float) {
        val path = Path()
        path.moveTo(x, y)
        val gdBuilder = GestureDescription.Builder()
        val gestureDesc = gdBuilder.addStroke(GestureDescription.StrokeDescription(path, 0, 10)).build()
        dispatchGesture(gestureDesc, null, null)
    }

    // 使用手势模拟用户划动
    private fun gesture(path: Path) {
        var gdBuilder = GestureDescription.Builder()
        var gestureDesc = gdBuilder.addStroke(GestureDescription.StrokeDescription(path, 0, 10)).build()
        dispatchGesture(gestureDesc, null, null)
    }

}












