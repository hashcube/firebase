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
import java.util.Map;
import java.util.HashMap;

import android.support.v4.app.FragmentActivity;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.analytics.FirebaseAnalytics.Event;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;


public class FirebasePlugin implements IPlugin, GoogleApiClient.OnConnectionFailedListener {
  private FirebaseAnalytics mFirebaseAnalytics;
  private FragmentActivity _activity;
  private Map<String, String> events;
  private Map<String, String> params;

  private void init()
  {
    events = new HashMap<String, String>();
    events.put("join_group", Event.JOIN_GROUP);
    events.put("level_up", Event.LEVEL_UP);
    events.put("post_score", Event.POST_SCORE);
    events.put("select_content", Event.SELECT_CONTENT);
    events.put("spend_virtual_currency", Event.SPEND_VIRTUAL_CURRENCY);
    events.put("tutorial_begin", Event.TUTORIAL_BEGIN);
    events.put("tutorial_complete", Event.TUTORIAL_COMPLETE);
    events.put("unlock_achievement", Event.UNLOCK_ACHIEVEMENT);
    events.put("share", Event.SHARE);
    events.put("sign_up", Event.SIGN_UP);
    events.put("present_offer", Event.PRESENT_OFFER);
    events.put("login", Event.LOGIN);

    params = new HashMap<String, String>();
    params.put("group_id", Param.GROUP_ID);
    params.put("character", Param.CHARACTER);
    params.put("level", Param.LEVEL);
    params.put("score", Param.SCORE);
    params.put("content_type", Param.CONTENT_TYPE);
    params.put("item_id", Param.ITEM_ID);
    params.put("item_name", Param.ITEM_NAME);
    params.put("virtual_currency_name", Param.VIRTUAL_CURRENCY_NAME);
    params.put("value", Param.VALUE);
    params.put("achievement_id", Param.ACHIEVEMENT_ID);
    params.put("method", Param.SIGN_UP_METHOD);
    params.put("item_category", Param.ITEM_CATEGORY);
  }

  public FirebasePlugin() {
    this.init();
  }

  public void onCreateApplication(Context applicationContext) {
  }

  public void onCreate(Activity activity, Bundle savedInstance) {
  }

  public void onCreate(FragmentActivity activity, Bundle savedInstanceState) {
    this._activity = activity;
    try {
      this.mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity);

      GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(activity)
        .enableAutoManage(activity, this)
        .addApi(AppInvite.API)
        .build();

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
      boolean suggested = events.containsKey(eventName);
      JSONObject paramsObj = obj.getJSONObject("params");
      Iterator<String> iter = paramsObj.keys();

      while (iter.hasNext()) {
        String key = iter.next();
        String fireKey = suggested ? params.get(key) : key;

        try {
          if (Param.LEVEL.equals(fireKey) || Param.SCORE.equals(fireKey)) { //long
            bundle.putLong(fireKey, paramsObj.getLong(fireKey));
          } else if (Param.VALUE.equals(fireKey)) { //double
            bundle.putDouble(fireKey, paramsObj.getDouble(fireKey));
          } else {
            bundle.putString(fireKey, paramsObj.getString(fireKey));
          }

          // TODO: Need to implement using switch case. it suports for string from java 8 onwards.
          //switch(fireKey) {
          //  case Param.LEVEL://long
          //  case Param.SCORE:
          //    bundle.putLong(fireKey, paramsObj.getLong(fireKey));
          //    break;
          //  case Param.VALUE: //double
          //    bundle.putDouble(fireKey, paramsObj.getDouble(fireKey));
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
          //    bundle.putString(fireKey, paramsObj.getString(fireKey));
          //    break;
          //}
        } catch (JSONException e) {
          logger.log("{firebase} track - failure: " + eventName + " - " + e.getMessage());
        }
      }

      eventName = suggested ? events.get(eventName) : eventName;
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

  @Override
  public void onConnectionFailed(ConnectionResult connectionResult) {
  }
}
