package com.alps.sdk.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.alps.sdk.Alps
import com.alps.sdk.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.alps.sdk.ui.tabs.AlpsHomeFragment
import com.alps.sdk.ui.tabs.AlpsMessagesFragment
import com.alps.sdk.ui.tabs.AlpsAnswersFragment
import com.alps.sdk.ui.tabs.AlpsThreadFragment
import com.alps.sdk.storage.AlpsVisitorStore

class AlpsPanelActivity : AppCompatActivity() {

  private lateinit var viewPager: ViewPager2
  private lateinit var tabLayout: TabLayout
  private var isShowingThread = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_alps_panel)

    val widgetKey = intent.getStringExtra("widgetKey") ?: return

    // Setup toolbar
    val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
    setSupportActionBar(toolbar)
    supportActionBar?.title = "Support"
    supportActionBar?.setDisplayHomeAsUpEnabled(true)

    // Check for pre-chat form
    val config = Alps.getConfig()
    if (config?.visitorName == null || config.visitorEmail == null) {
      showPreChatForm()
    } else {
      setupMainPanel()
    }
  }

  private fun showPreChatForm() {
    val fragment = AlpsPreChatFormFragment()
    fragment.onSubmit = { name, email ->
      setupMainPanel()
    }

    supportFragmentManager.beginTransaction()
      .replace(R.id.fragmentContainer, fragment)
      .commit()
  }

  private fun setupMainPanel() {
    viewPager = findViewById(R.id.viewPager)
    tabLayout = findViewById(R.id.tabLayout)

    val adapter = AlpsPagerAdapter(this)
    viewPager.adapter = adapter

    TabLayoutMediator(tabLayout, viewPager) { tab, position ->
      tab.text = when (position) {
        0 -> "Home"
        1 -> "Messages"
        2 -> "Answers"
        else -> ""
      }
    }.attach()

    tabLayout.visibility = TabLayout.VISIBLE
    viewPager.visibility = ViewPager2.VISIBLE
  }

  fun openThread(conversationId: String) {
    val fragment = AlpsThreadFragment().apply {
      arguments = Bundle().apply {
        putString("conversationId", conversationId)
      }
    }

    supportFragmentManager.beginTransaction()
      .replace(R.id.fragmentContainer, fragment)
      .addToBackStack(null)
      .commit()

    tabLayout.visibility = TabLayout.GONE
    viewPager.visibility = ViewPager2.GONE
    isShowingThread = true

    supportActionBar?.setDisplayHomeAsUpEnabled(true)
  }

  override fun onBackPressed() {
    if (isShowingThread) {
      isShowingThread = false
      tabLayout.visibility = TabLayout.VISIBLE
      viewPager.visibility = ViewPager2.VISIBLE
      supportFragmentManager.popBackStack()
    } else {
      super.onBackPressed()
    }
  }

  override fun onSupportNavigateUp(): Boolean {
    onBackPressed()
    return true
  }

  inner class AlpsPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
      return when (position) {
        0 -> AlpsHomeFragment()
        1 -> AlpsMessagesFragment()
        2 -> AlpsAnswersFragment()
        else -> AlpsHomeFragment()
      }
    }
  }
}
