/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.ui.search

import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import coil.compose.AsyncImagePainter.*
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.placeholder.material.placeholder
import com.google.firebase.analytics.FirebaseAnalytics
import kotlin.random.Random
import com.alashow.base.util.event
import com.alashow.common.compose.LocalAnalytics
import com.alashow.datmusic.domain.models.errors.ApiCaptchaError
import com.alashow.ui.components.TextRoundedButton
import com.alashow.ui.components.shimmer
import com.alashow.ui.theme.AppTheme
import com.alashow.ui.theme.outlinedTextFieldColors

const val MAX_KEY_LENGTH = 20

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun CaptchaErrorDialog(
    captchaErrorShown: Boolean,
    setCaptchaErrorShown: (Boolean) -> Unit,
    captchaError: ApiCaptchaError,
    onCaptchaSubmit: (String) -> Unit,
    analytics: FirebaseAnalytics = LocalAnalytics.current,
) {
    var captchaVersion by remember(captchaError) { mutableStateOf(Random.nextInt()) }
    val (captchaKey, setCaptchaKey) = remember(captchaError) { mutableStateOf(TextFieldValue()) }

    if (captchaErrorShown) {
        Dialog(
            onDismissRequest = { setCaptchaErrorShown(false) },
            properties = DialogProperties(usePlatformDefaultWidth = true),
        ) {
            val imageUri = captchaError.error.captchaImageUrl.toUri().buildUpon().appendQueryParameter("v", captchaVersion.toString()).build()

            Surface(
                shape = MaterialTheme.shapes.medium,
                elevation = 2.dp,
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(AppTheme.specs.padding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppTheme.specs.padding),
                ) {
                    Text(
                        text = stringResource(R.string.captcha_title),
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    CaptchaErrorImage(imageUri, onReload = { captchaVersion = Random.nextInt() })

                    OutlinedTextField(
                        value = captchaKey,
                        onValueChange = { if (it.text.length <= MAX_KEY_LENGTH) setCaptchaKey(it) },
                        singleLine = true,
                        maxLines = 1,
                        placeholder = { Text(stringResource(R.string.captcha_hint), style = MaterialTheme.typography.body1.copy(fontSize = 14.sp)) },
                        textStyle = MaterialTheme.typography.body1.copy(fontSize = 14.sp),
                        colors = outlinedTextFieldColors(),
                        modifier = Modifier.height(50.dp)
                    )

                    TextRoundedButton(
                        text = stringResource(R.string.captcha_submit),
                        enabled = captchaKey.text.isNotBlank(),
                        onClick = {
                            setCaptchaErrorShown(false)
                            analytics.event("captcha.submit", mapOf("key" to captchaKey))
                            onCaptchaSubmit(captchaKey.text)
                        },
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }
}

@Composable
private fun CaptchaErrorImage(
    image: Uri,
    onReload: () -> Unit,
    modifier: Modifier = Modifier
) {
    val painter = rememberAsyncImagePainter(image)
    Box(modifier.fillMaxWidth()) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .padding(vertical = AppTheme.specs.paddingLarge)
                .width(130.dp)
                .clip(MaterialTheme.shapes.small)
                .aspectRatio(130f / 50f) // source captcha original ratio
                .align(Alignment.Center)
                .placeholder(
                    visible = painter.state is State.Loading,
                    highlight = shimmer(),
                )
        )

        var angle by remember { mutableStateOf(0f) }
        val rotation = animateFloatAsState(angle, tween(500))
        IconButton(
            onClick = { angle += 360; onReload(); },
            Modifier
                .graphicsLayer { rotationZ = rotation.value }
                .align(Alignment.CenterEnd)
        ) {
            Icon(
                tint = MaterialTheme.colors.secondary,
                imageVector = Icons.Default.Refresh,
                contentDescription = stringResource(R.string.captcha_reload)
            )
        }
    }
}
