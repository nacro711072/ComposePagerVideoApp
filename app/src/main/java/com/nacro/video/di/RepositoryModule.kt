package com.nacro.video.di

import com.nacro.video.repository.VideoRepository
import org.koin.dsl.module

val repositoryModule = module {
    factory {
        VideoRepository(get())
    }
}