package com.firekamp.mopub;


import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;


class BannerAdViewFactory extends PlatformViewFactory {
    private final BinaryMessenger messenger;

    public BannerAdViewFactory(BinaryMessenger messenger) {
        super(StandardMessageCodec.INSTANCE);
        this.messenger = messenger;
    }

    @Override
    public PlatformView create(Context context, int id, Object o) {
        return new BannerAdView(context, messenger, id);
    }
}


public class BannerAdView implements PlatformView, MethodChannel.MethodCallHandler {
    static MoPubView moPubView;
    private final MethodChannel methodChannel;
    Context context;

    LinearLayout view;

    BannerAdView(Context context, BinaryMessenger messenger, int id) {
        this.context = context;
        view = new LinearLayout(context.getApplicationContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        view.setLayoutParams(params);
        methodChannel = new MethodChannel(messenger, "com.firekamp.mopub/banneradview_" + id);
        methodChannel.setMethodCallHandler(this);

    }

    void loadBanner() {
        if (moPubView.getParent() != null) {
            ((ViewGroup) moPubView.getParent()).removeView(moPubView);
        }
        view.addView(moPubView);
    }

    void fetchAndLoad() {
            view.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
//        if (moPubView == null) {
            moPubView = new MoPubView(context);
            moPubView.setAdSize(MoPubView.MoPubAdSize.HEIGHT_50);
            moPubView.setAdUnitId(AdIds.banner);
            moPubView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
            MoPub.initializeSdk(MopubPlugin.activity, new SdkConfiguration.Builder(AdIds.banner).build(), new SdkInitializationListener() {
                @Override
                public void onInitializationFinished() {
                    Log.d(MopubPlugin.TAG, "onInitializationFinished");
                    moPubView.loadAd();
                }
            });
//        } else {
//            loadBanner();
//        }

        moPubView.setBannerAdListener(new MoPubView.BannerAdListener() {

            @Override
            public void onBannerLoaded(@NonNull MoPubView banner) {
                try {
                    loadBanner();
                    Log.d(MopubPlugin.TAG, "MobPub Ad onBannerLoaded");
                } catch (Exception e) {
                    Log.e(MopubPlugin.TAG, e.getMessage());
                }
            }

            @Override
            public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
                Log.d(MopubPlugin.TAG, "MobPub Ad Loading Failed");
            }

            @Override
            public void onBannerClicked(MoPubView banner) {
                Log.d(MopubPlugin.TAG, "MobPub onBannerClicked");
            }

            @Override
            public void onBannerExpanded(MoPubView banner) {
                Log.d(MopubPlugin.TAG, "MobPub Ad Expanded");
            }

            @Override
            public void onBannerCollapsed(MoPubView banner) {
                Log.d(MopubPlugin.TAG, "MobPub Ad Collapsed");
            }
        });
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
        try {
            switch (methodCall.method) {
                case BridgeMethods.showBanner:
                    moPubView.setVisibility(View.VISIBLE);
                    break;
                case BridgeMethods.hideBanner:
                    moPubView.setVisibility(View.GONE);
                    break;
                case BridgeMethods.fetchAndLoadBanner:
                    fetchAndLoad();
                    break;
                case BridgeMethods.resumeBannerRefresh:
                    //moPubView.fetchAndLoad();
                    //TODO:Need to check what should we do here.
                    break;
                default:
                    result.notImplemented();
            }
        } catch (Exception e) {
            Log.e(MopubPlugin.TAG, e.getMessage());
        }

    }

    @Override
    public void dispose() {
    }
}
