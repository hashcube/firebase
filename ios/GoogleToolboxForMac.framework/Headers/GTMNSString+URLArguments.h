#import <Foundation/Foundation.h>

/// Utilities for encoding and decoding URL arguments.
@interface NSString (GTMNSStringURLArgumentsAdditions)

/// Returns a string that is escaped properly to be a URL argument.
///
/// This differs from stringByAddingPercentEscapesUsingEncoding: in that it
/// will escape all the reserved characters (per RFC 3986
/// <http://www.ietf.org/rfc/rfc3986.txt>) which
/// stringByAddingPercentEscapesUsingEncoding would leave.
///
/// This will also escape '%', so this should not be used on a string that has
/// already been escaped unless double-escaping is the desired result.
///
/// NOTE: Apps targeting iOS 8 or OS X 10.10 and later should use
///       NSURLComponents and NSURLQueryItem to create properly-escaped
///       URLs instead of using these category methods.
- (NSString*)gtm_stringByEscapingForURLArgument;

/// Returns the unescaped version of a URL argument
///
/// This has the same behavior as stringByReplacingPercentEscapesUsingEncoding:,
/// except that it will also convert '+' to space.
- (NSString*)gtm_stringByUnescapingFromURLArgument;

@end
