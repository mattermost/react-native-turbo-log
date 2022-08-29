package com.mattermostreactnativeturbolog;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.charset.StandardCharsets;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;
import ch.qos.logback.core.util.FileSize;

/**
 * This is where the module implementation lives
 * The exposed methods can be defined in the `turbo` and `legacy` folders
 */
public class ReactNativeTurboLogModuleImpl  {
  public static final String NAME = "ReactNativeTurboLog";
  private static final int LOG_LEVEL_DEBUG = 0;
  private static final int LOG_LEVEL_INFO = 1;
  private static final int LOG_LEVEL_WARNING = 2;
  private static final int LOG_LEVEL_ERROR = 3;

  private static Logger logger = LoggerFactory.getLogger(ReactNativeTurboLogModuleImpl.class);
  private static String logsDirectory;
  private static ReadableMap configureOptions;

  public static void configure(ReactApplicationContext reactContext, ReadableMap options, Promise promise) {
    boolean dailyRolling = options.getBoolean("dailyRolling");
    int maximumFileSize = options.getInt("maximumFileSize");
    int maximumNumberOfFiles = options.getInt("maximumNumberOfFiles");
    logsDirectory = options.hasKey("logsDirectory")
      ? options.getString("logsDirectory")
      : reactContext.getCacheDir() + "/logs";
    String logPrefix = reactContext.getPackageName();

    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

    RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<>();
    rollingFileAppender.setContext(loggerContext);
    rollingFileAppender.setFile(logsDirectory + "/" + logPrefix + "-latest.log");

    if (dailyRolling) {
      SizeAndTimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new SizeAndTimeBasedRollingPolicy<>();
      rollingPolicy.setContext(loggerContext);
      rollingPolicy.setFileNamePattern(logsDirectory + "/" + logPrefix + "-%d{yyyy-MM-dd}.%i.log");
      rollingPolicy.setMaxFileSize(new FileSize(maximumFileSize));
      rollingPolicy.setTotalSizeCap(new FileSize(maximumNumberOfFiles * maximumFileSize));
      rollingPolicy.setMaxHistory(maximumNumberOfFiles);
      rollingPolicy.setParent(rollingFileAppender);
      rollingPolicy.start();
      rollingFileAppender.setRollingPolicy(rollingPolicy);

    } else if (maximumFileSize > 0) {
      FixedWindowRollingPolicy rollingPolicy = new FixedWindowRollingPolicy();
      rollingPolicy.setContext(loggerContext);
      rollingPolicy.setFileNamePattern(logsDirectory + "/" + logPrefix + "-%i.log");
      rollingPolicy.setMinIndex(1);
      rollingPolicy.setMaxIndex(maximumNumberOfFiles);
      rollingPolicy.setParent(rollingFileAppender);
      rollingPolicy.start();
      rollingFileAppender.setRollingPolicy(rollingPolicy);

      SizeBasedTriggeringPolicy triggeringPolicy = new SizeBasedTriggeringPolicy();
      triggeringPolicy.setContext(loggerContext);
      triggeringPolicy.setMaxFileSize(new FileSize(maximumFileSize));
      triggeringPolicy.start();
      rollingFileAppender.setTriggeringPolicy(triggeringPolicy);
    }

    PatternLayoutEncoder encoder = new PatternLayoutEncoder();
    encoder.setContext(loggerContext);
    encoder.setCharset(StandardCharsets.UTF_8);
    encoder.setPattern("%d{yyyy/MM/dd HH:mm:ss.SSS} %-5level %msg%n");
    encoder.start();

    rollingFileAppender.setEncoder(encoder);
    rollingFileAppender.start();

    ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    root.setLevel(Level.DEBUG);
    root.detachAndStopAllAppenders();
    root.addAppender(rollingFileAppender);

    configureOptions = options;
    promise.resolve(null);
  }

  public static void write(double level, ReadableArray messages) {
    String str = format(messages);
    switch ((int)level) {
      case LOG_LEVEL_DEBUG:
        logger.debug(str);
        break;
      case LOG_LEVEL_INFO:
        logger.info(str);
        break;
      case LOG_LEVEL_WARNING:
        logger.warn(str);
        break;
      case LOG_LEVEL_ERROR:
        logger.error(str);
        break;
    }
  }

  public static void getLogFilePaths(Promise promise) {
    try {
      WritableArray result = Arguments.createArray();
      for (File logFile: getLogFiles()) {
        result.pushString(logFile.getAbsolutePath());
      }
      promise.resolve(result);
    } catch (Exception e) {
      promise.resolve(Arguments.createArray());
    }
  }

  public static void deleteLogFiles(ReactApplicationContext reactContext, Promise promise) {
    try {
      for (File file: getLogFiles()) {
        file.delete();
      }
      if (configureOptions != null) {
        configure(reactContext, configureOptions, promise);
      } else {
        promise.resolve(true);
      }
    } catch (Exception e) {
      promise.resolve(false);
    }
  }

  private static File[] getLogFiles() {
    File directory = new File(logsDirectory);
    return directory.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return name.endsWith(".log");
      }
    });
  }

  private static String format(ReadableArray messages) {
    final int size = messages.size();
    String str = "";
    for (int i = 0; i < size; i++) {
      ReadableType type = messages.getType(i);
      switch (type) {
        case Null:
          break;
        case Boolean:
          str = str.concat(" " + messages.getBoolean(i));
          break;
        case Number:
          str = str.concat(" " + messages.getDouble(i));
          break;
        case String:
          str = str.concat(" " + messages.getString(i));
          break;
        case Map:
          try {
            JSONObject jsonObject = Helpers.convertMapToJson(messages.getMap(i));
            str = str.concat(" " + jsonObject.toString(2));
          } catch (JSONException e) {
            str = str.concat(" [Object error]");
          }
          break;
        case Array:
          try {
            JSONArray jsonArray = Helpers.convertArrayToJson(messages.getArray(i));
            str = str.concat(" " + jsonArray.toString(2));
          } catch (JSONException e) {
            str = str.concat(" [Array error]");
          }
          break;
      }
    }

    return str.trim();
  }
}
