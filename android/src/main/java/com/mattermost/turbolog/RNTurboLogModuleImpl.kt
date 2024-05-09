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
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.ReadableType
import org.json.JSONException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.charset.StandardCharsets

class RNTurboLogModuleImpl(reactApplicationContext: ReactApplicationContext) {
  private val LOG_LEVEL_DEBUG = 0
  private val LOG_LEVEL_INFO = 1
  private val LOG_LEVEL_WARNING = 2
  private val LOG_LEVEL_ERROR = 3

  private val logger: Logger = LoggerFactory.getLogger(RNTurboLogModuleImpl::class.java)
  private var logsDirectory: String? = null
  private var configureOptions: ReadableMap? = null
  private val context = reactApplicationContext

  companion object {
    const val NAME = "RNTurboLog"
    const val TAG = "TurboLogger"
  }

  fun configure(options: ReadableMap?, promise: Promise?) {
    if (options == null) {
      promise?.resolve(Error("Options not defined"))
      return
    }
    val dailyRolling = options.getBoolean("dailyRolling")
    val maximumFileSize = options.getInt("maximumFileSize")
    val maximumNumberOfFiles = options.getInt("maximumNumberOfFiles")
    logsDirectory = if (options.hasKey("logsDirectory")
    ) options.getString("logsDirectory")
    else context.cacheDir.toString() + "/logs"
    val logPrefix = context.packageName

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
    promise?.resolve(null)
  }

  fun write(level: Int, messages: ReadableArray?) {
    val str: String = format(messages)
    when (level) {
      LOG_LEVEL_DEBUG -> {
        logger.debug(str)
        Log.d(TAG, str)
      }

      LOG_LEVEL_INFO -> {
        logger.info(str)
        Log.i(TAG, str)
      }

      LOG_LEVEL_WARNING -> {
        logger.warn(str)
        Log.w(TAG, str)
      }

      LOG_LEVEL_ERROR -> {
        logger.error(str)
        Log.e(TAG, str)
      }
    }
  }

  fun getLogFilePaths(promise: Promise?) {
    try {
      val result = Arguments.createArray()
      for (logFile in getLogFiles()) {
        result.pushString(logFile.absolutePath)
      }
      promise?.resolve(result)
    } catch (e: Exception) {
      promise?.resolve(Arguments.createArray())
    }
  }

  fun deleteLogFiles(promise: Promise?) {
    try {
      for (file in getLogFiles()) {
        file.delete()
      }
      if (configureOptions != null) {
        configure(configureOptions, promise)
      } else {
        promise?.resolve(true)
      }
    } catch (e: java.lang.Exception) {
      promise?.resolve(false)
    }
  }

  private fun getLogFiles(): Array<out File> {
    val directory = logsDirectory?.let { File(it) }
    return directory?.listFiles { _, name -> name.endsWith(".log") } ?: arrayOf()
  }

  private fun format(messages: ReadableArray?): String {
    val size = messages?.size() ?: 0
    var str = ""
    for (i in 0 until size) {
      val type = messages?.getType(i)
      when (type) {
        ReadableType.Null -> {}
        ReadableType.Boolean -> str = str + " " + messages.getBoolean(i)
        ReadableType.Number -> str = str + " " + messages.getDouble(i)
        ReadableType.String -> str = str + " " + messages.getString(i)
        ReadableType.Map -> try {
          val jsonObject = Helpers.convertMapToJson(messages.getMap(i))
          str = str + " " + jsonObject.toString(2)
        } catch (e: JSONException) {
          str = "$str [Object error]"
        }

        ReadableType.Array -> try {
          val jsonArray = Helpers.convertArrayToJson(messages.getArray(i))
          str = str + " " + jsonArray.toString(2)
        } catch (e: JSONException) {
          str = "$str [Array error]"
        }

        null -> str = "$str null"
      }
    }

    return str.trim { it <= ' ' }
  }
}
