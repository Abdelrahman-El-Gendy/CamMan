package com.gndy.camman.presentation.navigation.builders

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

/**
 * Interface for entry builders following Navigation 3 modularization pattern.
 * Each feature module implements this to provide its navigation entries.
 *
 * This pattern allows:
 * - Clear separation of navigation logic per feature
 * - Easy dependency injection of entry builders
 * - Scalable navigation as app grows
 *
 * Reference: https://developer.android.com/guide/navigation/navigation-3/modularize
 */
interface NavEntryBuilder {
    /**
     * Builds navigation entries for this feature module.
     * Called from the main NavGraph to register all screens.
     */
    fun NavGraphBuilder.buildEntries(navController: NavHostController)
}

/**
 * Type alias for entry builder functions (similar to Navigation 3's EntryProviderScope)
 */
typealias EntryBuilderFunction = NavGraphBuilder.(NavHostController) -> Unit
