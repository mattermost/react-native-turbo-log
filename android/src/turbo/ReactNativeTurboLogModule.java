package com.mattermostreactnativeturbolog;

import androidx.annotation.NonNull;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

public class ReactNativeTurboLogModule extends NativeReactNativeTurboLogSpec {
  public static final String NAME = ReactNativeTurboLogModuleImpl.NAME;
  private final ReactApplicationContext reactContext;

  ReactNativeTurboLogModule(ReactApplicationContext context) {
    super(context);
    this.reactContext = context;
  }

  @Override
  @NonNull
  public String getName() {
    return ReactNativeTurboLogModuleImpl.NAME;
  }

  @Override
  public void configure(ReadableMap options, Promise promise) {
    ReactNativeTurboLogModuleImpl.configure(reactContext, options, promise);
  }

  @Override
  public void write(double level, ReadableArray messages) {
    ReactNativeTurboLogModuleImpl.write(level, messages);
  }

  @Override
  public void getLogFilePaths(Promise promise) {
    ReactNativeTurboLogModuleImpl.getLogFilePaths(promise);
  }

  @Override
  public void deleteLogFiles(Promise promise) {
    ReactNativeTurboLogModuleImpl.deleteLogFiles(reactContext, promise);
  }
}
