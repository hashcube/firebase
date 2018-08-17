#import "PluginManager.h"
#import "Firebase.h"

@interface FirebasePlugin : GCPlugin

@property (assign) FIRRemoteConfig *remoteConfig;
@property (assign) NSDictionary *pendingEventData;

@end
