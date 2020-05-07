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
    let moPubBannerId: String
    let moPubInterstitialId: String
    let moPubRewardId: String
    
    //AdColony
    let adColonyAppId: String
    let adColonyBannerZoneId: String
    let adColonyInterstitialZoneId: String
    let adColonyRewardedZoneId: String
    
    //Vungle
    let vungleAppId: String
    //IronSource
    let ironSourceApplicationKey: String
    
    //AppLovin
    let appLovinSdkKey: String
    
    //Unity
    let unityGameId: String
    
    //Facebook
    let isFacebookEnabled: Bool
    
    
    init(moPubBannerId:String,
         moPubInterstitialId:String,
         moPubRewardId:String,
         adColonyAppId:String,
         adColonyBannerZoneId:String,
         adColonyInterstitialZoneId:String,
         adColonyRewardedZoneId:String,
         vungleAppId:String,
         ironSourceApplicationKey:String,
         appLovinSdkKey:String,
         unityGameId:String,
         isFacebookEnabled:Bool)
    {
        self.moPubBannerId=moPubBannerId;
        self.moPubInterstitialId=moPubInterstitialId;
        self.moPubRewardId=moPubRewardId;
        self.adColonyAppId=adColonyAppId;
        self.adColonyBannerZoneId=adColonyBannerZoneId;
        self.adColonyInterstitialZoneId=adColonyInterstitialZoneId;
        self.adColonyRewardedZoneId=adColonyRewardedZoneId;
        self.vungleAppId=vungleAppId;
        self.ironSourceApplicationKey=ironSourceApplicationKey;
        self.appLovinSdkKey=appLovinSdkKey;
        self.unityGameId=unityGameId;
        self.isFacebookEnabled=isFacebookEnabled;
    }
    
    
    func firstValidAdId() -> String!
    {
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
        return !unityGameId.isEmpty
    }
    
    func isAppLovinEnabled() -> Bool {
        return !appLovinSdkKey.isEmpty
    }
    
    func isIronSourceEnabled() -> Bool {
        return !ironSourceApplicationKey.isEmpty
    }
    
    func isVungleEnabled() -> Bool {
        return !vungleAppId.isEmpty
    }
    
    func isAdColonyEnabled() -> Bool {
        return !adColonyAppId.isEmpty
    }
    
    func isBannerEnabled() -> Bool {
        return !moPubBannerId.isEmpty
    }
    
    func isRewardEnabled() -> Bool {
        return !moPubRewardId.isEmpty
    }
    
    func isInterstitialEnabled() -> Bool {
        return !moPubRewardId.isEmpty
    }
    
    func getZoneIds() -> [String]
    {
        var zoneIds=[]
        if(!adColonyBannerZoneId.isEmpty)
        {
            zoneIds.append(adColonyBannerZoneId)
        }
        if(!adColonyInterstitialZoneId.isEmpty)
        {
            zoneIds.append(adColonyInterstitialZoneId)
        }
        if(!adColonyRewardedZoneId.isEmpty)
        {
            zoneIds.append(adColonyRewardedZoneId)
        }
        return zoneIds
    }
    
}
