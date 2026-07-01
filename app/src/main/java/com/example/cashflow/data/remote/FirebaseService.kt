package com.example.cashflow.data.remote

import android.util.Log
import com.example.cashflow.domain.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object FirebaseService {
    private const val TAG = "FirebaseService"
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun init() {
        db.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    fun isSignedIn(): Boolean = auth.currentUser != null

    suspend fun signInAnonymously(): Result<String> {
        return try {
            val result = auth.signInAnonymously().await()
            result.user?.uid?.let { Result.success(it) }
                ?: Result.failure(Exception("Gagal mendapat userId"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(idToken: String): Result<String> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            result.user?.uid?.let { Result.success(it) }
                ?: Result.failure(Exception("Gagal mendapat userId"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun getTransactionsStream(): Flow<List<Transaction>> = callbackFlow {
        var registration: ListenerRegistration? = null

        val authListener = FirebaseAuth.AuthStateListener { fAuth ->
            registration?.remove()
            val uid = fAuth.currentUser?.uid
            if (uid == null) {
                trySend(emptyList())
                return@AuthStateListener
            }

            val ref = db.collection("users").document(uid).collection("transactions")
            registration = ref.addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Listen error", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Transaction::class.java)?.copy(id = doc.id)
                    }
                    Log.d(TAG, "isFromCache=${snapshot.metadata.isFromCache}, count=${list.size}")
                    trySend(list)
                }
            }
        }

        auth.addAuthStateListener(authListener)

        awaitClose {
            registration?.remove()
            auth.removeAuthStateListener(authListener)
        }
    }

    suspend fun addTransaction(transaction: Transaction) {
        val uid = getCurrentUserId() ?: throw Exception("User not signed in")
        db.collection("users").document(uid)
            .collection("transactions").document(transaction.id)
            .set(transaction).await()
    }

    suspend fun deleteTransaction(transactionId: String) {
        val uid = getCurrentUserId() ?: throw Exception("User not signed in")
        db.collection("users").document(uid)
            .collection("transactions").document(transactionId)
            .delete().await()
    }
}
