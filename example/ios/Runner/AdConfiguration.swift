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
    private let moPubBannerId: String
    private let moPubInterstitialId: String
    private let moPubRewardId: String
    
    //AdColony
    private let adColonyAppId: String
    private let adColonyBannerZoneId: String
    private let adColonyInterstitialZoneId: String
    private let adColonyRewardedZoneId: String

    //Vungle
    private let vungleAppId: String
    //IronSource
    private let ironSourceApplicationKey: String
    
    //AppLovin
    private let appLovinSdkKey: String
    
    //Unity
    private let unityGameId: String
    
    //Facebook
    private let isFacebookEnabled: Bool
     
    
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
     
    
}
