#import "PluginManager.h"
#import "Firebase.h"

@interface FirebasePlugin : GCPlugin

@property (assign) FIRRemoteConfig *remoteConfig;
@property (retain) NSDictionary *pendingEventData;

@end
