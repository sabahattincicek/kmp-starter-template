package com.template.kmpstarter.app.di

import co.touchlab.kermit.Logger
import com.template.kmpstarter.core.common.config.AppConfig
import com.template.kmpstarter.core.common.di.commonModule
import com.template.kmpstarter.core.common.logging.configureLogging
import com.template.kmpstarter.data.di.dataModules
import com.template.kmpstarter.domain.di.domainModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

/**
 * Starts the app: configures logging and starts Koin with the modules of every layer. Called once
 * by each platform entry point before the UI is shown.
 *
 * @param config values only the platform knows, such as the debug flag.
 * @param platform extra Koin setup of the platform, e.g. `androidContext(this)` on Android.
 */
fun initKoin(config: AppConfig, platform: KoinAppDeclaration = {}) {
    configureLogging(config.isDebug)
    startKoin {
        platform()
        modules(appModules(config))
    }
    Logger.withTag("Koin").i { "Koin started: isDebug=${config.isDebug}" }
}

/** Every Koin module of the app, in dependency order. Feature modules add their module here. */
fun appModules(config: AppConfig): List<Module> = listOf(
    module { single { config } },
    commonModule,
    domainModule,
) + dataModules + featureModules

/**
 * Koin modules of the feature modules (their ViewModels). Add one entry per feature, for example
 * `profileModule`.
 */
private val featureModules: List<Module> = listOf()
