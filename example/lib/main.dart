import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:mopub/mopub.dart';
import 'package:mopub_example/second_page.dart';

void main() {
  runApp(MyApp());
}

Future<String> initAds() async{
  MoPubData moPubData;
  if (kReleaseMode) {
//      moPubData = new MoPubData(
//          bannerAdId: "d461add2090246fc8cc90e1013fea995",
//          interstitialAdId: "47ac05ce10a740379c97eb8bb538cb26",
//          rewardAdId: "b16545998e534585aee852322bdf253a",
//          ironSource: "",
//          unity: "",
//          vungle: "");

    moPubData = new MoPubData(
        bannerAdId: "0ac59b0996d947309c33f59d6676399f",
        interstitialAdId: "4f117153f5c24fa6a3a92b818a5eb630",
        rewardAdId: "8f000bd5e00246de9c789eed39ff6096",
        ironSourceApplicationKey: "c1c89155",
        adColonyAppId: "appfd4c2cc7a7bd44a8b9",
        adColonyBannerZoneId:"vz4a904f7b37d344bf94",
        adColonyInterstitialZoneId: "vz9f125915714f4c1790",
        adColonyRewardedZoneId: "vzfb78adfe3d0b41018b",
        unityGameId: "3515411",
        appLovinSdkKey:"MwwhiwzfiNGzQHR005ynVW4i8kdnEzHVyb0XH1bePmmMlPMkeURbWZ2l8xQV6NvcVqlPwEuH730sVr68GJWWvg",
        vungleAppId: "5ea85d0d1aeed60001704b3f");
  } else {
    moPubData = new MoPubData(
        bannerAdId: "0ac59b0996d947309c33f59d6676399f",
        interstitialAdId: "4f117153f5c24fa6a3a92b818a5eb630",
        rewardAdId: "8f000bd5e00246de9c789eed39ff6096",
        ironSourceApplicationKey: "c1c89155",
        adColonyAppId: "appfd4c2cc7a7bd44a8b9",
        adColonyBannerZoneId:"vz4a904f7b37d344bf94",
        adColonyInterstitialZoneId: "vz9f125915714f4c1790",
        adColonyRewardedZoneId: "vzfb78adfe3d0b41018b",
        unityGameId: "3515411",
        appLovinSdkKey:"MwwhiwzfiNGzQHR005ynVW4i8kdnEzHVyb0XH1bePmmMlPMkeURbWZ2l8xQV6NvcVqlPwEuH730sVr68GJWWvg",
        vungleAppId: "5ea85d0d1aeed60001704b3f");
  }
  return await MoPubAd.initialize(moPubData);
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  MoPubBannerViewCreatedController _controller;

  @override
  void initState() {
    MoPubAd.startListening();
    super.initState();
    MoPubAd.rewardEvents = ((RewardAdStatus status) {
      if(status==RewardAdStatus.closed)
        {
          MoPubAd.precacheRewardAd();
        }
    });
  }

  getButton(String buttonText, {Function onPressed}) {
    return FlatButton(
      child: Text(
        buttonText,
        style: TextStyle(color: Colors.white),
      ),
      color: Colors.red,
      onPressed: () {
        onPressed();
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('MoPub Sample App'),
        ),
        body: FutureBuilder(
          future:initAds(),
         builder: (context,snapchat)
          {
            if(snapchat.connectionState==ConnectionState.done)
              {
                return SingleChildScrollView(
                  child: Padding(
                    padding: const EdgeInsets.all(30.0),
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      crossAxisAlignment: CrossAxisAlignment.stretch,
                      children: <Widget>[
                        getButton("showBannerAd", onPressed: () {
                          _controller?.fetchAndLoad();
                          _controller?.show();
                        }),
                        getButton("hideBannerAd", onPressed: () {
                          _controller?.hide();
                        }),
                        getButton("precacheInterstitialAd", onPressed: () {
                          MoPubAd.precacheInterstitialAd();
                        }),
                        getButton("showInterstitialAd", onPressed: () {
                          MoPubAd.showInterstitialAd();
                        }),
                        getButton("precacheRewardAd", onPressed: () {
                          MoPubAd.precacheRewardAd();
                        }),
                        getButton("showRewardAd", onPressed: () {
                          MoPubAd.showRewardAd();
                        }),
                        Container(
                            height: 50,
                            child:
                            MoPubBannerView(onMoPubBannerViewCreated: (controller) {
                              _controller = controller;
                            })),
                        getButton("Nex Screen", onPressed: () {
                          Navigator.push(context, MaterialPageRoute(
                            builder: (context)
                                {
                                  return SecondPage();
                                }
                          ));
                        }),
                      ],
                    ),
                  ),
                );
              }
            else
              {
                return Center(child: CupertinoActivityIndicator());
              }
          },
        ),
      ),
    );
  }
}
