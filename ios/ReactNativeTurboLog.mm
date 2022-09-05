#import "ReactNativeTurboLog.h"

#ifdef RCT_NEW_ARCH_ENABLED
#import "RNReactNativeTurboLogSpec.h"
#endif

#import <CocoaLumberjack/CocoaLumberjack.h>
#import "TurboLogFormatter.h"

enum LogLevel {
    LOG_LEVEL_DEBUG,
    LOG_LEVEL_INFO,
    LOG_LEVEL_WARNING,
    LOG_LEVEL_ERROR
};

static const DDLogLevel ddLogLevel = DDLogLevelDebug;

@interface ReactNativeTurboLog()
@property (nonatomic, strong) DDFileLogger* fileLogger;
-(NSString *) format:(NSArray*) messageArray;
@end

@implementation ReactNativeTurboLog
RCT_EXPORT_MODULE()

RCT_REMAP_METHOD(configure,
                 configureWithOptions:(NSDictionary *)options
                 withResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject) {
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

RCT_EXPORT_METHOD(deleteLogFiles:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    [self.fileLogger rollLogFileWithCompletionBlock:^{
        NSArray<DDLogFileInfo*> *files = [self.fileLogger.logFileManager unsortedLogFileInfos];
        for (DDLogFileInfo* file in files) {
            [[NSFileManager defaultManager] removeItemAtPath:file.filePath error:nil];
        }
        
        resolve(@YES);
    }];
}

RCT_EXPORT_METHOD(getLogFilePaths:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    resolve(self.fileLogger.logFileManager.sortedLogFilePaths);
}

RCT_EXPORT_METHOD(write:(NSNumber* _Nonnull)level message:(NSArray*)message) {
    NSString *str =  [self format:message];
    switch (level.integerValue) {
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
    return std::make_shared<facebook::react::NativeReactNativeTurboLogSpecJSI>(params);
}
#endif

@end
