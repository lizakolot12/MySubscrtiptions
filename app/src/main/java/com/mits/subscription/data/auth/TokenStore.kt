package com.mits.subscription.data.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.security.GeneralSecurityException
import java.security.KeyStore
import javax.inject.Inject
import javax.inject.Singleton

/** Persists auth tokens encrypted at rest; never logged, never exposed outside this package other than as read accessors. */
@Singleton
class TokenStore @Inject constructor(@ApplicationContext context: Context) {

    private val prefs: SharedPreferences by lazy {
        try {
            createPrefs(context)
        } catch (e: GeneralSecurityException) {
            recreatePrefs(context)
        } catch (e: IOException) {
            recreatePrefs(context)
        }
    }

    private fun createPrefs(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        return EncryptedSharedPreferences.create(
            context,
            FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    /**
     * The Keystore master key no longer matches the stored keyset (restored backup, reinstall,
     * invalidated key). The tokens are unrecoverable, so drop them and the key; the user signs in again.
     */
    private fun recreatePrefs(context: Context): SharedPreferences {
        context.deleteSharedPreferences(FILE_NAME)
        context.deleteSharedPreferences(KEYSET_FILE_NAME)
        try {
            KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
                .deleteEntry(MasterKey.DEFAULT_MASTER_KEY_ALIAS)
        } catch (_: GeneralSecurityException) {
        } catch (_: IOException) {
        }
        return createPrefs(context)
    }

    val accessToken: String?
        get() = prefs.getString(KEY_ACCESS_TOKEN, null)

    val refreshToken: String?
        get() = prefs.getString(KEY_REFRESH_TOKEN, null)

    val userEmail: String?
        get() = prefs.getString(KEY_EMAIL, null)

    fun save(accessToken: String, refreshToken: String, email: String) {
        prefs.edit {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            putString(KEY_EMAIL, email)
        }
    }

    fun clear() {
        prefs.edit { clear() }
    }

    private companion object {
        const val FILE_NAME = "auth_tokens"
        const val KEYSET_FILE_NAME = "__androidx_security_crypto_encrypted_prefs__"
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
        const val KEY_EMAIL = "user_email"
    }
}
