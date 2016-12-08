var FireBase = Class(function () {
  "use strict";

  this.setUserId = function (uid) {
    this.setUserData({
      uid: uid
    });
  };

  this.setUserData = function (data) {
    if (!data) {
      return;
    }
    NATIVE.plugins.sendEvent("FireBasePlugin", "setUserData", JSON.stringify(data));
  };

  this.logEvent = function (e_name, params) {
    NATIVE.plugins.sendEvent("FireBasePlugin", "logEvent", JSON.stringify({
      eventName: e_name,
      params: params || {}
    }));
  };

  this.setScreen = function (name) {
    NATIVE.plugins.sendEvent("FireBasePlugin", "setScreen", JSON.stringify({
      name: name
    }));
  };

});

exports = new FireBase();
