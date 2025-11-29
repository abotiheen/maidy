package com.example.maidy.core.di

import com.example.maidy.MainViewModel
import com.example.maidy.core.data.AuthRepository
import com.example.maidy.core.data.BookingRepository
import com.example.maidy.core.data.ChatRepository
import com.example.maidy.core.data.MaidRepository
import com.example.maidy.core.data.NotificationPreferencesManager
import com.example.maidy.core.data.SessionManager
import com.example.maidy.core.data.UserRepository
import com.example.maidy.core.service.FcmTokenManager
import com.example.maidy.core.service.GeminiChatService
import com.example.maidy.core.service.MaidGeminiChatService
import com.example.maidy.core.service.NotificationService
import com.example.maidy.core.util.ImageCompressor
import com.example.maidy.feature.admin.AdminAddMaidViewModel
import com.example.maidy.feature.adjust_recurring.AdjustRecurringViewModel
import com.example.maidy.feature.all_bookings.AllBookingsViewModel
import com.example.maidy.feature.auth.AuthViewModel
import com.example.maidy.feature.chat.ChatViewModel
import com.example.maidy.feature.edit_profile.EditProfileViewModel
import com.example.maidy.feature.rating.RatingViewModel
import com.example.maidy.feature_maid.auth.MaidAuthViewModel
import com.example.maidy.feature_maid.booking_details.MaidBookingDetailsViewModel
import com.example.maidy.feature_maid.chat.MaidChatViewModel
import com.example.maidy.feature_maid.profile.MaidProfileViewModel as MaidOwnProfileViewModel
import com.example.maidy.feature_maid.edit_profile.MaidEditProfileViewModel
import com.example.maidy.feature_maid.home.MaidHomeViewModel
import com.example.maidy.feature_maid.all_bookings.MaidAllBookingsViewModel
import com.example.maidy.feature.booking.BookingStatusViewModel
import com.example.maidy.feature.booking_details.BookingDetailsViewModel
import com.example.maidy.feature.home.HomeViewModel
import com.example.maidy.feature.maid_details.MaidProfileViewModel
import com.example.maidy.feature.maidlist.MaidListViewModel
import com.example.maidy.feature.search.SearchViewModel
import com.example.maidy.feature.settings.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Firebase instances
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseStorage.getInstance() }

    // Session Manager
    single { SessionManager(androidContext()) }
    
    // Notification Preferences
    single { NotificationPreferencesManager(androidContext()) }
    
    // Utilities
    single { ImageCompressor(androidContext()) }

    // Services
    single { FcmTokenManager(get(), get()) }  // Now accepts both UserRepository and MaidRepository
    single { NotificationService(get(), get()) }
    single { GeminiChatService(get(), get(), get()) }
    single { MaidGeminiChatService(get(), get()) }

    // Repositories
    single { AuthRepository(get()) }
    single { UserRepository(get(), get()) }
    single { MaidRepository(get(), get()) }
    single { BookingRepository(get(), get()) }
    single { ChatRepository(get()) }

    // ViewModels
    viewModel { AuthViewModel(get(), get(), get(), get()) }
    viewModel { MaidAuthViewModel(get(), get(), get(), get()) }
    viewModel { MaidOwnProfileViewModel(get(), get(), get(), get(), get()) }
    viewModel { MaidEditProfileViewModel(get(), get(), get(), get()) }
    viewModel { MaidHomeViewModel(get(), get(), get()) }
    viewModel { MaidAllBookingsViewModel(get(), get()) }
    viewModel { (bookingId: String) -> MaidBookingDetailsViewModel(bookingId, get()) }
    viewModel { ProfileViewModel(get(), get(), get(), get(), get()) }
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { AdminAddMaidViewModel(get(), get()) }
    viewModel { MaidListViewModel(get()) }
    viewModel { MainViewModel(get()) }
    viewModel { MaidProfileViewModel(get()) }
    viewModel { SearchViewModel(get()) }
    viewModel { AllBookingsViewModel(get(), get()) }
    viewModel { (maidId: String) -> BookingDetailsViewModel(maidId, get(), get(), get(), get()) }
    viewModel { (bookingId: String) -> BookingStatusViewModel(bookingId, get(), get()) }
    viewModel { (bookingId: String) -> AdjustRecurringViewModel(bookingId, get(), get()) }
    viewModel { (bookingId: String) -> RatingViewModel(bookingId, get(), get(), get(), get()) }
    viewModel { EditProfileViewModel(get(), get(), get()) }
    viewModel { ChatViewModel(get()) }
    viewModel { MaidChatViewModel(get()) }
}
