package com.firekamp.mopub;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.adcolony.sdk.AdColonyAppOptions;
import com.google.gson.Gson;
import com.mopub.common.AdapterConfiguration;
import com.mopub.common.MoPub;
import com.mopub.common.MoPubReward;
import com.mopub.common.OnNetworkInitializationFinishedListener;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.mobileads.AdColonyAdapterConfiguration;
import com.mopub.mobileads.AppLovinAdapterConfiguration;
import com.mopub.mobileads.FacebookAdapterConfiguration;
import com.mopub.mobileads.IronSourceAdapterConfiguration;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubRewardedVideoListener;
import com.mopub.mobileads.MoPubRewardedVideos;
import com.mopub.mobileads.MoPubView;
import com.mopub.mobileads.UnityAdsAdapterConfiguration;
import com.mopub.mobileads.VungleAdapterConfiguration;

import org.json.JSONArray;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


final class RewardAdEvent {
    public static final int notFetched = 0;
    public static final int failed = 1;
    public static final int loaded = 2;
    public static final int started = 3;
    public static final int completed = 4;
    public static final int clicked = 5;
    public static final int closed = 6;
}

final class InterstitialAdEvent {
    public static final int loaded = 0;
    public static final int failed = 1;
    public static final int shown = 2;
    public static final int clicked = 3;
    public static final int dismissed = 4;
}

final class AdAdapterValueConstants {
    public static final String gameId = "gameId"; //Unity
    public static final String appId = "appId"; //AdColony and Vungle
    public static final String applicationKey = "applicationKey"; //IronSource
    public static final String sdkKey = "sdk_key"; //AppLovin
    public static final String clientOptions = "clientOptions"; //AdColony
    public static final String allZoneIds = "allZoneIds"; //AdColony
}

final class BridgeMethods {
    public static final String fetchAndLoadBanner = "fetchAndLoadBanner";
    public static final String prefetchInterstitial = "prefetchInterstitial";
    public static final String prefetchReward = "prefetchReward";
    public static final String showInterstitialAd = "showInterstitialAd";
    public static final String showRewardAd = "showRewardAd";
    public static final String resumeBannerRefresh = "resumeBannerRefresh";
    public static final String stopBannerRefresh = "stopBannerRefresh";
    public static final String showBanner = "showBanner";
    public static final String hideBanner = "hideBanner";
}


public class AdManager {
    private static AdManager adManager;
    private MoPubInterstitial mMoPubInterstitial;
    public MoPubView moPubView;
    private AdEvents adEvents;

    public static AdManager getInstance() {
        if (adManager == null) {
            adManager = new AdManager();
        }
        return adManager;
    }

    void fetchAndLoadBanner() {
        moPubView.loadAd();
        toggleBanner(true);
    }

    void toggleBanner(boolean isShow) {
        try {
            if (moPubView.getParent() != null) {
                ((ViewGroup) moPubView.getParent()).setVisibility(isShow ? View.VISIBLE : View.GONE);
            }
        } catch (Exception e) {
            Log.e(MopubPlugin.TAG, "toggleBanner" + e.getMessage());
        }
    }

    void hideBanner() {
        toggleBanner(false);
    }

    void fetchInterstitial() {
        try {
            mMoPubInterstitial.load();
            Log.d(MopubPlugin.TAG, "Interstitial Load Called");
        } catch (Exception e) {
            adEvents.interstitialSuccess(InterstitialAdEvent.failed);
            Log.e(MopubPlugin.TAG, "fetchInterstitial" + e.getMessage());
        }
    }

    void showInterstitial() {
        try {
            if (mMoPubInterstitial != null && mMoPubInterstitial.isReady()) {
                mMoPubInterstitial.show();
                Log.d(MopubPlugin.TAG, "Interstitial Show Called");
            } else {
                adEvents.interstitialSuccess(InterstitialAdEvent.failed);
                Log.e(MopubPlugin.TAG, "Interstitial is not ready");
            }
        } catch (Exception e) {
            adEvents.interstitialSuccess(InterstitialAdEvent.failed);
            Log.e(MopubPlugin.TAG, "showInterstitial" + e.getMessage());
        }
    }

    void showRewardVideo() {
        try {
            if (MoPubRewardedVideos.hasRewardedVideo(AdIds.reward)) {
                final String adUnitId = AdIds.reward;
                if (MoPubRewardedVideos.getAvailableRewards(adUnitId).iterator().hasNext()) {
                    MoPubReward currentReward = MoPubRewardedVideos.getAvailableRewards(adUnitId).iterator().next();
                    MoPubRewardedVideos.selectReward(adUnitId, currentReward);
                    MoPubRewardedVideos.showRewardedVideo(adUnitId);
                    Log.d(MopubPlugin.TAG, "Reward Show Called");
                } else {
                    adEvents.interstitialSuccess(RewardAdEvent.failed);
                    Log.e(MopubPlugin.TAG, "Reward not found");
                }
            } else {
                adEvents.interstitialSuccess(RewardAdEvent.notFetched);
                Log.e(MopubPlugin.TAG, "Reward not found");
            }
        } catch (Exception e) {
            adEvents.interstitialSuccess(RewardAdEvent.failed);
            Log.e(MopubPlugin.TAG, "showRewardVideo" + RewardAdEvent.failed + e.getMessage());
        }
    }

    void fetchRewardVideo() {
        try {
            MoPubRewardedVideos.loadRewardedVideo(AdIds.reward);
            Log.d(MopubPlugin.TAG, "Reward video load called");
        } catch (Exception e) {
            adEvents.interstitialSuccess(RewardAdEvent.failed);
            Log.e(MopubPlugin.TAG, "showRewardVideo" + e.getMessage());
        }
    }

    void init(@NonNull AdEvents adEventsCallBack) {
        try {
            adEvents = adEventsCallBack;
            MoPub.initializeSdk(MopubPlugin.activity, new SdkConfiguration.Builder(AdIds.banner).build(), new SdkInitializationListener() {
                @Override
                public void onInitializationFinished() {
                    Log.d(MopubPlugin.TAG, "SDK initialization Finished");
                }
            });
            //Banner
            moPubView = new MoPubView(MopubPlugin.activity);
            moPubView.setAdSize(MoPubView.MoPubAdSize.MATCH_VIEW);
            moPubView.setAdUnitId(AdIds.banner);
            moPubView.setBackgroundColor(ContextCompat.getColor(MopubPlugin.activity, android.R.color.transparent));
            moPubView.setBannerAdListener(new MoPubView.BannerAdListener() {
                @Override
                public void onBannerLoaded(@NonNull MoPubView banner) {
                    Log.d(MopubPlugin.TAG_BANNER, "MobPub onBannerLoaded");
                }

                @Override
                public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
                    Log.d(MopubPlugin.TAG_BANNER, "MobPub Loading Failed");
                }

                @Override
                public void onBannerClicked(MoPubView banner) {
                    Log.d(MopubPlugin.TAG_BANNER, "MobPub onBannerClicked");
                }

                @Override
                public void onBannerExpanded(MoPubView banner) {
                    Log.d(MopubPlugin.TAG_BANNER, "MobPub Expanded");
                }

                @Override
                public void onBannerCollapsed(MoPubView banner) {
                    Log.d(MopubPlugin.TAG_BANNER, "MobPub Collapsed");
                }
            });
            //Interstitial
            mMoPubInterstitial = new MoPubInterstitial(MopubPlugin.activity, AdIds.interstitial);
            mMoPubInterstitial.setInterstitialAdListener(new MoPubInterstitial.InterstitialAdListener() {
                @Override
                public void onInterstitialLoaded(MoPubInterstitial interstitial) {
                    adEvents.interstitialSuccess(InterstitialAdEvent.loaded);
                    Log.d(MopubPlugin.TAG, "MobPub onInterstitialLoaded");
                }

                @Override
                public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
                    adEvents.interstitialSuccess(InterstitialAdEvent.failed);
                    Log.d(MopubPlugin.TAG, "MobPub onInterstitialFailed");
                }

                @Override
                public void onInterstitialShown(MoPubInterstitial interstitial) {
                    adEvents.interstitialSuccess(InterstitialAdEvent.shown);
                    Log.d(MopubPlugin.TAG, "MobPub onInterstitialShown");
                }

                @Override
                public void onInterstitialClicked(MoPubInterstitial interstitial) {
                    adEvents.interstitialSuccess(InterstitialAdEvent.clicked);
                    Log.d(MopubPlugin.TAG, "MobPub onInterstitialClicked");
                }

                @Override
                public void onInterstitialDismissed(MoPubInterstitial interstitial) {
                    adEvents.interstitialSuccess(InterstitialAdEvent.dismissed);
                    Log.d(MopubPlugin.TAG, "MobPub onInterstitialDismissed");
                }
            });
            //Reward Video
            MoPubRewardedVideos.setRewardedVideoListener(new MoPubRewardedVideoListener() {
                @Override
                public void onRewardedVideoLoadSuccess(@NonNull String adUnitId) {
                    adEvents.rewardSuccess(RewardAdEvent.loaded);
                    Log.d(MopubPlugin.TAG, "MobPub onRewardedVideoLoadSuccess");
                }

                @Override
                public void onRewardedVideoLoadFailure(@NonNull String adUnitId, @NonNull MoPubErrorCode errorCode) {
                    adEvents.rewardSuccess(RewardAdEvent.failed);
                    Log.d(MopubPlugin.TAG, "MobPub onRewardedVideoLoadFailure");
                }

                @Override
                public void onRewardedVideoStarted(@NonNull String adUnitId) {
                    adEvents.rewardSuccess(RewardAdEvent.started);
                    Log.d(MopubPlugin.TAG, "MobPub onRewardedVideoStarted");
                }

                @Override
                public void onRewardedVideoPlaybackError(@NonNull String adUnitId, @NonNull MoPubErrorCode errorCode) {
                    adEvents.rewardSuccess(RewardAdEvent.failed);
                    Log.d(MopubPlugin.TAG, "MobPub onRewardedVideoPlaybackError");
                }

                @Override
                public void onRewardedVideoClicked(@NonNull String adUnitId) {
                    adEvents.rewardSuccess(RewardAdEvent.clicked);
                    Log.d(MopubPlugin.TAG, "MobPub onRewardedVideoClicked");
                }

                @Override
                public void onRewardedVideoClosed(@NonNull String adUnitId) {
                    adEvents.rewardSuccess(RewardAdEvent.completed);
                    //Mani - In Android, After closing the first video only second video is getting loaded, So sending completed event to match with iOS.
                    Log.d(MopubPlugin.TAG, "MobPub onRewardedVideoClosed");
                }

                @Override
                public void onRewardedVideoCompleted(@NonNull Set<String> adUnitIds, @NonNull MoPubReward reward) {
                    adEvents.rewardSuccess(RewardAdEvent.completed);
                    Log.d(MopubPlugin.TAG, "MobPub onRewardedVideoCompleted");
                }
            });
            //Third party networks
            AdColonyAdapterConfiguration adColonyAdapterConfiguration = new AdColonyAdapterConfiguration();//Since Library forces client option should not be null, but actual adcolony allows client as optional. We are using AdColonyAdapterConfigurationCustom
            Map<String, String> adColonyConfiguration = new HashMap<>();
            AdColonyAppOptions appOptions = AdColonyAppOptions.getMoPubAppOptions("");
            appOptions.setGDPRConsentString("1");
            appOptions.setGDPRRequired(true);
            Gson gson = new Gson();
            String clientOption = gson.toJson(appOptions);
            adColonyConfiguration.put(AdAdapterValueConstants.appId, AdIds.adColonyAppId);
            String[] zoneIds = {AdIds.adColonyBannerZoneId, AdIds.adColonyInterstitialZoneId, AdIds.adColonyRewardedZoneId};
            adColonyConfiguration.put(AdAdapterValueConstants.allZoneIds, new JSONArray(Arrays.asList(zoneIds)).toString());
            adColonyConfiguration.put(AdAdapterValueConstants.clientOptions, clientOption);
            adColonyAdapterConfiguration.initializeNetwork(MopubPlugin.activity, adColonyConfiguration, new OnNetworkInitializationFinishedListener() {
                @Override
                public void onNetworkInitializationFinished(@NonNull Class<? extends AdapterConfiguration> clazz, @NonNull MoPubErrorCode moPubErrorCode) {
                    Log.d(MopubPlugin.TAG, "AdColony onNetworkInitializationFinished " + moPubErrorCode);
                }
            });

            VungleAdapterConfiguration vungleAdapterConfiguration = new VungleAdapterConfiguration();
            Map<String, String> vungleConfiguration = new HashMap<>();
            vungleConfiguration.put(AdAdapterValueConstants.appId, AdIds.vungleAppId);
            vungleAdapterConfiguration.initializeNetwork(MopubPlugin.activity, vungleConfiguration, new OnNetworkInitializationFinishedListener() {
                @Override
                public void onNetworkInitializationFinished(@NonNull Class<? extends AdapterConfiguration> clazz, @NonNull MoPubErrorCode moPubErrorCode) {
                    Log.d(MopubPlugin.TAG, "Vungle onNetworkInitializationFinished " + moPubErrorCode);
                }
            });

            AppLovinAdapterConfiguration appLovinAdapterConfiguration = new AppLovinAdapterConfiguration();
            Map<String, String> appLovinConfiguration = new HashMap<String, String>();
            appLovinConfiguration.put(AdAdapterValueConstants.sdkKey, AdIds.appLovinSdkKey);
            appLovinAdapterConfiguration.initializeNetwork(MopubPlugin.activity, appLovinConfiguration, new OnNetworkInitializationFinishedListener() {
                @Override
                public void onNetworkInitializationFinished(@NonNull Class<? extends AdapterConfiguration> clazz, @NonNull MoPubErrorCode moPubErrorCode) {
                    Log.d(MopubPlugin.TAG, "AppLovin onNetworkInitializationFinished " + moPubErrorCode);
                }
            });

            IronSourceAdapterConfiguration ironSourceAdapterConfiguration = new IronSourceAdapterConfiguration();
            Map<String, String> ironSourceConfiguration = new HashMap<>();
            ironSourceConfiguration.put(AdAdapterValueConstants.applicationKey, AdIds.ironSourceApplicationKey);
            ironSourceAdapterConfiguration.initializeNetwork(MopubPlugin.activity, ironSourceConfiguration, new OnNetworkInitializationFinishedListener() {
                @Override
                public void onNetworkInitializationFinished(@NonNull Class<? extends AdapterConfiguration> clazz, @NonNull MoPubErrorCode moPubErrorCode) {
                    Log.d(MopubPlugin.TAG, "IronSource onNetworkInitializationFinished " + moPubErrorCode);
                }
            });

            FacebookAdapterConfiguration facebookAdapterConfiguration = new FacebookAdapterConfiguration();
            facebookAdapterConfiguration.initializeNetwork(MopubPlugin.activity, null, new OnNetworkInitializationFinishedListener() {
                @Override
                public void onNetworkInitializationFinished(@NonNull Class<? extends AdapterConfiguration> clazz, @NonNull MoPubErrorCode moPubErrorCode) {
                    Log.d(MopubPlugin.TAG, "Facebook onNetworkInitializationFinished " + moPubErrorCode);
                }
            });

            Map<String, String> unityConfiguration = new HashMap<>();
            unityConfiguration.put(AdAdapterValueConstants.gameId, AdIds.unityGameId);
            UnityAdsAdapterConfiguration unityAdsAdapterConfiguration = new UnityAdsAdapterConfiguration();
            unityAdsAdapterConfiguration.initializeNetwork(MopubPlugin.activity, unityConfiguration, new OnNetworkInitializationFinishedListener() {
                @Override
                public void onNetworkInitializationFinished(@NonNull Class<? extends AdapterConfiguration> clazz, @NonNull MoPubErrorCode moPubErrorCode) {
                    Log.d(MopubPlugin.TAG, "Unity onNetworkInitializationFinished " + moPubErrorCode);
                }
            });
        } catch (Exception e) {
            Log.e(MopubPlugin.TAG, "init" + e.getMessage());
        }

    }
}
