/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.ui.library

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.ui.Scaffold
import com.alashow.base.util.extensions.Callback
import com.alashow.common.compose.rememberFlowWithLifecycle
import com.alashow.datmusic.domain.entities.Album
import com.alashow.datmusic.domain.entities.LibraryItems
import com.alashow.datmusic.domain.entities.Playlist
import com.alashow.datmusic.ui.library.items.LibraryItemRow
import com.alashow.datmusic.ui.library.playlists.PlaylistRow
import com.alashow.domain.models.Success
import com.alashow.navigation.LocalNavigator
import com.alashow.navigation.Navigator
import com.alashow.navigation.screens.LeafScreen
import com.alashow.ui.components.AppTopBar
import com.alashow.ui.components.EmptyErrorBox
import com.alashow.ui.components.FullScreenLoading
import com.alashow.ui.components.IconButton
import com.alashow.ui.theme.AppTheme

@Composable
fun Library() {
    Library(viewModel = hiltViewModel())
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun Library(
    viewModel: LibraryViewModel,
    navigator: Navigator = LocalNavigator.current
) {
    val listState = rememberLazyListState()
    val asyncLibraryItems by rememberFlowWithLifecycle(viewModel.libraryItems)

    Scaffold(
        topBar = {
            LibraryTopBar(
                onCreatePlaylist = {
                    navigator.navigate(LeafScreen.CreatePlaylist().createRoute())
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        when (val items = asyncLibraryItems) {
            is Success -> {
                LazyColumn(
                    contentPadding = padding,
                    state = listState
                ) {
                    libraryList(items())
                }
            }
            else -> FullScreenLoading()
        }
    }
}

@Composable
private fun LibraryTopBar(onCreatePlaylist: Callback = {}) {
    AppTopBar(
        title = stringResource(R.string.library_title),
        actions = {
            IconButton(
                onClick = onCreatePlaylist,
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.playlist_create),
                    modifier = Modifier.size(AppTheme.specs.iconSize)
                )
            }
        }
    )
}

fun LazyListScope.libraryList(
    items: LibraryItems = listOf(),
) {
    val downloadsEmpty = items.isEmpty()
    if (downloadsEmpty) {
        item {
            EmptyErrorBox(
                message = stringResource(R.string.library_empty),
                retryVisible = false,
                modifier = Modifier.fillParentMaxHeight()
            )
        }
    }

    items(items, key = { it.getIdentifier() }) {
        when (it) {
            is Playlist -> PlaylistRow(playlist = it)
            is Album -> LibraryItemRow(libraryItem = it, typeRes = R.string.albums_detail_title)
            else -> LibraryItemRow(libraryItem = it, typeRes = R.string.unknown)
        }
    }
}
