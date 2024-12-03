package com.mattermost.turbolog

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.ReadableType
import org.json.JSONException
import java.io.File

class RNTurboLogModuleImpl(reactApplicationContext: ReactApplicationContext) {
  private val LOG_LEVEL_DEBUG = 0
  private val LOG_LEVEL_INFO = 1
  private val LOG_LEVEL_WARNING = 2
  private val LOG_LEVEL_ERROR = 3

  companion object {
    const val NAME = "RNTurboLog"
    const val TAG = "TurboLogger"
  }

  fun configure(options: ReadableMap?, promise: Promise?) {
    promise?.resolve(null)
  }

  fun write(level: Int, messages: ReadableArray?) {
    val str: String = format(messages)
    when (level) {
      LOG_LEVEL_DEBUG -> {
        TurboLog.d(TAG, str)
      }

      LOG_LEVEL_INFO -> {
        TurboLog.i(TAG, str)
      }

      LOG_LEVEL_WARNING -> {
        TurboLog.w(TAG, str)
      }

      LOG_LEVEL_ERROR -> {
        TurboLog.e(TAG, str)
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
      TurboLog.reconfigure()
    } catch (e: java.lang.Exception) {
      promise?.resolve(false)
    }
  }

  private fun getLogFiles(): Array<out File> {
    return TurboLog.getLogFiles()
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
