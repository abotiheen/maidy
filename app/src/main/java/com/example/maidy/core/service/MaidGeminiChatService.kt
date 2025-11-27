package com.example.maidy.core.service

import com.example.maidy.core.data.BookingRepository
import com.example.maidy.core.data.SessionManager
import com.example.maidy.core.model.Booking
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
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonObject as KJsonObject

/**
 * Service for Gemini AI chat interactions specialized for maids.
 * Lets the AI preview all bookings for the current maid and answer questions about them.
 */
class MaidGeminiChatService(
    private val bookingRepository: BookingRepository,
    private val sessionManager: SessionManager
) {
    private val getMaidBookingsTool = FunctionDeclaration(
        name = "getMaidBookings",
        description = "Get all bookings assigned to the current maid (history and upcoming schedule). Returns a JSON list.",
        parameters = emptyMap<String, Schema>()
    )

    private val generativeModel = Firebase.vertexAI.generativeModel(
        modelName = "gemini-2.5-flash",
        tools = listOf(Tool.functionDeclarations(listOf(getMaidBookingsTool))),
        systemInstruction = content {
            text(
                """
                You are a helpful assistant for maids in the Maidy app.
                You help service providers manage their cleaning job bookings, answer questions about their schedule, and provide detail about their completed and pending work.
                Always use the provided tool to preview their bookings and answer based only on real data.
                Never speculate; always say 'I couldn't find any bookings for you' if no data is available.
            """.trimIndent()
            )
        }
    )

    private var chatSession: Chat? = null

    suspend fun processMessage(userMessage: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (chatSession == null) {
                chatSession = generativeModel.startChat()
            }
            val chat = chatSession!!
            var response = chat.sendMessage(userMessage)

            // Tool function loop
            var turnCount = 0
            while (response.functionCalls.isNotEmpty() && turnCount < 5) {
                val functionCall = response.functionCalls.first()
                val functionName = functionCall.name
                val functionResult = when (functionName) {
                    "getMaidBookings" -> executeGetMaidBookings()
                    else -> JSONObject().put("error", "Unknown function")
                }
                // Send result back to model
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
            Result.success(response.text ?: "I'm sorry, I couldn't answer your request.")
        } catch (e: Exception) {
            chatSession = null
            Result.failure(e)
        }
    }

    fun clearHistory() {
        chatSession = null
    }

    private suspend fun executeGetMaidBookings(): JSONObject {
        val maidId = sessionManager.getCurrentUserId()
        if (maidId == null) {
            return JSONObject().put("error", "Maid not logged in.")
        }
        val result = bookingRepository.getMaidBookings(maidId)
        val bookings = result.getOrNull() ?: emptyList()
        if (bookings.isEmpty()) {
            return JSONObject().put("message", "No bookings found.")
        }
        return formatBookingsToJson(bookings)
    }

    private fun jsonToKotlinxJsonObject(json: JSONObject): KJsonObject {
        val map = mutableMapOf<String, JsonElement>()
        val keys = json.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = json.get(key)
            map[key] = when (value) {
                is String -> JsonPrimitive(value)
                is Number -> JsonPrimitive(value)
                is Boolean -> JsonPrimitive(value)
                is JSONObject -> JsonPrimitive(value.toString())
                is JSONArray -> JsonPrimitive(value.toString())
                else -> JsonPrimitive(value.toString())
            }
        }
        return KJsonObject(map)
    }

    private fun formatBookingsToJson(bookings: List<Booking>): JSONObject {
        val jsonArray = JSONArray()
        bookings.take(15).forEach { booking ->
            val json = JSONObject().apply {
                put("type", booking.bookingType.displayName())
                put("user", booking.userFullName)
                put("status", booking.status.name)
                put("isRecurring", booking.isRecurring)
                put("scheduled", booking.nextScheduledDate?.toDate()?.toString() ?: "Not scheduled")
                put("bookingId", booking.id)
            }
            jsonArray.put(json)
        }
        return JSONObject().apply { put("bookings", jsonArray) }
    }
}
