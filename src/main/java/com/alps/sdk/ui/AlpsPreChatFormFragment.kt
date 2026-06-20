package com.alps.sdk.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.alps.sdk.Alps
import com.alps.sdk.storage.AlpsVisitorStore

class AlpsPreChatFormFragment : Fragment() {

  private lateinit var nameInput: EditText
  private lateinit var emailInput: EditText
  private lateinit var submitButton: Button
  var onSubmit: ((String, String) -> Unit)? = null

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
      setPadding(24, 32, 24, 32)
      setBackgroundColor(android.graphics.Color.WHITE)

      // Title
      addView(TextView(requireContext()).apply {
        text = "Before we chat..."
        textSize = 18f
        layoutParams = LinearLayout.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = 24 }
      })

      // Name input
      nameInput = EditText(requireContext()).apply {
        hint = "Your name"
        layoutParams = LinearLayout.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          48
        ).apply { bottomMargin = 16 }
        setPadding(12, 12, 12, 12)
        setBackgroundResource(android.R.drawable.edit_text)
      }
      addView(nameInput)

      // Email input
      emailInput = EditText(requireContext()).apply {
        hint = "Your email"
        inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        layoutParams = LinearLayout.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          48
        ).apply { bottomMargin = 24 }
        setPadding(12, 12, 12, 12)
        setBackgroundResource(android.R.drawable.edit_text)
      }
      addView(emailInput)

      // Submit button
      submitButton = Button(requireContext()).apply {
        text = "Continue"
        layoutParams = LinearLayout.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          48
        )
        setOnClickListener { submitForm() }
      }
      addView(submitButton)
    }
  }

  private fun submitForm() {
    val name = nameInput.text.toString().trim()
    val email = emailInput.text.toString().trim()

    if (name.isEmpty() || email.isEmpty()) {
      Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
      return
    }

    val config = Alps.getConfig()
    if (config != null) {
      config.visitorName = name
      config.visitorEmail = email
      requireContext().let { AlpsVisitorStore.save(it, config) }
    }

    onSubmit?.invoke(name, email)
  }
}
