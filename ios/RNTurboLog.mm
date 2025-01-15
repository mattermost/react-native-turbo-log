#import "RNTurboLog.h"

#import "mattermost_react_native_turbo_log-Swift.h"

@implementation RNTurboLog
RCT_EXPORT_MODULE()

RCT_REMAP_METHOD(configure,
                 configureWithOptions:(NSDictionary *)options
                 withResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject) {
  [self setConfig:options withResolver:resolve withRejecter:reject];
}

RCT_REMAP_METHOD(deleteLogFiles, resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    [self deleteLogFiles:resolve reject:reject];
}

RCT_REMAP_METHOD(getLogFilePaths, withResolver:(RCTPromiseResolveBlock)resolve withRejecter:(RCTPromiseRejectBlock)reject) {
    [self getLogFilePaths:resolve reject:reject];
}

RCT_REMAP_METHOD(write, logLevel:(NSNumber* _Nonnull)logLevel message:(NSArray*)message) {
    [self write:logLevel.integerValue message:message];
}

#if RCT_NEW_ARCH_ENABLED
- (void)configure:(JS::NativeRNTurboLog::SpecConfigureOptions &)options resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
  resolve(nil);
}
#endif

- (void)setConfig:(NSDictionary *)options withResolver:(RCTPromiseResolveBlock)resolve withRejecter:(RCTPromiseRejectBlock)reject {
  resolve(nil);
}

- (void)deleteLogFiles:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
  [TurboLogSwift deleteLogFiles];
  resolve(@YES);
}


- (void)getLogFilePaths:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject { 
  resolve([TurboLogSwift getLogFilePaths]);
}

- (void)write:(double)logLevel message:(NSArray *)message { 
  [TurboLogSwift writeWithLogLevel:logLevel message:message];
}

// Don't compile this code when we build for the old architecture.
#ifdef RCT_NEW_ARCH_ENABLED
- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeRNTurboLogSpecJSI>(params);
}
#endif

@end
