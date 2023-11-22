import {
  configure as configureNative,
  deleteLogFiles,
  getLogFilePaths,
  write,
} from './native';
import { type ConfigureOptions, LogLevel } from './types';

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

      const console = {
        ...c,
      };

      Object.defineProperty(console, 'debug', {
        value: (...args: any) => {
          this.log(LogLevel.Debug, ...args);
          if (__DEV__) {
            c.debug(...args);
          }
        },
        writable: false,
      });

      Object.defineProperty(console, 'log', {
        value: (...args: any) => {
          this.log(LogLevel.Info, ...args);
          if (__DEV__) {
            c.log(...args);
          }
        },
        writable: false,
      });

      Object.defineProperty(console, 'info', {
        value: (...args: any) => {
          this.log(LogLevel.Info, ...args);
          if (__DEV__) {
            c.info(...args);
          }
        },
        writable: false,
      });

      Object.defineProperty(console, 'warn', {
        value: (...args: any) => {
          this.log(LogLevel.Warning, ...args);
          if (__DEV__) {
            c.warn(...args);
          }
        },
        writable: false,
      });

      Object.defineProperty(console, 'error', {
        value: (...args: any) => {
          this.log(LogLevel.Error, ...args);
          if (__DEV__) {
            c.error(...args);
          }
        },
        writable: false,
      });

      global.console = console;
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
