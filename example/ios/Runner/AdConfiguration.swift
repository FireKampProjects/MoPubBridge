//
//  AdConfiguration.swift
//  Runner
//
//  Created by Manikandan Selvanathan on 2020-05-07.
//  Copyright © 2020 The Chromium Authors. All rights reserved.
//

import Foundation


class AdConfiguration
{
    let moPubBannerId: String?
    let moPubInterstitialId: String?
    let moPubRewardId: String?
    
    //AdColony
    let adColonyAppId: String?
    let adColonyBannerZoneId: String?
    let adColonyInterstitialZoneId: String?
    let adColonyRewardedZoneId: String?
    
    //Vungle
    let vungleAppId: String?
    //IronSource
    let ironSourceApplicationKey: String?
    
    //AppLovin
    let appLovinSdkKey: String?
    
    //Unity
    let unityGameId: String?
    
    //Facebook
    let isFacebookEnabled: Bool?
    
    
    init(moPubBannerId : String?,
         moPubInterstitialId:String?,
         moPubRewardId:String?,
         adColonyAppId:String?,
         adColonyBannerZoneId:String?,
         adColonyInterstitialZoneId:String?,
         adColonyRewardedZoneId:String?,
         vungleAppId:String?,
         ironSourceApplicationKey:String?,
         appLovinSdkKey:String?,
         unityGameId:String?,
         isFacebookEnabled:Bool){
        self.moPubBannerId = moPubBannerId
        self.moPubInterstitialId = moPubInterstitialId
        self.moPubRewardId = moPubRewardId
        self.adColonyAppId = adColonyAppId
        self.adColonyBannerZoneId = adColonyBannerZoneId
        self.adColonyInterstitialZoneId = adColonyInterstitialZoneId
        self.adColonyRewardedZoneId = adColonyRewardedZoneId
        self.vungleAppId = vungleAppId
        self.ironSourceApplicationKey = ironSourceApplicationKey
        self.appLovinSdkKey = appLovinSdkKey
        self.unityGameId = unityGameId
        self.isFacebookEnabled = isFacebookEnabled
    }
    
    
    func firstValidAdId() -> String?{
        if (isBannerEnabled()) {
            return moPubBannerId;
        }
        if (isInterstitialEnabled()) {
            return moPubInterstitialId;
        }
        if (isRewardEnabled()) {
            return moPubRewardId;
        }
        return nil;
    }
    
    func isUnityEnabled() -> Bool {
        return !(unityGameId?.isEmpty ?? true)
    }
    
    func isAppLovinEnabled() -> Bool {
        return !(appLovinSdkKey?.isEmpty ?? true)
    }
    
    func isIronSourceEnabled() -> Bool {
        return !(ironSourceApplicationKey?.isEmpty ?? true)
    }
    
    func isVungleEnabled() -> Bool {
        return !(vungleAppId?.isEmpty ?? true)
    }
    
    func isAdColonyEnabled() -> Bool {
        return !(adColonyAppId?.isEmpty ?? true)
    }
    
    func isBannerEnabled() -> Bool {
        return !(moPubBannerId?.isEmpty ?? true)
    }
    
    func isRewardEnabled() -> Bool {
        return !(moPubRewardId?.isEmpty ?? true)
    }
    
    func isInterstitialEnabled() -> Bool {
        return !(moPubInterstitialId?.isEmpty ?? true)
    }
    
    func getZoneIds() -> [String]{
        var zoneIds:[String]=[]
        if let bannerId = adColonyBannerZoneId, !bannerId.isEmpty {
            zoneIds.append(bannerId)
        }
        if let interstitialId = adColonyBannerZoneId, !interstitialId.isEmpty {
            zoneIds.append(interstitialId)
        }
        if let rewardId = adColonyBannerZoneId, !rewardId.isEmpty {
            zoneIds.append(rewardId)
        }
        return zoneIds
    }
    
}
