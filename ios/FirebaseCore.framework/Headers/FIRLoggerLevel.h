#import "FIRCoreSwiftNameSupport.h"

/**
 * The log levels used by internal logging.
 */
typedef NS_ENUM(NSInteger, FIRLoggerLevel) {
  /** Error level, matches ASL_LEVEL_ERR. */
  FIRLoggerLevelError = 3,
  /** Warning level, matches ASL_LEVEL_WARNING. */
  FIRLoggerLevelWarning = 4,
  /** Notice level, matches ASL_LEVEL_NOTICE. */
  FIRLoggerLevelNotice = 5,
  /** Info level, matches ASL_LEVEL_NOTICE. */
  FIRLoggerLevelInfo = 6,
  /** Debug level, matches ASL_LEVEL_DEBUG. */
  FIRLoggerLevelDebug = 7,
  /** Minimum log level. */
  FIRLoggerLevelMin = FIRLoggerLevelError,
  /** Maximum log level. */
  FIRLoggerLevelMax = FIRLoggerLevelDebug
} FIR_SWIFT_NAME(FirebaseLoggerLevel);
