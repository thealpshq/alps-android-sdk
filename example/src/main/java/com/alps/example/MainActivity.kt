package com.alps.example

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.alps.sdk.Alps

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Initialize Alps SDK
    Alps.configure(
      context = this,
      widgetKey = "test_widget_key",
      userName = null,
      userEmail = null
    )

    val layout = LinearLayout(this).apply {
      orientation = LinearLayout.VERTICAL
      layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.MATCH_PARENT
      )
      setPadding(32, 32, 32, 32)

      // Title
      addView(TextView(this@MainActivity).apply {
        text = "Alps SDK Example"
        textSize = 24f
        layoutParams = LinearLayout.LayoutParams(
          LinearLayout.LayoutParams.MATCH_PARENT,
          LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = 64 }
      })

      // Show Chat button
      addView(Button(this@MainActivity).apply {
        text = "Show Chat"
        layoutParams = LinearLayout.LayoutParams(
          LinearLayout.LayoutParams.MATCH_PARENT,
          56
        ).apply { bottomMargin = 16 }
        setOnClickListener {
          Alps.show(this@MainActivity)
        }
      })

      // Identify button
      addView(Button(this@MainActivity).apply {
        text = "Identify as User"
        layoutParams = LinearLayout.LayoutParams(
          LinearLayout.LayoutParams.MATCH_PARENT,
          56
        ).apply { bottomMargin = 16 }
        setOnClickListener {
          Alps.identify("Test User", "test@example.com")
        }
      })

      // Logout button
      addView(Button(this@MainActivity).apply {
        text = "Logout"
        layoutParams = LinearLayout.LayoutParams(
          LinearLayout.LayoutParams.MATCH_PARENT,
          56
        )
        setOnClickListener {
          Alps.logout()
        }
      })
    }

    setContentView(layout)
  }
}
