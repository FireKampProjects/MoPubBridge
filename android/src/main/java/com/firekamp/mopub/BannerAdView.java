package com.firekamp.mopub;


import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;


class BannerAdViewFactory extends PlatformViewFactory {
    static BannerAdView bannerAdView;
    private final BinaryMessenger messenger;

    public BannerAdViewFactory(BinaryMessenger messenger) {
        super(StandardMessageCodec.INSTANCE);
        this.messenger = messenger;
    }

    @Override
    public PlatformView create(Context context, int id, Object o) {
        return bannerAdView = new BannerAdView(MopubPlugin.activity, messenger, id);
    }

}


public class BannerAdView implements PlatformView {

    Context context;
    LinearLayout view;
    int id = 0;

    BannerAdView(Context context, BinaryMessenger messenger, int id) {
        try{
            this.context = MopubPlugin.activity;
            view = new LinearLayout(context.getApplicationContext());
            view.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            view.setLayoutParams(params);
            if (AdManager.getInstance().moPubView.getParent() != null) {
                ((ViewGroup) AdManager.getInstance().moPubView.getParent()).removeView(AdManager.getInstance().moPubView);
            }
            view.addView(AdManager.getInstance().moPubView);
            Log.d(MopubPlugin.TAG_BANNER, "MobPub BannerAdView Initialized" + view.toString());
        }
        catch(Exception e)
        {
            Log.e(MopubPlugin.TAG_BANNER, "MobPub BannerAdView BannerAdView" +e.getMessage());
        }

    }

    @Override
    public View getView() {
        Log.d(MopubPlugin.TAG_BANNER, "MobPub BannerAdView GetView" + view.toString());
        return view;
    }

    @Override
    public void onFlutterViewAttached(View flutterView) {
        Log.d(MopubPlugin.TAG_BANNER, "MobPub BannerAdView onFlutterViewAttached" + view.toString());
    }

    @Override
    public void onFlutterViewDetached() {
        Log.d(MopubPlugin.TAG_BANNER, "MobPub BannerAdView onFlutterViewDetached" + view.toString());
    }

    @Override
    public void dispose() {
    }

}
