package com.quickthought.orio.core.di

import com.quickthought.orio.data.sms.SmsLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SmsModule {

    @Provides
    @Singleton
    fun provideSmsLogger(postgrest: Postgrest): SmsLogger {
        return SmsLogger(postgrest)
    }
}
