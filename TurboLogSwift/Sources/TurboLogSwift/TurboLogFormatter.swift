import Foundation
import CocoaLumberjackSwift

class TurboLoggerFormatter: NSObject, DDLogFormatter {
    private let dateFormatter: DateFormatter

    override init() {
        dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy/MM/dd HH:mm:ss:SSS"
        super.init()
    }

    func format(message logMessage: DDLogMessage) -> String? {
        let logLevel: String
        switch logMessage.flag {
        case .error:
            logLevel = "ERROR"
        case .warning:
            logLevel = "WARNING"
        case .info:
            logLevel = "INFO"
        case .debug:
            logLevel = "DEBUG"
        default:
            logLevel = "VERBOSE"
        }

        let dt = dateFormatter.string(from: logMessage.timestamp)
        let msg = logMessage.message

        return "\(dt) \(logLevel) \(msg)"
    }
}
