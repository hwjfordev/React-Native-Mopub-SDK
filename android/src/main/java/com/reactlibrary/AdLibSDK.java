package com.reactlibrary;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import javax.annotation.Nullable;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.common.logging.MoPubLog;

public class AdLibSDK extends ReactContextBaseJavaModule {

    public static final String EVENT_SDK_INITIALIZED = "onSDKInitialized";

    ReactApplicationContext mReactContext;

    public AdLibSDK(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
    }

    @Override
    public String getName() {
        return "AdLibSDK";
    }

    private void sendEvent(String eventName, @Nullable WritableMap params) {
        getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }

    @ReactMethod
    public void initializeSDK(final String unitID) {
        Log.i("Mopub SDK", "Initialization...");
        
        
        Handler mainHandler;
        // HWJ, wrap this line as workaround for crash issue 
        // """Attempt to invoke virtual method 'android.os.Looper android.app.Activity.getMainLooper()' on a null object reference com.reactlibrary.AdLibSDK.initializeSDK"""
        
        try {
            mainHandler = new Handler(getCurrentActivity().getMainLooper());
        } catch(Exception e){
            e.printStackTrace();
            return;
        }
        
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {

                SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(unitID)
                        .withLogLevel(MoPubLog.LogLevel.DEBUG)
                        .build();

                MoPub.initializeSdk(getCurrentActivity(), sdkConfiguration, initSdkListener());

            }

            private SdkInitializationListener initSdkListener() {
                return new SdkInitializationListener() {
                    @Override
                    public void onInitializationFinished() {
                        sendEvent(EVENT_SDK_INITIALIZED, null);
                    }
                };
            }
        };
        mainHandler.post(myRunnable);
    }
}
