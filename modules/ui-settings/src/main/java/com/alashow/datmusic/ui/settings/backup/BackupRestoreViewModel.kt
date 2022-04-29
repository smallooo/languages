/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.ui.settings.backup

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import com.alashow.base.ui.SnackbarManager
import com.alashow.base.util.CreateFileContract
import com.alashow.base.util.event
import com.alashow.base.util.extensions.stateInDefault
import com.alashow.base.util.toUiMessage
import com.alashow.datmusic.data.interactors.backup.CreateDatmusicBackupToFile
import com.alashow.datmusic.data.interactors.backup.RestoreDatmusicFromFile
import com.alashow.datmusic.playback.PlaybackConnection
import com.alashow.datmusic.ui.settings.R
import com.alashow.domain.models.Fail
import com.alashow.domain.models.Success
import com.alashow.i18n.UiMessage

val BACKUP_FILE_PARAMS = CreateFileContract.Params(suggestedName = "datmusic-backup", fileExtension = "json", fileMimeType = "application/json")

@HiltViewModel
class BackupRestoreViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val backupToFile: CreateDatmusicBackupToFile,
    private val restoreFromFile: RestoreDatmusicFromFile,
    private val snackbarManager: SnackbarManager,
    private val analytics: FirebaseAnalytics,
    private val playbackConnection: PlaybackConnection,
) : ViewModel() {

    private val isBackingUp = MutableStateFlow(false)
    private val isRestoring = MutableStateFlow(false)

    val state = combine(isBackingUp, isRestoring, ::BackupRestoreViewState)
        .stateInDefault(viewModelScope, BackupRestoreViewState.Empty)

    init {
        viewModelScope.launch {
            restoreFromFile.warnings.collectLatest { snackbarManager.addMessage(it.toUiMessage()) }
        }
    }

    fun backupTo(file: Uri) = viewModelScope.launch {
        analytics.event("settings.db.backup")
        playbackConnection.transportControls?.stop()
        backupToFile(file).collectLatest {
            isBackingUp.value = it.isLoading
            when (it) {
                is Fail -> snackbarManager.addMessage(it.error.toUiMessage())
                is Success -> snackbarManager.addMessage(UiMessage.Resource(R.string.settings_database_backup_complete))
                else -> Unit
            }
        }
    }

    fun restoreFrom(file: Uri) = viewModelScope.launch {
        analytics.event("settings.db.restore")
        playbackConnection.transportControls?.stop()
        restoreFromFile(file).collectLatest {
            isRestoring.value = it.isLoading
            when (it) {
                is Fail -> snackbarManager.addMessage(it.error.toUiMessage())
                is Success -> snackbarManager.addMessage(UiMessage.Resource(R.string.settings_database_restore_complete))
                else -> Unit
            }
        }
    }
}
