import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

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
  static const _channel = MethodChannel('com.example.data_channel');

  void sendDataToNative(String data, String platform) async {
    try {
      await _channel
          .invokeMethod('sendData', {'data': data, 'platform': platform});
    } on PlatformException catch (e) {
      debugPrint("Failed to send data to native: '${e.message}'.");
    }
  }

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
              ],
            ),
          ),
        ),
      ),
    );
  }

// _shareImage() async {
//   try {
//     final ByteData bytes = await rootBundle.load('assets/image.jpg');
//     final Uint8List list = bytes.buffer.asUint8List();
//
//     final tempDir = await getTemporaryDirectory();
//     final file = await new File('${tempDir.path}/image.jpg').create();
//     file.writeAsBytesSync(list);
//
//     final channel = const MethodChannel('channel:me.albie.share/share');
//     channel.invokeMethod('shareFile', 'image.jpg');
//   } catch (e) {
//     print('Share error: $e');
//   }
// }
}
