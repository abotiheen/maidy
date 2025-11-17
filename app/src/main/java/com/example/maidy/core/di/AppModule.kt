package com.example.maidy.core.di

import com.example.maidy.core.data.AuthRepository
import com.example.maidy.core.data.SessionManager
import com.example.maidy.core.data.UserRepository
import com.example.maidy.feature.auth.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Firebase instances
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }

    // Session Manager
    single { SessionManager(androidContext()) }

    // Repositories
    single { AuthRepository(get()) }
    single { UserRepository(get()) }

    // ViewModel
    viewModel { AuthViewModel(get(), get(), get()) }
}