import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:mopubwrapper/ad_manager.dart';

  class MoPubBannerView extends StatelessWidget {
  const MoPubBannerView({Key key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    if (Platform.isAndroid) {
      return AdManager.platformView;
    } else {
      return Container();
    }
  }

}


