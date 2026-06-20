package com.alps.sdk

import android.content.Context
import android.content.SharedPreferences
import java.util.*

class AlpsConfig(
  val widgetKey: String,
  var visitorName: String? = null,
  var visitorEmail: String? = null,
  var conversationId: String? = null,
  var pusherKey: String? = null,
  var pusherCluster: String? = null,
  private val context: Context? = null
) {

  val apiBaseURL = "https://api.tryalps.com/api/v1"
  val cdnBaseURL = "https://cdn.tryalps.com"

  val visitorId: String
    get() {
      return if (!visitorEmail.isNullOrEmpty()) {
        visitorEmail!!
      } else if (!visitorName.isNullOrEmpty()) {
        visitorName!!
      } else {
        getStoredVisitorId()
      }
    }

  private fun getStoredVisitorId(): String {
    val prefs = context?.getSharedPreferences("alps_visitor", Context.MODE_PRIVATE)
    val stored = prefs?.getString("visitor_id_$widgetKey", null)
    return stored ?: UUID.randomUUID().toString().also {
      prefs?.edit()?.putString("visitor_id_$widgetKey", it)?.apply()
    }
  }
}
