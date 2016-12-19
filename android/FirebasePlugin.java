package com.tealeaf.plugin.plugins;

import com.tealeaf.logger;
import com.tealeaf.TeaLeaf;
import com.tealeaf.plugin.IPlugin;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import java.util.Iterator;

import com.google.firebase.analytics.FirebaseAnalytics;


public class FirebasePlugin implements IPlugin {
  private FirebaseAnalytics mFirebaseAnalytics;
  private Activity _activity;

  public FirebasePlugin() {
  }

  public void onCreateApplication(Context applicationContext) {
  }

  public void onCreate(Activity activity, Bundle savedInstanceState) {
    this._activity = activity;
    try {
      this.mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity);
    } catch (Exception ex) {
      logger.log("{firebase} init - failure: " + ex.getMessage());
    }
  }

  public void onResume() {
  }

  public void onStart() {
  }

  public void onFirstRun() {
  }

  public void onRenderPause() {
  }

  public void onRenderResume() {
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
        String value = null;
        try {
          value = paramsObj.getString(key);
        } catch (JSONException e) {
          logger.log("{firebase} track - failure: " + eventName + " - " + e.getMessage());
        }

        if (value != null) {
          bundle.putString(key, value);
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
      JSONObject obj = new JSONObject(screenData);
      this.mFirebaseAnalytics.setCurrentScreen(this._activity, obj.getString("name"), "");
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
}
