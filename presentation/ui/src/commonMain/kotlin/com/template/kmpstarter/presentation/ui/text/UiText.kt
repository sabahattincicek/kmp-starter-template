package com.template.kmpstarter.presentation.ui.text

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.StringResource as ComposeStringResource

/**
 * User-facing text that a ViewModel can hold without resolving it: either a Compose string resource
 * with format arguments or an already resolved string. The UI resolves it when it draws.
 *
 * - From a resource: `UiText.StringResource(Res.string.hello_user, userName)`
 * - From a dynamic value, e.g. a server message: `UiText.DynamicString(message)`
 *
 * Resolve it with [asString] in a composable, or [resolve] in a coroutine.
 */
sealed interface UiText {
    data class DynamicString(val value: String) : UiText

    class StringResource(
        val resource: ComposeStringResource,
        vararg val args: Any,
    ) : UiText {
        /** Two resources are equal when they point to the same string with the same arguments. */
        override fun equals(other: Any?): Boolean =
            other is StringResource && resource == other.resource && args.contentEquals(other.args)

        /** Hash code consistent with [equals]. */
        override fun hashCode(): Int = 31 * resource.hashCode() + args.contentHashCode()
    }

    /** Resolves this text to a plain string outside of Compose, for example in a ViewModel. */
    suspend fun resolve(): String = when (this) {
        is DynamicString -> value
        is StringResource -> getString(resource, *args)
    }

    /** Resolves this text to a plain string inside a composable. */
    @Composable
    fun asString(): String = when (this) {
        is DynamicString -> value
        is StringResource -> stringResource(resource, *args)
    }
}
