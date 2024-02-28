package com.example.share_intent

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel


class MainActivity : FlutterActivity() {
    private val CHANNEL = "samples.flutter.dev/battery"
    private val DATA_CHANNEL = "com.example.data_channel" // New channel for data

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
            // This method is invoked on the main thread.
            call, result ->
            if (call.method == "getBatteryLevel") {
                val batteryLevel = getBatteryLevel()

                if (batteryLevel != -1) {
                    result.success(batteryLevel)
                } else {
                    result.error("UNAVAILABLE", "Battery level not available.", null)
                }
            } else {
                result.notImplemented()
            }
        }

        // Method channel for receiving data from Flutter
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, DATA_CHANNEL).setMethodCallHandler { call, result ->
            if (call.method == "sendData") {
                val data = call.argument<String>("data")
                if (data != null) {
                    val platform = call.argument<String>("platform")
                            ?: "facebook" // Default platform
                    handleDataFromFlutter(data, platform)
                    result.success("Data received successfully")
                } else {
                    result.error("UNAVAILABLE", "Data not available.", null)
                }
            } else {
                result.notImplemented()
            }
        }

    }


    private fun getBatteryLevel(): Int {
        val batteryLevel: Int
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        } else {
            val intent = ContextWrapper(applicationContext).registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            batteryLevel = intent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100 / intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        }

        return batteryLevel
    }


    private fun handleDataFromFlutter(data: String, platform: String) {
        Log.d("Platform", "Selected platform: $platform")
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, data)

        when (platform) {
            "Facebook" -> {
                if (VERSION.SDK_INT >= VERSION_CODES.DONUT) {
                    shareIntent.setPackage("com.facebook.katana")
                } // Package name for Facebook
            }

            "WhatsApp" -> {
                if (VERSION.SDK_INT >= VERSION_CODES.DONUT) {
                    shareIntent.setPackage("com.whatsapp")
                } // Package name for WhatsApp
            }

            "Instagram" -> {
                if (VERSION.SDK_INT >= VERSION_CODES.DONUT) {
                    shareIntent.setPackage("com.instagram.android")
                } // Package name for Instagram
            }

            else -> {
                // If the platform is not recognized, show a toast or handle it accordingly
                Toast.makeText(context, "Unsupported platform", Toast.LENGTH_SHORT).show()
                return // Exit the function if platform is unsupported
            }
        }

        try {
            startActivity(shareIntent)
        } catch (e: ActivityNotFoundException) {
            // Handle case when app is not installed
            Toast.makeText(context, "App not installed", Toast.LENGTH_SHORT).show()
        }
    }

}