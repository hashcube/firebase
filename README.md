# firebase
Firebase for gameclosure

# How to update
 
  * Create a sample project in android studio and import latest firebase-core
  * There will be multiple libs,
    * common
    * analytic
    * analytic-impl
    * iid
  * hc-firebase contains all these files
  * So you can extract new libs and compaign all the libs and just keep the same structure of hc-hashcube.jar
  * You can use '''jar cf''' command to create new jar file
  * firebase-invites lib you can update separately
  * You might update google-play libs also if both are not compatible
  * In the same way using gradle you can get the latest google-play libs
  * basement, base, games and plus libs you should update with the gameplay module
  * play-task you can add with firebase module
  * app/build/intermediates/manifests/full/debug/AndroidManifest.xml in this path there will be a generated manifest you can compire this manifest and new one to make sure there any no conflicts with permissions
  * Before adding each lib, extract and compire with existing one and build once after deleting additionaly created folders, this will help us to reduce unwanted files
