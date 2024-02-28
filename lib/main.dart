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
  static const platform = MethodChannel('samples.flutter.dev/battery');
  static const _channel = MethodChannel('com.example.data_channel');
  String _batteryLevel = 'Unknown battery level.';

  Future<void> _getBatteryLevel() async {
    String batteryLevel;
    try {
      final result = await platform.invokeMethod<int>('getBatteryLevel');
      batteryLevel = 'Battery level at $result % .';
    } on PlatformException catch (e) {
      batteryLevel = "Failed to get battery level: '${e.message}'.";
    }

    setState(() {
      _batteryLevel = batteryLevel;
    });
  }

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
                ElevatedButton(
                  onPressed: _getBatteryLevel,
                  child: const Text('Get Battery Level'),
                ),
                Text(_batteryLevel),
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
}
