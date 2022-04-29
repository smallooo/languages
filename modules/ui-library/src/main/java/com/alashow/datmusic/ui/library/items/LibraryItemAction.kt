/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.ui.library.items

import com.alashow.datmusic.domain.entities.LibraryItem
import com.alashow.datmusic.ui.library.R

typealias LibraryItemActionHandler = (LibraryItemAction) -> Unit

sealed class LibraryItemAction(open val item: LibraryItem) {
    data class Edit(override val item: LibraryItem) : LibraryItemAction(item)
    data class Delete(override val item: LibraryItem) : LibraryItemAction(item)
    data class Download(override val item: LibraryItem) : LibraryItemAction(item)

    companion object {
        fun from(actionLabelRes: Int, item: LibraryItem) = when (actionLabelRes) {
            R.string.library_item_menu_edit -> Edit(item)
            R.string.library_item_menu_delete -> Delete(item)
            R.string.library_item_menu_download -> Download(item)
            else -> error("Unknown action: $actionLabelRes")
        }
    }
}
