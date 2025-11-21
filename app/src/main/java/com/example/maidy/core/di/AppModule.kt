package com.example.maidy.core.di

import com.example.maidy.core.data.AuthRepository
import com.example.maidy.core.data.BookingRepository
import com.example.maidy.core.data.MaidRepository
import com.example.maidy.core.data.SessionManager
import com.example.maidy.core.data.UserRepository
import com.example.maidy.core.util.ImageCompressor
import com.example.maidy.feature.admin.AdminAddMaidViewModel
import com.example.maidy.feature.adjust_recurring.AdjustRecurringViewModel
import com.example.maidy.feature.auth.AuthViewModel
import com.example.maidy.feature.edit_profile.EditProfileViewModel
import com.example.maidy.feature.rating.RatingViewModel
import com.example.maidy.feature.booking.BookingStatusViewModel
import com.example.maidy.feature.booking_details.BookingDetailsViewModel
import com.example.maidy.feature.home.HomeViewModel
import com.example.maidy.feature.maid_details.MaidProfileViewModel
import com.example.maidy.feature.maidlist.MaidListViewModel
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
    
    // Utilities
    single { ImageCompressor(androidContext()) }

    // Repositories
    single { AuthRepository(get()) }
    single { UserRepository(get(), get()) }
    single { MaidRepository(get(), get()) }
    single { BookingRepository(get()) }

    // ViewModels
    viewModel { AuthViewModel(get(), get(), get()) }
    viewModel { ProfileViewModel(get(), get(), get()) }
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { AdminAddMaidViewModel(get(), get()) }
    viewModel { MaidListViewModel(get()) }
    viewModel { MaidProfileViewModel(get()) }
    viewModel { (maidId: String) -> BookingDetailsViewModel(maidId, get(), get(), get(), get()) }
    viewModel { (bookingId: String) -> BookingStatusViewModel(bookingId, get(), get()) }
    viewModel { (bookingId: String) -> AdjustRecurringViewModel(bookingId, get(), get()) }
    viewModel { (bookingId: String) -> RatingViewModel(bookingId, get(), get(), get(), get()) }
    viewModel { EditProfileViewModel(get(), get(), get()) }
}
