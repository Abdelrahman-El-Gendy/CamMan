package com.gndy.camman.presentation.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gndy.camman.domain.repository.AuthRepository
import com.gndy.camman.presentation.navigation.UserBottomNavItem
import com.gndy.camman.presentation.screens.chat.ConversationsListViewModel
import com.gndy.camman.presentation.screens.common.ProfilePlaceholderScreen
import com.gndy.camman.presentation.screens.nearby.NearbyPhotographersScreen
import com.gndy.camman.presentation.screens.user.BrowsePhotographersScreen
import com.gndy.camman.presentation.screens.user.FavoritesScreen
import com.gndy.camman.presentation.theme.Gold
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import com.gndy.camman.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserMainScreen(
    onNavigateToPhotographer: (String) -> Unit,
    onNavigateToChat: (String) -> Unit = {},
    onNavigateToBooking: (String) -> Unit = {},
    onNavigateToConversations: () -> Unit = {},
    onNavigateToFilter: () -> Unit = {},
    onRequestLocationPermission: ((Boolean) -> Unit) -> Unit = { _ -> },
    onOpenLocationSettings: () -> Unit = {},
    onSignOut: () -> Unit,  // Goes back to SignIn after sign out
    authRepository: AuthRepository = koinInject(),
    conversationsViewModel: ConversationsListViewModel = koinViewModel()
) {
    // User is always authenticated when reaching this screen
    var selectedTab by rememberSaveable { mutableStateOf(0) }
    val conversationsState by conversationsViewModel.uiState.collectAsState()
    
    // Tab titles for the top bar
    val tabTitles = listOf("Home", "NearBy", "Favorites", "Bookings", "Profile")

    Scaffold(
        containerColor = Color(0xFF0F172A),
        topBar = {
            if (selectedTab != 0 && selectedTab != 1) { 
                TopAppBar(
                    title = {
                        Text(
                            text = tabTitles.getOrElse(selectedTab) { "CamMan" },
                            fontWeight = FontWeight.Bold
                        )
                    },
                    actions = {
                        // Messages icon with unread badge
                        IconButton(onClick = onNavigateToConversations) {
                            Box {
                                Icon(
                                    imageVector = Icons.Default.MailOutline,
                                    contentDescription = "Messages",
                                    tint = Gold
                                )
                                // Unread badge
                                if (conversationsState.totalUnreadCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .offset(x = 4.dp, y = (-4).dp)
                                            .size(18.dp)
                                            .background(Color.Red, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (conversationsState.totalUnreadCount > 9) "9+" 
                                                   else "${conversationsState.totalUnreadCount}",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            }
        },
        bottomBar = {
            ModernBottomNavigation(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 0.dp) // We'll handle padding inside or via the floating bar
        ) {
            when (selectedTab) {
                0 -> BrowsePhotographersScreen(
                    onNavigateToPhotographer = onNavigateToPhotographer,
                    onNavigateToConversations = onNavigateToConversations,
                    onNavigateToFilter = onNavigateToFilter
                )

                1 -> NearbyPhotographersScreen(
                    onNavigateBack = { selectedTab = 0 }, 
                    onNavigateToPhotographer = onNavigateToPhotographer,
                    onNavigateToChat = onNavigateToChat,
                    onNavigateToBooking = onNavigateToBooking,
                    onNavigateToFilter = onNavigateToFilter,
                    onRequestLocationPermission = { callback -> onRequestLocationPermission(callback) },
                    onOpenLocationSettings = onOpenLocationSettings
                )

                2 -> FavoritesScreen(
                    onNavigateToPhotographer = onNavigateToPhotographer
                )

                3 -> UserBookingsScreenContent()
                
                4 -> AuthenticatedProfileScreen(
                    onSignOut = onSignOut,
                    authRepository = authRepository
                )
            }
        }
    }
}

@Composable
private fun ModernBottomNavigation(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .navigationBarsPadding()
            .fillMaxWidth()
            .height(72.dp),
        shape = RoundedCornerShape(36.dp),
        color = Color(0xFF1E293B).copy(alpha = 0.95f),
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            UserBottomNavItem.items.forEachIndexed { index, item ->
                val isSelected = selectedTab == index
                val color = if (isSelected) Color(0xFF0096FF) else Color.White.copy(alpha = 0.4f)
                
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onTabSelected(index) }
                        ),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = stringResource(item.titleRes),
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(item.titleRes),
                        style = MaterialTheme.typography.labelSmall,
                        color = color,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 10.sp
                    )
                }
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

/**
 * Profile screen for authenticated users
 */
@Composable
fun AuthenticatedProfileScreen(
    onSignOut: () -> Unit,
    authRepository: AuthRepository
) {
    val currentUser = authRepository.currentUser
    ProfilePlaceholderScreen(
        title = stringResource(Res.string.my_profile),
        subtitle = currentUser?.displayName ?: currentUser?.email,
        onSignOut = onSignOut
    )
}
