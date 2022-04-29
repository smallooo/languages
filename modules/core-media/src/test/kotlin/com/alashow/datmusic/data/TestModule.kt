/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.data

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.alashow.base.di.TestAppModule
import com.alashow.datmusic.data.db.TestDatabaseModule

@Module(includes = [TestAppModule::class, TestDatabaseModule::class])
@InstallIn(SingletonComponent::class)
class TestModule
