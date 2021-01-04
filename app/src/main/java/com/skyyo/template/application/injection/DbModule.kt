package com.skyyo.template.application.injection

import android.content.Context
import androidx.room.Room
import com.skyyo.template.application.persistance.room.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object DbModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, AppDatabase::class.java, "template_db").build()

    @Singleton
    @Provides
    fun provideProductsDao(appDatabase: AppDatabase) = appDatabase.productsDao()
}
