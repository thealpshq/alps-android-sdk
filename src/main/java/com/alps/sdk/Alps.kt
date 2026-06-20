package com.alps.sdk

import android.app.Activity
import android.content.Context
import com.alps.sdk.network.AlpsApiClient
import com.alps.sdk.storage.AlpsVisitorStore
import com.alps.sdk.ui.AlpsPanelActivity

object Alps {

  private var config: AlpsConfig? = null
  private var apiClient: AlpsApiClient? = null
  private var context: Context? = null

  fun configure(
    context: Context,
    widgetKey: String,
    userName: String? = null,
    userEmail: String? = null
  ) {
    this.context = context.applicationContext
    config = AlpsConfig(
      widgetKey = widgetKey,
      visitorName = userName,
      visitorEmail = userEmail,
      context = context
    )
    apiClient = AlpsApiClient(config!!)

    // Setup launcher overlay
    setupLauncher(context)
    fetchWidgetData()
  }

  fun show(activity: Activity) {
    config ?: return
    val intent = android.content.Intent(activity, AlpsPanelActivity::class.java)
    intent.putExtra("widgetKey", config!!.widgetKey)
    activity.startActivity(intent)
  }

  fun hide() {
    // Handled by fragment/activity lifecycle
  }

  fun identify(name: String, email: String) {
    config?.apply {
      visitorName = name
      visitorEmail = email
    }
    context?.let { config?.let { cfg -> AlpsVisitorStore.save(it, cfg) } }
  }

  fun logout() {
    context?.let { AlpsVisitorStore.clear(it) }
    config?.apply {
      visitorName = null
      visitorEmail = null
    }
  }

  private fun setupLauncher(context: Context) {
    // Launcher setup will be done via overlay window manager in AlpsPanelActivity
  }

  private fun fetchWidgetData() {
    apiClient?.fetchWidgetData(
      onSuccess = { data ->
        config?.apply {
          pusherKey = data.pusherKey
          pusherCluster = data.pusherCluster
        }
      },
      onError = { error ->
        android.util.Log.e("Alps", "Failed to fetch widget data: $error")
      }
    )
  }

  fun getConfig(): AlpsConfig? = config
  fun getApiClient(): AlpsApiClient? = apiClient
  fun getContext(): Context? = context
}
