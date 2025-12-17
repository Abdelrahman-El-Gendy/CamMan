package com.gndy.camman.di

import com.gndy.camman.presentation.navigation.builders.AuthEntryBuilder
import com.gndy.camman.presentation.navigation.builders.BookingEntryBuilder
import com.gndy.camman.presentation.navigation.builders.NavEntryBuilder
import com.gndy.camman.presentation.navigation.builders.PhotographerEntryBuilder
import com.gndy.camman.presentation.navigation.builders.PortfolioEntryBuilder
import com.gndy.camman.presentation.navigation.builders.UserEntryBuilder
import org.koin.dsl.module

/**
 * Navigation Module for Dependency Injection.
 *
 * This follows the Navigation 3 modularization pattern using dependency injection
 * to provide entry builders. Similar to Dagger's @IntoSet/@Multibindings pattern,
 * but adapted for Koin.
 *
 * Reference: https://developer.android.com/guide/navigation/navigation-3/modularize
 *
 * Benefits:
 * - Feature modules can contribute their navigation entries independently
 * - No need to explicitly list every entry builder in the main NavGraph
 * - Easy to add/remove features by just adding/removing the module
 * - Better separation of concerns
 *
 * Usage:
 * ```kotlin
 * // In your activity or composable:
 * val entryBuilders: Set<NavEntryBuilder> by inject()
 *
 * NavHost(...) {
 *     entryBuilders.forEach { builder ->
 *         with(builder) { buildEntries(navController) }
 *     }
 * }
 * ```
 */
val navigationModule = module {
    // ============== Entry Builders ==============
    // Each feature module provides its own entry builder

    // Auth Feature
    single<NavEntryBuilder>(qualifier = org.koin.core.qualifier.named("auth")) {
        AuthEntryBuilder()
    }

    // User Feature
    single<NavEntryBuilder>(qualifier = org.koin.core.qualifier.named("user")) {
        UserEntryBuilder()
    }

    // Photographer Feature
    single<NavEntryBuilder>(qualifier = org.koin.core.qualifier.named("photographer")) {
        PhotographerEntryBuilder()
    }

    // Portfolio Feature
    single<NavEntryBuilder>(qualifier = org.koin.core.qualifier.named("portfolio")) {
        PortfolioEntryBuilder()
    }

    // Booking Feature
    single<NavEntryBuilder>(qualifier = org.koin.core.qualifier.named("booking")) {
        BookingEntryBuilder()
    }

    // ============== Collect All Entry Builders ==============
    // This provides a Set of all entry builders, similar to Dagger's @IntoSet

    single<Set<NavEntryBuilder>> {
        setOf(
            get(qualifier = org.koin.core.qualifier.named("auth")),
            get(qualifier = org.koin.core.qualifier.named("user")),
            get(qualifier = org.koin.core.qualifier.named("photographer")),
            get(qualifier = org.koin.core.qualifier.named("portfolio")),
            get(qualifier = org.koin.core.qualifier.named("booking"))
        )
    }
}

/**
 * Alternative approach using a list for ordered navigation.
 * Use this if the order of entry registration matters.
 */
val navigationModuleOrdered = module {
    single<List<NavEntryBuilder>> {
        listOf(
            AuthEntryBuilder(),
            UserEntryBuilder(),
            PhotographerEntryBuilder(),
            PortfolioEntryBuilder(),
            BookingEntryBuilder()
        )
    }
}
