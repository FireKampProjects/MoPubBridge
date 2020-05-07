//
//  AdManager.swift
//  Runner
//
//  Created by Kamran Pirwani on 3/21/20.
//  Copyright © 2020 The Chromium Authors. All rights reserved.
//

import Foundation
import MoPub

private enum AdAdapterValueConstants: String {
    case gameId = "gameId"
    case appId = "appId"
    case applicationKey = "applicationKey"
    case allZoneIds = "allZoneIds"
}

private enum AdAdapterConstants: String {
    case unity = "UnityAdsAdapterConfiguration"
    case ironSource = "IronSourceAdapterConfiguration"
    case vungle = "VungleAdapterConfiguration"
    case adColony = "AdColonyAdapterConfiguration"
    
}

public enum InterstitialAdEvent: Int {
    case loaded = 0
    case failed = 1
    case shown = 3
    case clicked = 4
    case dismissed = 5
}

public enum RewardAdEvent: Int {
    case notFetched = 0
    case failed = 1
    case loaded = 2
    case started = 3
    case completed = 4
    case clicked = 5
    case closed = 6
}

@objcMembers
public class AdManager: NSObject {
    static let shared = AdManager()
    
    private var adConfiguration:AdConfiguration?
    
    public var interstitialStream: ((_ status: InterstitialAdEvent) -> (Void))?
    public var rewardStream: ((_ status: RewardAdEvent) -> (Void))?
    
    private var bannerView: MPAdView?
    private var interstitial: MPInterstitialAdController?
    
    public var rootViewController: UIViewController!
    
    private var bannerAdSize: CGSize {
        let preset = UIDevice.current.userInterfaceIdiom == .pad ? kMPPresetMaxAdSize90Height : kMPPresetMaxAdSize50Height
        return preset
    }
    
    func configure(adConfig:AdConfiguration){
        self.adConfiguration=adConfig
        if(adConfig.isBannerEnabled()){
            configureBanner(adConfig)
        }
        if(adConfig.isInterstitialEnabled()){
            configureInterstitial(adConfig)
        }
        if(adConfig.isRewardEnabled()){
            configrueRewardVideo(adConfig)
        }
        configureThirdPartyNetwork(adConfig)
    }
    
    func configureBanner(_ adConfig:AdConfiguration){
        bannerView = MPAdView(adUnitId:adConfig.moPubBannerId)
        bannerView?.frame = CGRect(x: 0, y: 0, width: UIScreen.main.bounds.size.width, height: self.bannerAdSize.height)
        bannerView?.delegate = self
    }
    
    func configrueRewardVideo(_ adConfig:AdConfiguration){
        MPRewardedVideo.setDelegate(self, forAdUnitId: adConfig.moPubRewardId)
    }
    
    func configureInterstitial(_ adConfig:AdConfiguration){
        interstitial = MPInterstitialAdController(forAdUnitId: adConfig.moPubInterstitialId)
        interstitial?.delegate = self
    }
    
    func fetchAndLoadBanner() {
        if let _ = bannerView?.superview {
            print("Fetching and loading when already visible")
        } else {
            print("Fetching banner ad")
            bannerView?.loadAd(withMaxAdSize: bannerAdSize)
        }
    }
    
    func configureThirdPartyNetwork(_ adConfig : AdConfiguration){
        let config = MPMoPubConfiguration(adUnitIdForAppInitialization: adConfig.firstValidAdId())
        let mediaConfigurations:NSMutableDictionary = [:]
        
        if(adConfig.isAdColonyEnabled()){
            let adColonySettings = [AdAdapterValueConstants.appId.rawValue: adConfig.adColonyAppId ?? "",AdAdapterValueConstants.allZoneIds.rawValue:adConfig.getZoneIds()] as [String : Any]
            mediaConfigurations[AdAdapterConstants.adColony.rawValue] = adColonySettings
        }
        
        if(adConfig.isUnityEnabled()){
            let unitySettings = [AdAdapterConstants.unity.rawValue: adConfig.unityGameId]
            mediaConfigurations[AdAdapterConstants.unity.rawValue] = unitySettings
        }
        
        if(adConfig.isIronSourceEnabled()){
            let ironsourceSettings = [AdAdapterValueConstants.applicationKey.rawValue:adConfig.ironSourceApplicationKey]
            mediaConfigurations[AdAdapterConstants.ironSource.rawValue] = ironsourceSettings
        }
        
        if(adConfig.isVungleEnabled()){
            let vungleSettings = [AdAdapterValueConstants.appId.rawValue: adConfig.vungleAppId]
            mediaConfigurations[AdAdapterConstants.vungle.rawValue] = vungleSettings
        }
        config.mediatedNetworkConfigurations = mediaConfigurations
        config.globalMediationSettings = []
        config.loggingLevel = .info
        
        MoPub.sharedInstance().initializeSdk(with: config) {
            print("Initialized MP")
        }
        
    }
    func hideBannerAd() {
        if let banner = bannerView, let adconfig = adConfiguration, adconfig.isBannerEnabled() {
            banner.stopAutomaticallyRefreshingContents()
            banner.removeFromSuperview()
        }
        else
        {
            assert(false, "Banner not configured")
        }
    }
    
    func showBannerAd() {
        if let banner = bannerView, let adconfig = adConfiguration, adconfig.isBannerEnabled() {
            addBannerViewToView(banner)
        }
        else{
            assert(false, "Banner not configured")
        }
    }
    
    func prefetchInterstitial() {
        if let adconfig = adConfiguration, adconfig.isInterstitialEnabled() {
            print("Fetching interstitial ad")
            interstitial?.loadAd()
        }
        else{
            assert(false, "Interstitial not configured")
        }
    }
    
    func prefetchReward() {
        if let adconfig = adConfiguration, adconfig.isRewardEnabled() {
            print("Fetching reward ad")
            MPRewardedVideo.loadAd(withAdUnitID: adConfiguration?.moPubRewardId, withMediationSettings: [])
        }
        else{
            assert(false, "Reward not configured")
        }
    }
    
    func showInterstitialAd() {
        if let adconfig = adConfiguration, adconfig.isInterstitialEnabled() {
            if interstitial?.ready ?? false {
                print("Showing interstitial ad")
                interstitial?.show(from: rootViewController)
            } else {
                prefetchInterstitial()
            }
        }
        else{
            assert(false, "Interstitial not configured")
        }
    }
    
    func showRewardAd() {
        if let adconfig = adConfiguration, adconfig.isRewardEnabled() {
            let adId = adconfig.moPubRewardId
            if MPRewardedVideo.hasAdAvailable(forAdUnitID: adId) {
                guard let reward = MPRewardedVideo.availableRewards(forAdUnitID: adId)?.first, let mpReward = reward as? MPRewardedVideoReward else {
                    assert(false, "Busted reward")
                    return
                }
                print("Showing reward ad")
                MPRewardedVideo.presentAd(forAdUnitID: adId, from: rootViewController, with: mpReward)
            } else {
                prefetchReward()
                print("Tried to show reward ad, but not fetched")
                rewardStream?(.notFetched)
            }
        }
        else{
            assert(false, "Reward not configured")
        }
    }
}

extension AdManager: MPAdViewDelegate {
    public func viewControllerForPresentingModalView() -> UIViewController! {
        return rootViewController
    }
    
    public func adViewDidLoadAd(_ view: MPAdView!, adSize: CGSize) {
        print("banner did load for id: \(view.adUnitId)")
        if let banner=bannerView{
            addBannerViewToView(banner)
        }
    }
    
    func addBannerViewToView(_ bannerView: UIView) {
        guard let rootView = rootViewController.view else { return }
        bannerView.translatesAutoresizingMaskIntoConstraints = false
        rootView.addSubview(bannerView)
        
        if #available(iOS 11.0, *) {
            let guide: UILayoutGuide = rootView.safeAreaLayoutGuide
            bannerView.leadingAnchor.constraint(equalTo: guide.leadingAnchor).isActive = true
            bannerView.trailingAnchor.constraint(equalTo: guide.trailingAnchor).isActive = true
            bannerView.bottomAnchor.constraint(equalTo: guide.bottomAnchor).isActive = true
            bannerView.heightAnchor.constraint(equalToConstant: bannerView.bounds.size.height).isActive = true
        } else {
            let guide: UILayoutGuide = rootView.layoutMarginsGuide
            let left = NSLayoutConstraint(item: bannerView,
                                          attribute: .leading,
                                          relatedBy: .equal,
                                          toItem: guide,
                                          attribute: .leading,
                                          multiplier: 1,
                                          constant: 0)
            let right = NSLayoutConstraint(item: bannerView,
                                           attribute: .trailing,
                                           relatedBy: .equal,
                                           toItem: guide,
                                           attribute: .trailing,
                                           multiplier: 1,
                                           constant: 0)
            let bottom = NSLayoutConstraint(item: bannerView,
                                            attribute: .bottom,
                                            relatedBy: .equal,
                                            toItem: guide,
                                            attribute: .bottom,
                                            multiplier: 1,
                                            constant: 0)
            let height = NSLayoutConstraint(item: bannerView,
                                            attribute: .height,
                                            relatedBy: .equal,
                                            toItem: nil,
                                            attribute: .height,
                                            multiplier: 1,
                                            constant: bannerView.bounds.size.height)
            NSLayoutConstraint.activate([
                left,
                right,
                bottom,
                height
            ])
        }
        rootView.layoutIfNeeded()
    }
    
    public func adView(_ view: MPAdView!, didFailToLoadAdWithError error: Error!) {
        print("banner failed to load: \(error.localizedDescription)")
        bannerView?.removeFromSuperview()
    }
}

extension AdManager: MPInterstitialAdControllerDelegate {
    
    public func interstitialDidLoadAd(_ interstitial: MPInterstitialAdController!) {
        print("Interstitial did load for id \(interstitial.adUnitId)")
        interstitialStream?(.loaded)
    }
    
    public func interstitialDidFail(toLoadAd interstitial: MPInterstitialAdController!, withError error: Error!) {
        print("Interstitial did not load for id \(interstitial.adUnitId)")
        interstitialStream?(.failed)
    }
    
    
    public func interstitialWillAppear(_ interstitial: MPInterstitialAdController!) {
        print("Interstitial will appear")
    }
    
    public func interstitialWillDisappear(_ interstitial: MPInterstitialAdController!) {
        print("Interstitial will disappear")
    }
    
    public func interstitialDidExpire(_ interstitial: MPInterstitialAdController!) {
        print("Interstitial DidExpire")
        interstitialStream?(.failed)
    }
    public func interstitialDidAppear(_ interstitial: MPInterstitialAdController!) {
        print("Interstitial did appear")
        interstitialStream?(.shown)
    }
    
    public func interstitialDidDisappear(_ interstitial: MPInterstitialAdController!) {
        print("Interstitial did disappear")
        interstitialStream?(.dismissed)
    }
    
    public func interstitialDidReceiveTapEvent(_ interstitial: MPInterstitialAdController!) {
        print("Interstitial DidReceiveTapEvent")
        interstitialStream?(.clicked)
    }
    
}

extension AdManager: MPRewardedVideoDelegate {
    public func rewardedVideoAdDidLoad(forAdUnitID adUnitID: String!) {
        print("reward did load for id \(adUnitID)")
        rewardStream?(.loaded)
    }
    
    public func rewardedVideoAdDidFailToLoad(forAdUnitID adUnitID: String!, error: Error!) {
        print("reward failed to load: \(error)")
        rewardStream?(.failed)
    }
    
    public func rewardedVideoAdDidFailToPlay(forAdUnitID adUnitID: String!, error: Error!) {
        print("reward failed to play: \(error)")
        rewardStream?(.failed)
    }
    
    public func rewardedVideoAdShouldReward(forAdUnitID adUnitID: String!, reward: MPRewardedVideoReward!) {
        print("reward received: \(reward.currencyType)")
        rewardStream?(.completed)
    }
    
    public func rewardedVideoAdDidExpire(forAdUnitID adUnitID: String!) {
        print("reward expired")
        rewardStream?(.failed)
    }
    
    public func rewardedVideoAdDidAppear(forAdUnitID adUnitID: String!) {
        print("reward did appear")
        rewardStream?(.started)
    }
    
    public func rewardedVideoAdWillAppear(forAdUnitID adUnitID: String!) {
        print("reward will appear")
    }
    
    public func rewardedVideoAdWillDisappear(forAdUnitID adUnitID: String!) {
        print("reward will disappear")
    }
    
    public func rewardedVideoAdDidDisappear(forAdUnitID adUnitID: String!) {
        print("reward did disappear")
        rewardStream?(.closed)
    }
    
    public func rewardedVideoAdDidReceiveTapEvent(forAdUnitID adUnitID: String!) {
        print("reward did Receive Tap Event")
        rewardStream?(.clicked)
    }
    
}
