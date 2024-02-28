import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:path_provider/path_provider.dart';

void main() {
  runApp(const ShareIntent());
}

class ShareIntent extends StatefulWidget {
  const ShareIntent({super.key});

  @override
  State<ShareIntent> createState() => _ShareIntentState();
}

class _ShareIntentState extends State<ShareIntent> {
  final TextEditingController inputController = TextEditingController();
  static const _shareTextChannel =
      MethodChannel('com.example.share_text_channel');
  static const _shareImageChannel =
      MethodChannel('com.example.share_image_channel');

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        body: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 20.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: [
              TextField(
                controller: inputController,
                decoration: const InputDecoration(hintText: 'Input Text'),
              ),
              Wrap(
                runSpacing: 8,
                children: [
                  _buildShareButton(
                      'Share Text to Facebook', () => _shareText('Facebook')),
                  _buildShareButton(
                      'Share Text to Instagram', () => _shareText('Instagram')),
                  _buildShareButton(
                      'Share Text to WhatsApp', () => _shareText('WhatsApp')),
                ],
              ),
              Wrap(
                runSpacing: 8,
                children: [
                  _buildShareButton(
                      'Share Image to Facebook', () => _shareImage('Facebook')),
                  _buildShareButton('Share Image to Instagram',
                      () => _shareImage('Instagram')),
                  _buildShareButton(
                      'Share Image to WhatsApp', () => _shareImage('WhatsApp')),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildShareButton(String label, VoidCallback onPressed) {
    return ElevatedButton(
      onPressed: onPressed,
      child: Text(label),
    );
  }

  void _shareText(String platform) async {
    try {
      await _shareTextChannel.invokeMethod('sendData',
          {'data': inputController.text.toString(), 'platform': platform});
    } on PlatformException catch (e) {
      debugPrint("Failed to send data to native: '${e.message}'.");
    }
  }

  Future<void> _shareImage(String platform) async {
    try {
      final ByteData bytes = await rootBundle.load('assets/flutter.jpg');
      final Uint8List list = bytes.buffer.asUint8List();
      final tempDir = await getTemporaryDirectory();
      final file = await File('${tempDir.path}/flutter.jpg').create();
      await file.writeAsBytes(list);

      // Invoke method channel with platform and image URL
      await _shareImageChannel.invokeMethod(
          'shareImage', {'platform': platform, 'imageUrl': file.path});
    } catch (e) {
      debugPrint('Share error: $e');
    }
  }
}
