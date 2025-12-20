package com.gndy.camman.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.gndy.camman.resources.Res
import com.gndy.camman.resources.*
import org.jetbrains.compose.resources.StringResource

/**
 * Bottom navigation items for User (Client)
 */
sealed class UserBottomNavItem(
    val route: String,
    val titleRes: StringResource,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Browse : UserBottomNavItem(
        route = "browse_photographers",
        titleRes = Res.string.nav_browse,
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search
    )

    data object Nearby : UserBottomNavItem(
        route = "nearby_photographers",
        titleRes = Res.string.nav_nearby,
        selectedIcon = Icons.Filled.LocationOn,
        unselectedIcon = Icons.Outlined.LocationOn
    )

    data object Bookings : UserBottomNavItem(
        route = "user_bookings",
        titleRes = Res.string.nav_bookings,
        selectedIcon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth
    )

    data object Favorites : UserBottomNavItem(
        route = "favorites",
        titleRes = Res.string.nav_favorites,
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder
    )

    data object Profile : UserBottomNavItem(
        route = "user_profile",
        titleRes = Res.string.nav_profile,
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )

    companion object {
        val items = listOf(Browse, Nearby, Bookings, Favorites, Profile)
    }
}

/**
 * Bottom navigation items for Photographer
 */
sealed class PhotographerBottomNavItem(
    val route: String,
    val titleRes: StringResource,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Home : PhotographerBottomNavItem(
        route = "photographer_home",
        titleRes = Res.string.nav_home,
        selectedIcon = Icons.Filled.Dashboard,
        unselectedIcon = Icons.Outlined.Dashboard
    )

    data object Portfolio : PhotographerBottomNavItem(
        route = "photographer_portfolio",
        titleRes = Res.string.nav_portfolio,
        selectedIcon = Icons.Filled.PhotoLibrary,
        unselectedIcon = Icons.Outlined.PhotoLibrary
    )

    data object Bookings : PhotographerBottomNavItem(
        route = "photographer_bookings",
        titleRes = Res.string.nav_bookings,
        selectedIcon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth
    )

    data object Profile : PhotographerBottomNavItem(
        route = "photographer_profile",
        titleRes = Res.string.nav_profile,
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )

    companion object {
        val items = listOf(Home, Portfolio, Bookings, Profile)
    }
}
