import {
  configure as configureNative,
  deleteLogFiles,
  getLogFilePaths,
  write,
} from './native';
import { ConfigureOptions, LogLevel } from './types';

class TurboLoggerStatic {
  private logToFile = false;

  async configure(options: ConfigureOptions = {}): Promise<void> {
    const { captureConsole = true, logToFile = true } = options;

    await configureNative(options);
    this.logToFile = logToFile;

    if (captureConsole) {
      const c = {
        ...global.console,
      };

      global.console.debug = (...args) => {
        this.log(LogLevel.Debug, ...args);
        if (__DEV__) {
          c.debug(...args);
        }
      };

      global.console.log = (...args) => {
        this.log(LogLevel.Info, ...args);
        if (__DEV__) {
          c.log(...args);
        }
      };

      global.console.info = (...args) => {
        this.log(LogLevel.Info, ...args);
        if (__DEV__) {
          c.info(...args);
        }
      };

      global.console.warn = (...args) => {
        this.log(LogLevel.Warning, ...args);
        if (__DEV__) {
          c.warn(...args);
        }
      };

      global.console.error = (...args) => {
        this.log(LogLevel.Error, ...args);
        if (__DEV__) {
          c.error(...args);
        }
      };
    }
  }

  async deleteLogs(): Promise<boolean> {
    return deleteLogFiles();
  }

  async getLogPaths(): Promise<string[]> {
    return getLogFilePaths();
  }

  setLogToFile(enabled: boolean) {
    this.logToFile = enabled;
  }

  debug(...args: any) {
    this.log(LogLevel.Debug, ...args);
  }

  info(...args: any) {
    this.log(LogLevel.Info, ...args);
  }

  warn(...args: any) {
    this.log(LogLevel.Warning, ...args);
  }

  error(...args: any) {
    this.log(LogLevel.Error, ...args);
  }

  log(level: LogLevel, ...args: any) {
    if (this.logToFile) {
      write(level, args);
    }
  }
}

const turboLogger = new TurboLoggerStatic();
export default turboLogger;
