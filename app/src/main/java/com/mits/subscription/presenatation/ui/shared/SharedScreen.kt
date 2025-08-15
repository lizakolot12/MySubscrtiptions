package com.mits.subscription.presenatation.ui.shared

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mits.subscription.R
import com.mits.subscription.presenatation.ui.components.FilePreview
import com.mits.subscription.presenatation.ui.theme.md_theme_light_primary
import com.mits.subscription.presenatation.ui.theme.md_theme_light_primaryContainer
import com.mits.subscription.presenatation.ui.theme.md_theme_light_surfaceVariant

@Composable
fun SharedScreen(
    onBack: () -> Unit,
    sharedViewModel: SharedViewModel = hiltViewModel()
) {
    val uiState by sharedViewModel.uiState.collectAsStateWithLifecycle()
    Shared(
        uiState,
        onBack = onBack,
        onClick = { subscriptionId ->
            sharedViewModel.addFileToSubscription(
                subscriptionId
            )
        }
    )
}

@Composable
fun Shared(
    uiState: SharedViewModel.SharedState,
    onBack: () -> Unit,
    onClick: (subscriptionId: Long) -> Unit
) {
    Scaffold(
        topBar = { TopBar(onBack) },
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
                    SuccessSharedStateView(uiState, onClick)
                }

                SharedViewModel.SharedState.Finish -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                }

                is SharedViewModel.SharedState.Finish -> {
                    LaunchedEffect(Unit) {
                        val toast = Toast.makeText(
                            context,
                            context.getString(R.string.success_payment_added),
                            Toast.LENGTH_LONG
                        )
                        toast.show()
                        onBack.invoke()
                    }
                }
            }
        }
    }
}

@Composable
private fun SuccessSharedStateView(
    uiState: SharedViewModel.SharedState.Success,
    onClick: (subscriptionId:Long) -> Unit
) {
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
                FilePreview(it)
            }
        }
        SubscriptionList(
            uiState.workshops,
            onClick = onClick
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TopBar(onBack: () -> Unit) {
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
}

@Composable
fun SubscriptionList(
    workshops: List<WorkShopUiState>,
    onClick: (subscriptionId:Long) -> Unit
) {
    LazyColumn {
        items(workshops, key = {it.id}) { workshop ->
            WorkshopItem(workshop = workshop, onClick = onClick)
        }
    }
}

@Composable
fun WorkshopItem(
    workshop: WorkShopUiState,
    onClick: (subscriptionId: Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        WorkshopTitle(workshop.name)

        SubscriptionView(workshop.currentSubscription, onClick)

        if (workshop.old.isNotEmpty()) {
            AnimatedVisibility(expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            stringResource(R.string.label_collapse),
                            textAlign = TextAlign.End,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .clickable { expanded = false }
                                .background(md_theme_light_surfaceVariant, RoundedCornerShape(8.dp))
                                .padding(4.dp)
                        )
                    }
                    workshop.old.forEach { subscription ->
                        SubscriptionView(subscription, onClick)
                    }
                }
            }
            AnimatedVisibility(!expanded) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        stringResource(R.string.label_expand),
                        textAlign = TextAlign.End,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .clickable { expanded = true }
                            .background(md_theme_light_surfaceVariant, RoundedCornerShape(8.dp))
                            .padding(4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SubscriptionView(
    subscription: SubscriptionState?,
    onClick: (subscriptionId: Long) -> Unit
) {
    subscription?.let {
        Text(
            text = it.description,
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 8.dp)
                .background(md_theme_light_surfaceVariant, RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .clickable { onClick(it.id) }
                .padding(8.dp)
        )
    }
}

@Composable
private fun WorkshopTitle(workshopTitle:String) {
    Text(
        workshopTitle,
        modifier = Modifier
            .fillMaxWidth()
            .background(md_theme_light_primaryContainer, RoundedCornerShape(16.dp))
            .padding(8.dp)
    )
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