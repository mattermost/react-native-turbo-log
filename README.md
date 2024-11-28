# @mattermost/react-native-turbo-log
File logger for React Native (TurboModules) with configurable rolling policy

## Installation

```sh
npm install @mattermost/react-native-turbo-log
npx pod-install
```

## Usage

BREAKING CHANGE
Starting on version 0.5 we add the native logs also to the file. This means the configuration has to be done also on the native side.

### iOS
Not yet supported

### Android
```kotlin
import com.mattermost.turbolog.TurboLog
import com.mattermost.turbolog.ConfigureOptions

class MainApplication : NavigationApplication(), INotificationsApplication {
    // ...
    override fun onCreate() {
        // ...
        TurboLog.configure(options = ConfigureOptions())
        TurboLog.i("Label", "Text")
    }
}
```
Configure the application on application create. Use `TurboLog.X` instead of `Log.X` on the native side to get the logs show in the files.

### JavaScript
```js
import TurboLogger from "@mattermost/react-native-turbo-log";

await TurboLogger.configure();

// ...

```
After configuring TurboLogger, your app will store logs in the filesystem. If not specified, all your `console` calls for
`debug`, `log`, `info`, `warn` and `error` will be captured by `TurboLogger` and they'll also get writen to the log files.

You can configure TurboLogger to customize the rolling policy, the interception of `console` calls or to enable / disable logging to the filesystem.

## API

#### TurboLogger.configure(options?: ConfigureOptions) => Promise<void>

Starting on 0.5, configuration has to be made at native level, and extra configuration can be given at JavaScript level.

Initialize the TurboLogger with the specified options. Once the configure promise is resolved, all `console` calls are captured and writen to the log file unless specified otherwise. To ensure that no logs are missing, it is good practice to `await` this call at the launch of your app.

| Option | Description | Default | Set at |
| --- | --- | --- | --- |
| `logLevel` | Minimum log level for file output (it won't affect console output) | LogLevel.Debug | Native |
| `captureConsole` | If `true`, all `console` calls are automatically captured and written to a log file  | `true` | JavaScript |
| `dailyRolling` | If `true`, a new log file is created every day | `false` | Native |
| `maximumFileSize` | A new log file is created when current log file exceeds the given size in bytes. Set it to `0` to disable | `1024 * 1024` (1MB) | Native |
| `maximumNumberOfFiles` | Maximum number of log files to keep. When a new log file is created, if the total number of files exceeds this limit, the oldest file is deleted. `0` to disable | `5` | Native |
| `logsDirectory` | Absolute path of directory where log files are stored. If not defined, log files are stored in the cache directory of the app | `undefined` | Native |
| `logToFile` | If `true`, log files are created and written to the filesystem. It can also be changed by calling the `setLogToConsole` method | `true` | Native |

#### TurboLogger.deleteLogs(): Promise<boolean>

Remove all your app log files from the filesystem.

#### TurboLogger.getLogPaths(): Promise<string[]>

Return the absolute path of all the log files.

#### TurboLogger.setLogToFile(enabled: boolean)

Enable or disable logging messages to files

## Logging without console

If you don't want to use `console` calls for file logging, you can instead access TurboLogger methods to write messages to the log files, this approach allows you to log only the relevant messages for your app while discarding `console` calls made by any third-party library.

#### TurboLogger.debug(...args: any)

Shortcut for `TurboLogger.log(LogLevel.Debug, ...args)`.

#### TurboLogger.info(...args: any)

Shortcut for `TurboLogger.log(LogLevel.Info, ...args)`.

#### TurboLogger.warn(...args: any)

Shortcut for `TurboLogger.log(LogLevel.Warning, ...args)`.

#### TurboLogger.error(...args: any)

Shortcut for `TurboLogger.log(LogLevel.Error, ...args)`.

#### TurboLogger.log(level: LogLevel, ...args: any)

Append the log message using the same formatting as `console` with the specified level.

**Important:** log formatting does not support `string substitution`


## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow and remember to follow the [Code Of Conduct](https://github.com/mattermost/.github/blob/master/CODE_OF_CONDUCT.md).

## License

[MIT License](LICENSE)

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
