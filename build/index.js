
var path = require('path'),
  fs = require('fs-extra'),
  xcodeUtil = require('../../devkit-core/modules/native-ios/lib/xcodeUtil'),
  copyFile = Promise.promisify(fs.copy);

exports.onCreateProject = function (api, app, config, cb) {
  var firebase = app.manifest.addons.firebase || {},
    app_path = app.paths.root,
    xcodeProjectPath = config.xcodeProjectPath,
    plist_file = 'GoogleService-Info.plist',
    addPlist = function () {
      return xcodeUtil.getXcodeProject(xcodeProjectPath)
        .then(function (xcodeProject) {
          xcodeProject._project.addProductFile(plist_file, {
            group: 'CustomTemplate'
          });

          return xcodeProject.write();
        });
    },
    srcFile, destFile, googlePlist;

  if (config.target !== 'native-ios' || !firebase.ios) {
    return;
  }

  srcFile = path.join(app_path, firebase.ios),
  destFile = path.join(xcodeProjectPath, plist_file);

  return Promise
    .resolve()
    .then(function () {
      return copyFile(srcFile, destFile);
    })
    .then(addPlist)
    .then(cb);
}
