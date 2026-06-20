package com.alps.sdk.realtime

import com.google.gson.Gson
import com.pusher.client.Pusher
import com.pusher.client.channel.PrivateChannel
import com.pusher.client.channel.PusherEvent
import com.alps.sdk.network.Message

class AlpsPusherClient {

  private var pusher: Pusher? = null
  private var conversationChannel: PrivateChannel? = null

  var onMessageReceived: ((Message) -> Unit)? = null
  var onTypingIndicator: ((String) -> Unit)? = null
  var onConversationStatusChanged: ((String) -> Unit)? = null

  fun connect(pusherKey: String, cluster: String, conversationId: String) {
    pusher = Pusher(pusherKey, com.pusher.client.PusherOptions().apply {
      setCluster(cluster)
      setEncrypted(true)
    })

    pusher?.connect(object : com.pusher.client.connection.ConnectionEventListener {
      override fun onConnectionStateChange(change: com.pusher.client.connection.ConnectionStateChange?) {
        if (change?.currentState == "CONNECTED") {
          subscribeToConversation(conversationId)
        }
      }

      override fun onError(message: String?, code: String?, e: Exception?) {
        e?.printStackTrace()
      }
    })
  }

  private fun subscribeToConversation(conversationId: String) {
    val channelName = "private-conversation-$conversationId"
    conversationChannel = pusher?.subscribePrivate(
      channelName,
      object : com.pusher.client.channel.PrivateChannelEventListener {
        override fun onAuthenticationFailure(message: String?, e: Exception?) {
          e?.printStackTrace()
        }

        override fun onSubscriptionSucceeded(channelName: String?) {
          // Subscribed
        }

        override fun onEvent(event: PusherEvent?) {
          handleEvent(event)
        }
      }
    ) as? PrivateChannel
  }

  private fun handleEvent(event: PusherEvent?) {
    event ?: return

    when (event.eventName) {
      "message:new" -> {
        try {
          val gson = Gson()
          val message = gson.fromJson(event.data, Message::class.java)
          onMessageReceived?.invoke(message)
        } catch (e: Exception) {
          e.printStackTrace()
        }
      }
      "client-typing-start" -> {
        try {
          val gson = Gson()
          val data = gson.fromJson(event.data, Map::class.java) as? Map<*, *>
          val senderName = data?.get("senderName") as? String
          senderName?.let { onTypingIndicator?.invoke(it) }
        } catch (e: Exception) {
          e.printStackTrace()
        }
      }
      "conversation:status-changed" -> {
        try {
          val gson = Gson()
          val data = gson.fromJson(event.data, Map::class.java) as? Map<*, *>
          val status = data?.get("status") as? String
          status?.let { onConversationStatusChanged?.invoke(it) }
        } catch (e: Exception) {
          e.printStackTrace()
        }
      }
    }
  }

  fun disconnect() {
    pusher?.disconnect()
  }
}
