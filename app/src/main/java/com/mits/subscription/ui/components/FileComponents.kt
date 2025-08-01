package com.mits.subscription.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mits.subscription.R
import com.mits.subscription.data.repo.PaymentFile
import java.io.File
import java.util.Date

@Composable
fun PaymentFileView(paymentFile: PaymentFile?, onFileHandler: (String?) -> Unit) {
    val context = LocalContext.current
    Log.e("TEST", "Compose : $paymentFile")

    val photoUri =
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            File(context.cacheDir, "${Date().time}.jpg")
        )

    val takePicture = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            onFileHandler(photoUri.toString())
            Log.e("TEST", "result = $photoUri")
        }
    }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { fileUri ->
        Log.e("TEST", "fileUri = $fileUri")
        if (fileUri != null) {
            onFileHandler(fileUri.toString())
            Log.e("TEST", "result = $fileUri  path = ${fileUri.path}")
        }
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(vertical = 8.dp)
    ) {
        if (paymentFile?.isPicture() == true) {
            PhotoView(paymentFile.uri)
        }
        if (paymentFile?.isForExternalUse() == true) {
            Row(modifier = Modifier.clickable { openPdfFile(context, paymentFile.uri) }) {
                Image(
                    painter = painterResource(id = R.drawable.outline_attach_file_24),
                    contentDescription = paymentFile.name,
                )
                Text(
                    text = paymentFile.name,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Row {
            FilledTonalButton(
                onClick = {
                    takePicture.launch(photoUri)
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .padding(vertical = 8.dp),
            ) {
                Row {
                    Text(
                        stringResource(
                            id = R.string.btn_take_payment_photo
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }

            FilledTonalButton(
                onClick = {
                    filePicker.launch(arrayOf("application/pdf", "image/*"))
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .padding(vertical = 8.dp),
            ) {
                Row {
                    Text(
                        stringResource(id = R.string.btn_chose_payment_photo),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

    }
}

fun openPdfFile(context: Context, uri: Uri) {

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        Toast.makeText(context, "No app found to open PDF", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun PhotoView(
    uri: Uri
) {
    val context = LocalContext.current

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    val newScale = (scale * zoom).coerceIn(1f, 10f)
                    scale = newScale

                    if (scale > 1f) {
                        offset += pan
                    } else {
                        offset = Offset.Zero
                    }
                }
            }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(uri)
                .crossfade(true).build(),
            contentDescription = "Selected image",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
        )
    }
}
