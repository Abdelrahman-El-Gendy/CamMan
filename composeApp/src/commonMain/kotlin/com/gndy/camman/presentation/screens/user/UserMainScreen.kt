package com.gndy.camman.presentation.screens.user

import androidx.compose.foundation.layout.Box
import com.gndy.camman.presentation.screens.common.PlaceholderScreen
import com.gndy.camman.presentation.screens.common.ProfilePlaceholderScreen
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.gndy.camman.presentation.navigation.UserBottomNavItem
import com.gndy.camman.presentation.screens.nearby.NearbyPhotographersScreen
import com.gndy.camman.presentation.theme.Gold
import org.jetbrains.compose.resources.stringResource
import com.gndy.camman.resources.*

/**
 * Tab indices for UserMain bottom navigation
 */
object UserMainTabs {
    const val BROWSE = 0
    const val NEARBY = 1
    const val BOOKINGS = 2
    const val FAVORITES = 3
    const val PROFILE = 4
}

@Composable
fun UserMainScreen(
    onNavigateToPhotographer: (String) -> Unit,
    onNavigateToSignIn: () -> Unit,
    initialTab: Int = UserMainTabs.BROWSE
) {
    var selectedTab by rememberSaveable { mutableStateOf(initialTab) }
    
    // Update tab if initialTab changes (e.g., when navigating from booking confirmation)
    LaunchedEffect(initialTab) {
        if (initialTab != UserMainTabs.BROWSE) {
            selectedTab = initialTab
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                UserBottomNavItem.items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == index) item.selectedIcon else item.unselectedIcon,
                                contentDescription = stringResource(item.titleRes)
                            )
                        },
                        label = { Text(stringResource(item.titleRes)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Gold,
                            selectedTextColor = Gold,
                            indicatorColor = Gold.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> BrowsePhotographersScreen(
                    onNavigateToPhotographer = onNavigateToPhotographer
                )
                
                1 -> NearbyPhotographersScreen(
                    onNavigateToPhotographer = onNavigateToPhotographer,
                    onNavigateToChat = { /* TODO: Implement chat navigation */ },
                    onNavigateToBooking = { /* TODO: Implement booking navigation */ }
                )

                2 -> UserBookingsScreenContent()
                3 -> FavoritesScreen()
                4 -> UserProfileScreen(onSignOut = onNavigateToSignIn)
            }
        }
    }
}

@Composable
fun UserBookingsScreenContent() {
    MyBookingsScreenContent(
        onBookingClick = { /* Navigate to booking detail */ }
    )
}

@Composable
fun FavoritesScreen() {
    PlaceholderScreen(
        title = stringResource(Res.string.favorites),
        description = stringResource(Res.string.favorites_desc)
    )
}

@Composable
fun UserProfileScreen(onSignOut: () -> Unit) {
    ProfilePlaceholderScreen(
        title = stringResource(Res.string.my_profile),
        onSignOut = onSignOut
    )
}
