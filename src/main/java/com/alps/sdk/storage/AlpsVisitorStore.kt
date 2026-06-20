package com.alps.sdk.storage

import android.content.Context
import android.content.SharedPreferences
import com.alps.sdk.AlpsConfig

object AlpsVisitorStore {

  private const val PREFS_NAME = "alps_visitor_store"

  fun save(context: Context, config: AlpsConfig) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val key = "alps-visitor-${config.widgetKey}"

    prefs.edit().apply {
      putString("$key:name", config.visitorName ?: "")
      putString("$key:email", config.visitorEmail ?: "")
      putString("$key:conversationId", config.conversationId ?: "")
      putString("$key:visitorId", config.visitorId)
      apply()
    }
  }

  fun load(context: Context, widgetKey: String): VisitorData? {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val key = "alps-visitor-$widgetKey"

    val name = prefs.getString("$key:name", "")?.takeIf { it.isNotEmpty() }
    val email = prefs.getString("$key:email", "")?.takeIf { it.isNotEmpty() }
    val conversationId = prefs.getString("$key:conversationId", "")?.takeIf { it.isNotEmpty() }

    return if (name == null && email == null && conversationId == null) {
      null
    } else {
      VisitorData(name, email, conversationId)
    }
  }

  fun getVisitorId(context: Context, widgetKey: String): String? {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val key = "alps-visitor-$widgetKey"
    return prefs.getString("$key:visitorId", null)
  }

  fun saveConversationId(context: Context, widgetKey: String, conversationId: String) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().putString("alps-conv-$widgetKey", conversationId).apply()
  }

  fun getConversationId(context: Context, widgetKey: String): String? {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return prefs.getString("alps-conv-$widgetKey", null)
  }

  fun clear(context: Context) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().clear().apply()
  }

  fun clearForWidget(context: Context, widgetKey: String) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().apply {
      remove("alps-visitor-$widgetKey")
      remove("alps-conv-$widgetKey")
      apply()
    }
  }

  data class VisitorData(
    val name: String?,
    val email: String?,
    val conversationId: String?
  )
}
