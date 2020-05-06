package com.firekamp.mopub;

public interface AdEvents {
    void interstitialSuccess(Object event);
    void interstitialError(String errorCode, String errorMessage, Object errorDetails);
    void rewardSuccess(Object event);
    void rewardError(String errorCode, String errorMessage, Object errorDetails);
}
