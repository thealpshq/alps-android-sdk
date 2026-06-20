package com.alps.sdk.ui.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alps.sdk.Alps
import com.alps.sdk.network.Article
import java.util.*

class AlpsAnswersFragment : Fragment() {

  private lateinit var searchInput: EditText
  private lateinit var recyclerView: RecyclerView
  private lateinit var emptyStateView: TextView
  private val articles = mutableListOf<Article>()
  private var allArticles = listOf<Article>()
  private lateinit var adapter: ArticleAdapter
  private var searchTimer: Timer? = null

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
      setPadding(0, 0, 0, 0)

      // Search input
      searchInput = EditText(requireContext()).apply {
        hint = "Search help articles..."
        layoutParams = LinearLayout.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          56
        ).apply { setMargins(12, 8, 12, 8) }
        setPadding(12, 12, 12, 12)
      }
      addView(searchInput)

      // RecyclerView
      recyclerView = RecyclerView(requireContext()).apply {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = ArticleAdapter(articles)
      }
      addView(recyclerView, LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        0,
        1f
      ))

      // Empty state
      emptyStateView = TextView(requireContext()).apply {
        text = "No articles found"
        textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        layoutParams = LinearLayout.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT
        )
        visibility = View.GONE
      }
      addView(emptyStateView)
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setupSearch()
    loadFeaturedArticles()
  }

  override fun onDestroy() {
    super.onDestroy()
    searchTimer?.cancel()
  }

  private fun setupSearch() {
    searchInput.addTextChangedListener(object : android.text.TextWatcher {
      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        searchTimer?.cancel()
        val keyword = s.toString().trim()

        if (keyword.isEmpty()) {
          articles.clear()
          articles.addAll(allArticles.take(5))
          recyclerView.adapter?.notifyDataSetChanged()
          updateEmptyState()
          return
        }

        searchTimer = Timer().apply {
          schedule(object : TimerTask() {
            override fun run() {
              performSearch(keyword)
            }
          }, 300)
        }
      }

      override fun afterTextChanged(s: android.text.Editable?) {}
    })
  }

  private fun loadFeaturedArticles() {
    allArticles = emptyList()
    articles.clear()

    // Load from widget data
    val config = Alps.getConfig()
    // In a full implementation, we'd access widget data from the panel
    // For now, show featured articles from initial fetch
    recyclerView.adapter?.notifyDataSetChanged()
    updateEmptyState()
  }

  private fun performSearch(keyword: String) {
    Alps.getApiClient()?.search(
      keyword,
      onSuccess = { response ->
        articles.clear()
        articles.addAll(response.articles ?: emptyList())
        recyclerView.adapter?.notifyDataSetChanged()
        updateEmptyState()
      },
      onError = { error ->
        android.util.Log.e("AlpsAnswersFragment", "Search error: $error")
      }
    )
  }

  private fun updateEmptyState() {
    if (articles.isEmpty()) {
      emptyStateView.visibility = View.VISIBLE
      recyclerView.visibility = View.GONE
    } else {
      emptyStateView.visibility = View.GONE
      recyclerView.visibility = View.VISIBLE
    }
  }

  inner class ArticleAdapter(
    private val items: List<Article>
  ) : RecyclerView.Adapter<ArticleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
      val container = LinearLayout(parent.context).apply {
        orientation = LinearLayout.VERTICAL
        layoutParams = RecyclerView.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.WRAP_CONTENT
        )
        setPadding(16, 12, 16, 12)
      }
      return ArticleViewHolder(container)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
      holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
  }

  inner class ArticleViewHolder(private val container: LinearLayout) :
    RecyclerView.ViewHolder(container) {
    fun bind(article: Article) {
      container.removeAllViews()

      val titleView = TextView(container.context).apply {
        text = article.title
        textSize = 14f
        setTypeface(null, android.graphics.Typeface.BOLD)
        layoutParams = LinearLayout.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = 4 }
      }
      container.addView(titleView)

      if (!article.description.isNullOrEmpty()) {
        val descView = TextView(container.context).apply {
          text = article.description
          textSize = 12f
          setTextColor(android.graphics.Color.GRAY)
          layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
          )
        }
        container.addView(descView)
      }

      container.setOnClickListener {
        openArticle(article)
      }
    }
  }

  private fun openArticle(article: Article) {
    android.util.Log.d("AlpsAnswersFragment", "Opening article: ${article.id}")
  }
}
