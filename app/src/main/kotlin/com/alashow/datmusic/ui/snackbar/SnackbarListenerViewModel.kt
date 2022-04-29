/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.ui.snackbar

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.alashow.base.ui.SnackbarManager
import com.alashow.base.ui.SnackbarMessage

@HiltViewModel
class SnackbarListenerViewModel @Inject constructor(
    private val handle: SavedStateHandle,
    private val snackbarManager: SnackbarManager,
) : ViewModel() {
    val messages = snackbarManager.messages

    fun onSnackbarActionPerformed(message: SnackbarMessage<*>) {
        snackbarManager.onMessageActionPerformed(message)
    }
}
