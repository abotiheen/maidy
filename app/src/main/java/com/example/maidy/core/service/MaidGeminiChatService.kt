package com.example.maidy.core.service

import com.example.maidy.core.data.BookingRepository
import com.example.maidy.core.data.SessionManager
import com.example.maidy.core.model.Booking
import com.example.maidy.core.model.BookingStatus
import com.google.firebase.Firebase
import com.google.firebase.vertexai.Chat
import com.google.firebase.vertexai.type.FunctionDeclaration
import com.google.firebase.vertexai.type.FunctionResponsePart
import com.google.firebase.vertexai.type.Schema
import com.google.firebase.vertexai.type.StringFormat
import com.google.firebase.vertexai.type.Tool
import com.google.firebase.vertexai.type.content
import com.google.firebase.vertexai.vertexAI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive

class MaidGeminiChatService(
    private val bookingRepository: BookingRepository,
    private val sessionManager: SessionManager
) {
    private val getMaidBookingsTool = FunctionDeclaration(
        name = "getMaidBookings",
        description = "Get all bookings assigned to the current maid (history and upcoming schedule).",
        parameters = emptyMap()
    )

    private val updateBookingStatusTool = FunctionDeclaration(
        name = "updateBookingStatus",
        description = """
            Update the status of exactly ONE booking that is assigned to the currently logged-in maid.
            Requires an exact bookingId. You can conclude what is the booking id if the user is referring to a specific booking by its status, date, or other details that identify the booking, but if you are not sure what booking the user is referring to never guess the id for the booking. Must not update any other booking.
        """.trimIndent(),
        parameters = mapOf(
            "bookingId" to Schema.string(
                description = "Exact booking ID to update.",
                nullable = false,
                format = StringFormat.Custom("uuid")
            ),
            "newStatus" to Schema.enumeration(
                values = BookingStatus.values().map { it.name },
                description = "New booking status. Must be one of the known BookingStatus enum names.",
                nullable = false
            )
        )
    )

    private val generativeModel = Firebase.vertexAI.generativeModel(
        modelName = "gemini-2.5-flash",
        tools = listOf(
            Tool.functionDeclarations(listOf(getMaidBookingsTool, updateBookingStatusTool))
        ),
        systemInstruction = content {
            text(
                """
                You are a helpful assistant for maids in the Maidy app.
                You help service providers manage their cleaning job bookings, answer questions about their schedule, and provide detail about their completed and pending work.
                Always use the provided tool to preview their bookings and answer based only on real data.
                Never speculate; always say 'I couldn't find any bookings for you' if no data is available.
                
                rules you must obey:
                - when you get any details, format it in a way that will make it pretty and understandable for the user.
                - when you get data from data base do not ever display it as it is. for example you get data for a booking that looks like this
                 booking_id: 213124fesfe
                 booking status: canceled
                 booking_date: october second.
                 user_name: ali
                 instead you will generate a paragraph that will display these details to the user in a human way. you will say somethings like "you have a booking by ali on october second and the current status is cancelled 
            """.trimIndent()
            )
        }
    )

    private var chatSession: Chat? = null

    suspend fun processMessage(userMessage: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (chatSession == null) chatSession = generativeModel.startChat()
            val chat = chatSession!!

            var response = chat.sendMessage(userMessage)

            var turnCount = 0
            while (response.functionCalls.isNotEmpty() && turnCount < 5) {
                val call = response.functionCalls.first() // handle one at a time
                val result: JsonObject = when (call.name) {
                    "getMaidBookings" -> executeGetMaidBookings()
                    "updateBookingStatus" -> executeUpdateBookingStatus(call.args)
                    else -> buildJsonObject { put("error", JsonPrimitive("Unknown function: ${call.name}")) }
                }

                response = chat.sendMessage(
                    content {
                        part(FunctionResponsePart(call.name, result))
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

    private suspend fun executeGetMaidBookings(): JsonObject {
        val maidId = sessionManager.getCurrentUserId()
            ?: return buildJsonObject { put("error", JsonPrimitive("Maid not logged in.")) }

        val bookings = bookingRepository.getMaidBookings(maidId).getOrNull().orEmpty()

        // Keep structure stable for the model
        return buildJsonObject {
            put("maidId", JsonPrimitive(maidId))
            put("bookings", bookingsToJsonArray(bookings.take(15)))
            put("rawBookings_policy", JsonPrimitive("INTERNAL_ONLY: never show rawBookings to the user; use summaryText instead."))
            if (bookings.isEmpty()) put("message", JsonPrimitive("No bookings found."))
        }
    }

    /**
     * Safely updates ONLY the requested booking (no side effects):
     * - validates args
     * - ensures booking belongs to current maid
     * - updates status only (and updatedAt) with notifyUser=false
     */
    private suspend fun executeUpdateBookingStatus(args: Map<String, JsonElement>): JsonObject {
        val maidId = sessionManager.getCurrentUserId()
            ?: return buildJsonObject { put("error", JsonPrimitive("Maid not logged in.")) }

        val bookingId = args["bookingId"]?.jsonPrimitive?.contentOrNull()?.trim()
        if (bookingId.isNullOrEmpty()) {
            return buildJsonObject { put("error", JsonPrimitive("Missing required parameter: bookingId")) }
        }

        val rawStatus = args["newStatus"]?.jsonPrimitive?.contentOrNull()?.trim()
        val newStatus = BookingStatus.values().firstOrNull { it.name.equals(rawStatus, ignoreCase = true) }
            ?: return buildJsonObject {
                put("error", JsonPrimitive("Invalid newStatus: $rawStatus"))
                put("allowedStatuses", buildJsonArray {
                    BookingStatus.values().forEach { add(JsonPrimitive(it.name)) }
                })
            }

        val bookingResult = bookingRepository.getBookingById(bookingId)
        val booking = bookingResult.getOrNull()
            ?: return buildJsonObject { put("error", JsonPrimitive("Booking not found: $bookingId")) }

        if (booking.maidId != maidId) {
            return buildJsonObject {
                put("error", JsonPrimitive("Not allowed: booking is not assigned to this maid."))
                put("bookingId", JsonPrimitive(bookingId))
            }
        }

        val oldStatus = booking.status.name
        if (oldStatus == newStatus.name) {
            return buildJsonObject {
                put("success", JsonPrimitive(true))
                put("bookingId", JsonPrimitive(bookingId))
                put("oldStatus", JsonPrimitive(oldStatus))
                put("newStatus", JsonPrimitive(newStatus.name))
                put("message", JsonPrimitive("No change: booking already has this status."))
            }
        }

        val updateResult = bookingRepository.updateBookingStatus(
            bookingId = bookingId,
            newStatus = newStatus,
        )

        if (updateResult.isFailure) {
            return buildJsonObject {
                put("error", JsonPrimitive(updateResult.exceptionOrNull()?.message ?: "Failed to update status"))
                put("bookingId", JsonPrimitive(bookingId))
            }
        }

        return buildJsonObject {
            put("success", JsonPrimitive(true))
            put("bookingId", JsonPrimitive(bookingId))
            put("oldStatus", JsonPrimitive(oldStatus))
            put("newStatus", JsonPrimitive(newStatus.name))
        }
    }

    private fun bookingsToJsonArray(bookings: List<Booking>): JsonArray = buildJsonArray {
        bookings.forEach { booking ->
            add(
                buildJsonObject {
                    put("bookingId", JsonPrimitive(booking.id))
                    put("type", JsonPrimitive(booking.bookingType.displayName()))
                    put("user", JsonPrimitive(booking.userFullName))
                    put("status", JsonPrimitive(booking.status.name))
                    put("isRecurring", JsonPrimitive(booking.isRecurring))

                    val date = booking.nextScheduledDate?.toDate()
                    put("scheduledEpochMillis", date?.time?.let { JsonPrimitive(it) } ?: JsonNull)
                    put("scheduledText", JsonPrimitive(date?.toString() ?: "Not scheduled"))
                }
            )
        }
    }

    private fun JsonPrimitive.contentOrNull(): String? = try {
        this.content
    } catch (_: Exception) {
        null
    }
}
