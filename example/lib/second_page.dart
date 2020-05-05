import 'package:flutter/material.dart';
import 'package:mopub/mopub.dart';

class SecondPage extends StatefulWidget {
  @override
  _SecondPageState createState() => _SecondPageState();
}

class _SecondPageState extends State<SecondPage> {
  MoPubBannerViewCreatedController _controller;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        child: Center(
          child: Container(
              height: 50,
              child:
              MoPubBannerView(onMoPubBannerViewCreated: (controller) {
                _controller = controller;
                _controller.fetchAndLoad();
              })),
        ),
      ),
    );
  }
}
