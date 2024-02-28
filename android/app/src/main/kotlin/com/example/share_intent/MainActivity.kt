package com.example.share_intent

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel


class MainActivity : FlutterActivity() {
    private val SHARE_TEXT_CHANNEL = "com.example.share_text_channel"

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        // Method channel for receiving data from Flutter
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, SHARE_TEXT_CHANNEL).setMethodCallHandler { call, result ->
            if (call.method == "sendData") {
                val data = call.argument<String>("data")
                if (data != null) {
                    val platform = call.argument<String>("platform")
                            ?: "facebook" // Default platform
                    shareText(data, platform)
                    result.success("Data received successfully")
                } else {
                    result.error("UNAVAILABLE", "Data not available.", null)
                }
            } else {
                result.notImplemented()
            }
        }

    }

    // Share Text on Social Platform from Flutter
    private fun shareText(data: String, platform: String) {
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

    // Share Image on Social Platform from Flutter

}