/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.ui.settings.backup

data class BackupRestoreViewState(val isBackingUp: Boolean = false, val isRestoring: Boolean = false) {
    companion object {
        val Empty = BackupRestoreViewState()
    }
}
