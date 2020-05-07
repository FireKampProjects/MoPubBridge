import 'dart:async';
import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:mopub/models/mopub_data.dart';

enum InterstitialAdStatus {
  loaded,
  failed,
  shown,
  clicked,
  dismissed
}

enum RewardAdStatus {
  notFetched,
  failed,
  loaded,
  started,
  completed,
  clicked,
  closed
}

class AdManager {
  static var _methodChannel = MethodChannel('com.firekamp.mopub/method');

  static AndroidView platformView = AndroidView(viewType: 'com.firekamp.mopub/banneradview');

  static Function(RewardAdStatus) rewardEvents;
  static Function(InterstitialAdStatus) interstitialEvents;

  static var _rewardStream = EventChannel('com.firekamp.mopub/reward_stream');
  static var _interstitialStream =
      EventChannel('com.firekamp.mopub/interstitial_stream');

  static StreamSubscription interstitialStream;
  static StreamSubscription rewardStream;

  static startListening() {
    rewardStream =
        _rewardStream.receiveBroadcastStream("reward").listen(_updateRewardFromStream);
    interstitialStream = _interstitialStream
        .receiveBroadcastStream("interstitial")
        .listen(_updateInterstitialFromStream);
  }

  static Future<String> initialize(MoPubData data) async {
    try {
      var json = jsonEncode(data);
      return await _methodChannel.invokeMethod('configure', json);
    } on PlatformException catch (e) {
      print(e.message);
      return e.message;
    }
  }

  static void _updateRewardFromStream(statusValue) {
    var status = RewardAdStatus.values[statusValue];
    if (rewardEvents != null) {
      rewardEvents(status);
    }
  }

  static void _updateInterstitialFromStream(statusValue) {
    var status = InterstitialAdStatus.values[statusValue];
    if (interstitialEvents != null) {
      interstitialEvents(status);
    }
  }

  static stopListening() {
    if (interstitialStream != null) {
      interstitialStream.cancel();
      interstitialStream = null;
    }

    if (rewardStream != null) {
      rewardStream.cancel();
      rewardStream = null;
    }

  }

  static fetchAndLoadBanner()
  {
    try {
      _methodChannel.invokeMethod('fetchAndLoadBanner');
    } on PlatformException catch (e) {
      print("Error fetching and loading the banner  ${e.message}");
    }
  }

  static showBanner()
  {
    try {
      _methodChannel.invokeMethod('showBanner');
    } on PlatformException catch (e) {
      print("Error showing banner bridge ${e.message}");
    }
  }

  static hideBanner()
  {
    try {
      _methodChannel.invokeMethod('hideBanner');
    } on PlatformException catch (e) {
      print("Error hiding banner from bridge ${e.message}");
    }
  }

  static precacheInterstitialAd() {
    try {
      _methodChannel.invokeMethod('prefetchInterstitial');
    } on PlatformException catch (e) {
      print("Error precaching interstitial from bridge ${e.message}");
    }
  }

  static precacheRewardAd() {
    try {
      _methodChannel.invokeMethod('prefetchReward');
    } on PlatformException catch (e) {
      print("Error precaching reward from bridge ${e.message}");
    }
  }

  static showInterstitialAd() {
    try {
      _methodChannel.invokeMethod('showInterstitialAd');
    } on PlatformException catch (e) {
      print("Error showing interstitial from bridge ${e.message}");
    }
  }

  static showRewardAd() {
    try {
      _methodChannel.invokeMethod('showRewardAd');
    } on PlatformException catch (e) {
      print("Error showing reward from bridge ${e.message}");
    }
  }
}
