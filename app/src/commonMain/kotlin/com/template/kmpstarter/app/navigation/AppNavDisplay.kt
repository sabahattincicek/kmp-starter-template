package com.template.kmpstarter.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

/**
 * Every navigation key of the app. iOS and the web cannot find keys by reflection, so the back
 * stack can only save and restore the keys registered here. Add one `subclass` line per feature
 * key.
 */
private val navKeys = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(StartKey::class, StartKey.serializer())
            // subclass(ProfileKey::class, ProfileKey.serializer())
        }
    }
}

/**
 * The app's navigation graph, built with Navigation 3. The back stack is a list of feature keys
 * (`NavKey`); pushing a key opens its screen and popping it goes back.
 *
 * To add a screen: define its key and `xxxEntry()` in the feature module, register the key in
 * [navKeys], call the entry inside `entryProvider` below, and push the key from a navigation
 * callback. Once the first feature exists, start the back stack with its key and delete [StartKey]
 * and `StartPlaceholder.kt`.
 */
@Composable
fun AppNavDisplay(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(navKeys, StartKey)

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            // Keeps each screen's rememberSaveable state while it is on the back stack.
            rememberSaveableStateHolderNavEntryDecorator(),
            // Gives each screen its own ViewModels, cleared when the screen is popped.
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<StartKey> { StartPlaceholder() }
            // profileEntry()
        },
    )
}
