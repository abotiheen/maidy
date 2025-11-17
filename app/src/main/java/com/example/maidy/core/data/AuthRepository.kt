package com.example.maidy.core.data

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    
    // Send OTP to phone number
    fun sendOtp(
        phoneNumber: String,
        activity: Activity
    ): Flow<OtpState> = callbackFlow {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Auto-verification (happens rarely on real devices)
                trySend(OtpState.AutoVerified(credential))
            }
            
            override fun onVerificationFailed(e: FirebaseException) {
                trySend(OtpState.Error(e.message ?: "Verification failed"))
                close()
            }
            
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                trySend(OtpState.CodeSent(verificationId, token))
            }
        }
        
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        
        PhoneAuthProvider.verifyPhoneNumber(options)
        
        awaitClose { }
    }
    
    // Verify OTP code
    suspend fun verifyOtp(verificationId: String, code: String): Result<String> {
        return try {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            val authResult = auth.signInWithCredential(credential).await()
            val userId = authResult.user?.uid ?: throw Exception("User ID not found")
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Sign in with auto-verified credential
    suspend fun signInWithCredential(credential: PhoneAuthCredential): Result<String> {
        return try {
            val authResult = auth.signInWithCredential(credential).await()
            val userId = authResult.user?.uid ?: throw Exception("User ID not found")
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Get current user ID
    fun getCurrentUserId(): String? = auth.currentUser?.uid
    
    // Get current user phone
    fun getCurrentUserPhone(): String? = auth.currentUser?.phoneNumber
    
    // Check if user is logged in
    fun isLoggedIn(): Boolean = auth.currentUser != null
    
    // Sign out
    fun signOut() = auth.signOut()
}

// States for OTP flow
sealed class OtpState {
    data class CodeSent(
        val verificationId: String,
        val token: PhoneAuthProvider.ForceResendingToken
    ) : OtpState()
    
    data class AutoVerified(val credential: PhoneAuthCredential) : OtpState()
    data class Error(val message: String) : OtpState()
}

