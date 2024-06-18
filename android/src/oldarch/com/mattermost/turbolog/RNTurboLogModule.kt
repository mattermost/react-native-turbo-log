package com.mattermost.turbolog

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap

class RNTurboLogModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
  private var implementation: RNTurboLogModuleImpl = RNTurboLogModuleImpl(reactContext)
  override fun getName(): String = RNTurboLogModuleImpl.NAME

  @ReactMethod
  fun configure(options: ReadableMap?, promise: Promise?) {
    implementation.configure(options, promise)
  }

  @ReactMethod
  fun write(level: Double, messages: ReadableArray?) {
    implementation.write(level.toInt(), messages)
  }

  @ReactMethod
  fun getLogFilePaths(promise: Promise?) {
    implementation.getLogFilePaths(promise)
  }

  @ReactMethod
  fun deleteLogFiles(promise: Promise?) {
    implementation.deleteLogFiles(promise)
  }
}
