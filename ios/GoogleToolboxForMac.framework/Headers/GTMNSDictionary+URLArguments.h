#import <Foundation/Foundation.h>

/// Utility for building a URL or POST argument string.
@interface NSDictionary (GTMNSDictionaryURLArgumentsAdditions)

/// Returns a dictionary of the decoded key-value pairs in a http arguments
/// string of the form key1=value1&key2=value2&...&keyN=valueN.
/// Keys and values will be unescaped automatically.
/// Only the first value for a repeated key is returned.
///
/// NOTE: Apps targeting iOS 8 or OS X 10.10 and later should use
///       NSURLComponents and NSURLQueryItem to create URLs with
///       query arguments instead of using these category methods.
+ (NSDictionary *)gtm_dictionaryWithHttpArgumentsString:(NSString *)argString;

/// Gets a string representation of the dictionary in the form
/// key1=value1&key2=value2&...&keyN=valueN, suitable for use as either
/// URL arguments (after a '?') or POST body. Keys and values will be escaped
/// automatically, so should be unescaped in the dictionary.
- (NSString *)gtm_httpArgumentsString;

@end
