package com.firekamp.mopubwrapper;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

class AdConfiguration {
    final String moPubBannerId;
    final String moPubInterstitialId;
    final String moPubRewardId;

    //AdColony
    final String adColonyAppId;
    private final String adColonyBannerZoneId;
    private final String adColonyInterstitialZoneId;
    private final String adColonyRewardedZoneId;

    //Vungle
    final String vungleAppId;

    //IronSource
    final String ironSourceApplicationKey;

    //AppLovin
    final String appLovinSdkKey;

    //Unity
    final String unityGameId;

    private final boolean isFacebookEnabled;

    AdConfiguration(String moPubBannerId,
                    String moPubInterstitialId,
                    String moPubRewardId,
                    String adColonyAppId,
                    String adColonyBannerZoneId,
                    String adColonyInterstitialZoneId,
                    String adColonyRewardedZoneId,
                    String vungleAppId,
                    String ironSourceApplicationKey,
                    String appLovinSdkKey,
                    String unityGameId,
                    boolean isFacebookEnabled) {
        this.moPubBannerId = moPubBannerId;
        this.moPubInterstitialId = moPubInterstitialId;
        this.moPubRewardId = moPubRewardId;
        this.adColonyAppId = adColonyAppId;
        this.adColonyBannerZoneId = adColonyBannerZoneId;
        this.adColonyInterstitialZoneId = adColonyInterstitialZoneId;
        this.adColonyRewardedZoneId = adColonyRewardedZoneId;
        this.vungleAppId = vungleAppId;
        this.ironSourceApplicationKey = ironSourceApplicationKey;
        this.appLovinSdkKey = appLovinSdkKey;
        this.unityGameId = unityGameId;
        this.isFacebookEnabled = isFacebookEnabled;
    }

    String firstValidAdId() {
        if (isBannerEnabled()) {
            return moPubBannerId;
        }

        if (isInterstitialEnabled()) {
            return moPubInterstitialId;
        }

        if (isRewardEnabled()) {
            return moPubRewardId;
        }

        return null;
    }

    boolean isUnityEnabled() {
        return !TextUtils.isEmpty(unityGameId);
    }

    boolean isAppLovinEnabled() {
        return !TextUtils.isEmpty(appLovinSdkKey);
    }

    boolean isIronSourceEnabled() {
        return !TextUtils.isEmpty(ironSourceApplicationKey);
    }

    boolean isVungleEnabled() {
        return !TextUtils.isEmpty(vungleAppId);
    }

    boolean isAdColonyEnabled() {
        return !TextUtils.isEmpty(adColonyAppId);
    }

    List<String> adColonyZoneIds() {
        List<String> zoneIds = new ArrayList<String>();

        if (!TextUtils.isEmpty(adColonyBannerZoneId)) {
            zoneIds.add(adColonyBannerZoneId);
        }

        if (!TextUtils.isEmpty(adColonyRewardedZoneId)) {
            zoneIds.add(adColonyRewardedZoneId);
        }

        if (!TextUtils.isEmpty(adColonyInterstitialZoneId)) {
            zoneIds.add(adColonyInterstitialZoneId);
        }

        return zoneIds;
    }

    boolean isBannerEnabled() {
        return !TextUtils.isEmpty(moPubBannerId);
    }

    boolean isRewardEnabled() {
        return !TextUtils.isEmpty(moPubRewardId);
    }

    boolean isInterstitialEnabled() {
        return !TextUtils.isEmpty(moPubInterstitialId);
    }

    boolean isFacebookEnabled() {
        return isFacebookEnabled;
    }

}