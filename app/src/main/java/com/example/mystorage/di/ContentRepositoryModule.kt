package com.example.mystorage.di

import com.example.mystorage.data.room.ContentDAO
import com.example.mystorage.data.room.ContentRoomDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ContentRepositoryModule {
    @Singleton
    @Provides
    fun provideContentDao(contentDatabase: ContentRoomDB): ContentDAO {
        return contentDatabase.contentDao()
    }
}