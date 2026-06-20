package com.alps.sdk.ui.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.alps.sdk.Alps
import com.alps.sdk.ui.AlpsPanelActivity

class AlpsHomeFragment : Fragment() {

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
      setPadding(16, 16, 16, 16)

      // Welcome message
      addView(TextView(requireContext()).apply {
        text = "Welcome to Support"
        textSize = 18f
        layoutParams = LinearLayout.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = 16 }
      })

      // Start button
      addView(Button(requireContext()).apply {
        text = "Start a Conversation"
        layoutParams = LinearLayout.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          56
        ).apply { bottomMargin = 16 }
        setOnClickListener {
          if (activity is AlpsPanelActivity) {
            (activity as AlpsPanelActivity).openThread("")
          }
        }
      })
    }
  }
}
