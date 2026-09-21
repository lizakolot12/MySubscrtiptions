package com.mits.subscription.data.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import javax.inject.Inject

/**
 * Wraps Credential Manager's "Sign in with Google" flow. [requestGoogleIdToken] takes an Activity
 * context (needed to host the account-picker UI) supplied by the caller — never injected via
 * @ApplicationContext, which cannot show UI.
 */
class GoogleAuthManager @Inject constructor() {

    suspend fun requestGoogleIdToken(activityContext: Context, serverClientId: String): String {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val credentialManager = CredentialManager.create(activityContext)
        val result = credentialManager.getCredential(context = activityContext, request = request)
        val credential = GoogleIdTokenCredential.createFrom(result.credential.data)
        return credential.idToken
    }
}
