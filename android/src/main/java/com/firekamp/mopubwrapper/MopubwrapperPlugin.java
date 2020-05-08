package com.firekamp.mopubwrapper;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.ads.Ad;

import org.json.JSONException;
import org.json.JSONObject;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * MopubWrapperPlugin
 */
public class MopubwrapperPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware, EventChannel.StreamHandler {

  final static String PLUGIN_TAG = "MopubwrapperPlugin";
  static Activity activity;

  private static final String METHOD_CHANNEL = "com.firekamp.mopub/method";
  private static final String EVENT_INTERSTITIAL_CHANNEL = "com.firekamp.mopub/interstitial_stream";
  private static final String EVENT_REWARD_CHANNEL = "com.firekamp.mopub/reward_stream";
  private static final String VIEW_BANNER = "com.firekamp.mopub/banneradview";

  private EventChannel.EventSink INTERSTITIAL_EVENTS;
  private EventChannel.EventSink REWARD_EVENTS;

  private final AdManager adManager = AdManager.getInstance();

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    final BinaryMessenger binaryMessenger = flutterPluginBinding.getBinaryMessenger();
    final MethodChannel methodChannel = new MethodChannel(binaryMessenger, METHOD_CHANNEL);
    final EventChannel eventInterstitialChannel = new EventChannel(binaryMessenger, EVENT_INTERSTITIAL_CHANNEL);
    final EventChannel eventRewardChannel = new EventChannel(binaryMessenger, EVENT_REWARD_CHANNEL);

    methodChannel.setMethodCallHandler(this);
    eventInterstitialChannel.setStreamHandler(this);
    eventRewardChannel.setStreamHandler(this);
    flutterPluginBinding.getPlatformViewRegistry().registerViewFactory(VIEW_BANNER, new BannerAdViewFactory(binaryMessenger));
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    try {
      if (call.method.equals(BridgeMethods.configure)) {
        final AdConfiguration configuration = fetchConfiguration(call);
        configureAdManager(configuration);
        result.success(null);
      }
      else if (call.method.equals(BridgeMethods.fetchAndLoadBanner)) {
        adManager.fetchAndLoadBanner();
        result.success(null);
      } else if (call.method.equals(BridgeMethods.showBanner)) {
        adManager.showBanner();
        result.success(null);
      } else if (call.method.equals(BridgeMethods.hideBanner)) {
        adManager.hideBanner();
        result.success(null);
      } else if (call.method.equals(BridgeMethods.prefetchInterstitial)) {
        adManager.fetchInterstitial();
        result.success(null);
      } else if (call.method.equals(BridgeMethods.prefetchReward)) {
        adManager.fetchRewardVideo();
        result.success(null);
      } else if (call.method.equals(BridgeMethods.showInterstitialAd)) {
        adManager.showInterstitial();
        result.success(null);
      } else if (call.method.equals(BridgeMethods.showRewardAd)) {
        adManager.showRewardVideo();
        result.success(null);
      } else {
        result.notImplemented();
      }
    } catch (Exception e) {
      result.error(null, e.getMessage(), e);
      Log.e(PLUGIN_TAG, e.getMessage());
    }
  }

  AdConfiguration fetchConfiguration(MethodCall method) throws JSONException {
    final JSONObject data = new JSONObject(method.arguments().toString());

    String bannerAdId = null;
    if (data.has("bannerAdId")) {
      bannerAdId = data.get("bannerAdId").toString();
    }

    String interstitialAdId = null;
    if (data.has("interstitialAdId")) {
      interstitialAdId = data.get("interstitialAdId").toString();
    }

    String rewardAdId = null;
    if (data.has("rewardAdId")) {
      rewardAdId = data.get("rewardAdId").toString();
    }

    String vungleAppId = null;
    if (data.has("vungleAppId")) {
      vungleAppId = data.get("vungleAppId").toString();
    }

    String ironSourceApplicationKey = null;
    if (data.has("ironSourceApplicationKey")) {
      ironSourceApplicationKey = data.get("ironSourceApplicationKey").toString();
    }

    String appLovinSdkKey = null;
    if (data.has("appLovinSdkKey")) {
      appLovinSdkKey = data.get("appLovinSdkKey").toString();
    }

    String adColonyAppId = null;
    if (data.has("adColonyAppId")) {
      adColonyAppId = data.get("adColonyAppId").toString();
    }

    String adColonyBannerZoneId = null;
    if (data.has("adColonyBannerZoneId")) {
      adColonyBannerZoneId = data.get("adColonyBannerZoneId").toString();
    }

    String adColonyInterstitialZoneId = null;
    if (data.has("adColonyInterstitialZoneId")) {
      adColonyInterstitialZoneId = data.get("adColonyInterstitialZoneId").toString();
    }

    String adColonyRewardedZoneId = null;
    if (data.has("adColonyRewardedZoneId")) {
      adColonyRewardedZoneId = data.get("adColonyRewardedZoneId").toString();
    }

    String unityGameId = null;
    if (data.has("unityGameId")) {
      unityGameId = data.get("unityGameId").toString();
    }

    boolean isFacebookEnabled = false;
    if (data.has("facebookEnabled")) {
      isFacebookEnabled = data.getBoolean("facebookEnabled");
    }

    return new AdConfiguration(bannerAdId,
            interstitialAdId,
            rewardAdId,
            adColonyAppId,
            adColonyBannerZoneId,
            adColonyInterstitialZoneId,
            adColonyRewardedZoneId,
            vungleAppId,
            ironSourceApplicationKey,
            appLovinSdkKey,
            unityGameId,
            isFacebookEnabled
    );
  }

  private void configureAdManager(AdConfiguration configuration) {
    adManager.configure(configuration, new AdEvents() {
      @Override
      public void interstitialEvent(InterstitialAdEvent event) {
        if (INTERSTITIAL_EVENTS != null) {
          INTERSTITIAL_EVENTS.success(event.getValue());
        }
      }

      @Override
      public void rewardEvent(RewardAdEvent event) {
        if (REWARD_EVENTS != null) {
          REWARD_EVENTS.success(event.getValue());
        }
      }
    });
  }

  @Override
  public void onAttachedToActivity(ActivityPluginBinding binding) {
    binding.getActivity();
    activity = binding.getActivity();
  }


  @Override
  public void onDetachedFromEngine(FlutterPluginBinding binding) { }

  @Override
  public void onDetachedFromActivityForConfigChanges() { }

  @Override
  public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) { }

  @Override
  public void onDetachedFromActivity() { }

  private void listenToEvents(Object arguments, EventChannel.EventSink events) {
    if (arguments == null)
      return;
    if (arguments.toString().equals("interstitial")) {
      INTERSTITIAL_EVENTS = events;
    } else if (arguments.toString().equals("reward")) {
      REWARD_EVENTS = events;
    }
  }

  @Override
  public void onListen(Object arguments, EventChannel.EventSink events) {
    listenToEvents(arguments, events);
  }

  @Override
  public void onCancel(Object arguments) {
    listenToEvents(arguments, null);
  }
}
