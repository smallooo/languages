/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.playback

import android.content.ComponentName
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.alashow.datmusic.data.repos.audio.AudiosRepo
import com.alashow.datmusic.downloader.Downloader
import com.alashow.datmusic.playback.players.AudioPlayerImpl
import com.alashow.datmusic.playback.services.PlayerService

@InstallIn(SingletonComponent::class)
@Module
class PlaybackModule {

    @Provides
    @Singleton
    fun playbackConnection(
        @ApplicationContext context: Context,
        audiosRepo: AudiosRepo,
        audioPlayer: AudioPlayerImpl,
        downloader: Downloader,
    ): PlaybackConnection = PlaybackConnectionImpl(
        context = context,
        serviceComponent = ComponentName(context, PlayerService::class.java),
        audiosRepo = audiosRepo,
        audioPlayer = audioPlayer,
        downloader = downloader,
    )
}
