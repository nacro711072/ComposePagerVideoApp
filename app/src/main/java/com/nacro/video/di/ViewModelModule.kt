package com.nacro.video.di

import com.nacro.video.ui.page.MainViewModel
import org.koin.dsl.module

val viewModelModules = module {
    factory {
        MainViewModel(get())
    }
}