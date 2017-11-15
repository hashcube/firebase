#if DEBUG

#import <stdarg.h>
#import "GTMDefines.h"

static void GTMAssertSelectorNilOrImplementedWithReturnTypeAndArguments(id obj, SEL sel, const char *retType, ...) {

  // verify that the object's selector is implemented with the proper
  // number and type of arguments
  va_list argList;
  va_start(argList, retType);

  if (obj && sel) {
    // check that the selector is implemented
    _GTMDevAssert([obj respondsToSelector:sel],
                  @"\"%@\" selector \"%@\" is unimplemented or misnamed",
                  NSStringFromClass([obj class]),
                  NSStringFromSelector(sel));

    const char *expectedArgType;
    NSUInteger argCount = 2; // skip self and _cmd
    NSMethodSignature *sig = [obj methodSignatureForSelector:sel];

    // check that each expected argument is present and of the correct type
    while ((expectedArgType = va_arg(argList, const char*)) != 0) {

      if ([sig numberOfArguments] > argCount) {
        const char *foundArgType = [sig getArgumentTypeAtIndex:argCount];

        _GTMDevAssert(0 == strncmp(foundArgType, expectedArgType, strlen(expectedArgType)),
                      @"\"%@\" selector \"%@\" argument %u should be type %s",
                      NSStringFromClass([obj class]),
                      NSStringFromSelector(sel),
                      (uint32_t)(argCount - 2),
                      expectedArgType);
      }
      argCount++;
    }

    // check that the proper number of arguments are present in the selector
    _GTMDevAssert(argCount == [sig numberOfArguments],
                  @"\"%@\" selector \"%@\" should have %u arguments",
                  NSStringFromClass([obj class]),
                  NSStringFromSelector(sel),
                  (uint32_t)(argCount - 2));

    // if asked, validate the return type
    if (retType && (strcmp("gtm_skip_return_test", retType) != 0)) {
      const char *foundRetType = [sig methodReturnType];
      _GTMDevAssert(0 == strncmp(foundRetType, retType, strlen(retType)),
                    @"\"%@\" selector \"%@\" return type should be type %s",
                    NSStringFromClass([obj class]),
                    NSStringFromSelector(sel),
                    retType);
    }
  }

  va_end(argList);
}

#define GTMAssertSelectorNilOrImplementedWithArguments(obj, sel, ...) \
  GTMAssertSelectorNilOrImplementedWithReturnTypeAndArguments((obj), (sel), "gtm_skip_return_test", __VA_ARGS__)

#else // DEBUG

// make it go away if not debug
#define GTMAssertSelectorNilOrImplementedWithReturnTypeAndArguments(obj, sel, retType, ...) do { } while (0)
#define GTMAssertSelectorNilOrImplementedWithArguments(obj, sel, ...) do { } while (0)

#endif // DEBUG
