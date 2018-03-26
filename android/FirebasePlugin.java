package com.tealeaf.plugin.plugins;

import com.tealeaf.logger;
import com.tealeaf.TeaLeaf;
import com.tealeaf.EventQueue;
import com.tealeaf.plugin.IPlugin;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import java.util.Iterator;

import android.support.v4.app.FragmentActivity;
import android.content.pm.ApplicationInfo;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.analytics.FirebaseAnalytics.Event;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;


public class FirebasePlugin implements IPlugin, GoogleApiClient.OnConnectionFailedListener {
  private FirebaseAnalytics mFirebaseAnalytics;
  private FragmentActivity _activity;
  private FirebaseRemoteConfig mFirebaseRemoteConfig;

  public class ConfigValue extends com.tealeaf.event.Event {
    Map<String, String> data;

    public ConfigValue(Map<String, String> data) {
      super("ConfigValue");
      this.data = data;
    }
  }

  public FirebasePlugin() {
  }

  public void onCreateApplication(Context applicationContext) {
  }

  public void onCreate(Activity activity, Bundle savedInstance) {
  }

  public void onCreate(FragmentActivity activity, Bundle savedInstanceState) {
    this._activity = activity;
    try {
      this.mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity);

      if(!android.os.Build.MANUFACTURER.equals("Amazon")) {
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(activity)
          .enableAutoManage(activity, this)
          .addApi(AppInvite.API)
          .build();

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(isDebuggable())
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);

        AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, activity, false)
          .setResultCallback(
            new ResultCallback<AppInviteInvitationResult>() {
              @Override
              public void onResult(AppInviteInvitationResult result) {
                if (result.getStatus().isSuccess()) {
                  // Extract deep link from Intent
                  Intent intent = result.getInvitationIntent();
                  String deepLink = AppInviteReferral.getDeepLink(intent);
                  logger.log("{firebase} app launched from deeplink: " + deepLink);
                  // TODO: Can send events with url, Based on url,
                  // can do specific actions in game.
                }
              }
            });
      }
    } catch (Exception ex) {
      logger.log("{firebase} init - failure: " + ex.getMessage());
    }
  }

  public void onResume() {
    if (this.mFirebaseAnalytics == null) {
      return;
    }

    this.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, null);
  }

  public void onStart() {
  }

  public void onFirstRun() {
  }

  public void onRenderPause() {
  }

  public void onRenderResume() {
  }

  private boolean isDebuggable() {
    return 0 != (_activity.getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE);
  }

  private void fetchConfig(final JSONObject obj) {
    long cacheExpiration = 3600; // 1 hour in seconds.
    // If your app is using developer mode, cacheExpiration is set to 0, so each fetch will
    // retrieve values from the service.
    if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
        cacheExpiration = 0;
    }

    // [START fetch_config_with_callback]
    // cacheExpirationSeconds is set to cacheExpiration here, indicating the next fetch request
    // will use fetch data from the Remote Config service, rather than cached parameter values,
    // if cached parameter values are more than cacheExpiration seconds old.
    // See Best Practices in the README for more information.
    mFirebaseRemoteConfig.fetch(cacheExpiration)
      .addOnCompleteListener(this._activity, new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
          if (task.isSuccessful()) {
            mFirebaseRemoteConfig.activateFetched();
            getConfig(obj);
          } else {
            logger.log("{firebase} fetchConfig - failure");
          }
        }
      });
    // [END fetch_config_with_callback]
  }

  public void initAbTesting(String config) {
    try {
      JSONObject obj = new JSONObject(config);

      setDefaultConfigValues(obj);
      fetchConfig(obj);
    } catch (JSONException e) {
      logger.log("{firebase} initAbTesting - failure: " + e.getMessage());
    }
  }

  private void setDefaultConfigValues(JSONObject obj) {
    try {
      Map<String, Object> map = new HashMap<String, Object>();
      Iterator<String> iter = obj.keys();

      while (iter.hasNext()) {
        String key = iter.next();
        String value = obj.getString(key);

        map.put(key, value);
      }

      mFirebaseRemoteConfig.setDefaults(map);
    } catch (JSONException e) {
      logger.log("{firebase} initAbTesting - failure: " + e.getMessage());
    }
  }

  private void getConfig(JSONObject obj) {
    String value;
    Map<String, String> map = new HashMap<String, String>();
    Iterator<String> iter = obj.keys();

    while (iter.hasNext()) {
      String key = iter.next();
      value = mFirebaseRemoteConfig.getString(key);
      map.put(key, value);
    }
    EventQueue.pushEvent(new ConfigValue(map));
  }

  public void setUserData(String json) {
    if (this.mFirebaseAnalytics == null) {
      return;
    }

    try {
      JSONObject obj = new JSONObject(json);
      Iterator<String> iter = obj.keys();
      while (iter.hasNext()) {
        String key = iter.next();
        String value = obj.getString(key);

        if ("uid".equals(key)) {
          this.mFirebaseAnalytics.setUserId(value);
        } else {
          this.mFirebaseAnalytics.setUserProperty(key, value);
        }
      }

      logger.log("{firebase} setUserData - success");
    } catch (JSONException e) {
      logger.log("{firebase} setUser - failure: " + e.getMessage());
    }
  }

  public void logEvent(String json) {
    String eventName = "";

    if (this.mFirebaseAnalytics == null) {
      return;
    }

    try {
      Bundle bundle = new Bundle();
      JSONObject obj = new JSONObject(json);
      eventName = obj.getString("eventName");
      JSONObject paramsObj = obj.getJSONObject("params");
      Iterator<String> iter = paramsObj.keys();

      while (iter.hasNext()) {
        String key = iter.next();

        try {
          if (Param.LEVEL.equals(key) || Param.SCORE.equals(key)) { //long
            bundle.putLong(key, paramsObj.getLong(key));
          } else if (Param.VALUE.equals(key)) { //double
            bundle.putDouble(key, paramsObj.getDouble(key));
          } else {
            bundle.putString(key, paramsObj.getString(key));
          }

          // TODO: Need to implement using switch case. it suports for string from java 8 onwards.
          //switch(key) {
          //  case Param.LEVEL://long
          //  case Param.SCORE:
          //    bundle.putLong(key, paramsObj.getLong(key));
          //    break;
          //  case Param.VALUE: //double
          //    bundle.putDouble(key, paramsObj.getDouble(key));
          //    break;
          //  case Param.CHARACTER:
          //  case Param.GROUP_ID:
          //  case Param.CONTENT_TYPE:
          //  case Param.ITEM_ID:
          //  case Param.ITEM_NAME:
          //  case Param.VIRTUAL_CURRENCY_NAME:
          //  case Param.ACHIEVEMENT_ID:
          //  case Param.SIGN_UP_METHOD:
          //  case Param.ITEM_CATEGORY:
          //  default:
          //    bundle.putString(key, paramsObj.getString(key));
          //    break;
          //}
        } catch (JSONException e) {
          logger.log("{firebase} track - failure: " + eventName + " - " + e.getMessage());
        }
      }

      this.mFirebaseAnalytics.logEvent(eventName, bundle);
      logger.log("{firebase} track - success: " + eventName);
    } catch (JSONException e) {
      logger.log("{firebase} track - failure: " + eventName + " - " + e.getMessage());
    }
  }

  public void setScreen(String screenData) {
    if (this.mFirebaseAnalytics == null) {
      return;
    }

    try {
      final Activity current_activity = this._activity;
      final FirebaseAnalytics _mFirebaseAnalytics = this.mFirebaseAnalytics;
      JSONObject obj = new JSONObject(screenData);
      final String screeen_name = obj.getString("name");
      current_activity.runOnUiThread(new Runnable() {
        @Override
        public void run() {
           _mFirebaseAnalytics.setCurrentScreen(current_activity, screeen_name, null);
        }
      });
    } catch (JSONException ex) {
      logger.log("{firebase} set screen - failure: " + ex.getMessage());
    }
  }

  public void onPause() {
  }

  public void onStop() {
  }

  public void onDestroy() {
  }

  public void onNewIntent(Intent intent) {
  }

  public void setInstallReferrer(String referrer) {
  }

  public void onActivityResult(Integer request, Integer result, Intent data) {
  }

  public boolean consumeOnBackPressed() {
    return true;
  }

  public void onBackPressed() {
  }

  @Override
  public void onConnectionFailed(ConnectionResult connectionResult) {
  }
}
