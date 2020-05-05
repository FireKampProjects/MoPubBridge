package com.firekamp.mopub;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.mopub.mobileads.MoPubView;

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
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * MopubPlugin
 */
public class MopubPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware , EventChannel.StreamHandler {

    public final static String TAG = "MoPub";
    static Activity activity;
    static final String METHOD_CHANNEL="com.firekamp.mopub/method";
    static final String EVENT_INTERSTITIAL_CHANNEL="com.firekamp.mopub/interstitial_stream";
    static final String EVENT_REWARD_CHANNEL="com.firekamp.mopub/reward_stream";
    static final String VIEW_BANNER="com.firekamp.mopub/banneradview";
    static EventChannel.EventSink INTERSTITIAL_EVENTS;
    static EventChannel.EventSink REWARD_EVENTS;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        final MethodChannel methodChannel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), METHOD_CHANNEL);
        final EventChannel eventInterstitialChannel = new EventChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), EVENT_INTERSTITIAL_CHANNEL);
        final EventChannel eventRewardChannel = new EventChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), EVENT_REWARD_CHANNEL);


        methodChannel.setMethodCallHandler(new MopubPlugin());
        eventInterstitialChannel.setStreamHandler(new MopubPlugin());
        eventRewardChannel.setStreamHandler(new MopubPlugin());
        BinaryMessenger messenger = flutterPluginBinding.getBinaryMessenger();
        flutterPluginBinding.getPlatformViewRegistry().registerViewFactory(VIEW_BANNER, new BannerAdViewFactory(messenger));

    }


    public static void registerWith(Registrar registrar) {
        activity = registrar.activity();
        final MethodChannel channel = new MethodChannel(registrar.messenger(), METHOD_CHANNEL);
        final EventChannel rewardChannel = new EventChannel(registrar.messenger(), EVENT_REWARD_CHANNEL);
        final EventChannel interstitialChannel = new EventChannel(registrar.messenger(), EVENT_INTERSTITIAL_CHANNEL);

        channel.setMethodCallHandler(new MopubPlugin());
        interstitialChannel.setStreamHandler(new MopubPlugin());
        rewardChannel.setStreamHandler(new MopubPlugin());

        registrar
                .platformViewRegistry()
                .registerViewFactory(
                        VIEW_BANNER, new BannerAdViewFactory(registrar.messenger()));

    }


    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {

        try {
            if (call.method.equals("init")) {
                init(call,result);
                AdManager.getInstance().init();
                return;
            }
            if (AdIds.banner == null) {
                result.error("002", "Not initialized. Please pass the data by MoPubAd.initialize(moPubData) ", null);
                return;
            }

            if (call.method.equals(BridgeMethods.prefetchInterstitial)) {
                AdManager.getInstance().fetchInterstitial();
            } else if (call.method.equals(BridgeMethods.prefetchReward)) {
                AdManager.getInstance().fetchRewardVideo();
            } else if (call.method.equals(BridgeMethods.showInterstitialAd)) {
                AdManager.getInstance().showInterstitial();
            } else if (call.method.equals(BridgeMethods.showRewardAd)) {
                AdManager.getInstance().showRewardVideo();
            } else {
                result.notImplemented();
            }
        } catch (Exception e) {
            result.error("001",e.getMessage(),e);
            Log.e(TAG, e.getMessage());
        }
    }

    void init(MethodCall method, Result result) throws JSONException {
        JSONObject data = new JSONObject(method.arguments().toString());

        if (data.has("bannerAdId")) {
            AdIds.banner = data.get("bannerAdId").toString();
        }

        if (data.has("interstitialAdId")) {
            AdIds.interstitial = data.get("interstitialAdId").toString();
        }

        if (data.has("rewardAdId")) {
            AdIds.reward = data.get("rewardAdId").toString();
        }
        if (data.has("vungleAppId")) {
            AdIds.vungleAppId = data.get("vungleAppId").toString();
        }
        if (data.has("ironSourceApplicationKey")) {
            AdIds.ironSourceApplicationKey = data.get("ironSourceApplicationKey").toString();
        }
        if (data.has("appLovinSdkKey")) {
            AdIds.appLovinSdkKey = data.get("appLovinSdkKey").toString();
        }

        if (data.has("adColonyAppId")) {
            AdIds.adColonyAppId = data.get("adColonyAppId").toString();
        }
        if (data.has("adColonyBannerZoneId")) {
            AdIds.adColonyBannerZoneId = data.get("adColonyBannerZoneId").toString();
        }
        if (data.has("adColonyInterstitialZoneId")) {
            AdIds.adColonyInterstitialZoneId = data.get("adColonyInterstitialZoneId").toString();
        }
        if (data.has("adColonyRewardedZoneId")) {
            AdIds.adColonyRewardedZoneId = data.get("adColonyRewardedZoneId").toString();
        }

        if (data.has("unityGameId")) {
            AdIds.unityGameId = data.get("unityGameId").toString();
        }
        result.success("success");
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    }

    @Override
    public void onAttachedToActivity(ActivityPluginBinding binding) {
        if (binding.getActivity() != null) {
            activity = binding.getActivity();
        }
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {

    }

    @Override
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {

    }

    @Override
    public void onDetachedFromActivity() {

    }

    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
        if(arguments==null)
            return;
        if(arguments.toString().equals("interstitial"))
        {
            INTERSTITIAL_EVENTS=events;
        }
        else if(arguments.toString().equals("reward"))
        {
            REWARD_EVENTS=events;
        }
        Log.d(TAG,"onListen");
    }

    @Override
    public void onCancel(Object arguments) {
        if(arguments==null)
            return;
        if(arguments.toString().equals("interstitial"))
        {
            INTERSTITIAL_EVENTS=null;
        }
        else if(arguments.toString().equals("reward"))
        {
            REWARD_EVENTS=null;
        }
        Log.d(TAG,"onCancel");
    }
}
