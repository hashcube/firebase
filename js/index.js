import util.underscore as _;

var FireBase = Class(function () {
  "use strict";

  var fire_events;

  this.events = fire_events = {
    join_group: ['group_id'],
    level_up: ['character', 'level'],
    post_score: ['level', 'character', 'score'],
    select_content: ['content_type', 'item_id'],
    spend_virtual_currency: ['item_name', 'virtual_currency_name', 'value'],
    tutorial_begin: [],
    tutorial_complete: [],
    unlock_achievement: ['achievement_id'],
    share: ['content_type', 'item_id'],
    sign_up: ['method'],
    present_offer: ['item_id', 'item_name', 'item_category'],
    login: []
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
