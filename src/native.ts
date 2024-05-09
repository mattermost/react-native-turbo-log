import type { ConfigureOptions } from './types';

const RNTurboLog = require('./NativeRNTurboLog').default;

export function configure(options: ConfigureOptions = {}): Promise<void> {
  const opts = {
    dailyRolling: options.dailyRolling ?? false,
    maximumFileSize: options.maximumFileSize ?? 1024 * 1024,
    maximumNumberOfFiles: options.maximumNumberOfFiles ?? 5,
    logsDirectory: options.logsDirectory,
  };

  return RNTurboLog.configure(opts);
}

export function deleteLogFiles(): Promise<boolean> {
  return RNTurboLog.deleteLogFiles();
}

export function getLogFilePaths(): Promise<string[]> {
  return RNTurboLog.getLogFilePaths();
}

export function write(logLevel: number, message: Array<any>) {
  RNTurboLog.write(logLevel, message);
}
