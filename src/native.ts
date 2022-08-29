import { NativeModules, Platform } from 'react-native';

import type { ConfigureOptions } from './types';

const LINKING_ERROR =
  `The package '@mattermost/react-native-turbo-log' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

// @ts-expect-error
const isTurboModuleEnabled = global.__turboModuleProxy != null;

const ReactNativeTurboLogModule = isTurboModuleEnabled
  ? require('./NativeReactNativeTurboLog').default
  : NativeModules.ReactNativeTurboLog;

const ReactNativeTurboLog = ReactNativeTurboLogModule
  ? ReactNativeTurboLogModule
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function configure(options: ConfigureOptions): Promise<void> {
  const opts = {
    dailyRolling: options.dailyRolling ?? true,
    maximumFileSize: options.maximumFileSize ?? 1024 * 1024,
    maximumNumberOfFiles: options.maximumNumberOfFiles ?? 5,
    logsDirectory: options.logsDirectory,
  };

  return ReactNativeTurboLog.configure(opts);
}

export function deleteLogFiles(): Promise<boolean> {
  return ReactNativeTurboLog.deleteLogFiles();
}

export function getLogFilePaths(): Promise<string[]> {
  return ReactNativeTurboLog.getLogFilePaths();
}

export function write(logLevel: number, message: Array<any>) {
  ReactNativeTurboLog.write(logLevel, message);
}
