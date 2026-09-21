@file:OptIn(ExperimentalMaterial3Api::class)

package com.mits.subscription.presenatation.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mits.subscription.R
import com.mits.subscription.presenatation.ui.components.parseMillis
import com.mits.subscription.presenatation.ui.theme.md_theme_light_error
import com.mits.subscription.presenatation.ui.theme.md_theme_light_primary

@Composable
fun LoginScreen(
    onBack: () -> Unit,
    loginViewModel: LoginViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val state by loginViewModel.state.collectAsStateWithLifecycle()
    val isLoggedIn by loginViewModel.isLoggedIn.collectAsStateWithLifecycle()
    val userEmail by loginViewModel.userEmail.collectAsStateWithLifecycle()
    val lastSyncCompletedAt by loginViewModel.lastSyncCompletedAt.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        text = stringResource(R.string.title_account),
                        textAlign = TextAlign.Center,
                        color = md_theme_light_primary,
                        modifier = Modifier.fillMaxWidth(),
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            "",
                            tint = md_theme_light_primary,
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else if (isLoggedIn) {
                Text(
                    text = stringResource(R.string.label_signed_in_as, userEmail.orEmpty()),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp),
                )
                OutlinedButton(onClick = { loginViewModel.signOut() }) {
                    Text(text = stringResource(R.string.btn_sign_out))
                }

                Text(
                    text = stringResource(
                        R.string.label_last_sync,
                        lastSyncCompletedAt?.let { parseMillis(it) } ?: stringResource(R.string.label_never_synced),
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
                )
                OutlinedButton(onClick = { loginViewModel.syncNow() }) {
                    Text(text = stringResource(R.string.btn_sync_now))
                }
            } else {
                Button(onClick = { loginViewModel.signIn(context) }) {
                    Text(text = stringResource(R.string.btn_sign_in_google))
                }
                state.error?.let { error ->
                    Text(
                        text = stringResource(R.string.sign_in_error, error),
                        color = md_theme_light_error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 16.dp),
                    )
                }
            }
        }
    }
}
