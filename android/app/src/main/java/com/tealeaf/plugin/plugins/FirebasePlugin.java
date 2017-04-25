package com.tealeaf.plugin.plugins;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tealeaf.logger;
import com.tealeaf.plugin.IPlugin;
import com.tealeaf.EventQueue;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.analytics.FirebaseAnalytics.Event;
import com.google.firebase.analytics.FirebaseAnalytics.Param;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;


public class FirebasePlugin implements IPlugin, GoogleApiClient.OnConnectionFailedListener{
    public static final String TAG = "{firebase}";

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FragmentActivity _activity;

    public FirebasePlugin() {
    }

    public void onCreateApplication(Context applicationContext) {
    }

    public void onCreate(Activity activity, Bundle savedInstance) {
    }

    public void onCreate(FragmentActivity activity, Bundle savedInstanceState) {
        this._activity = activity;
        try {
            this.mFirebaseDatabase = FirebaseDatabase.getInstance();
            this.mAuth = FirebaseAuth.getInstance();
            this.mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity);

            if(!android.os.Build.MANUFACTURER.equals("Amazon")) {
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
            }
        } catch (Exception ex) {
            Log.e(TAG, "init - failure: " + ex.getMessage());
        }
        Log.i(TAG, "init - done.");
    }

    public void onResume() {
        if (this.mFirebaseAnalytics == null) {
            return;
        }

        this.mFirebaseAnalytics.logEvent(Event.APP_OPEN, null);
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

    public void writeDatabase(String json) {

        Log.i(TAG, "inside writeDatabase function");

        try {
            final JSONObject jsonData = new JSONObject(json);
            String accessToken = jsonData.getString("accessToken");
            AuthCredential credential = FacebookAuthProvider.getCredential(accessToken);

            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this._activity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.i(TAG, "signInWithCredential:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                try {
                                    String uid = user.getUid();
                                    String node = jsonData.getString("node");
                                    JSONObject data = jsonData.getJSONObject("data");

                                    DatabaseReference dbReference = mFirebaseDatabase.getReference().child(node).child(uid);
                                    Iterator<String> iter = data.keys();
                                    while (iter.hasNext()) {
                                        String key = iter.next();
                                        String value = data.getString(key);

                                        dbReference.child(key).setValue(value);
                                    }
                                }
                                catch (JSONException e){
                                    Log.e(TAG, "JSONException: " + e.getLocalizedMessage());
                                }

                            } else {
                                Log.w(TAG, "Auth failed: " + task.getException());
                            }
                        }
                    });
        }
        catch (Exception e) {
            Log.e(TAG, "Exception on setting database property: " + e.getLocalizedMessage());
        }
        finally {
            mAuth.signOut();
        }
    }

    public void readDatabase(String json) {
        Log.i(TAG, "Inside readDatabase function");

        try {
            final JSONObject jsonData = new JSONObject(json);
            String accessToken = jsonData.getString("accessToken");
            AuthCredential credential = FacebookAuthProvider.getCredential(accessToken);

            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this._activity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.i(TAG, "signInWithCredential:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                try {
                                    String uid = user.getUid();
                                    String node = jsonData.getString("node");

                                    DatabaseReference dbReference = mFirebaseDatabase.getReference().child(node).child(uid);
                                    dbReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Map<String, Object> values = (Map<String, Object>)dataSnapshot.getValue();
                                            EventQueue.pushEvent(new DatabaseReadData(values));
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError error) {
                                            Log.e(TAG, "Failed to read value." + error.toException());
                                        }
                                    });
                                }
                                catch (JSONException e){
                                    Log.e(TAG, "JSONException: " + e.getLocalizedMessage());
                                }

                            } else {
                                Log.w(TAG, "Auth failed: " + task.getException());
                            }
                        }
                    });
        }
        catch (Exception e) {
            Log.e(TAG, "Exception on setting database property: " + e.getLocalizedMessage());
        }
        finally {
            mAuth.signOut();
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

    public class DatabaseReadData extends com.tealeaf.event.Event {

        public Map<String, Object> data;

        public DatabaseReadData(Map<String, Object> data) {
            super("dataReady");
            this.data = data;
        }
    }
}
