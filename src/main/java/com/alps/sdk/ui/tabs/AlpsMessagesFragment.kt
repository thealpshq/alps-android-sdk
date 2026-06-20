package com.alps.sdk.ui.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alps.sdk.Alps
import com.alps.sdk.network.ConversationSummary
import java.text.SimpleDateFormat
import java.util.*

class AlpsMessagesFragment : Fragment() {

  private lateinit var recyclerView: RecyclerView
  private lateinit var progressBar: ProgressBar
  private lateinit var emptyStateView: TextView
  private val conversations = mutableListOf<ConversationSummary>()
  private lateinit var adapter: ConversationAdapter

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return LinearLayout(requireContext()).apply {
      orientation = LinearLayout.VERTICAL
      layoutParams = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
      )

      // RecyclerView
      recyclerView = RecyclerView(requireContext()).apply {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = ConversationAdapter(conversations)
      }
      addView(recyclerView, LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        0,
        1f
      ))

      // Progress bar
      progressBar = ProgressBar(requireContext()).apply {
        layoutParams = LinearLayout.LayoutParams(
          ViewGroup.LayoutParams.WRAP_CONTENT,
          ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
          gravity = android.view.Gravity.CENTER
        }
      }
      addView(progressBar)

      // Empty state
      emptyStateView = TextView(requireContext()).apply {
        text = "No conversations yet.\nStart one to get help!"
        textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        visibility = View.GONE
      }
      addView(emptyStateView)
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    loadConversations()
  }

  private fun loadConversations() {
    val config = Alps.getConfig() ?: return
    val email = config.visitorEmail ?: run {
      showEmptyState()
      return
    }

    progressBar.visibility = View.VISIBLE
    Alps.getApiClient()?.fetchCustomerConversations(
      email,
      onSuccess = { response ->
        conversations.clear()
        conversations.addAll(response.conversations.sortedByDescending { it.lastMessageAt ?: it.createdAt })
        recyclerView.adapter?.notifyDataSetChanged()
        progressBar.visibility = View.GONE
        if (conversations.isEmpty()) {
          showEmptyState()
        } else {
          emptyStateView.visibility = View.GONE
        }
      },
      onError = { error ->
        android.util.Log.e("AlpsMessagesFragment", "Error: $error")
        progressBar.visibility = View.GONE
        showEmptyState()
      }
    )
  }

  private fun showEmptyState() {
    emptyStateView.visibility = View.VISIBLE
    recyclerView.visibility = View.GONE
  }

  inner class ConversationAdapter(
    private val items: List<ConversationSummary>
  ) : RecyclerView.Adapter<ConversationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
      return ConversationViewHolder(TextView(parent.context).apply {
        layoutParams = RecyclerView.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          100
        )
        setPadding(16, 12, 16, 12)
        textSize = 14f
      })
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
      val conv = items[position]
      holder.bind(conv)
    }

    override fun getItemCount(): Int = items.size
  }

  inner class ConversationViewHolder(private val textView: TextView) :
    RecyclerView.ViewHolder(textView) {
    fun bind(conv: ConversationSummary) {
      textView.text = "${conv.lastMessage?.content ?: "No messages"} · ${formatDate(conv.lastMessageAt ?: conv.createdAt)}"
      textView.setOnClickListener {
        openThread(conv.id)
      }
    }
  }

  private fun openThread(conversationId: String) {
    Alps.getConfig()?.conversationId = conversationId
    if (activity is com.alps.sdk.ui.AlpsPanelActivity) {
      (activity as com.alps.sdk.ui.AlpsPanelActivity).openThread(conversationId)
    }
  }

  private fun formatDate(dateString: String): String {
    return try {
      val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
      val date = format.parse(dateString) ?: return dateString
      SimpleDateFormat("MMM d, h:mm a", Locale.US).format(date)
    } catch (e: Exception) {
      dateString
    }
  }
}
