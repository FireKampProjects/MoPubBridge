package com.firekamp;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.adcolony.sdk.AdColony;
import com.adcolony.sdk.AdColonyAppOptions;
import com.mopub.common.BaseAdapterConfiguration;
import com.mopub.common.OnNetworkInitializationFinishedListener;
import com.mopub.common.Preconditions;
import com.mopub.common.logging.MoPubLog;
import com.mopub.common.logging.MoPubLog.AdapterLogEvent;
import com.mopub.common.util.Json;
import com.mopub.mobileads.MoPubErrorCode;

import java.util.Map;

public class AdColonyAdapterConfigurationCustom extends BaseAdapterConfiguration {
    private static final String CLIENT_OPTIONS_KEY = "clientOptions";
    private static final String APP_ID_KEY = "appId";
    private static final String ALL_ZONE_IDS_KEY = "allZoneIds";
    private static final String ADAPTER_NAME = com.mopub.mobileads.AdColonyAdapterConfiguration.class.getSimpleName();
    private static final String ADAPTER_VERSION = "4.1.0.0";
    private static final String BIDDING_TOKEN = "1";
    private static final String MOPUB_NETWORK_NAME = "adcolony";

    public AdColonyAdapterConfigurationCustom() {
    }

    @NonNull
    public String getAdapterVersion() {
        return "4.1.0.0";
    }

    @Nullable
    public String getBiddingToken(@NonNull Context context) {
        return "1";
    }

    @NonNull
    public String getMoPubNetworkName() {
        return "adcolony";
    }

    @NonNull
    public String getNetworkSdkVersion() {
        String sdkVersion = AdColony.getSDKVersion();
        if (!TextUtils.isEmpty(sdkVersion)) {
            return sdkVersion;
        } else {
            String adapterVersion = this.getAdapterVersion();
            return adapterVersion.substring(0, adapterVersion.lastIndexOf(46));
        }
    }

    public void initializeNetwork(@NonNull Context context, @Nullable Map<String, String> configuration, @NonNull OnNetworkInitializationFinishedListener listener) {
        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(listener);
        boolean networkInitializationSucceeded = false;
        Class var5 = com.mopub.mobileads.AdColonyAdapterConfiguration.class;
        synchronized(com.mopub.mobileads.AdColonyAdapterConfiguration.class) {
            try {
                if (this.isAdColonyConfigured()) {
                    networkInitializationSucceeded = true;
                } else if (configuration != null) {
                    String adColonyClientOptions = (String)configuration.get("clientOptions");
                    String adColonyAppId = (String)configuration.get("appId");
                    String[] adColonyAllZoneIds = extractAllZoneIds(configuration);
                    if (!TextUtils.isEmpty(adColonyAppId) && adColonyAllZoneIds.length != 0) {
                        AdColonyAppOptions adColonyAppOptions = AdColonyAppOptions.getMoPubAppOptions(adColonyClientOptions);
                        boolean result=AdColony.configure((Application)context.getApplicationContext(), adColonyAppOptions, adColonyAppId, adColonyAllZoneIds);
                        networkInitializationSucceeded = true;
                    } else {
                        MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, new Object[]{ADAPTER_NAME, "AdColony's initialization not started. Ensure AdColony's appId, zoneId, and/or clientOptions are populated on the MoPub dashboard. Note that initialization on the first app launch is a no-op."});
                    }
                }
            } catch (Exception var11) {
                MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM_WITH_THROWABLE, new Object[]{"Initializing AdColony has encountered an exception.", var11});
            }
        }

        if (networkInitializationSucceeded) {
            listener.onNetworkInitializationFinished(com.mopub.mobileads.AdColonyAdapterConfiguration.class, MoPubErrorCode.ADAPTER_INITIALIZATION_SUCCESS);
        } else {
            listener.onNetworkInitializationFinished(com.mopub.mobileads.AdColonyAdapterConfiguration.class, MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
        }

    }

    private boolean isAdColonyConfigured() {
        return !AdColony.getSDKVersion().isEmpty();
    }

    @NonNull
    private static String[] extractAllZoneIds(@NonNull Map<String, String> serverExtras) {
        Preconditions.checkNotNull(serverExtras);
        String[] result = Json.jsonArrayToStringArray((String)serverExtras.get("allZoneIds"));
        if (result.length == 0) {
            result = new String[]{""};
        }

        return result;
    }
}
