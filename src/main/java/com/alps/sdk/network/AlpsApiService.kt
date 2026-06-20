package com.alps.sdk.network

import retrofit2.Call
import retrofit2.http.*

interface AlpsApiService {

  @GET("user/widget-data/{widgetKey}")
  fun fetchWidgetData(
    @Path("widgetKey") widgetKey: String
  ): Call<WidgetDataResponse>

  @POST("message/customer")
  fun sendMessage(
    @Body request: SendMessageRequest
  ): Call<SendMessageResponse>

  @GET("message/customer/conversations")
  fun fetchCustomerConversations(
    @Query("widgetKey") widgetKey: String,
    @Query("email") email: String
  ): Call<CustomerConversationsResponse>

  @GET("report/search/{widgetKey}")
  fun search(
    @Path("widgetKey") widgetKey: String,
    @Query("keyword") keyword: String
  ): Call<SearchResponse>

  @POST("pusher/auth")
  @FormUrlEncoded
  fun authPusher(
    @Field("socket_id") socketId: String,
    @Field("channel_name") channelName: String,
    @Field("widgetKey") widgetKey: String
  ): Call<String>
}
