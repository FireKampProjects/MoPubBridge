import 'package:flutter/material.dart';
import 'package:mopub/mopub.dart';
import 'package:mopub_example/main.dart';

class SecondPage extends StatefulWidget {
  @override
  _SecondPageState createState() => _SecondPageState();
}

class _SecondPageState extends State<SecondPage> {

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        child: Center(
          child: Container(
              height: 50,
              child:MoPubBannerView(),
        ),
      ),
    ));
  }
  @override
  void initState() {
    super.initState();
    //MoPubAd.fetchAndLoadBanner();
  }
}


