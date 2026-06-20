package com.alps.sdk.ui.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alps.sdk.Alps
import com.alps.sdk.network.Message
import com.alps.sdk.realtime.AlpsPusherClient
import java.text.SimpleDateFormat
import java.util.*

class AlpsThreadFragment : Fragment() {

  private lateinit var recyclerView: RecyclerView
  private lateinit var messageInput: EditText
  private lateinit var sendButton: Button
  private val messages = mutableListOf<Message>()
  private lateinit var adapter: MessageAdapter
  private var pusherClient: AlpsPusherClient? = null
  private var conversationId: String? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    conversationId = arguments?.getString("conversationId")
    return LinearLayout(requireContext()).apply {
      orientation = LinearLayout.VERTICAL
      layoutParams = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
      )

      // RecyclerView
      recyclerView = RecyclerView(requireContext()).apply {
        layoutManager = LinearLayoutManager(requireContext()).apply {
          stackFromEnd = true
        }
        adapter = MessageAdapter(messages)
      }
      addView(recyclerView, LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        0,
        1f
      ))

      // Input area
      val inputContainer = LinearLayout(requireContext()).apply {
        orientation = LinearLayout.HORIZONTAL
        layoutParams = LinearLayout.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.WRAP_CONTENT
        )
        setBackgroundColor(android.graphics.Color.parseColor("#F5F5F5"))
        setPadding(8, 8, 8, 8)
      }

      messageInput = EditText(requireContext()).apply {
        hint = "Type a message..."
        layoutParams = LinearLayout.LayoutParams(
          0,
          48,
          1f
        ).apply { marginEnd = 8 }
        setPadding(12, 0, 12, 0)
        setBackgroundResource(android.R.drawable.edit_text)
      }
      inputContainer.addView(messageInput)

      sendButton = Button(requireContext()).apply {
        text = "Send"
        layoutParams = LinearLayout.LayoutParams(
          48,
          48
        )
        setOnClickListener { onSendMessage() }
      }
      inputContainer.addView(sendButton)

      addView(inputContainer)
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setupPusher()
  }

  override fun onDestroy() {
    super.onDestroy()
    pusherClient?.disconnect()
  }

  private fun setupPusher() {
    conversationId ?: return
    val config = Alps.getConfig() ?: return
    val pusherKey = config.pusherKey ?: return
    val pusherCluster = config.pusherCluster ?: return

    pusherClient = AlpsPusherClient().apply {
      onMessageReceived = { message ->
        messages.add(message)
        recyclerView.adapter?.notifyItemInserted(messages.size - 1)
        recyclerView.scrollToPosition(messages.size - 1)
      }
      onConversationStatusChanged = { status ->
        android.util.Log.d("AlpsThreadFragment", "Status: $status")
      }
      connect(pusherKey, pusherCluster, conversationId!!)
    }
  }

  private fun onSendMessage() {
    val text = messageInput.text.toString().trim()
    if (text.isEmpty()) return

    val config = Alps.getConfig() ?: return
    val name = config.visitorName ?: "Guest"
    val email = config.visitorEmail ?: ""

    Alps.getApiClient()?.sendMessage(
      name, email, text,
      onSuccess = { response ->
        config.conversationId = response.conversationId
        messages.add(response.message)
        recyclerView.adapter?.notifyItemInserted(messages.size - 1)
        recyclerView.scrollToPosition(messages.size - 1)
        messageInput.text.clear()
      },
      onError = { error ->
        android.util.Log.e("AlpsThreadFragment", "Send failed: $error")
        Toast.makeText(requireContext(), "Failed to send message", Toast.LENGTH_SHORT).show()
      }
    )
  }

  inner class MessageAdapter(private val items: List<Message>) :
    RecyclerView.Adapter<MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
      return MessageViewHolder(FrameLayout(parent.context).apply {
        layoutParams = RecyclerView.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.WRAP_CONTENT
        )
      })
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
      holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
  }

  inner class MessageViewHolder(private val container: FrameLayout) :
    RecyclerView.ViewHolder(container) {
    fun bind(message: Message) {
      container.removeAllViews()

      val isFromCustomer = message.direction == "outbound"

      val bubbleView = FrameLayout(container.context).apply {
        setBackgroundColor(
          if (isFromCustomer) android.graphics.Color.parseColor("#007AFF")
          else android.graphics.Color.parseColor("#E5E5EA")
        )
        layoutParams = FrameLayout.LayoutParams(
          FrameLayout.LayoutParams.WRAP_CONTENT,
          FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
          gravity = if (isFromCustomer) android.view.Gravity.END else android.view.Gravity.START
          setMargins(if (isFromCustomer) 64 else 8, 4, if (isFromCustomer) 8 else 64, 4)
        }
        setPadding(12, 8, 12, 8)
      }

      val textView = TextView(container.context).apply {
        text = message.content
        textSize = 14f
        setTextColor(if (isFromCustomer) android.graphics.Color.WHITE else android.graphics.Color.BLACK)
        layoutParams = FrameLayout.LayoutParams(
          FrameLayout.LayoutParams.WRAP_CONTENT,
          FrameLayout.LayoutParams.WRAP_CONTENT
        )
      }

      bubbleView.addView(textView)
      container.addView(bubbleView)
    }
  }
}
