package com.template.kmpstarter.data.di

import org.koin.core.module.Module

/**
 * Every Koin module of the data layer, loaded by `initKoin` in `:app`. When an optional part is
 * removed, remove its module from this list as well.
 */
val dataModules: List<Module> = listOf(
    repositoryModule,
    networkModule, // Network
    databaseModule, // Room
    settingsModule, // Settings
)
