import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

AndroidView view;

Widget getAndroidView() {
  if(view==null) {
    view= AndroidView(
      viewType: 'com.firekamp.mopub/banneradview',
    );
  }
  return view;
}
  class MoPubBannerView extends StatefulWidget {
  const MoPubBannerView({Key key}) : super(key: key);

  @override
  State<StatefulWidget> createState() => _MoPubBannerView();
}

class _MoPubBannerView extends State<MoPubBannerView> {

  @override
  Widget build(BuildContext context) {
    return getAndroidView();
  }

}


