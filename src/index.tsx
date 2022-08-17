import { NativeModules, Platform } from 'react-native';

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

export function multiply(a: number, b: number): Promise<number> {
  return ReactNativeTurboLog.multiply(a, b);
}
