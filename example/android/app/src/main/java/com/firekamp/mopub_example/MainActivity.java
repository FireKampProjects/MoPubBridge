package com.firekamp.mopub_example;

import androidx.annotation.NonNull;

import com.firekamp.mopub.MopubPlugin;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugins.GeneratedPluginRegistrant;

public class MainActivity extends FlutterActivity {
  @Override
  public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
    GeneratedPluginRegistrant.registerWith(flutterEngine);
    flutterEngine.getPlugins().add(new MopubPlugin());
  }
}
