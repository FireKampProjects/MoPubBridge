package com.firekamp.mopub;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.core.content.ContextCompat;

import com.mopub.mobileads.MoPubView;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

class BannerAdViewFactory extends PlatformViewFactory {
    BannerAdViewFactory(BinaryMessenger messenger) {
        super(StandardMessageCodec.INSTANCE);
    }

    @Override
    public PlatformView create(Context context, int id, Object o) {
        final MoPubView bannerView = AdManager.getInstance().getBannerView();
        return new BannerAdView(MopubPlugin.activity, bannerView);
    }

}

public class BannerAdView implements PlatformView {
    static ViewGroup oldParent;
    private LinearLayout view;
    private MoPubView bannerView;

    BannerAdView(Context context, MoPubView bannerView) {
        this.bannerView = bannerView;
        view = new LinearLayout(context.getApplicationContext());
        view.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        view.setLayoutParams(params);
        addBannerView();
    }

    private void addBannerView() {
        if (bannerView != null) {
            if (bannerView.getParent() != null) {
                final ViewGroup parent = (ViewGroup) bannerView.getParent();
                oldParent = parent;
                parent.removeAllViews();
            }
            view.addView(bannerView);
        }
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public void dispose() {
        view.removeView(bannerView);
        if (oldParent != null) {
            oldParent.addView(bannerView);
            oldParent = null;
        }
    }

}
