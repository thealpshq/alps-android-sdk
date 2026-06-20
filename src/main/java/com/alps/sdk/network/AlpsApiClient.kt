package com.alps.sdk.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AlpsApiClient(private val config: AlpsConfig) {

  private val retrofit: Retrofit = buildRetrofit()
  private val service: AlpsApiService = retrofit.create(AlpsApiService::class.java)

  private fun buildRetrofit(): Retrofit {
    val logging = HttpLoggingInterceptor().apply {
      level = HttpLoggingInterceptor.Level.BODY
    }

    val okHttpClient = OkHttpClient.Builder()
      .addInterceptor(logging)
      .connectTimeout(30, TimeUnit.SECONDS)
      .readTimeout(30, TimeUnit.SECONDS)
      .writeTimeout(30, TimeUnit.SECONDS)
      .build()

    return Retrofit.Builder()
      .baseUrl("https://api.tryalps.com/api/v1/")
      .client(okHttpClient)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
  }

  fun fetchWidgetData(onSuccess: (WidgetDataResponse) -> Unit, onError: (String) -> Unit) {
    service.fetchWidgetData(config.widgetKey).enqueue(object : retrofit2.Callback<WidgetDataResponse> {
      override fun onResponse(
        call: retrofit2.Call<WidgetDataResponse>,
        response: retrofit2.Response<WidgetDataResponse>
      ) {
        if (response.isSuccessful) {
          response.body()?.let { onSuccess(it) }
        } else {
          onError("Failed to fetch widget data: ${response.code()}")
        }
      }

      override fun onFailure(call: retrofit2.Call<WidgetDataResponse>, t: Throwable) {
        onError(t.message ?: "Network error")
      }
    })
  }

  fun sendMessage(
    name: String?,
    email: String?,
    message: String,
    onSuccess: (SendMessageResponse) -> Unit,
    onError: (String) -> Unit
  ) {
    val request = SendMessageRequest(
      widgetKey = config.widgetKey,
      name = name,
      email = email,
      message = message,
      conversationId = config.conversationId,
      workspaceId = null,
      sessionId = config.visitorId,
      source = "mobile-sdk",
      os = "Android"
    )

    service.sendMessage(request).enqueue(object : retrofit2.Callback<SendMessageResponse> {
      override fun onResponse(
        call: retrofit2.Call<SendMessageResponse>,
        response: retrofit2.Response<SendMessageResponse>
      ) {
        if (response.isSuccessful) {
          response.body()?.let { onSuccess(it) }
        } else {
          onError("Failed to send message: ${response.code()}")
        }
      }

      override fun onFailure(call: retrofit2.Call<SendMessageResponse>, t: Throwable) {
        onError(t.message ?: "Network error")
      }
    })
  }

  fun fetchCustomerConversations(
    email: String,
    onSuccess: (CustomerConversationsResponse) -> Unit,
    onError: (String) -> Unit
  ) {
    if (email.isEmpty()) {
      onError("Email is required")
      return
    }

    service.fetchCustomerConversations(config.widgetKey, email)
      .enqueue(object : retrofit2.Callback<CustomerConversationsResponse> {
        override fun onResponse(
          call: retrofit2.Call<CustomerConversationsResponse>,
          response: retrofit2.Response<CustomerConversationsResponse>
        ) {
          if (response.isSuccessful) {
            response.body()?.let { onSuccess(it) }
          } else {
            onError("Failed to fetch conversations: ${response.code()}")
          }
        }

        override fun onFailure(call: retrofit2.Call<CustomerConversationsResponse>, t: Throwable) {
          onError(t.message ?: "Network error")
        }
      })
  }

  fun search(
    keyword: String,
    onSuccess: (SearchResponse) -> Unit,
    onError: (String) -> Unit
  ) {
    service.search(config.widgetKey, keyword).enqueue(object : retrofit2.Callback<SearchResponse> {
      override fun onResponse(
        call: retrofit2.Call<SearchResponse>,
        response: retrofit2.Response<SearchResponse>
      ) {
        if (response.isSuccessful) {
          response.body()?.let { onSuccess(it) }
        } else {
          onError("Search failed: ${response.code()}")
        }
      }

      override fun onFailure(call: retrofit2.Call<SearchResponse>, t: Throwable) {
        onError(t.message ?: "Network error")
      }
    })
  }
}
