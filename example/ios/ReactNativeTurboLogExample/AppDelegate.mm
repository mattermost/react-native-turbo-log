#import "AppDelegate.h"

#import <React/RCTBundleURLProvider.h>
#import <TurboLogIOSNative/TurboLog.h>

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
  self.moduleName = @"main";
  // You can add your custom initial props in the dictionary below.
  // They will be passed down to the ViewController used by React Native.
  self.initialProps = @{};
  
  NSError *error = nil;
  [TurboLog configureWithDailyRolling:FALSE maximumFileSize:1000 maximumNumberOfFiles:2 logsDirectory:@"" logsFilename:nil error:&error];
  if (error) {
    NSLog(@"Failed to configure TurboLog: %@", error.localizedDescription);
  }
  
  [TurboLog writeWithLogLevel:TurboLogLevelDebug message:@[@"Example error"]];
  return [super application:application didFinishLaunchingWithOptions:launchOptions];
}

- (NSURL *)sourceURLForBridge:(RCTBridge *)bridge
{
  return [self bundleURL];
}
 
- (NSURL *)bundleURL
{
#if DEBUG
  return [[RCTBundleURLProvider sharedSettings] jsBundleURLForBundleRoot:@"index"];
#else
  return [[NSBundle mainBundle] URLForResource:@"main" withExtension:@"jsbundle"];
#endif
}

@end
