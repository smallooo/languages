/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.data.config

import kotlinx.serialization.builtins.ListSerializer
import com.alashow.data.RemoteConfig
import com.alashow.datmusic.domain.entities.SettingsLink

const val REMOTE_CONFIG_SETTINGS_LINKS_KEY = "settings_links"

fun RemoteConfig.getSettingsLinks() = get(REMOTE_CONFIG_SETTINGS_LINKS_KEY, ListSerializer(SettingsLink.serializer()), emptyList())
