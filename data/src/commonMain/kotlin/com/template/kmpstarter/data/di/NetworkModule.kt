// OPTIONAL (Network) — delete this file if the project does not call an HTTP API, together with
// the Network lines in data/build.gradle.kts and in DataModules.kt (see "Removing an optional part"
// in docsagent/SETUP.md).
package com.template.kmpstarter.data.di

import co.touchlab.kermit.Logger as KermitLogger
import com.template.kmpstarter.core.common.config.AppConfig
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

/** Base URL of the backend. Replace it, or pass it in through `AppConfig` per environment. */
const val BASE_URL = "https://example.com/"

private const val TIMEOUT_MILLIS = 30_000L

private val log = KermitLogger.withTag("NetworkModule")

/**
 * Koin module providing the JSON parser and the Ktor [HttpClient]. The HTTP engine is chosen per
 * platform from the dependencies (OkHttp on Android, Darwin on iOS, Fetch on the web). Add one line
 * per API class, for example:
 *
 * ```
 * singleOf(::ProfileApi)
 * ```
 */
val networkModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }
    }
    single { createHttpClient(json = get(), config = get()) }
}

/**
 * Creates the [HttpClient]: JSON content negotiation, timeouts, [BASE_URL] as the default host,
 * and request and response bodies logged in debug builds only.
 */
private fun createHttpClient(json: Json, config: AppConfig): HttpClient {
    log.d { "Creating HttpClient: baseUrl=$BASE_URL, timeout=${TIMEOUT_MILLIS}ms, logging=${config.isDebug}" }
    return HttpClient {
        install(ContentNegotiation) { json(json) }
        install(HttpTimeout) {
            connectTimeoutMillis = TIMEOUT_MILLIS
            requestTimeoutMillis = TIMEOUT_MILLIS
            socketTimeoutMillis = TIMEOUT_MILLIS
        }
        install(Logging) {
            level = if (config.isDebug) LogLevel.BODY else LogLevel.NONE
            logger = object : Logger {
                override fun log(message: String) {
                    KermitLogger.withTag("HttpClient").d { message }
                }
            }
            sanitizeHeader { header -> header == HttpHeaders.Authorization || header == HttpHeaders.Cookie }
        }
        defaultRequest { url(BASE_URL) }
    }
}
