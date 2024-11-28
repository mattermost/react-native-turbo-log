package com.mattermost.turbolog

import android.util.Log
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy
import ch.qos.logback.core.util.FileSize
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.charset.StandardCharsets

data class ConfigureOptions(
  val dailyRolling: Boolean? = null,
  val logsDirectory: String? = null,
  val maximumFileSize: Int? = null,
  val maximumNumberOfFiles: Int? = null,
  val logPrefix: String? = null,
)

class TurboLog {
  companion object {
    private val logger: Logger = LoggerFactory.getLogger(TurboLog::class.java)
    private var logsDirectory: String? = null
    private var configureOptions: ConfigureOptions? = null

    fun getLogFiles(): Array<out File> {
      val directory = logsDirectory?.let { File(it) }
      return directory?.listFiles { _, name -> name.endsWith(".log") } ?: arrayOf()
    }

    fun reconfigure() {
      configureOptions?.let { configure(it) }
    }

    fun configure(options: ConfigureOptions) {
      val dailyRolling = options.dailyRolling ?: false
      val maximumFileSize = options.maximumFileSize ?: (1024 * 1024)
      val maximumNumberOfFiles = options.maximumNumberOfFiles ?: 2
      logsDirectory = options.logsDirectory ?: ""
      val logPrefix = options.logPrefix ?: ""

      val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext

      val rollingFileAppender = RollingFileAppender<ILoggingEvent>()
      rollingFileAppender.context = loggerContext
      rollingFileAppender.file = "$logsDirectory/$logPrefix-latest.log"

      if (dailyRolling) {
        val rollingPolicy = SizeAndTimeBasedRollingPolicy<ILoggingEvent>()
        rollingPolicy.context = loggerContext
        rollingPolicy.fileNamePattern = "$logsDirectory/$logPrefix-%d{yyyy-MM-dd}.%i.log"
        rollingPolicy.setMaxFileSize(FileSize(maximumFileSize.toLong()))
        rollingPolicy.setTotalSizeCap(FileSize(maximumNumberOfFiles.toLong() * maximumFileSize))
        rollingPolicy.maxHistory = maximumNumberOfFiles
        rollingPolicy.setParent(rollingFileAppender)
        rollingPolicy.start()
        rollingFileAppender.setRollingPolicy(rollingPolicy)
      } else if (maximumFileSize > 0) {
        val rollingPolicy = FixedWindowRollingPolicy()
        rollingPolicy.context = loggerContext
        rollingPolicy.fileNamePattern = "$logsDirectory/$logPrefix-%i.log"
        rollingPolicy.minIndex = 1
        rollingPolicy.maxIndex = maximumNumberOfFiles
        rollingPolicy.setParent(rollingFileAppender)
        rollingPolicy.start()
        rollingFileAppender.rollingPolicy = rollingPolicy

        val triggeringPolicy = SizeBasedTriggeringPolicy<ILoggingEvent>()
        triggeringPolicy.context = loggerContext
        triggeringPolicy.maxFileSize = FileSize(maximumFileSize.toLong())
        triggeringPolicy.start()
        rollingFileAppender.setTriggeringPolicy(triggeringPolicy)
      }

      val encoder = PatternLayoutEncoder()
      encoder.context = loggerContext
      encoder.charset = StandardCharsets.UTF_8
      encoder.pattern = "%d{yyyy/MM/dd HH:mm:ss.SSS} %-5level %msg%n"
      encoder.start()

      rollingFileAppender.encoder = encoder
      rollingFileAppender.start()

      val root = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger
      root.level = Level.DEBUG
      root.detachAndStopAllAppenders()
      root.addAppender(rollingFileAppender)

      configureOptions = options
    }

    fun d(tag: String, message: String) {
      if (configureOptions != null) {
        logger.debug("$tag: $message")
      }
      Log.d(tag, message)
    }

    fun e(tag: String, message: String) {
      if (configureOptions != null) {
        logger.error("$tag: $message")
      }
      Log.e(tag, message)
    }

    fun i(tag: String, message: String) {
      if (configureOptions != null) {
        logger.info("$tag: $message")
      }
      Log.i(tag, message)
    }

    fun w(tag: String, message: String) {
      if (configureOptions != null) {
        logger.warn("$tag: $message")
      }
      Log.w(tag, message)
    }
  }
}
