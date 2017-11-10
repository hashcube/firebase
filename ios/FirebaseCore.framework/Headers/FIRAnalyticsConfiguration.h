#import <Foundation/Foundation.h>

#import "FIRCoreSwiftNameSupport.h"

NS_ASSUME_NONNULL_BEGIN

/**
 * This class provides configuration fields for Firebase Analytics.
 */
FIR_SWIFT_NAME(AnalyticsConfiguration)
@interface FIRAnalyticsConfiguration : NSObject

/**
 * Returns the shared instance of FIRAnalyticsConfiguration.
 */
+ (FIRAnalyticsConfiguration *)sharedInstance FIR_SWIFT_NAME(shared());

/**
 * Sets the minimum engagement time in seconds required to start a new session. The default value
 * is 10 seconds.
 */
- (void)setMinimumSessionInterval:(NSTimeInterval)minimumSessionInterval;

/**
 * Sets the interval of inactivity in seconds that terminates the current session. The default
 * value is 1800 seconds (30 minutes).
 */
- (void)setSessionTimeoutInterval:(NSTimeInterval)sessionTimeoutInterval;

/**
 * Sets whether analytics collection is enabled for this app on this device. This setting is
 * persisted across app sessions. By default it is enabled.
 */
- (void)setAnalyticsCollectionEnabled:(BOOL)analyticsCollectionEnabled;

@end

NS_ASSUME_NONNULL_END
