
class MoPubData
{
  final String bannerAdId;
  final String interstitialAdId;
  final String rewardAdId;
  final String ironSourceApplicationKey;
  final String vungleAppId;
  final String appLovinSdkKey;
  final String adColonyAppId;
  final String adColonyBannerZoneId;
  final String adColonyInterstitialZoneId;
  final String adColonyRewardedZoneId;
  final String unityGameId;
  final bool facebookEnabled;

  MoPubData({this.bannerAdId, this.interstitialAdId, this.rewardAdId, this.ironSourceApplicationKey, this.vungleAppId,this.appLovinSdkKey,this.adColonyAppId,this.adColonyBannerZoneId,this.adColonyInterstitialZoneId,this.adColonyRewardedZoneId,this.unityGameId, this.facebookEnabled});

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['bannerAdId'] = this.bannerAdId;
    data['interstitialAdId'] = this.interstitialAdId;
    data['rewardAdId'] = this.rewardAdId;
    data['ironSourceApplicationKey'] = this.ironSourceApplicationKey;
    data['vungleAppId'] = this.vungleAppId;
    data['appLovinSdkKey'] = this.appLovinSdkKey;
    data['adColonyAppId'] = this.adColonyAppId;
    data['adColonyBannerZoneId'] = this.adColonyBannerZoneId;
    data['adColonyInterstitialZoneId'] = this.adColonyInterstitialZoneId;
    data['adColonyRewardedZoneId'] = this.adColonyRewardedZoneId;
    data['unityGameId'] = this.unityGameId;
    data['facebookEnabled'] = this.facebookEnabled;
    return data;
  }

}