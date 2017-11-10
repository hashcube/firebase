#import <Foundation/Foundation.h>

#import "FIRAnalyticsConfiguration.h"
#import "FIRCoreSwiftNameSupport.h"
#import "FIRLoggerLevel.h"

/**
 * The log levels used by FIRConfiguration.
 */
typedef NS_ENUM(NSInteger, FIRLogLevel) {
  /** Error */
  kFIRLogLevelError __deprecated = 0,
  /** Warning */
  kFIRLogLevelWarning __deprecated,
  /** Info */
  kFIRLogLevelInfo __deprecated,
  /** Debug */
  kFIRLogLevelDebug __deprecated,
  /** Assert */
  kFIRLogLevelAssert __deprecated,
  /** Max */
  kFIRLogLevelMax __deprecated = kFIRLogLevelAssert
} DEPRECATED_MSG_ATTRIBUTE(
    "Use -FIRDebugEnabled and -FIRDebugDisabled or setLoggerLevel. See FIRApp.h for more details.");

NS_ASSUME_NONNULL_BEGIN

/**
 * This interface provides global level properties that the developer can tweak, and the singleton
 * of the Firebase Analytics configuration class.
 */
FIR_SWIFT_NAME(FirebaseConfiguration)
@interface FIRConfiguration : NSObject

#if defined(__IPHONE_10_0) && __IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_10_0
/** Returns the shared configuration object. */
@property(class, nonatomic, readonly) FIRConfiguration *sharedInstance FIR_SWIFT_NAME(shared);
#else
/** Returns the shared configuration object. */
+ (FIRConfiguration *)sharedInstance FIR_SWIFT_NAME(shared());
#endif  // defined(__IPHONE_10_0) && __IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_10_0

/** The configuration class for Firebase Analytics. */
@property(nonatomic, readwrite) FIRAnalyticsConfiguration *analyticsConfiguration;

/** Global log level. Defaults to kFIRLogLevelError. */
@property(nonatomic, readwrite, assign) FIRLogLevel logLevel DEPRECATED_MSG_ATTRIBUTE(
    "Use -FIRDebugEnabled and -FIRDebugDisabled or setLoggerLevel. See FIRApp.h for more details.");

/**
 * Sets the logging level for internal Firebase logging. Firebase will only log messages
 * that are logged at or below loggerLevel. The messages are logged both to the Xcode
 * console and to the device's log. Note that if an app is running from AppStore, it will
 * never log above FIRLoggerLevelNotice even if loggerLevel is set to a higher (more verbose)
 * setting.
 *
 * @param loggerLevel The maximum logging level. The default level is set to FIRLoggerLevelNotice.
 */
- (void)setLoggerLevel:(FIRLoggerLevel)loggerLevel;

@end

NS_ASSUME_NONNULL_END
