package com.mattermostreactnativeturbolog;

import androidx.annotation.NonNull;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;

public class ReactNativeTurboLogModule extends NativeReactNativeTurboLogSpec {
  public static final String NAME = ReactNativeTurboLogModuleImpl.NAME;

  ReactNativeTurboLogModule(ReactApplicationContext context) {
    super(context);
  }

  @Override
  @NonNull
  public String getName() {
    return ReactNativeTurboLogModuleImpl.NAME;
  }

  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @Override
  @ReactMethod
  public void multiply(double a, double b, Promise promise) {
    ReactNativeTurboLogModuleImpl.multiply(a, b, promise);
  }
}
