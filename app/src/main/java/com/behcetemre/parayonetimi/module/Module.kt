package com.behcetemre.parayonetimi.module

import android.content.Context
import androidx.room.Room
import com.behcetemre.parayonetimi.database.AppDao
import com.behcetemre.parayonetimi.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) : AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    @Singleton
    @Provides
    fun provideDao(db: AppDatabase) : AppDao {
        return db.appDao()
    }
}