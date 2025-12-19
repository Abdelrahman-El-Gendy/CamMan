package com.gndy.camman.presentation.screens.permission

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gndy.camman.presentation.theme.Gold

/**
 * Screen that requests location permission from the user.
 * This screen is shown at app startup before the main navigation.
 */
@Composable
fun LocationPermissionScreen(
    onAllowLocation: () -> Unit,
    onSkip: () -> Unit,
    isPermissionGranted: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Location Icon with animated background
            Surface(
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                color = Gold.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Surface(
                        modifier = Modifier.size(90.dp),
                        shape = CircleShape,
                        color = Gold.copy(alpha = 0.2f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(50.dp),
                                tint = Gold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = "Enable Location",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Subtitle
            Text(
                text = "Allow CamMan to access your location to discover photographers near you",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Benefits List
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "With location access you can:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    PermissionBenefit(
                        icon = Icons.Default.NearMe,
                        title = "Find Nearby Photographers",
                        description = "Discover professional photographers in your area"
                    )

                    PermissionBenefit(
                        icon = Icons.Default.Search,
                        title = "Distance-Based Search",
                        description = "Filter photographers by how far they are from you"
                    )

                    PermissionBenefit(
                        icon = Icons.Default.MyLocation,
                        title = "Accurate Travel Times",
                        description = "See estimated travel times to photographer locations"
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Permission Status Indicator (if already granted)
            if (isPermissionGranted) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Location access granted",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF4CAF50)
                    )
                }
            }

            // Allow Location Button
            Button(
                onClick = onAllowLocation,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Gold)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isPermissionGranted) "Continue" else "Allow Location Access",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Skip Button
            if (!isPermissionGranted) {
                TextButton(
                    onClick = onSkip,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Maybe Later",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Privacy Note
            Text(
                text = "Your location is only used to find nearby photographers and is never shared without your consent.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun PermissionBenefit(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(10.dp),
            color = Gold.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Gold,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

/**
 * Platform-specific implementation of location permission screen
 * that handles the actual permission request using platform APIs
 */
@Composable
expect fun PlatformLocationPermissionScreen(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
    onSkip: () -> Unit
)
