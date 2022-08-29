//
//  TurboLoggerFormatter.m
//  mattermost-react-native-turbo-log
//
//  Created by Elias Nahum on 24-08-22.
//

#import "Foundation/Foundation.h"
#import <CocoaLumberjack/CocoaLumberjack.h>
#import "TurboLogFormatter.h"

@implementation TurboLoggerFormatter
NSDateFormatter* dateFormatter;

- (instancetype)init {
    if (self = [super init]) {
        dateFormatter = [[NSDateFormatter alloc] init];
        dateFormatter.dateFormat = @"yyyy/MM/dd HH:mm:ss:SSS";
    }
    return self;
}

- (NSString*)formatLogMessage:(DDLogMessage*)logMessage {
    NSString* logLevel;
    switch (logMessage.flag) {
        case DDLogFlagError:
            logLevel = @"ERROR";
            break;
        case DDLogFlagWarning:
            logLevel = @"WARNING";
            break;
        case DDLogFlagInfo:
            logLevel = @"INFO";
            break;
        case DDLogFlagDebug:
            logLevel = @"DEBUG";
            break;
            
        default:
            logLevel = @"VERBOSE";
            break;
    }
    
    NSString* dt = [dateFormatter stringFromDate:logMessage.timestamp];
    NSString* msg = logMessage.message;
    
    return [NSString stringWithFormat:@"%@ %@ %@", dt, logLevel, msg];
}

@end
