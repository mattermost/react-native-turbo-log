import Foundation
import CocoaLumberjackSwift

@objc enum LogLevel: Int {
    case debug = 0
    case info
    case warning
    case error
}

@objc
public class TurboLogSwift: NSObject {
  nonisolated(unsafe) static var fileLogger: DDFileLogger?

    @objc public static func configure(options: NSDictionary) {
        let dailyRolling = options["dailyRolling"] as? Bool ?? false
        let maximumFileSize = options["maximumFileSize"] as? UInt64 ?? 0
        let maximumNumberOfFiles = options["maximumNumberOfFiles"] as? UInt ?? 0
        let logsDirectory = options["logsDirectory"] as? String ?? ""

        let fileManager = DDLogFileManagerDefault(logsDirectory: logsDirectory)
        fileManager.maximumNumberOfLogFiles = maximumNumberOfFiles

        let fileLogger = DDFileLogger(logFileManager: fileManager)
        fileLogger.logFormatter = TurboLoggerFormatter()
        fileLogger.rollingFrequency = dailyRolling ? 24 * 60 * 60 : 0
        fileLogger.maximumFileSize = maximumFileSize

        DDLog.removeAllLoggers()
        DDLog.add(fileLogger)

        if let bundleIdentifier = Bundle.main.bundleIdentifier {
            DDLog.add(DDOSLogger(subsystem: bundleIdentifier, category: "TurboLogger"))
        }

        self.fileLogger = fileLogger
    }

    @objc public static func getLogFilePaths() -> [String] {
        return fileLogger?.logFileManager.sortedLogFilePaths ?? []
    }

    @objc public static func write(logLevel: Double, message: [String]) {
        let logMessage = message.joined(separator: " ")
        guard let logLevel = LogLevel(rawValue: Int(logLevel)) else { return }
        switch logLevel {
        case .debug:
            DDLogDebug("\(logMessage)")
        case .info:
            DDLogInfo("\(logMessage)")
        case .warning:
            DDLogWarn("\(logMessage)")
        case .error:
            DDLogError("\(logMessage)")
        }
    }

    @objc public static func deleteLogFiles() {
        fileLogger?.rollLogFile(withCompletion: nil)
    }
}
