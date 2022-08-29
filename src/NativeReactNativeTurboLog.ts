import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  configure(options: {
    dailyRolling: boolean;
    maximumFileSize: number;
    maximumNumberOfFiles: number;
    logsDirectory: string;
  }): Promise<void>;
  deleteLogFiles(): Promise<boolean>;
  getLogFilePaths(): Promise<string[]>;
  write(logLevel: number, message: Array<any>): void;
}

export default TurboModuleRegistry.getEnforcing<Spec>('ReactNativeTurboLog');
