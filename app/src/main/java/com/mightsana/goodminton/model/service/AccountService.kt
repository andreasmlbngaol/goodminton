package com.mightsana.goodminton.model.service

import com.google.firebase.auth.FirebaseUser

interface AccountService {
    val currentUser: FirebaseUser?
    val currentUserEmail: String
    val currentUserId: String
    val currentProfilePhotoUrl: String?
    val createdTimestamp: Long
    suspend fun reloadUser()
    suspend fun isEmailVerified(): Boolean
    suspend fun signInWithGoogle(idToken: String)
    suspend fun signInWithEmailAndPassword(email: String, password: String)
    suspend fun signUpWithEmailAndPassword(email: String, password: String)
    suspend fun sendEmailVerification()
    suspend fun signOut()
}