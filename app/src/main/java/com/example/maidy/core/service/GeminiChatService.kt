package com.example.maidy.core.service

import com.example.maidy.core.data.BookingRepository
import com.example.maidy.core.data.MaidRepository
import com.example.maidy.core.data.SessionManager
import com.example.maidy.core.model.Booking
import com.example.maidy.core.model.Maid
import com.google.firebase.Firebase
import com.google.firebase.vertexai.Chat
import com.google.firebase.vertexai.type.FunctionDeclaration
import com.google.firebase.vertexai.type.Schema
import com.google.firebase.vertexai.type.Tool
import com.google.firebase.vertexai.type.content
import com.google.firebase.vertexai.vertexAI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale

// Import kotlinx serialization types
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonObject as KJsonObject

/**
 * Service for handling Gemini AI chat interactions using Firebase Vertex AI
 * Provides intelligent responses to user queries about bookings and maids using Function Calling
 */
class GeminiChatService(
    private val bookingRepository: BookingRepository,
    private val maidRepository: MaidRepository,
    private val sessionManager: SessionManager
) {

    // 1. Define Tools (Function Declarations)
    // Using emptyMap<String, Schema>() is acceptable for this SDK version
    private val getUserBookingsTool = FunctionDeclaration(
        name = "getUserBookings",
        description = "Get the current user's bookings/appointments history and upcoming schedule. Returns a JSON list of bookings.",
        parameters = emptyMap<String, Schema>()
    )

    private val getAvailableMaidsTool = FunctionDeclaration(
        name = "getAvailableMaids",
        description = "Get a list of available professional maids with their ratings, rates, and details. Returns a JSON list of maids.",
        parameters = emptyMap<String, Schema>()
    )

    // 2. Initialize Model with Tools
    private val generativeModel = Firebase.vertexAI.generativeModel(
        modelName = "gemini-2.5-flash",
        tools = listOf(
            Tool.functionDeclarations(
                listOf(
                    getUserBookingsTool,
                    getAvailableMaidsTool
                )
            )
        ),
        systemInstruction = content {
            text(
                """
                You are a friendly and helpful assistant for the Maidy app (a maid booking service).
                Your goal is to help users manage their bookings and find the best maids.
                
                Services offered:
                - Deep Cleaning
                - Standard Cleaning
                - Move-out Clean
                
                Always use the provided tools to fetch real data when the user asks about bookings or maids.
                If the user is not logged in (getUserBookings returns error), kindly ask them to log in.
                
                Keep your responses concise, warm, and helpful. 
                Do not make up booking IDs or maid names. Only use what the tools return.
            """.trimIndent()
            )
        }
    )

    // 3. Persist Chat State
    private var chatSession: Chat? = null

    /**
     * Process user message and generate AI response
     */
    suspend fun processMessage(userMessage: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            println("🤖 GeminiChatService: Processing message: $userMessage")

            // Initialize chat if null
            if (chatSession == null) {
                chatSession = generativeModel.startChat()
            }
            val chat = chatSession!!

            var response = chat.sendMessage(userMessage)

            // Handle function calls loop
            var turnCount = 0
            while (response.functionCalls.isNotEmpty() && turnCount < 5) {
                val functionCall = response.functionCalls.first()
                val functionName = functionCall.name
                println("🤖 Executing tool: $functionName")

                val functionResult = when (functionName) {
                    "getUserBookings" -> executeGetUserBookings()
                    "getAvailableMaids" -> executeGetAvailableMaids()
                    else -> JSONObject().put("error", "Unknown function")
                }

                // Send the result back to the model.
                // We must convert org.json.JSONObject to kotlinx.serialization.json.JsonObject
                response = chat.sendMessage(
                    content {
                        part(
                            com.google.firebase.vertexai.type.FunctionResponsePart(
                                functionName,
                                jsonToKotlinxJsonObject(functionResult)
                            )
                        )
                    }
                )
                turnCount++
            }

            println("✅ GeminiChatService: Response generated: ${response.text}")
            Result.success(response.text ?: "I'm sorry, I couldn't generate a text response, but I processed your request.")
        } catch (e: Exception) {
            println("❌ GeminiChatService: Error - ${e.message}")
            e.printStackTrace()
            // Reset chat on fatal error to clear bad state
            chatSession = null
            Result.failure(e)
        }
    }

    /**
     * Clears the current chat history. Call this when user logs out or clicks "New Chat".
     */
    fun clearHistory() {
        chatSession = null
    }

    // --- Tool Implementations ---

    private suspend fun executeGetUserBookings(): JSONObject {
        val userId = sessionManager.getCurrentUserId()
        if (userId == null) {
            return JSONObject().put("error", "User not logged in. Please ask user to log in.")
        }

        val result = bookingRepository.getUserBookings(userId)
        val bookings = result.getOrNull() ?: emptyList()

        if (bookings.isEmpty()) {
            return JSONObject().put("message", "No bookings found.")
        }

        return formatBookingsToJson(bookings)
    }

    private suspend fun executeGetAvailableMaids(): JSONObject {
        val result = maidRepository.getAllMaids()
        val maids = result.getOrNull() ?: emptyList()
        val availableMaids = maids.filter { it.available }

        if (availableMaids.isEmpty()) {
            return JSONObject().put("message", "No maids currently available.")
        }

        return formatMaidsToJson(availableMaids)
    }

    // --- Formatting Helpers ---

    /**
     * Converts an org.json.JSONObject to a kotlinx.serialization.json.JsonObject
     * This is required because Firebase Vertex AI uses Kotlinx Serialization.
     */
    private fun jsonToKotlinxJsonObject(json: JSONObject): KJsonObject {
        val map = mutableMapOf<String, JsonElement>()
        val keys = json.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = json.get(key)

            // We convert values to JsonPrimitive (Strings/Numbers) or String representation of complex objects
            // This avoids deep recursion complexity for now, as the model can parse JSON strings inside values easily.
            map[key] = when (value) {
                is String -> JsonPrimitive(value)
                is Number -> JsonPrimitive(value)
                is Boolean -> JsonPrimitive(value)
                // For nested Arrays/Objects, passing them as stringified JSON is safer and cleaner for the model to read
                is JSONObject -> JsonPrimitive(value.toString())
                is JSONArray -> JsonPrimitive(value.toString())
                else -> JsonPrimitive(value.toString())
            }
        }
        return KJsonObject(map)
    }

    private fun formatBookingsToJson(bookings: List<Booking>): JSONObject {
        val jsonArray = JSONArray()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        bookings.take(15).forEach { booking ->
            val dateStr = booking.nextScheduledDate?.toDate()?.let { date ->
                dateFormat.format(date)
            } ?: "Not scheduled"

            val json = JSONObject().apply {
                put("type", booking.bookingType.displayName())
                put("maid", booking.maidFullName)
                put("status", booking.status.name)
                put("isRecurring", booking.isRecurring)
                put("scheduled", dateStr)
                put("bookingId", booking.id)
            }
            jsonArray.put(json)
        }
        return JSONObject().apply { put("bookings", jsonArray) }
    }

    private fun formatMaidsToJson(maids: List<Maid>): JSONObject {
        val jsonArray = JSONArray()

        // Sort by rating and take top 10 to manage token usage
        maids.sortedByDescending { it.averageRating }.take(10).forEach { maid ->
            val json = JSONObject().apply {
                put("name", maid.fullName)
                put("rating", maid.averageRating)
                put("reviews", maid.reviewCount)
                put("hourlyRate", maid.hourlyRate)
                put("specialty", maid.specialtyTag)
                put("services", JSONArray(maid.services))
                put("maidId", maid.id)
            }
            jsonArray.put(json)
        }
        return JSONObject().apply { put("maids", jsonArray) }
    }
}