import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';
import type { Double, Int32 } from 'react-native/Libraries/Types/CodegenTypes';

export interface Spec extends TurboModule {
  configure(options: {
    dailyRolling: boolean;
    maximumFileSize: Int32;
    maximumNumberOfFiles: Int32;
    logsDirectory: string;
  }): Promise<void>;
  deleteLogFiles(): Promise<boolean>;
  getLogFilePaths(): Promise<string[]>;
  write(logLevel: Double, message: Array<any>): void;
}

export default TurboModuleRegistry.getEnforcing<Spec>('RNTurboLog');
