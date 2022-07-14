package com.example.autotouch

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {

    val SDK_INT: Int = Build.VERSION.SDK_INT
    private val requestFloatWindowPermissionCode = 100
    private val autoTouchAcsyServiceClassPath =
        "com.example.autotouch/com.example.autotouch.TouchAcsyService"
    private var floatState = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i("123","---onCreate")

        findViewById<Button>(R.id.floatStart).setOnClickListener(this)
        findViewById<Button>(R.id.accessStart).setOnClickListener(this)

        val receiver = myReciver()
        val filter = IntentFilter("accessibilityServerStart")
        registerReceiver(receiver,filter)

        if (!checkAccessibilityServiceEnable(autoTouchAcsyServiceClassPath)) {
            val accessStateTxt = findViewById<TextView>(R.id.accessState)
            accessStateTxt.text = "未启用"
            accessStateTxt.setTextColor(Color.RED)
            val i = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(i)
        }

        val floatBtn = Button(this)
        floatBtn.text = "模拟点击"
        floatBtn.setOnClickListener {
            val intent = Intent("click")
            sendBroadcast(intent)
            Toast.makeText(this, "已模拟", Toast.LENGTH_SHORT).show()
        }
        addFloatWin(floatBtn)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.floatStart -> {
                if (!floatState) {
                    when (SDK_INT) {
                        in 25..99 -> {
                            // 7.0以上
                            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                            intent.data = Uri.parse("package:$packageName")
                            startActivityForResult(intent, requestFloatWindowPermissionCode)
                        }
                        else -> {
                            // 7.0以下暂无法使用
                        }
                    }
                }
            }
        }
    }

    private inner class myReciver: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val accessStateTxt = findViewById<TextView>(R.id.accessState)
            accessStateTxt.text = "已启用"
            accessStateTxt.setTextColor(Color.GREEN)
        }

    }

    private fun checkAccessibilityServiceEnable(serviceClassPath: String): Boolean {
        var ok = 0
        try {
            ok = Settings.Secure.getInt(
                applicationContext.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            )
        } catch (e: Exception) {
            Log.e("1","获取无障碍服务已启用列表失败 \n ${e.message}")
        }
        var ms = TextUtils.SimpleStringSplitter(':')
        if (ok == 1) {
            var settingValue = Settings.Secure.getString(
                applicationContext.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            if (settingValue.isNotEmpty()) {
                ms.setString(settingValue)
                while (ms.hasNext()) {
                    val accessibilityService = ms.next()
                    if (accessibilityService.equals(serviceClassPath, ignoreCase = true)) {
                        Log.i("123", "find ok")
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun addFloatWin(view: View) {
        val windowManager = application.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.addView(view, WindowManager.LayoutParams().apply {
            type = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1 -> WindowManager.LayoutParams.TYPE_PHONE
                else -> WindowManager.LayoutParams.TYPE_TOAST
            }
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            gravity = Gravity.START or Gravity.BOTTOM
            format = PixelFormat.TRANSLUCENT
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestFloatWindowPermissionCode
            && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1
        ) {
            if (Settings.canDrawOverlays(this)) {
                floatState = true
                val floatStateTxt = findViewById<TextView>(R.id.floatState)
                floatStateTxt.text = "已启用"
                floatStateTxt.setTextColor(Color.GREEN)
                Log.i("ABC", "用户给予悬浮窗权限了")
            } else {
                val floatStateTxt = findViewById<TextView>(R.id.floatState)
                floatStateTxt.text = "未启用"
                floatStateTxt.setTextColor(Color.RED)
                Log.i("ABC", "用户没给悬浮窗权限")
            }
        }
    }

    private var time = 0L
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if(System.currentTimeMillis() - time > 1500){
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show()
                time = System.currentTimeMillis()
            }else{
                moveTaskToBack(false)
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onRestart() {
        super.onRestart()
        Log.i("123","--- onrestart")
    }

    override fun onStart() {
        super.onStart()
        Log.i("123","---onstart")
    }

    override fun onResume() {
        super.onResume()
        Log.i("123","---onresume")
    }

    override fun onPause() {
        super.onPause()
        Log.i("123","---onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.i("123","---onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("123","---onDestroy")
    }
}
