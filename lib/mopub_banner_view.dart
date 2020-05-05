import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

typedef void MoPubBannerViewCreatedCallback(MoPubBannerViewCreatedController controller);

class MoPubBannerView extends StatefulWidget {
  const MoPubBannerView({
    Key key,
    this.onMoPubBannerViewCreated,
  }) : super(key: key);

  final MoPubBannerViewCreatedCallback onMoPubBannerViewCreated;

  @override
  State<StatefulWidget> createState() => _TextViewState();
}

class _TextViewState extends State<MoPubBannerView> {
  @override
  Widget build(BuildContext context) {
    if (defaultTargetPlatform == TargetPlatform.android) {
      return AndroidView(
        viewType: 'com.firekamp.mopub/banneradview',
        onPlatformViewCreated: _onPlatformViewCreated,
      );
    }
    return Text(
        '$defaultTargetPlatform is not yet supported by the text_view plugin');
  }

  void _onPlatformViewCreated(int id) {
    if (widget.onMoPubBannerViewCreated == null) {
      return;
    }
    widget.onMoPubBannerViewCreated(new MoPubBannerViewCreatedController._(id));
  }
}

class MoPubBannerViewCreatedController {
  MoPubBannerViewCreatedController._(int id)
      : _channel = new MethodChannel('com.firekamp.mopub/banneradview_$id');

  final MethodChannel _channel;

  Future<void> show() async {
    return _channel.invokeMethod("showBanner");
  }

  Future<void> hide() async {
    return _channel.invokeMethod("hideBanner");
  }

  Future<void> fetchAndLoad() async {
    return _channel.invokeMethod("fetchAndLoadBanner");
  }

  Future<void> resumeRefresh() async {
    return _channel.invokeMethod("resumeBannerRefresh");
  }

}