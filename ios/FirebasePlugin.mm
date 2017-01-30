#import "FirebasePlugin.h"
#import "Firebase.h"

@implementation FirebasePlugin

// The plugin must call super dealloc.
- (void) dealloc {
  [super dealloc];
}

// The plugin must call super init.
- (id) init {
  self = [super init];
  if (!self) {
    return nil;
  }
  return self;
}

- (void) initializeWithManifest:(NSDictionary *)manifest appDelegate:(TeaLeafAppDelegate *)appDelegate {
  @try {
    [FIRApp configure];
    NSLog(@"{firebase} initDone");
  } @catch (NSException *exception) {
    NSLog(@"{firebase} Failed to initialize with exception: %@", exception);
  }
}

- (void) applicationDidBecomeActive:(UIApplication *)app {
  @try {
    [FIRAnalytics logEventWithName:kFIREventAppOpen parameters:nil];
  }
  @catch (NSException *exception) {
    NSLOG(@"{firebase} Exception while logging appopen event: %@", exception);
  }
}

- (void) setUserData: (NSDictionary*) userData {
  NSLog(@"{firebase} inside setUserData function");
  @try {
    [userData enumerateKeysAndObjectsUsingBlock:^(id key, id value, BOOL* stop) {
      NSString *prop = (NSString *) key;
      NSString *currValue = (NSString *) value;

      if ([prop isEqualToString:@"uid"]) {
        [FIRAnalytics setUserID:currValue];
      } else {
        [FIRAnalytics setUserPropertyString:currValue forName:prop];
      }
    }];
  } @catch (NSException *exception) {
    NSLog(@"{firebase} Exception on setting user property: %@", exception);
  } 
}

- (void) logEvent: (NSDictionary*) eventData {
  NSLog(@"{firebase} inside logEvent function");
  @try {
    NSString *eventName = [eventData valueForKey:@"eventName"];
    NSDictionary *evtParams = [eventData objectForKey:@"params"];
    NSMutableDictionary *params = [[NSMutableDictionary alloc]init];

    if (!evtParams || [evtParams count] <= 0) {
      [FIRAnalytics logEventWithName:eventName parameters:nil];

      NSLog(@"{firebase} Delivered event '%@'", eventName);
    } else {
      for (NSString* key in evtParams) {
        if ([kFIRParameterLevel isEqualToString: key] || [kFIRParameterScore isEqualToString: key]) {
          [params setObject: [NSNumber numberWithLongLong: [[evtParams objectForKey: key] longLongValue]] forKey: key];
        } else if([kFIRParameterValue isEqualToString: key]) {
          [params setObject: [NSNumber numberWithDouble: [[evtParams objectForKey: key] doubleValue]] forKey: key];
        } else {
          [params setObject: (NSString *)[evtParams objectForKey: key] forKey: key];
        }
      }

      [FIRAnalytics logEventWithName:eventName parameters:params];

      NSLog(@"{firebase} Delivered event '%@' with %d params", eventName, (int)[params count]);
    }
  } @catch (NSException *exception) {
    NSLog(@"{firebase} Exception while processing event: %@", exception);
  }
}

- (void) setScreen: (NSDictionary*) screenData {
  @try {
      [FIRAnalytics setScreenName: [screenData valueForKey:@"name"] screenClass:nil];
  } @catch (NSException *exception) {
    NSLOG(@"{firebase} Exception on set screen: ", exception);
  }
}

@end
