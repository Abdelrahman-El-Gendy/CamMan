package com.gndy.camman.presentation.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.gndy.camman.domain.model.Photographer
import com.gndy.camman.presentation.theme.Gold
import org.jetbrains.compose.resources.stringResource
import com.gndy.camman.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onNavigateToPhotographer: (String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Wedding", "Portrait", "Event")

    Scaffold(
        containerColor = Color(0xFF0F172A), // Matches design background
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Favorites",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                actions = {
                    IconButton(onClick = { /* Sort/Filter */ }) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "Sort",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Category Selector
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    CategoryChip(
                        label = category,
                        isSelected = isSelected,
                        onClick = { selectedCategory = category }
                    )
                }
            }

            // Favorites List
            LazyColumn(
                contentPadding = PaddingValues(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Mock data for designers
                val mockPhotographers = listOf(
                    Photographer(
                        id = "1",
                        name = "Sarah Jenkins",
                        specialties = listOf("Wedding & Lifestyle"),
                        rating = 4.9f,
                        reviewCount = 124,
                        startingPrice = 250.0,
                        profileImageUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?q=80&w=250&h=250&auto=format&fit=crop",
                        coverImageUrl = "https://images.unsplash.com/photo-1519741497674-611481863552?q=80&w=800&auto=format&fit=crop",
                        portfolioPreviewUrls = listOf(
                            "https://images.unsplash.com/photo-1583939003579-730e3918a45a?q=80&w=300&h=300&auto=format&fit=crop",
                            "https://images.unsplash.com/photo-1511285560929-80b456fea0bc?q=80&w=300&h=300&auto=format&fit=crop",
                            "https://images.unsplash.com/photo-1519225421980-715cb0215aed?q=80&w=300&h=300&auto=format&fit=crop"
                        ),
                        bio = "Specializing in lifestyle and wedding photography.",
                        location = "Los Angeles, CA"
                    ),
                    Photographer(
                        id = "2",
                        name = "Marcus Chen",
                        specialties = listOf("Urban & Street"),
                        rating = 5.0f,
                        reviewCount = 42,
                        startingPrice = 180.0,
                        profileImageUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?q=80&w=250&h=250&auto=format&fit=crop",
                        coverImageUrl = "https://images.unsplash.com/photo-1449824913935-59a10b8d2000?q=80&w=800&auto=format&fit=crop",
                        portfolioPreviewUrls = listOf(
                            "https://images.unsplash.com/photo-1514565131-fce0801e5785?q=80&w=300&h=300&auto=format&fit=crop",
                            "https://images.unsplash.com/photo-1477332552946-cfb384aeaf1c?q=80&w=300&h=300&auto=format&fit=crop",
                            "https://images.unsplash.com/photo-1449156001103-f2419b1434b9?q=80&w=300&h=300&auto=format&fit=crop"
                        ),
                        bio = "Urban explorer and street photographer.",
                        location = "New York, NY"
                    ),
                    Photographer(
                        id = "3",
                        name = "Elena Rodriguez",
                        specialties = listOf("Fashion & Editorial"),
                        rating = 4.7f,
                        reviewCount = 89,
                        startingPrice = 350.0,
                        profileImageUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?q=80&w=250&h=250&auto=format&fit=crop",
                        coverImageUrl = "https://images.unsplash.com/photo-1469334031218-e382a71b716b?q=80&w=800&auto=format&fit=crop",
                        portfolioPreviewUrls = listOf(
                            "https://images.unsplash.com/photo-1492707892479-7bc8d5a4ee93?q=80&w=300&h=300&auto=format&fit=crop",
                            "https://images.unsplash.com/photo-1483985988355-763728e1935b?q=80&w=300&h=300&auto=format&fit=crop",
                            "https://images.unsplash.com/photo-1509631179647-0177331693ae?q=80&w=300&h=300&auto=format&fit=crop"
                        ),
                        bio = "Fashion industry professional with an eye for detail.",
                        location = "Paris, FR"
                    )
                )

                items(mockPhotographers) { photographer ->
                    FavoritePhotographerCard(
                        photographer = photographer,
                        onViewProfile = { onNavigateToPhotographer(photographer.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        color = if (isSelected) Color.White else Color(0xFF1E293B).copy(alpha = 0.5f),
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)) else null
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
            color = if (isSelected) Color.Black else Color.White,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun FavoritePhotographerCard(
    photographer: Photographer,
    onViewProfile: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E293B) // Slate blue card
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header: Profile info & Heart
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = photographer.profileImageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = photographer.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = photographer.specialties.firstOrNull() ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFFE91E63).copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Unfavorite",
                        tint = Color(0xFFE91E63),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Stats row: Rating & Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rating Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.05f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${photographer.rating} (${photographer.reviewCount})",
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = "Starting at",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.4f)
                    )
                    Text(
                        text = "$${photographer.startingPrice?.toInt()}/hr",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Portfolio Preview
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                photographer.portfolioPreviewUrls.take(3).forEachIndexed { index, url ->
                    Box(modifier = Modifier.weight(1f).height(100.dp)) {
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                        
                        if (index == 2) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.Black.copy(alpha = 0.4f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+${(10..25).random()}", // Mock count
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Action Button
            Button(
                onClick = onViewProfile,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0096FF)
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("View Profile", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
