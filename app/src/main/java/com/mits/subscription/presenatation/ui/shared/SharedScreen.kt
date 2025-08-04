package com.mits.subscription.presenatation.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mits.subscription.R
import com.mits.subscription.model.Subscription
import com.mits.subscription.model.Workshop
import com.mits.subscription.presenatation.ui.components.FilePreview
import com.mits.subscription.presenatation.ui.list.DATE_FORMATTER
import com.mits.subscription.presenatation.ui.theme.md_theme_light_primary
import com.mits.subscription.presenatation.ui.theme.md_theme_light_primaryContainer
import com.mits.subscription.presenatation.ui.theme.md_theme_light_surfaceVariant

@Composable
fun SharedScreen(
    onBack: () -> Unit,
    sharedViewModel: SharedViewModel = hiltViewModel()
) {
    val uiState = sharedViewModel.uiState.collectAsState().value
    Shared(
        uiState,
        onBack = onBack,
        onClick = { subscription ->
            sharedViewModel.addFileToSubscription(
                subscription
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Shared(
    uiState: SharedViewModel.SharedState,
    onBack: () -> Unit,
    onClick: (subscription: Subscription) -> Unit

) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        text = stringResource(R.string.title),
                        textAlign = TextAlign.Center,
                        color = md_theme_light_primary,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onBack.invoke()
                        }) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            "",
                            tint = md_theme_light_primary
                        )
                    }
                },
            )
        },
    ) { padding ->
        val context = LocalContext.current
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (uiState) {
                SharedViewModel.SharedState.Loading -> ProgressIndicator()
                is SharedViewModel.SharedState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),

                        ) {
                        Text(
                            text = stringResource(R.string.shared_workshops),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        uiState.paymentFile?.let {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .height(150.dp),
                            ) {
                                FilePreview(context, it)
                            }
                        }
                        SubscriptionList(
                            uiState.workshops,
                            onClick = { subscription ->
                                onClick.invoke(subscription)
                            }
                        )
                    }
                }

                SharedViewModel.SharedState.Finish -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            LaunchedEffect(uiState) {
                if (uiState is SharedViewModel.SharedState.Finish) {
                    onBack.invoke()
                }
            }
        }
    }
}

@Composable
fun SubscriptionList(workshops: List<Workshop>, onClick: (subscription: Subscription) -> Unit) {
    LazyColumn {

        workshops.forEach { workshop ->
            item {
                Text(
                    workshop.name, modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            md_theme_light_primaryContainer, RoundedCornerShape(16.dp)
                        )
                        .padding(8.dp)
                )
            }
            workshop.subscriptions.forEach { subscription ->
                item {
                    Text(
                        text = getDescription(subscription),
                        modifier = Modifier
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                            .background(md_theme_light_surfaceVariant)
                            .fillMaxWidth()
                            .clickable { onClick.invoke(subscription) }
                            .padding(8.dp)
                    )
                }
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                )
            }
        }
    }
}
private fun getDescription(subscription: Subscription): String {
    return buildString {
        subscription.detail?.takeIf { it.isNotBlank() }?.let {
            append(it)
        }
        subscription.message?.takeIf { it.isNotBlank() }?.let {
            if (isNotEmpty()) append(" ")
            append(it)
        }
        subscription.startDate?.let {
            val formatted = DATE_FORMATTER.format(it)
            if (isNotEmpty()) append(" ")
            append(formatted)
        }
    }
}


@Composable
private fun ProgressIndicator() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}