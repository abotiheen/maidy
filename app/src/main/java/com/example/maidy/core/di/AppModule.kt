package com.example.maidy.core.di

import com.example.maidy.core.data.AuthRepository
import com.example.maidy.core.data.MaidRepository
import com.example.maidy.core.data.SessionManager
import com.example.maidy.core.data.UserRepository
import com.example.maidy.core.util.ImageCompressor
import com.example.maidy.feature.admin.AdminAddMaidViewModel
import com.example.maidy.feature.auth.AuthViewModel
import com.example.maidy.feature.home.HomeViewModel
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

    // ViewModels
    viewModel { AuthViewModel(get(), get(), get()) }
    viewModel { ProfileViewModel(get(), get(), get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { AdminAddMaidViewModel(get(), get()) }
    viewModel { MaidListViewModel(get()) }
}