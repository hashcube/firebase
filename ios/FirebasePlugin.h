#import "PluginManager.h"
#import <FirebaseDatabase/FirebaseDatabase.h>

@interface FirebasePlugin : GCPlugin
@property (strong, nonatomic) FIRDatabase *database;
@end
