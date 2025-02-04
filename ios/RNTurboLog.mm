#import "RNTurboLog.h"

#import <TurboLogIOSNative/TurboLog.h>

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
- (NSNumber *)processNumberValue:(std::optional<bool>)optionalBoolValue {
    // Use the boolean value
    if (optionalBoolValue.has_value()) {
        return [NSNumber numberWithBool:optionalBoolValue.value()];
    }

    return 0;
}

- (void)configure:(JS::NativeRNTurboLog::SpecConfigureOptions &)options resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
    id sharedKeySet = [NSDictionary sharedKeySetForKeys:@[@"dailyRolling", @"maximumFileSize", @"maximumNumberOfFiles", @"logsDirectory"]]; // returns NSSharedKeySet
    NSMutableDictionary *dict = [NSMutableDictionary dictionaryWithSharedKeySet:sharedKeySet];
    dict[@"dailyRolling"] = [self processNumberValue:options.dailyRolling()];
    dict[@"maximumFileSize"] = [self processNumberValue:options.maximumFileSize()];
    dict[@"maximumNumberOfFiles"] = [self processNumberValue:options.maximumNumberOfFiles()];
    dict[@"logsDirectory"] = options.logsDirectory();
    [self setConfig:dict withResolver:resolve withRejecter:reject];
}
#endif

-(void)setConfig:(NSDictionary *)options withResolver:(RCTPromiseResolveBlock)resolve withRejecter:(RCTPromiseRejectBlock)reject {
    resolve(nil);
}

- (void)deleteLogFiles:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
    NSError *error = nil;
    BOOL success = [TurboLog deleteLogFiles:&error];
    
    if (error) {
        reject(@"delete_error", error.localizedDescription, error);
    } else {
        resolve(@(success));
    }
}


- (void)getLogFilePaths:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
  resolve([TurboLog getLogFilePaths]);
}

- (void)write:(double)logLevel message:(NSArray *)message {
  [TurboLog writeWithLogLevel:(TurboLogLevel)logLevel message:message];
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
