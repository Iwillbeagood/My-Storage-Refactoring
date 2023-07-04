package com.example.mystorage.di

import android.content.Context
import com.example.mystorage.data.room.ContentRoomDB
import com.example.mystorage.di.coroutines.ApplicationScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ContentDatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context): ContentRoomDB =
        ContentRoomDB.getDatabase(context)
}