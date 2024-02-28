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
        body: Center(
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 20.0),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                TextField(
                  controller: inputController,
                  decoration: const InputDecoration(hintText: 'Input Text'),
                ),
                ElevatedButton.icon(
                  onPressed: () => sendDataToNative(
                      inputController.text.toString(), 'Facebook'),
                  icon: const Icon(Icons.share),
                  label: const Text('Facebook'),
                ),
                ElevatedButton.icon(
                  onPressed: () => sendDataToNative(
                      inputController.text.toString(), 'WhatsApp'),
                  icon: const Icon(Icons.share),
                  label: const Text('WhatsApp'),
                ),
                ElevatedButton.icon(
                  onPressed: () => sendDataToNative(
                      inputController.text.toString(), 'Instagram'),
                  icon: const Icon(Icons.share),
                  label: const Text('Instagram'),
                ),
                ElevatedButton.icon(
                  onPressed: () => shareImage,
                  icon: const Icon(Icons.share),
                  label: const Text('Share Image'),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  /// Share Text
  void sendDataToNative(String data, String platform) async {
    try {
      await _shareTextChannel
          .invokeMethod('sendData', {'data': data, 'platform': platform});
    } on PlatformException catch (e) {
      debugPrint("Failed to send data to native: '${e.message}'.");
    }
  }

  /// Share Image
  shareImage() async {
    try {
      final ByteData bytes = await rootBundle.load('assets/flutter.jpg');
      final Uint8List list = bytes.buffer.asUint8List();
      final tempDir = await getTemporaryDirectory();
      final file = await File('${tempDir.path}/flutter.jpg').create();
      file.writeAsBytesSync(list);
      _shareImageChannel.invokeMethod('shareFile', 'flutter.jpg');
    } catch (e) {
      debugPrint('Share error: $e');
    }
  }
}
