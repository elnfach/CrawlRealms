package com.elnfach.crawl_realms

import com.elnfach.realms.repository.ContentRepository
import com.elnfach.realms.viewmodel.RealmViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { ContentRepository(androidContext()) }

    viewModel {
        RealmViewModel(get())
    }
}