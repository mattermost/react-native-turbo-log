#import "RNTurboLog.h"

#import <CocoaLumberjack/CocoaLumberjack.h>
#import "TurboLogFormatter.h"

enum LogLevel {
    LOG_LEVEL_DEBUG,
    LOG_LEVEL_INFO,
    LOG_LEVEL_WARNING,
    LOG_LEVEL_ERROR
};

static const DDLogLevel ddLogLevel = DDLogLevelDebug;

@interface RNTurboLog()
@property (nonatomic, strong) DDFileLogger* fileLogger;
-(NSString *) format:(NSArray*) messageArray;
@end

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
    NSNumber* dailyRolling = options[@"dailyRolling"];
    NSNumber* maximumFileSize = options[@"maximumFileSize"];
    NSNumber* maximumNumberOfFiles = options[@"maximumNumberOfFiles"];
    NSString* logsDirectory = options[@"logsDirectory"];
    
    id<DDLogFileManager> fileManager = [[DDLogFileManagerDefault alloc] initWithLogsDirectory:logsDirectory];
    fileManager.maximumNumberOfLogFiles = [maximumNumberOfFiles unsignedIntegerValue];
    
    DDFileLogger* fileLogger = [[DDFileLogger alloc] initWithLogFileManager:fileManager];
    fileLogger.logFormatter = [[TurboLoggerFormatter alloc] init];
    fileLogger.rollingFrequency = [dailyRolling boolValue] ? 24 * 60 * 60 : 0;
    fileLogger.maximumFileSize = [maximumFileSize unsignedIntegerValue];
    [DDLog removeAllLoggers];
    [DDLog addLogger:fileLogger];
    NSString *bundleIdentifier = [[NSBundle mainBundle] bundleIdentifier];
    [DDLog addLogger:[[DDOSLogger alloc] initWithSubsystem:bundleIdentifier category:@"TurboLogger"]];
    self.fileLogger = fileLogger;
    
    resolve(nil);
}

- (void)deleteLogFiles:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject { 
    [self.fileLogger rollLogFileWithCompletionBlock:^{
        NSArray<DDLogFileInfo*> *files = [self.fileLogger.logFileManager unsortedLogFileInfos];
        for (DDLogFileInfo* file in files) {
            [[NSFileManager defaultManager] removeItemAtPath:file.filePath error:nil];
        }
        
        resolve(@YES);
    }];
}


- (void)getLogFilePaths:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject { 
    resolve(self.fileLogger.logFileManager.sortedLogFilePaths);
}

- (void)write:(NSInteger)logLevel message:(NSArray *)message { 
    NSString *str =  [self format:message];
    switch (logLevel) {
        case LOG_LEVEL_DEBUG:
            DDLogDebug(@"%@", str);
            break;
        case LOG_LEVEL_INFO:
            DDLogInfo(@"%@", str);
            break;
        case LOG_LEVEL_WARNING:
            DDLogWarn(@"%@", str);
            break;
        case LOG_LEVEL_ERROR:
            DDLogError(@"%@", str);
            break;
    }
}



-(NSString *) format:(NSArray*) messageArray {
    NSString *str = @"";
    for (id object in messageArray) {
        str = [str stringByAppendingFormat:@" %@", object];
    }
    NSCharacterSet *charc=[NSCharacterSet characterSetWithCharactersInString:@" "];
    return [str stringByTrimmingCharactersInSet:charc];
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
