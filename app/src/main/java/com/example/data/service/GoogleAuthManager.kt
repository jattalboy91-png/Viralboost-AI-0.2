package com.example.data.service

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

private const val AUTH_TAG = "GoogleAuthManager"

/**
 * Handles real Google Sign-In via Jetpack Credential Manager + Firebase Authentication.
 */
object GoogleAuthManager {

    private fun resolveWebClientId(context: Context): String {
        return try {
            val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
            if (resId != 0) context.getString(resId)
            else "1092939667146-j6nnibnqt7r8tgmuhga3behhns01cgni.apps.googleusercontent.com"
        } catch (_: Throwable) {
            "1092939667146-j6nnibnqt7r8tgmuhga3behhns01cgni.apps.googleusercontent.com"
        }
    }

    suspend fun signInWithGoogle(activityContext: Context): Result<FirebaseUser> {
        return try {
            val credentialManager = CredentialManager.create(activityContext)
            val webClientId = resolveWebClientId(activityContext)

            val result = try {
                val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(webClientId)
                    .build()
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(signInWithGoogleOption)
                    .build()
                credentialManager.getCredential(
                    request = request,
                    context = activityContext
                )
            } catch (noCred: NoCredentialException) {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(webClientId)
                    .setAutoSelectEnabled(false)
                    .build()
                val fallbackRequest = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()
                credentialManager.getCredential(
                    request = fallbackRequest,
                    context = activityContext
                )
            }

            val credential = result.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                val authResult = FirebaseAuth.getInstance().signInWithCredential(firebaseCredential).await()
                val user = authResult.user
                    ?: return Result.failure(IllegalStateException("Firebase user missing after Google Sign-In."))
                Result.success(user)
            } else {
                Result.failure(IllegalStateException("Unexpected credential type returned."))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: GetCredentialCancellationException) {
            Result.failure(Exception("Google Sign-In was cancelled."))
        } catch (e: GetCredentialException) {
            Log.e(AUTH_TAG, "Credential Manager sign-in failed", e)
            Result.failure(Exception(e.localizedMessage ?: "Google Sign-In failed on this device."))
        } catch (e: Exception) {
            Log.e(AUTH_TAG, "Google Sign-In error", e)
            Result.failure(e)
        }
    }

    suspend fun signOut(context: Context) {
        try {
            FirebaseAuth.getInstance().signOut()
        } catch (e: Throwable) {
            Log.w(AUTH_TAG, "Firebase signOut warning: ${e.message}")
        }
        try {
            val credentialManager = CredentialManager.create(context)
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        } catch (e: Throwable) {
            Log.w(AUTH_TAG, "CredentialManager clear warning: ${e.message}")
        }
    }

    fun currentUser(): FirebaseUser? {
        return try {
            FirebaseAuth.getInstance().currentUser
        } catch (_: Throwable) {
            null
        }
    }
}
