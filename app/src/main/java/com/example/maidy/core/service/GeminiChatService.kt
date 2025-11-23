package com.example.maidy.core.service

import com.example.maidy.BuildConfig
import com.example.maidy.core.data.BookingRepository
import com.example.maidy.core.data.MaidRepository
import com.example.maidy.core.data.SessionManager
import com.example.maidy.core.model.Booking
import com.example.maidy.core.model.Maid
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Service for handling Gemini AI chat interactions
 * Provides intelligent responses to user queries about bookings and maids
 */
class GeminiChatService(
    private val bookingRepository: BookingRepository,
    private val maidRepository: MaidRepository,
    private val sessionManager: SessionManager
) {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    /**
     * Process user message and generate AI response
     */
    suspend fun processMessage(userMessage: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            println("🤖 GeminiChatService: Processing message: $userMessage")

            // Classify user intent
            val intent = classifyIntent(userMessage)
            println("🤖 Intent detected: $intent")

            // Get context-aware response based on intent
            val response = when (intent) {
                "QUERY_BOOKINGS" -> handleBookingsQuery(userMessage)
                "SEARCH_MAIDS" -> handleMaidsSearch(userMessage)
                else -> handleGeneralQuery(userMessage)
            }

            println("✅ GeminiChatService: Response generated")
            Result.success(response)
        } catch (e: Exception) {
            println("❌ GeminiChatService: Error - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Classify user intent using simple keyword matching
     */
    private fun classifyIntent(message: String): String {
        val lowerMessage = message.lowercase()

        return when {
            lowerMessage.contains("booking") ||
                    lowerMessage.contains("appointment") ||
                    lowerMessage.contains("schedule") ||
                    lowerMessage.contains("upcoming") ||
                    lowerMessage.contains("my bookings") ||
                    lowerMessage.contains("status") -> "QUERY_BOOKINGS"

            lowerMessage.contains("maid") ||
                    lowerMessage.contains("best") ||
                    lowerMessage.contains("top rated") ||
                    lowerMessage.contains("highest rated") ||
                    lowerMessage.contains("available") ||
                    lowerMessage.contains("find") ||
                    lowerMessage.contains("search") ||
                    lowerMessage.contains("recommend") -> "SEARCH_MAIDS"

            else -> "GENERAL"
        }
    }

    /**
     * Handle queries about user's bookings
     */
    private suspend fun handleBookingsQuery(message: String): String {
        val userId =
            sessionManager.getCurrentUserId() ?: return "Please log in to view your bookings."

        val bookingsResult = bookingRepository.getUserBookings(userId)

        return if (bookingsResult.isSuccess) {
            val bookings = bookingsResult.getOrNull() ?: emptyList()

            if (bookings.isEmpty()) {
                "You don't have any bookings yet. Would you like to browse available maids and create a booking?"
            } else {
                generateBookingsSummary(bookings, message)
            }
        } else {
            "I'm having trouble accessing your bookings right now. Please try again later."
        }
    }

    /**
     * Generate AI summary of user's bookings
     */
    private suspend fun generateBookingsSummary(
        bookings: List<Booking>,
        userQuery: String
    ): String {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())

        val bookingsInfo = bookings.take(10).joinToString("\n") { booking ->
            val dateStr = booking.nextScheduledDate?.toDate()?.let { date ->
                dateFormat.format(date)
            } ?: "Not scheduled"
            """
            - ${booking.bookingType.displayName()} with ${booking.maidFullName}
              Status: ${booking.status.name}
              ${if (booking.isRecurring) "Recurring: ${booking.recurringType?.name}" else "One-time booking"}
              Next scheduled: $dateStr
            """.trimIndent()
        }

        val prompt = """
            You are a helpful assistant for the Maidy app (a maid booking service).
            
            User question: "$userQuery"
            
            User's bookings:
            $bookingsInfo
            
            Total bookings: ${bookings.size}
            
            Provide a friendly, concise response about their bookings. If they have upcoming bookings, 
            mention the next one with details. If asking about a specific booking or status, focus on that.
            Be conversational and helpful. Keep response under 150 words.
        """.trimIndent()

        return try {
            val response = generativeModel.generateContent(prompt)
            response.text ?: "You have ${bookings.size} booking(s) in total."
        } catch (e: Exception) {
            println("❌ Error generating bookings summary: ${e.message}")
            val upcomingBookings = bookings.filter {
                it.status.name != "COMPLETED" && it.status.name != "CANCELLED"
            }
            if (upcomingBookings.isNotEmpty()) {
                val next = upcomingBookings.first()
                val dateStr = next.nextScheduledDate?.toDate()?.let { date ->
                    dateFormat.format(date)
                } ?: "an unscheduled date"
                """
                You have ${bookings.size} booking(s) total.
                
                Your next booking:
                • ${next.bookingType.displayName()} with ${next.maidFullName}
                • Status: ${next.status.name}
                • Scheduled: $dateStr
                """.trimIndent()
            } else {
                "You have ${bookings.size} booking(s), but no upcoming appointments scheduled."
            }
        }
    }

    /**
     * Handle maid search queries
     */
    private suspend fun handleMaidsSearch(message: String): String {
        val maidsResult = maidRepository.getAllMaids()

        return if (maidsResult.isSuccess) {
            val maids = maidsResult.getOrNull() ?: emptyList()

            if (maids.isEmpty()) {
                "I couldn't find any maids available at the moment. Please check back later!"
            } else {
                generateMaidsSummary(maids, message)
            }
        } else {
            "I'm having trouble searching for maids right now. Please try again later."
        }
    }

    /**
     * Generate AI summary of available maids
     */
    private suspend fun generateMaidsSummary(maids: List<Maid>, userQuery: String): String {
        // Filter available maids and sort by rating
        val availableMaids = maids.filter { it.available }
            .sortedByDescending { it.averageRating }
            .take(5)

        val maidsInfo = availableMaids.joinToString("\n") { maid ->
            """
            - ${maid.fullName}
              Rating: ${maid.averageRating}/5.0 (${maid.reviewCount} reviews)
              Hourly Rate: $${maid.hourlyRate}/hour
              ${if (maid.specialtyTag.isNotEmpty()) "Specialty: ${maid.specialtyTag}" else ""}
              ${
                if (maid.services.isNotEmpty()) "Services: ${
                    maid.services.take(3).joinToString(", ")
                }" else ""
            }
            """.trimIndent()
        }

        val prompt = """
            You are a helpful assistant for the Maidy app (a maid booking service).
            
            User question: "$userQuery"
            
            Top available maids (sorted by rating):
            $maidsInfo
            
            Total available maids: ${availableMaids.size}
            
            Provide a friendly response recommending maids based on their query.
            Focus on the highest-rated maids, their specialties, and rates. 
            Be enthusiastic and helpful. Keep response under 150 words.
        """.trimIndent()

        return try {
            val response = generativeModel.generateContent(prompt)
            response.text
                ?: "We have ${availableMaids.size} maids available. Check the Maids tab to see their profiles!"
        } catch (e: Exception) {
            println("❌ Error generating maids summary: ${e.message}")
            if (availableMaids.isNotEmpty()) {
                val topMaid = availableMaids.first()
                """
                We have ${availableMaids.size} maids available!
                
                🌟 Top rated: ${topMaid.fullName}
                • Rating: ${topMaid.averageRating}/5.0 (${topMaid.reviewCount} reviews)
                • Rate: $${topMaid.hourlyRate}/hour
                ${if (topMaid.specialtyTag.isNotEmpty()) "• Specialty: ${topMaid.specialtyTag}" else ""}
                
                Check the Maids tab to see all available maids and book your service!
                """.trimIndent()
            } else {
                "We have several maids available. Check the Maids tab to browse their profiles and ratings!"
            }
        }
    }

    /**
     * Handle general/unknown queries
     */
    private suspend fun handleGeneralQuery(message: String): String {
        val prompt = """
            You are a helpful assistant for the Maidy app (a maid booking service).
            
            User message: "$message"
            
            Context: Maidy is an app where users can:
            - Find and book professional maids
            - Schedule one-time or recurring cleaning services
            - View maid profiles, ratings, and reviews
            - Track their bookings and service history
            
            Services offered:
            - Deep Cleaning
            - Standard Cleaning
            - Move-out Clean
            
            If the user is asking about:
            - Bookings/appointments → Tell them they can ask "Show me my bookings" or "What's my booking status?"
            - Finding maids → Tell them they can ask "Show me the best maids" or "Who are the top rated maids?"
            - Services → Explain the three service types
            
            Provide a helpful, conversational response. Keep under 100 words.
        """.trimIndent()

        return try {
            val response = generativeModel.generateContent(prompt)
            response.text
                ?: "I'm here to help with your Maidy bookings and finding the perfect maid. What can I assist you with?"
        } catch (e: Exception) {
            println("❌ Error generating general response: ${e.message}")
            """
            I'm your Maidy assistant! I can help you with:
            
            📅 Ask about your bookings and appointments
            🌟 Find the best and highest-rated maids
            🧹 Learn about our cleaning services
            
            Try asking:
            • "Show me my bookings"
            • "Who are the best maids?"
            • "What services do you offer?"
            
            What would you like to know?
            """.trimIndent()
        }
    }
}
