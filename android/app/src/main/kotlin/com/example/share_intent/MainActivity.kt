package com.example.share_intent

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Parcelable
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.content.FileProvider
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import java.io.File

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
                    if (call.method == "shareImage") {
                        val platform = call.argument<String>("platform")
                        val imageUrl = call.argument<String>("imageUrl")

                        if (platform != null && imageUrl != null) {
                            shareImage(platform, imageUrl)
                            result.success("Image shared successfully")
                        } else {
                            result.error("INVALID_PARAMETERS", "Missing platform or image URL", null)
                        }
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
        val file = File(imageUrl)

        if (!file.exists()) {
            Toast.makeText(context, "Image file not found", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"

        val uri = FileProvider.getUriForFile(this, applicationContext.packageName + ".provider", file)

        intent.putExtra(Intent.EXTRA_STREAM, uri as Parcelable)

        when (platform) {
            "Facebook" -> if (VERSION.SDK_INT >= VERSION_CODES.DONUT) {
                intent.setPackage("com.facebook.katana")
            }

            "WhatsApp" -> if (VERSION.SDK_INT >= VERSION_CODES.DONUT) {
                intent.setPackage("com.whatsapp")
            }

            "Instagram" -> if (VERSION.SDK_INT >= VERSION_CODES.DONUT) {
                intent.setPackage("com.instagram.android")
            }

            else -> {
                Toast.makeText(context, "Unsupported platform", Toast.LENGTH_SHORT).show()
                return
            }
        }

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "App not installed", Toast.LENGTH_SHORT).show()
        }
    }

}
