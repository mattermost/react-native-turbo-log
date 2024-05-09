#import <Foundation/Foundation.h>

#if RCT_NEW_ARCH_ENABLED

#import <RNTurboLogSpec/RNTurboLogSpec.h>
@interface RNTurboLog: NSObject <NativeRNTurboLogSpec>

#else

#import <React/RCTBridgeModule.h>
@interface RNTurboLog : NSObject <RCTBridgeModule>
#endif
@end
