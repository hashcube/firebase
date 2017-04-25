import util.underscore as _;

var EVENTS = {
    JOIN_GROUP: 'join_group',
    LEVEL_UP: 'level_up',
    POST_SCORE: 'post_score',
    SELECT_CONTENT: 'select_content',
    SPEND_VIRTUAL_CURRENCY: 'spend_virtual_currency',
    TUTORIAL_BEGIN: 'tutorial_begin',
    TUTORIAL_COMPLETE: 'tutorial_complete',
    UNLOCK_ACHIEVEMENT: 'unlock_achievement',
    SHARE: 'share',
    SIGN_UP: 'sign_up',
    PRESENT_OFFER: 'present_offer',
    LOGIN: 'login'
  },
  PARAMS = {
    GROUP_ID: 'group_id',
    CHARACTER: 'character',
    LEVEL: 'level',
    SCORE: 'score',
    CONTENT_TYPE: 'content_type',
    ITEM_ID: 'item_id',
    ITEM_NAME: 'item_name',
    VIRTUAL_CURRENCY_NAME: 'virtual_currency_name',
    VALUE: 'value',
    ACHIEVEMENT_ID: 'achievement_id',
    SIGN_UP_METHOD: 'sign_up_method',
    ITEM_CATEGORY: 'item_category'
  },
  FireBase = Class(function () {
    "use strict";

    var fire_events = {};
    var onReadDataForUser;

    this.init = function() {
      fire_events[EVENTS.JOIN_GROUP] = [PARAMS.GROUP_ID];
      fire_events[EVENTS.LEVEL_UP] = [PARAMS.CHARACTER, PARAMS.LEVEL];
      fire_events[EVENTS.POST_SCORE] = [PARAMS.LEVEL, PARAMS.CHARACTER, PARAMS.SCORE];
      fire_events[EVENTS.SELECT_CONTENT] = [PARAMS.CONTENT_TYPE, PARAMS.ITEM_ID];
      fire_events[EVENTS.SPEND_VIRTUAL_CURRENCY] = [PARAMS.ITEM_NAME, PARAMS.VIRTUAL_CURRENCY_NAME, PARAMS.VALUE];
      fire_events[EVENTS.TUTORIAL_BEGIN] = [];
      fire_events[EVENTS.TUTORIAL_COMPLETE] = [];
      fire_events[EVENTS.UNLOCK_ACHIEVEMENT] = [PARAMS.ACHIEVEMENT_ID];
      fire_events[EVENTS.SHARE] = [PARAMS.CONTENT_TYPE, PARAMS.ITEM_ID];
      fire_events[EVENTS.SIGN_UP] = [PARAMS.SIGN_UP_METHOD];
      fire_events[EVENTS.PRESENT_OFFER] = [PARAMS.ITEM_ID, PARAMS.ITEM_NAME, PARAMS.ITEM_CATEGORY];
      fire_events[EVENTS.LOGIN] = [];

      NATIVE.events.registerHandler('dataReady', function (data) {
        if (typeof onReadDataForUser === "function") {
          onReadDataForUser(data);
        } 
        else {
          logger.log('{firebase} WARN: onReadDataForUser callback not registered');
        }
      });
    };

    this.setUserId = function (uid) {
      this.setUserData({
        uid: uid
      });
    };

    this.setUserData = function (data) {
      if (!data) {
        return;
      }
      NATIVE.plugins.sendEvent("FirebasePlugin", "setUserData", JSON.stringify(data));
    };

    this.writeDatabase = function (accessToken, path, data) {
      if (!data || !path) {
        return;
      }

      NATIVE.plugins.sendEvent("FirebasePlugin", "writeDatabase", JSON.stringify({
        accessToken: accessToken,
        node: path,
        data: data
      }));
    };

    this.readDatabase = function (accessToken, path, cb) {
      if (!path) {
        return;
      }
      onReadDataForUser = cb;

      NATIVE.plugins.sendEvent("FirebasePlugin", "readDatabase", JSON.stringify({
        accessToken: accessToken,
        node: path
      }));
    };

    this.logEvent = function (e_name, params) {
      if (e_name in fire_events) {
        params = _.pick(params, fire_events[e_name]);
      }

      NATIVE.plugins.sendEvent("FirebasePlugin", "logEvent", JSON.stringify({
        eventName: e_name,
        params: params || {}
      }));
    };

    this.setScreen = function (name) {
      NATIVE.plugins.sendEvent("FirebasePlugin", "setScreen", JSON.stringify({
        name: name
      }));
    };
});

exports = new FireBase();
exports.EVENTS = EVENTS;
exports.PARAMS = PARAMS;
