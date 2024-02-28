package com.example.share_intent

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.widget.Toast
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {
    private val SHARE_TEXT_CHANNEL = "com.example.share_text_channel"
    private val SHARE_IMAGE_CHANNEL = "com.example.share_image_channel"

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, SHARE_TEXT_CHANNEL)
                .setMethodCallHandler { call, result ->
                    if (call.method == "sendData") {
                        val data = call.argument<String>("data")
                        val platform = call.argument<String>("platform") ?: "facebook"
                        data?.let { shareText(it, platform) }
                        result.success("Data received successfully")
                    } else {
                        result.notImplemented()
                    }
                }

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, SHARE_IMAGE_CHANNEL)
                .setMethodCallHandler { call, result ->
                    if (call.method == "sendImage") {
                        val imageUrl = call.argument<String>("imageUrl")
                        val platform = call.argument<String>("platform") ?: "facebook"
                        imageUrl?.let { shareImage(platform, it) }
                        result.success("Image data received successfully")
                    } else {
                        result.notImplemented()
                    }
                }
    }

    private fun shareText(data: String, platform: String) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, data)

        when (platform) {
            "Facebook" -> if (VERSION.SDK_INT >= VERSION_CODES.DONUT) {
                shareIntent.setPackage("com.facebook.katana")
            }

            "WhatsApp" -> if (VERSION.SDK_INT >= VERSION_CODES.DONUT) {
                shareIntent.setPackage("com.whatsapp")
            }

            "Instagram" -> if (VERSION.SDK_INT >= VERSION_CODES.DONUT) {
                shareIntent.setPackage("com.instagram.android")
            }

            else -> {
                Toast.makeText(context, "Unsupported platform", Toast.LENGTH_SHORT).show()
                return
            }
        }

        try {
            startActivity(shareIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "App not installed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareImage(platform: String, imageUrl: String) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "image/*"
        val uri = Uri.parse(imageUrl)
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)

        when (platform) {
            "Facebook" -> if (VERSION.SDK_INT >= VERSION_CODES.DONUT) {
                shareIntent.setPackage("com.facebook.katana")
            }

            "WhatsApp" -> if (VERSION.SDK_INT >= VERSION_CODES.DONUT) {
                shareIntent.setPackage("com.whatsapp")
            }

            "Instagram" -> if (VERSION.SDK_INT >= VERSION_CODES.DONUT) {
                shareIntent.setPackage("com.instagram.android")
            }

            else -> {
                Toast.makeText(context, "Unsupported platform", Toast.LENGTH_SHORT).show()
                return
            }
        }

        try {
            startActivity(shareIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "App not installed", Toast.LENGTH_SHORT).show()
        }
    }
}
