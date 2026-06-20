package com.alps.sdk.network

import com.google.gson.annotations.SerializedName
import java.io.Serializable

// Widget Data Response
data class WidgetDataResponse(
  val workspaceId: String,
  val categories: List<Category>,
  val playlists: List<Playlist>,
  val profile: Profile?,
  val teamName: String?,
  val teamAvatarUrl: String?,
  val widgetColor: String?,
  val headerImageUrl: String?,
  val welcomeMessage: String?,
  val launcherText: String?,
  val statusPageUrl: String?,
  val pusherKey: String?,
  val pusherCluster: String?,
  val aiAgent: AIAgent?,
  val onlineAgents: List<OnlineAgent>?
) : Serializable

data class Category(
  val id: String,
  val name: String,
  val articles: List<Article>
) : Serializable

data class Playlist(
  val id: String,
  val name: String,
  val guides: List<Guide>
) : Serializable

data class Article(
  val id: String,
  val title: String,
  val description: String?,
  val body: String?,
  val status: String?
) : Serializable

data class Guide(
  val id: String,
  val title: String,
  val content: String?,
  val status: String?
) : Serializable

data class Profile(
  val id: String,
  val name: String?
) : Serializable

data class AIAgent(
  val name: String?,
  val profilePictureUrl: String?
) : Serializable

data class OnlineAgent(
  val firstName: String?,
  val lastName: String?,
  val profilePicture: String?
) : Serializable

// Message & Conversation
data class Message(
  val id: String,
  val conversationId: String,
  val content: String,
  val direction: String,
  val senderType: String,
  val isNote: Boolean?,
  val read: Boolean?,
  val createdAt: String,
  val senderName: String?,
  val senderProfilePicture: String?
) : Serializable

data class Conversation(
  val id: String,
  val workspaceId: String,
  val status: String,
  val customer: Customer,
  val lastMessage: Message?,
  val lastMessageAt: String?,
  val createdAt: String,
  val messages: List<Message>?
) : Serializable

data class Customer(
  val name: String?,
  val email: String?
) : Serializable

data class SendMessageRequest(
  val widgetKey: String,
  val name: String?,
  val email: String?,
  val message: String,
  val conversationId: String?,
  val workspaceId: String?,
  val sessionId: String?,
  val source: String = "mobile-sdk",
  val os: String = "Android",
  val priority: String = "normal"
) : Serializable

data class SendMessageResponse(
  val conversationId: String,
  val workspaceId: String,
  val message: Message
) : Serializable

data class CustomerConversationsResponse(
  val conversations: List<ConversationSummary>
) : Serializable

data class ConversationSummary(
  val id: String,
  val lastMessage: Message?,
  val status: String,
  val createdAt: String,
  val lastMessageAt: String?
) : Serializable

// Search Response
data class SearchResponse(
  val articles: List<Article>?,
  val collections: List<Collection>?,
  val playlists: List<Playlist>?
) : Serializable

data class Collection(
  val id: String,
  val name: String?
) : Serializable
