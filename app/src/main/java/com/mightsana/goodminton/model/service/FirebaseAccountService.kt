package com.mightsana.goodminton.model.service

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAccountService @Inject constructor(): AccountService {
    private val auth = Firebase.auth
    override val currentUser: FirebaseUser? get() = auth.currentUser
    override val currentUserEmail: String get() = currentUser?.email ?: ""
    override val currentUserId: String get() = currentUser?.uid ?: ""
    override val currentProfilePhotoUrl: String? get() = currentUser?.photoUrl?.toString()
    override val createdTimestamp: Long get() = currentUser?.metadata?.creationTimestamp ?: 0
    override suspend fun reloadUser() {
        auth.currentUser?.reload()
    }
    override suspend fun isEmailVerified(): Boolean {
        return auth.currentUser?.isEmailVerified == true
    }
    override suspend fun signInWithGoogle(idToken: String) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(firebaseCredential).await()
    }
    override suspend fun signInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }
    override suspend fun signUpWithEmailAndPassword(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
    }
    override suspend fun sendEmailVerification() {
        auth.currentUser!!.sendEmailVerification().await()
    }
    override suspend fun signOut() {
        auth.signOut()
    }
}