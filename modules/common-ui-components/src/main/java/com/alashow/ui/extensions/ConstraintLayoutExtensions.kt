/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.ui.extensions

import androidx.constraintlayout.compose.ConstrainScope

fun ConstrainScope.centerHorizontally() {
    start.linkTo(parent.start)
    end.linkTo(parent.end)
}
