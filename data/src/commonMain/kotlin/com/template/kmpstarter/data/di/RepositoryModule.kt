package com.template.kmpstarter.data.di

import org.koin.dsl.module

/**
 * Koin module binding the repository interfaces from `:domain` to their implementations in
 * `data/repository`. Add one line per repository, for example:
 *
 * ```
 * singleOf(::DefaultProfileRepository) bind ProfileRepository::class
 * ```
 */
val repositoryModule = module {
}
