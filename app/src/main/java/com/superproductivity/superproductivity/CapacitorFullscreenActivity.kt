package com.superproductivity.superproductivity

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import com.getcapacitor.BridgeActivity

class CapacitorFullscreenActivity : BridgeActivity() {
    private lateinit var javaScriptInterface: JavaScriptInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register Plugin
        this.registerPlugin(SUPPlugin::class.java)

        // Hide the action bar
        supportActionBar?.hide()

        // Register OnBackPressedCallback to handle back button press
        onBackPressedDispatcher.addCallback(this) {
            Log.v("TW", "onBackPressed ${bridge.webView.canGoBack()}")
            if (bridge.webView.canGoBack()) {
                bridge.webView.goBack()
            } else {
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
            }
        }

        // Handle keyboard visibility changes
        val rootView = findViewById<View>(android.R.id.content)
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.rootView.height

            val keypadHeight = screenHeight - rect.bottom
            if (keypadHeight > screenHeight * 0.15) {
                // Keyboard is opened
                callJavaScriptFunction("window.SUPAndroid.next.isKeyboardShown$('true')")
            } else {
                // Keyboard is closed
                callJavaScriptFunction("window.SUPAndroid.next.isKeyboardShown$('false')")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Log.v("TW", "CapacitorFullscreenActivity: onPause")
        callJavaScriptFunction("window.SUPAndroid.next.onPause$()")
    }

    override fun onResume() {
        super.onResume()
        Log.v("TW", "CapacitorFullscreenActivity: onResume")
        callJavaScriptFunction("window.SUPAndroid.next.onResume$()")
    }

    private fun callJavaScriptFunction(script: String) {
        bridge?.webView?.post {
            bridge?.webView?.evaluateJavascript(script, null)
        }
    }

    companion object {
        const val WINDOW_INTERFACE_PROPERTY: String = "SUPAndroid"
        const val WINDOW_PROPERTY_F_DROID: String = "SUPFDroid"
    }
}
