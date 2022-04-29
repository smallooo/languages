/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.ui.search

import com.alashow.datmusic.data.DatmusicSearchParams
import com.alashow.datmusic.domain.entities.Audio
import com.alashow.datmusic.domain.models.errors.ApiCaptchaError

internal sealed class SearchAction {
    data class QueryChange(val query: String = "") : SearchAction()
    object Search : SearchAction()
    data class SelectBackendType(val selected: Boolean, val backendType: DatmusicSearchParams.BackendType) : SearchAction()

    data class AddError(val error: Throwable) : SearchAction()
    object ClearError : SearchAction()
    data class SubmitCaptcha(val captchaError: ApiCaptchaError, val solution: String) : SearchAction()

    data class PlayAudio(val audio: Audio) : SearchAction()
}
