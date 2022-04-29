/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.ui.detail

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.alashow.base.util.localizedMessage
import com.alashow.base.util.localizedTitle
import com.alashow.domain.models.Async
import com.alashow.domain.models.Fail
import com.alashow.ui.components.ErrorBox

open class MediaDetailFail<T> {

    open operator fun invoke(
        list: LazyListScope,
        details: Async<T>,
        onFailRetry: () -> Unit,
    ) {
        if (details is Fail) {
            list.item {
                ErrorBox(
                    title = stringResource(details.error.localizedTitle()),
                    message = stringResource(details.error.localizedMessage()),
                    onRetryClick = onFailRetry,
                    modifier = Modifier.fillParentMaxHeight(0.5f)
                )
            }
        }
    }
}
