package com.firekamp.mopubwrapper;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.adcolony.sdk.AdColony;
import com.adcolony.sdk.AdColonyAppOptions;
import com.mopub.common.AdapterConfiguration;
import com.mopub.common.MoPub;
import com.mopub.common.MoPubReward;
import com.mopub.common.OnNetworkInitializationFinishedListener;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.common.privacy.PersonalInfoManager;
import com.mopub.common.util.Json;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

enum RewardAdEvent {
    NOT_FETCHED(0),
    FAILED(1),
    LOADED(2),
    STARTED(3),
    COMPLETED(4),
    CLICKED(5),
    CLOSED(6);

    final private int value;

    RewardAdEvent(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

enum InterstitialAdEvent {
    LOADED(0),
    FAILED(1),
    SHOWED(2),
    CLICKED(3),
    DISMISSED(4);

    final private int value;

    InterstitialAdEvent(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

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
    public static final String configure = "configure";
    public static final String fetchAndLoadBanner = "fetchAndLoadBanner";
    public static final String prefetchInterstitial = "prefetchInterstitial";
    public static final String prefetchReward = "prefetchReward";
    public static final String showInterstitialAd = "showInterstitialAd";
    public static final String showRewardAd = "showRewardAd";
    public static final String showBanner = "showBanner";
    public static final String hideBanner = "hideBanner";
}


public class AdManager {
    private static AdManager adManager;
    private MoPubInterstitial mMoPubInterstitial;
    private MoPubView bannerView;
    private AdEvents adEvents;
    private AdConfiguration adConfiguration;

    static AdManager getInstance() {
        if (adManager == null) {
            adManager = new AdManager();
        }
        return adManager;
    }

    MoPubView getBannerView() {
        return bannerView;
    }

    // On iOS: This will load and show the banner on the root view
    // On Android: This will load and show the banners for platform views
    void fetchAndLoadBanner() {
        if (adConfiguration.isBannerEnabled() && bannerView != null) {
            bannerView.loadAd();
            toggleBanner(true);
        } else {
            throw new IllegalStateException("banner not and trying to fetch");
        }
    }

    private void toggleBanner(boolean isShow) {
        if (bannerView != null && bannerView.getParent() != null) {
            ((ViewGroup) bannerView.getParent()).setVisibility(isShow ? View.VISIBLE : View.GONE);
        }
    }

    void hideBanner() {
        toggleBanner(false);
    }

    void showBanner() {
        toggleBanner(true);
    }

    void fetchInterstitial() {
        if (mMoPubInterstitial != null) {
            mMoPubInterstitial.load();
            Log.d(MopubwrapperPlugin.PLUGIN_TAG, "Interstitial Load Called");
        } else {
            throw new IllegalStateException("Interstitial not configured and trying to fetch");
        }
    }

    void showInterstitial() {
        if (mMoPubInterstitial != null) {
            if (mMoPubInterstitial.isReady()) {
                mMoPubInterstitial.show();
                Log.d(MopubwrapperPlugin.PLUGIN_TAG, "Interstitial Shown");
            } else {
                adEvents.interstitialEvent(InterstitialAdEvent.FAILED);
                Log.e(MopubwrapperPlugin.PLUGIN_TAG, "Interstitial is not ready");
            }
        } else {
            throw new IllegalStateException("Interstitial not configured and trying to show");
        }
    }

    void showRewardVideo() {
        try {
            final String rewardId = adConfiguration.moPubRewardId;
            if (MoPubRewardedVideos.hasRewardedVideo(rewardId)) {
                if (MoPubRewardedVideos.getAvailableRewards(rewardId).iterator().hasNext()) {
                    MoPubReward currentReward = MoPubRewardedVideos.getAvailableRewards(rewardId).iterator().next();
                    MoPubRewardedVideos.selectReward(rewardId, currentReward);
                    MoPubRewardedVideos.showRewardedVideo(rewardId);
                    Log.d(MopubwrapperPlugin.PLUGIN_TAG, "Reward Show Called");
                } else {
                    adEvents.rewardEvent(RewardAdEvent.FAILED);
                    Log.e(MopubwrapperPlugin.PLUGIN_TAG, "Reward not found");
                }
            } else {
                adEvents.rewardEvent(RewardAdEvent.NOT_FETCHED);
                Log.e(MopubwrapperPlugin.PLUGIN_TAG, "Reward not found");
            }
        } catch (Exception e) {
            adEvents.rewardEvent(RewardAdEvent.FAILED);
            Log.e(MopubwrapperPlugin.PLUGIN_TAG, "showRewardVideo" + RewardAdEvent.FAILED + e.getMessage());
        }
    }

    void fetchRewardVideo() {
        if (adConfiguration.isRewardEnabled()) {
            MoPubRewardedVideos.loadRewardedVideo(adConfiguration.moPubRewardId);
            Log.d(MopubwrapperPlugin.PLUGIN_TAG, "Reward video load called");
        } else {
            throw new IllegalStateException("Reward not configured");
        }
    }

    void configure(@NonNull AdConfiguration configuration, @NonNull AdEvents adEventsCallBack) {
        try {
            this.adConfiguration = configuration;
            adEvents = adEventsCallBack;
            SdkConfiguration.Builder builder = new SdkConfiguration.Builder(adConfiguration.firstValidAdId());
            //builder.withLegitimateInterestAllowed(true); //TODO: Need to uncomment this. Once more researches have been about GDPR
            MoPub.initializeSdk(MopubwrapperPlugin.activity, builder.build(), new SdkInitializationListener() {
                @Override
                public void onInitializationFinished() {
                    //TODO: Need to uncomment this. Once more researches have been about GDPR
                    //PersonalInfoManager mPersonalInfoManager = MoPub.getPersonalInformationManager();
                    //mPersonalInfoManager.forceGdprApplies();
                    configureThirdPartyNetworks();
                    Log.d(MopubwrapperPlugin.PLUGIN_TAG, "SDK initialization Finished");
                }
            });

            if (adConfiguration.isBannerEnabled()) {
                configureBanner();
            }

            if (adConfiguration.isInterstitialEnabled()) {
                configureInterstitial();
            }

            if (adConfiguration.isRewardEnabled()) {
                configureReward();
            }
        } catch (Exception e) {
            Log.e(MopubwrapperPlugin.PLUGIN_TAG, "configure" + e.getMessage());
        }

    }

    private void configureThirdPartyNetworks() {
        //Third party networks
        if (adConfiguration.isAdColonyEnabled()) {
            final AdColonyAdapterConfiguration adColonyAdapterConfiguration = new AdColonyAdapterConfiguration();
            Map<String, String> adColonyConfiguration = new HashMap<>();
            adColonyConfiguration.put(AdAdapterValueConstants.appId, adConfiguration.adColonyAppId);
            final List<String> zoneIds = adConfiguration.adColonyZoneIds();
            adColonyConfiguration.put(AdAdapterValueConstants.allZoneIds, new JSONArray(zoneIds).toString());
            adColonyAdapterConfiguration.initializeNetwork(MopubwrapperPlugin.activity, adColonyConfiguration, new OnNetworkInitializationFinishedListener() {
                @Override
                public void onNetworkInitializationFinished(@NonNull Class<? extends AdapterConfiguration> clazz, @NonNull MoPubErrorCode moPubErrorCode) {
                    Log.d(MopubwrapperPlugin.PLUGIN_TAG, "AdColony onNetworkInitializationFinished " + moPubErrorCode);
                }
            });
        }

        //TODO: Mani fix, vungle is throwing errors
        if (adConfiguration.isVungleEnabled()) {
            final VungleAdapterConfiguration vungleAdapterConfiguration = new VungleAdapterConfiguration();
            Map<String, String> vungleConfiguration = new HashMap<>();
            vungleConfiguration.put(AdAdapterValueConstants.appId, adConfiguration.vungleAppId);
            vungleAdapterConfiguration.initializeNetwork(MopubwrapperPlugin.activity, vungleConfiguration, new OnNetworkInitializationFinishedListener() {
                @Override
                public void onNetworkInitializationFinished(@NonNull Class<? extends AdapterConfiguration> clazz, @NonNull MoPubErrorCode moPubErrorCode) {
                    Log.d(MopubwrapperPlugin.PLUGIN_TAG, "Vungle onNetworkInitializationFinished " + moPubErrorCode);
                }
            });
        }

        if (adConfiguration.isAppLovinEnabled()) {
            AppLovinAdapterConfiguration appLovinAdapterConfiguration = new AppLovinAdapterConfiguration();
            Map<String, String> appLovinConfiguration = new HashMap<String, String>();
            appLovinConfiguration.put(AdAdapterValueConstants.sdkKey, adConfiguration.appLovinSdkKey);
            appLovinAdapterConfiguration.initializeNetwork(MopubwrapperPlugin.activity, appLovinConfiguration, new OnNetworkInitializationFinishedListener() {
                @Override
                public void onNetworkInitializationFinished(@NonNull Class<? extends AdapterConfiguration> clazz, @NonNull MoPubErrorCode moPubErrorCode) {
                    Log.d(MopubwrapperPlugin.PLUGIN_TAG, "AppLovin onNetworkInitializationFinished " + moPubErrorCode);
                }
            });
        }

        if (adConfiguration.isIronSourceEnabled()) {
            IronSourceAdapterConfiguration ironSourceAdapterConfiguration = new IronSourceAdapterConfiguration();
            Map<String, String> ironSourceConfiguration = new HashMap<>();
            ironSourceConfiguration.put(AdAdapterValueConstants.applicationKey, adConfiguration.ironSourceApplicationKey);
            ironSourceAdapterConfiguration.initializeNetwork(MopubwrapperPlugin.activity, ironSourceConfiguration, new OnNetworkInitializationFinishedListener() {
                @Override
                public void onNetworkInitializationFinished(@NonNull Class<? extends AdapterConfiguration> clazz, @NonNull MoPubErrorCode moPubErrorCode) {
                    Log.d(MopubwrapperPlugin.PLUGIN_TAG, "IronSource onNetworkInitializationFinished " + moPubErrorCode);
                }
            });
        }

        if (adConfiguration.isFacebookEnabled()) {
            FacebookAdapterConfiguration facebookAdapterConfiguration = new FacebookAdapterConfiguration();
            facebookAdapterConfiguration.initializeNetwork(MopubwrapperPlugin.activity, null, new OnNetworkInitializationFinishedListener() {
                @Override
                public void onNetworkInitializationFinished(@NonNull Class<? extends AdapterConfiguration> clazz, @NonNull MoPubErrorCode moPubErrorCode) {
                    Log.d(MopubwrapperPlugin.PLUGIN_TAG, "Facebook onNetworkInitializationFinished " + moPubErrorCode);
                }
            });
        }

        if (adConfiguration.isUnityEnabled()) {
            Map<String, String> unityConfiguration = new HashMap<>();
            unityConfiguration.put(AdAdapterValueConstants.gameId, adConfiguration.unityGameId);
            UnityAdsAdapterConfiguration unityAdsAdapterConfiguration = new UnityAdsAdapterConfiguration();
            unityAdsAdapterConfiguration.initializeNetwork(MopubwrapperPlugin.activity, unityConfiguration, new OnNetworkInitializationFinishedListener() {
                @Override
                public void onNetworkInitializationFinished(@NonNull Class<? extends AdapterConfiguration> clazz, @NonNull MoPubErrorCode moPubErrorCode) {
                    Log.d(MopubwrapperPlugin.PLUGIN_TAG, "Unity onNetworkInitializationFinished " + moPubErrorCode);
                }
            });
        }
    }

    private void configureReward() {
        //Reward Video
        MoPubRewardedVideos.setRewardedVideoListener(new MoPubRewardedVideoListener() {
            @Override
            public void onRewardedVideoLoadSuccess(@NonNull String adUnitId) {
                adEvents.rewardEvent(RewardAdEvent.LOADED);
                Log.d(MopubwrapperPlugin.PLUGIN_TAG, "MobPub onRewardedVideoLoadSuccess");
            }

            @Override
            public void onRewardedVideoLoadFailure(@NonNull String adUnitId, @NonNull MoPubErrorCode errorCode) {
                adEvents.rewardEvent(RewardAdEvent.FAILED);
                Log.d(MopubwrapperPlugin.PLUGIN_TAG, "MobPub onRewardedVideoLoadFailure");
            }

            @Override
            public void onRewardedVideoStarted(@NonNull String adUnitId) {
                adEvents.rewardEvent(RewardAdEvent.STARTED);
                Log.d(MopubwrapperPlugin.PLUGIN_TAG, "MobPub onRewardedVideoStarted");
            }

            @Override
            public void onRewardedVideoPlaybackError(@NonNull String adUnitId, @NonNull MoPubErrorCode errorCode) {
                adEvents.rewardEvent(RewardAdEvent.FAILED);
                Log.d(MopubwrapperPlugin.PLUGIN_TAG, "MobPub onRewardedVideoPlaybackError");
            }

            @Override
            public void onRewardedVideoClicked(@NonNull String adUnitId) {
                adEvents.rewardEvent(RewardAdEvent.CLICKED);
                Log.d(MopubwrapperPlugin.PLUGIN_TAG, "MobPub onRewardedVideoClicked");
            }

            @Override
            public void onRewardedVideoClosed(@NonNull String adUnitId) {
                adEvents.rewardEvent(RewardAdEvent.COMPLETED);
                //Mani - In Android, After closing the first video only second video is getting loaded, So sending completed event to match with iOS.
                Log.d(MopubwrapperPlugin.PLUGIN_TAG, "MobPub onRewardedVideoClosed");
            }

            @Override
            public void onRewardedVideoCompleted(@NonNull Set<String> adUnitIds, @NonNull MoPubReward reward) {
                adEvents.rewardEvent(RewardAdEvent.COMPLETED);
                Log.d(MopubwrapperPlugin.PLUGIN_TAG, "MobPub onRewardedVideoCompleted");
            }
        });
    }

    private void configureInterstitial() {
        //Interstitial
        mMoPubInterstitial = new MoPubInterstitial(MopubwrapperPlugin.activity, adConfiguration.moPubInterstitialId);
        mMoPubInterstitial.setInterstitialAdListener(new MoPubInterstitial.InterstitialAdListener() {
            @Override
            public void onInterstitialLoaded(MoPubInterstitial interstitial) {
                adEvents.interstitialEvent(InterstitialAdEvent.LOADED);
                Log.d(MopubwrapperPlugin.PLUGIN_TAG, "MobPub onInterstitialLoaded");
            }

            @Override
            public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
                adEvents.interstitialEvent(InterstitialAdEvent.FAILED);
                Log.d(MopubwrapperPlugin.PLUGIN_TAG, "MobPub onInterstitialFailed");
            }

            @Override
            public void onInterstitialShown(MoPubInterstitial interstitial) {
                adEvents.interstitialEvent(InterstitialAdEvent.SHOWED);
                Log.d(MopubwrapperPlugin.PLUGIN_TAG, "MobPub onInterstitialShown");
            }

            @Override
            public void onInterstitialClicked(MoPubInterstitial interstitial) {
                adEvents.interstitialEvent(InterstitialAdEvent.CLICKED);
                Log.d(MopubwrapperPlugin.PLUGIN_TAG, "MobPub onInterstitialClicked");
            }

            @Override
            public void onInterstitialDismissed(MoPubInterstitial interstitial) {
                adEvents.interstitialEvent(InterstitialAdEvent.DISMISSED);
                Log.d(MopubwrapperPlugin.PLUGIN_TAG, "MobPub onInterstitialDismissed");
            }
        });
    }


    private void configureBanner() {
        //Banner
        bannerView = new MoPubView(MopubwrapperPlugin.activity);
        bannerView.setAdSize(MoPubView.MoPubAdSize.MATCH_VIEW);
        bannerView.setAdUnitId(adConfiguration.moPubBannerId);
        bannerView.setBackgroundColor(ContextCompat.getColor(MopubwrapperPlugin.activity, android.R.color.transparent));
        bannerView.setBannerAdListener(new MoPubView.BannerAdListener() {
            @Override
            public void onBannerLoaded(@NonNull MoPubView banner) {
                Log.d(MopubwrapperPlugin.PLUGIN_TAG, "MobPub onBannerLoaded");
            }

            @Override
            public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
                Log.d(MopubwrapperPlugin.PLUGIN_TAG, "MobPub Banner Loading Failed. Error: " + errorCode);
            }

            @Override
            public void onBannerClicked(MoPubView banner) {
                Log.d(MopubwrapperPlugin.PLUGIN_TAG, "MobPub onBannerClicked");
            }

            @Override
            public void onBannerExpanded(MoPubView banner) {
                Log.d(MopubwrapperPlugin.PLUGIN_TAG, "MobPub Banner Expanded");
            }

            @Override
            public void onBannerCollapsed(MoPubView banner) {
                Log.d(MopubwrapperPlugin.PLUGIN_TAG, "MobPub Banner Collapsed");
            }
        });
    }
}
