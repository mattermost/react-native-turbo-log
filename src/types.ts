export enum LogLevel {
  Debug = 0,
  Info = 1,
  Warning = 2,
  Error = 3,
}

export interface ConfigureOptions {
  captureConsole?: boolean;
  dailyRolling?: boolean;
  maximumFileSize?: number;
  maximumNumberOfFiles?: number;
  logsDirectory?: string;
}
