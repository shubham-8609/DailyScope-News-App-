package com.codeleg.dailyscope.DI

import android.app.Application
import com.codeleg.dailyscope.database.repository.NewsRepository
import com.codeleg.dailyscope.database.repository.SettingsRepository
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = [NewsRepositoryModule::class , NewsDaoModule::class])
@Singleton
interface AppComponent {

    fun getNewsRepository(): NewsRepository

    fun getSettingsRepository() : SettingsRepository

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

}