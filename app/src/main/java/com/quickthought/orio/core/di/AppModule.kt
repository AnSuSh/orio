package com.quickthought.orio.core.di

import android.app.Application
import androidx.room.Room
import com.quickthought.orio.data.local.MIGRATION_1_2
import com.quickthought.orio.data.local.OrioDatabase
import com.quickthought.orio.data.local.PreferenceManager
import com.quickthought.orio.data.local.TransactionsDAO
import com.quickthought.orio.data.repository.TransactionsRepositoryImpl
import com.quickthought.orio.domain.repository.TransactionsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOrioDatabase(app: Application): OrioDatabase {
        return Room.databaseBuilder(
            app,
            OrioDatabase::class.java,
            OrioDatabase.DATABASE_NAME
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    @Singleton
    fun providePreferenceManager(app: Application): PreferenceManager {
        return PreferenceManager(app)
    }

    @Provides
    @Singleton
    fun provideTransactionsDao(db: OrioDatabase): TransactionsDAO {
        return db.transactionsDao()
    }
}