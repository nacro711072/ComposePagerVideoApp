package com.nacro.video

import android.app.Application
import com.nacro.video.di.repositoryModule
import com.nacro.video.di.dataSourceModule
import com.nacro.video.di.viewModelModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class VideoApp: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@VideoApp)

            modules(
                dataSourceModule,
                repositoryModule,
                viewModelModules
            )
        }
    }
}