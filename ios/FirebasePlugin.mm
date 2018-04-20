#import "FirebasePlugin.h"
#import "Firebase.h"

// Change this valus to YES to debug remote config
static BOOL is_debug = NO;

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

    self.remoteConfig = [FIRRemoteConfig remoteConfig];
    FIRRemoteConfigSettings *remoteConfigSettings = [[FIRRemoteConfigSettings alloc] initWithDeveloperModeEnabled:is_debug];
    self.remoteConfig.configSettings = remoteConfigSettings;

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

- (void) initAbTesting: (NSDictionary*) config {
  NSLog(@"{firebase} initAbTesting");
  @try {
    [self setDefaultConfigValues:config];
    [self fetchConfig:config];
  } @catch (NSException *exception) {
    NSLog(@"{firebase} InitAbtesting failed: %@", exception);
  }
}

- (void) setDefaultConfigValues: (NSDictionary*) config {
  @try {
    NSLog(@"{firebase} setting default config");
    [self.remoteConfig setDefaults:config];
  } @catch (NSException *exception) {
    NSLog(@"{firebase} Unable to set default config: %@", exception);
  }
}

- (void) fetchConfig: (NSDictionary*) config {
  NSLog(@"{firebase} fetching latest config values from firebase");
  @try {
    int cacheExpiration = 3600;
    if (is_debug) {
      cacheExpiration = 0;
    }
    [self.remoteConfig fetchWithExpirationDuration:cacheExpiration completionHandler:^(FIRRemoteConfigFetchStatus status, NSError *error) {
      if (status == FIRRemoteConfigFetchStatusSuccess) {
        NSLog(@"{firebase} Config fetched!");
        [self.remoteConfig activateFetched];
        [self getConfig: config];
      } else {
        NSLog(@"{firebase} Config not fetched");
        NSLog(@"{firebase} Error %@", error.localizedDescription);
      }
   }];
  } @catch (NSException *exception) {
    NSLog(@"{firebase} Fetch config failed: %@", exception);
  }
}

- (void) getConfig: (NSDictionary*) items {
  NSLog(@"{firebase} getting config values from firebase");
  @try {
    NSMutableDictionary *results = [[NSMutableDictionary alloc] init];

    for (NSString* key in items) {
      NSString* value = self.remoteConfig[key].stringValue;
      [results setObject:value forKey:key];
    }

    [[PluginManager get] dispatchJSEvent:[NSDictionary dictionaryWithObjectsAndKeys:
                                          @"ConfigValue", @"name",
                                          results, @"data",
                                          nil]];
    return;
  } @catch (NSException *exception) {
    NSLog(@"{firebase} Unable to get config values: %@", exception);
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
