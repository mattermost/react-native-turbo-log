package com.mattermost.turbolog

import com.facebook.react.TurboReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.model.ReactModuleInfo
import com.facebook.react.module.model.ReactModuleInfoProvider
class RNTurboLogPackage : TurboReactPackage() {
  override fun getModule(name: String, reactContext: ReactApplicationContext): NativeModule? =
    if (name == RNTurboLogModuleImpl.NAME) {
      RNTurboLogModule(reactContext)
    } else {
      null
    }

  override fun getReactModuleInfoProvider() = ReactModuleInfoProvider {
    mapOf(
      RNTurboLogModuleImpl.NAME to ReactModuleInfo(
        RNTurboLogModuleImpl.NAME,
        RNTurboLogModuleImpl.NAME,
        false,
        false,
        false,
        BuildConfig.IS_NEW_ARCHITECTURE_ENABLED
      )
    )
  }
}

