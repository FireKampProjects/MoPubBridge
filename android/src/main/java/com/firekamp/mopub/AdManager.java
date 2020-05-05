package com.firekamp.mopub;

import android.util.Log;

import androidx.annotation.NonNull;

import com.adcolony.sdk.AdColony;
import com.adcolony.sdk.AdColonyAppOptions;
import com.firekamp.AdColonyAdapterConfigurationCustom;
import com.mopub.common.AdapterConfiguration;
import com.mopub.common.MoPub;
import com.mopub.common.MoPubReward;
import com.mopub.common.OnNetworkInitializationFinishedListener;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.common.util.Json;
import com.mopub.mobileads.AdColonyAdapterConfiguration;
import com.mopub.mobileads.AppLovinAdapterConfiguration;
import com.mopub.mobileads.FacebookAdapterConfiguration;
import com.mopub.mobileads.IronSourceAdapterConfiguration;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubRewardedVideoListener;
import com.mopub.mobileads.MoPubRewardedVideos;
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

    private RewardAdEvent() {
    }
}

final class InterstitialAdEvent {
    public static final int loaded = 0;
    public static final int failed = 1;
    public static final int shown = 2;
    public static final int clicked = 3;
    public static final int dismissed = 4;
}

final class AdAdapterValueConstants {
    public static final String gameId = "gameId";
    public static final String appId = "appId";
    public static final String applicationKey = "applicationKey";
    public static final String sdkKey = "sdk_key";
    public static final String clientOptions = "clientOptions";
    public static final String allZoneIds = "allZoneIds";

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

final class AdAdapterConstants {
    public static final String unity = "UnityAdsAdapterConfiguration";
    public static final String ironSource = "IronSourceAdapterConfiguration";
    public static final String vungle = "VungleAdapterConfiguration";
}


public class AdManager {

    static AdManager adManager;

    MoPubInterstitial mMoPubInterstitial;


    public static AdManager getInstance() {
        if (adManager == null) {
            adManager = new AdManager();
        }
        return adManager;
    }

    void init()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                initializeMediationAdapters();
            }
        }).start();
    }


    void fetchInterstitial() {
        try {
            String adUnitId = AdIds.interstitial;
            mMoPubInterstitial = new MoPubInterstitial(MopubPlugin.activity, adUnitId);


            MoPub.initializeSdk(MopubPlugin.activity, new SdkConfiguration.Builder(adUnitId).build(), new SdkInitializationListener() {
                @Override
                public void onInitializationFinished() {
                    Log.d(MopubPlugin.TAG, "onInitializationFinished");
                    mMoPubInterstitial.load();
                }
            });

            mMoPubInterstitial.setInterstitialAdListener(new MoPubInterstitial.InterstitialAdListener() {
                @Override
                public void onInterstitialLoaded(MoPubInterstitial interstitial) {
                    if (MopubPlugin.INTERSTITIAL_EVENTS != null) {
                        MopubPlugin.INTERSTITIAL_EVENTS.success(InterstitialAdEvent.loaded);
                    }
                    Log.d(MopubPlugin.TAG, "MobPub Ad onBannerLoaded");
                }

                @Override
                public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
                    if (MopubPlugin.INTERSTITIAL_EVENTS != null) {
                        MopubPlugin.INTERSTITIAL_EVENTS.success(InterstitialAdEvent.failed);
                    }
                    Log.d(MopubPlugin.TAG, "MobPub Ad onBannerLoaded");
                }

                @Override
                public void onInterstitialShown(MoPubInterstitial interstitial) {
                    if (MopubPlugin.INTERSTITIAL_EVENTS != null) {
                        MopubPlugin.INTERSTITIAL_EVENTS.success(InterstitialAdEvent.shown);
                    }
                    Log.d(MopubPlugin.TAG, "MobPub Ad onBannerLoaded");
                }

                @Override
                public void onInterstitialClicked(MoPubInterstitial interstitial) {
                    if (MopubPlugin.INTERSTITIAL_EVENTS != null) {
                        MopubPlugin.INTERSTITIAL_EVENTS.success(InterstitialAdEvent.clicked);
                    }
                    Log.d(MopubPlugin.TAG, "MobPub Ad onBannerLoaded");
                }

                @Override
                public void onInterstitialDismissed(MoPubInterstitial interstitial) {
                    if (MopubPlugin.INTERSTITIAL_EVENTS != null) {
                        MopubPlugin.INTERSTITIAL_EVENTS.success(InterstitialAdEvent.dismissed);
                    }
                    Log.d(MopubPlugin.TAG, "MobPub Ad onBannerLoaded");
                }
            });

        } catch (Exception e) {
            if (MopubPlugin.INTERSTITIAL_EVENTS != null) {
                MopubPlugin.INTERSTITIAL_EVENTS.error("103", e.getMessage(), null);
            }
            Log.e(MopubPlugin.TAG, "fetchInterstitial" + e.getMessage());
        }
    }

    void showInterstitial() {
        try {
            if (mMoPubInterstitial == null) {
                return;
            } else if (mMoPubInterstitial.isReady()) {
                mMoPubInterstitial.show();
            }
        } catch (Exception e) {
            Log.e(MopubPlugin.TAG, "showInterstitial" + e.getMessage());
        }
    }

    void showRewardVideo() {
        try {
            final String adUnitId = AdIds.reward;
            Set<MoPubReward> availableRewards = MoPubRewardedVideos.getAvailableRewards(adUnitId);
            MoPubReward currentReward = null;
            if (availableRewards.size() > 0) {
                for (MoPubReward reward : availableRewards) {
                    currentReward = reward;
                    break;
                }
            }
            MoPubRewardedVideos.selectReward(adUnitId, currentReward);
            MoPubRewardedVideos.showRewardedVideo(adUnitId);
            Log.d(MopubPlugin.TAG, "MobPub Ad onRewardedVideoLoadSuccess");
        } catch (Exception e) {
            Log.e(MopubPlugin.TAG, "showRewardVideo" + RewardAdEvent.failed + e.getMessage());
        }
    }

    void fetchRewardVideo() {
        try {

            final String adUnitId = AdIds.reward;
            if (!MoPub.isSdkInitialized()) {
                MoPub.initializeSdk(MopubPlugin.activity, new SdkConfiguration.Builder(adUnitId).build(), new SdkInitializationListener() {
                    @Override
                    public void onInitializationFinished() {
                        Log.d(MopubPlugin.TAG, "onInitializationFinished");
                        MoPubRewardedVideos.loadRewardedVideo(adUnitId);
                    }
                });
            } else {
                MoPubRewardedVideos.loadRewardedVideo(adUnitId);
            }
            MoPubRewardedVideos.setRewardedVideoListener(new MoPubRewardedVideoListener() {
                @Override
                public void onRewardedVideoLoadSuccess(@NonNull String adUnitId) {
                    if (MopubPlugin.REWARD_EVENTS != null) {
                        MopubPlugin.REWARD_EVENTS.success(RewardAdEvent.loaded);
                    }
                    Log.d(MopubPlugin.TAG, "MobPub Ad onRewardedVideoLoadSuccess");
                }

                @Override
                public void onRewardedVideoLoadFailure(@NonNull String adUnitId, @NonNull MoPubErrorCode errorCode) {
                    if (MopubPlugin.REWARD_EVENTS != null) {
                        MopubPlugin.REWARD_EVENTS.success(RewardAdEvent.failed);
                    }
                    Log.d(MopubPlugin.TAG, "MobPub Ad onRewardedVideoLoadFailure");
                }

                @Override
                public void onRewardedVideoStarted(@NonNull String adUnitId) {
                    if (MopubPlugin.REWARD_EVENTS != null) {
                        MopubPlugin.REWARD_EVENTS.success(RewardAdEvent.started);
                    }
                    Log.d(MopubPlugin.TAG, "MobPub Ad onRewardedVideoStarted");
                }

                @Override
                public void onRewardedVideoPlaybackError(@NonNull String adUnitId, @NonNull MoPubErrorCode errorCode) {
                    if (MopubPlugin.REWARD_EVENTS != null) {
                        MopubPlugin.REWARD_EVENTS.success(RewardAdEvent.failed);
                    }
                    Log.d(MopubPlugin.TAG, "MobPub Ad onRewardedVideoPlaybackError");
                }

                @Override
                public void onRewardedVideoClicked(@NonNull String adUnitId) {
                    if (MopubPlugin.REWARD_EVENTS != null) {
                        MopubPlugin.REWARD_EVENTS.success(RewardAdEvent.clicked);
                    }
                    Log.d(MopubPlugin.TAG, "MobPub Ad onRewardedVideoClicked");
                }

                @Override
                public void onRewardedVideoClosed(@NonNull String adUnitId) {
                    if (MopubPlugin.REWARD_EVENTS != null) {
                        MopubPlugin.REWARD_EVENTS.success(RewardAdEvent.completed);
                        //Mani - In Android, After closing the first video only second video is gettin loaded, So sending completed event to match with iOS.
                    }
                    Log.d(MopubPlugin.TAG, "MobPub Ad onRewardedVideoClosed");
                }

                @Override
                public void onRewardedVideoCompleted(@NonNull Set<String> adUnitIds, @NonNull MoPubReward reward) {
                    if (MopubPlugin.REWARD_EVENTS != null) {
                        MopubPlugin.REWARD_EVENTS.success(RewardAdEvent.completed);
                    }
                    Log.d(MopubPlugin.TAG, "MobPub Ad onRewardedVideoCompleted");

                }
            });
        } catch (Exception e) {
            if (MopubPlugin.REWARD_EVENTS != null) {
                MopubPlugin.REWARD_EVENTS.error("104", RewardAdEvent.failed + e.getMessage(), null);
            }
            Log.e(MopubPlugin.TAG, "showRewardVideo" + e.getMessage());
        }
    }


    void initializeMediationAdapters() {

        try {

            AdColonyAdapterConfigurationCustom adColonyAdapterConfiguration=new AdColonyAdapterConfigurationCustom();//Since Library forces client option should not be null, but actual adcolony allows client as optional. We are using AdColonyAdapterConfigurationCustom
            Map<String, String> adColonyConfiguration = new HashMap<>();
            adColonyConfiguration.put(AdAdapterValueConstants.appId,AdIds.adColonyAppId);
            String[] zoneIds={AdIds.adColonyBannerZoneId,AdIds.adColonyInterstitialZoneId,AdIds.adColonyRewardedZoneId};
            adColonyConfiguration.put(AdAdapterValueConstants.allZoneIds,new JSONArray(Arrays.asList(zoneIds)).toString());
            adColonyAdapterConfiguration.initializeNetwork(MopubPlugin.activity,adColonyConfiguration, new OnNetworkInitializationFinishedListener() {
                @Override
                public void onNetworkInitializationFinished(@NonNull Class<? extends AdapterConfiguration> clazz, @NonNull MoPubErrorCode moPubErrorCode) {
                    Log.d(MopubPlugin.TAG, " AdColony onNetworkInitializationFinished "+moPubErrorCode);
                }
            });

            VungleAdapterConfiguration vungleAdapterConfiguration = new VungleAdapterConfiguration();
            Map<String, String> vungleConfiguration = new HashMap<>();
            vungleConfiguration.put(AdAdapterValueConstants.appId, AdIds.vungleAppId);
            vungleAdapterConfiguration.initializeNetwork(MopubPlugin.activity, vungleConfiguration, new OnNetworkInitializationFinishedListener() {
                @Override
                public void onNetworkInitializationFinished(@NonNull Class<? extends AdapterConfiguration> clazz, @NonNull MoPubErrorCode moPubErrorCode) {
                    Log.d(MopubPlugin.TAG, " Vungle onNetworkInitializationFinished "+moPubErrorCode);
                }
            });

            AppLovinAdapterConfiguration appLovinAdapterConfiguration = new AppLovinAdapterConfiguration();
            Map<String, String> appLovinConfiguration = new HashMap<String, String>();
            appLovinConfiguration.put(AdAdapterValueConstants.sdkKey,AdIds.appLovinSdkKey);
            appLovinAdapterConfiguration.initializeNetwork(MopubPlugin.activity, appLovinConfiguration, new OnNetworkInitializationFinishedListener() {
                @Override
                public void onNetworkInitializationFinished(@NonNull Class<? extends AdapterConfiguration> clazz, @NonNull MoPubErrorCode moPubErrorCode) {
                    Log.d(MopubPlugin.TAG, "AppLovin onNetworkInitializationFinished "+moPubErrorCode);
                }
            });

            IronSourceAdapterConfiguration ironSourceAdapterConfiguration = new IronSourceAdapterConfiguration();
            Map<String, String> ironSourceConfiguration = new HashMap<>();
            ironSourceConfiguration.put(AdAdapterValueConstants.applicationKey, AdIds.ironSourceApplicationKey);
            ironSourceAdapterConfiguration.initializeNetwork(MopubPlugin.activity, ironSourceConfiguration, new OnNetworkInitializationFinishedListener() {
                @Override
                public void onNetworkInitializationFinished(@NonNull Class<? extends AdapterConfiguration> clazz, @NonNull MoPubErrorCode moPubErrorCode) {
                    Log.d(MopubPlugin.TAG, "IronSource onNetworkInitializationFinished "+moPubErrorCode);
                }
            });

            FacebookAdapterConfiguration facebookAdapterConfiguration = new FacebookAdapterConfiguration();
            facebookAdapterConfiguration.initializeNetwork(MopubPlugin.activity, null, new OnNetworkInitializationFinishedListener() {
                @Override
                public void onNetworkInitializationFinished(@NonNull Class<? extends AdapterConfiguration> clazz, @NonNull MoPubErrorCode moPubErrorCode) {
                    Log.d(MopubPlugin.TAG, "Facebook onNetworkInitializationFinished "+moPubErrorCode);
                }
            });


            Map<String, String> unityConfiguration = new HashMap<>();
            unityConfiguration.put(AdAdapterValueConstants.gameId,AdIds.unityGameId);
            UnityAdsAdapterConfiguration unityAdsAdapterConfiguration=new UnityAdsAdapterConfiguration();
            unityAdsAdapterConfiguration.initializeNetwork(MopubPlugin.activity, unityConfiguration, new OnNetworkInitializationFinishedListener() {
                @Override
                public void onNetworkInitializationFinished(@NonNull Class<? extends AdapterConfiguration> clazz, @NonNull MoPubErrorCode moPubErrorCode) {
                    Log.d(MopubPlugin.TAG, "Unity onNetworkInitializationFinished "+moPubErrorCode);
                }
            });


        } catch (Exception e) {
            Log.e(MopubPlugin.TAG, "initializeMediationAdapters" + e.getMessage());
        }

    }
}
