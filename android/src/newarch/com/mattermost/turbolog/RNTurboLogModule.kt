package com.mattermost.turbolog

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap

class RNTurboLogModule (reactContext: ReactApplicationContext) : NativeRNTurboLogSpec(reactContext) {
  private var implementation: RNTurboLogModuleImpl = RNTurboLogModuleImpl(reactContext)
  val reactContext = reactContext

  override fun getName(): String = RNTurboLogModuleImpl.NAME
  override fun configure(options: ReadableMap?, promise: Promise?) {
    implementation.configure(options, promise)
  }

  override fun deleteLogFiles(promise: Promise?) {
    implementation.deleteLogFiles(promise)
  }

  override fun getLogFilePaths(promise: Promise?) {
    implementation.getLogFilePaths(promise)
  }

  override fun write(logLevel: Double, message: ReadableArray?) {
    implementation.write(logLevel.toInt(), message)
  }
}
