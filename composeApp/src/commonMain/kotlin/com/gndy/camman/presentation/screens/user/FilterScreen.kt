package com.gndy.camman.presentation.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gndy.camman.presentation.theme.CamManColors
import com.gndy.camman.presentation.theme.Gold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    onNavigateBack: () -> Unit,
    onApplyFilters: (FilterState) -> Unit
) {
    var filterState by remember { mutableStateOf(FilterState()) }
    val scrollState = rememberScrollState()
    val colorScheme = MaterialTheme.colorScheme
    val extendedColors = CamManColors.extended

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Filters",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = extendedColors.textPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = extendedColors.textPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorScheme.background
                )
            )
        },
        bottomBar = {
            Surface(
                color = colorScheme.background,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                        .navigationBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { filterState = FilterState() }) {
                        Text(
                            "Reset",
                            color = extendedColors.textPrimary.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Button(
                        onClick = { onApplyFilters(filterState) },
                        modifier = Modifier
                            .height(60.dp)
                            .weight(1f)
                            .padding(start = 24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = extendedColors.primaryAccent
                        ),
                        shape = RoundedCornerShape(30.dp)
                    ) {
                        Text(
                            "Show 124 Results", // Mock result count
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
        ) {
            // Location Section
            FilterSectionHeader("LOCATION", textColor = extendedColors.textSecondary)
            OutlinedTextField(
                value = filterState.location,
                onValueChange = { filterState = filterState.copy(location = it) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = extendedColors.textSecondary.copy(alpha = 0.5f)
                    )
                },
                placeholder = {
                    Text(
                        "Search city...",
                        color = extendedColors.textSecondary.copy(alpha = 0.3f)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    unfocusedContainerColor = colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = extendedColors.textPrimary,
                    unfocusedTextColor = extendedColors.textPrimary
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Availability Section
            FilterSectionHeader("AVAILABILITY", textColor = extendedColors.textSecondary)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .clickable { /* Show date picker */ }
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = extendedColors.textSecondary.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = filterState.availabilityRange ?: "Select dates",
                        color = extendedColors.textPrimary,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = extendedColors.textSecondary.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Type of Shoot Section
            FilterSectionHeader("TYPE OF SHOOT", textColor = extendedColors.textSecondary)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                mainAxisSpacing = 12.dp,
                crossAxisSpacing = 12.dp
            ) {
                listOf("Wedding", "Portrait", "Commercial", "Fashion", "Lifestyle", "Event").forEach { type ->
                    val isSelected = filterState.shootType == type
                    ShootTypeChip(
                        label = type,
                        isSelected = isSelected,
                        onClick = {
                            filterState = filterState.copy(shootType = if (isSelected) null else type)
                        },
                        accentColor = extendedColors.primaryAccent,
                        surfaceColor = colorScheme.surfaceVariant,
                        textColor = extendedColors.textPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Hourly Rate Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterSectionHeader("HOURLY RATE", textColor = extendedColors.textSecondary)
                Text(
                    "$${filterState.minPrice.toInt()} - $${filterState.maxPrice.toInt()}",
                    color = extendedColors.primaryAccent,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            RangeSlider(
                value = filterState.minPrice..filterState.maxPrice,
                onValueChange = { range ->
                    filterState = filterState.copy(minPrice = range.start, maxPrice = range.endInclusive)
                },
                valueRange = 0f..500f,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    activeTrackColor = extendedColors.primaryAccent,
                    inactiveTrackColor = extendedColors.textPrimary.copy(alpha = 0.1f),
                    thumbColor = extendedColors.textPrimary
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("$0", color = extendedColors.textSecondary.copy(alpha = 0.4f), style = MaterialTheme.typography.labelMedium)
                Text("$500+", color = extendedColors.textSecondary.copy(alpha = 0.4f), style = MaterialTheme.typography.labelMedium)
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Client Rating Section
            FilterSectionHeader("CLIENT RATING", textColor = extendedColors.textSecondary)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf("Any", "3+", "4+", "5").forEach { rating ->
                    val isSelected = filterState.rating == rating
                    RatingChip(
                        label = rating,
                        isSelected = isSelected,
                        onClick = { filterState = filterState.copy(rating = rating) },
                        modifier = Modifier.weight(1f),
                        accentColor = extendedColors.primaryAccent,
                        surfaceColor = colorScheme.surfaceVariant,
                        textColor = extendedColors.textPrimary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun FilterSectionHeader(title: String, textColor: Color) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = textColor.copy(alpha = 0.5f),
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
private fun ShootTypeChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    accentColor: Color,
    surfaceColor: Color,
    textColor: Color
) {
    val backgroundColor = if (isSelected) accentColor else surfaceColor.copy(alpha = 0.5f)
    val contentColor = if (isSelected) Color.White else textColor.copy(alpha = 0.7f)
    
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = label,
            color = contentColor,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun RatingChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color,
    surfaceColor: Color,
    textColor: Color
) {
    val backgroundColor = if (isSelected) accentColor.copy(alpha = 0.1f) else surfaceColor.copy(alpha = 0.5f)
    val borderColor = if (isSelected) accentColor else Color.Transparent
    val contentColor = if (isSelected) accentColor else textColor.copy(alpha = 0.7f)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .then(if (isSelected) Modifier.border(2.dp, borderColor, RoundedCornerShape(16.dp)) else Modifier)
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                color = contentColor,
                fontWeight = FontWeight.Bold
            )
            if (label != "Any") {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = if (isSelected) accentColor else Color(0xFFFFD700),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun FlowRow(
    modifier: Modifier = Modifier,
    mainAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    crossAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.layout.Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val placeholders = measurables.map { it.measure(constraints.copy(minWidth = 0, minHeight = 0)) }
        val spacing = mainAxisSpacing.roundToPx()
        val crossSpacing = crossAxisSpacing.roundToPx()
        
        val rows = mutableListOf<List<androidx.compose.ui.layout.Placeable>>()
        var currentRow = mutableListOf<androidx.compose.ui.layout.Placeable>()
        var currentRowWidth = 0
        
        placeholders.forEach { placeable ->
            if (currentRowWidth + placeable.width + spacing > constraints.maxWidth && currentRow.isNotEmpty()) {
                rows.add(currentRow)
                currentRow = mutableListOf()
                currentRowWidth = 0
            }
            currentRow.add(placeable)
            currentRowWidth += placeable.width + spacing
        }
        rows.add(currentRow)
        
        val height = rows.sumOf { row -> row.maxOf { it.height } } + (rows.size - 1) * crossSpacing
        
        layout(constraints.maxWidth, height) {
            var y = 0
            rows.forEach { row ->
                var x = 0
                val rowHeight = row.maxOf { it.height }
                row.forEach { placeable ->
                    placeable.place(x, y)
                    x += placeable.width + spacing
                }
                y += rowHeight + crossSpacing
            }
        }
    }
}

@kotlinx.serialization.Serializable
data class FilterState(
    val location: String = "New York, NY",
    val availabilityRange: String? = "Oct 24 - Oct 28",
    val shootType: String? = "Wedding",
    val minPrice: Float = 50f,
    val maxPrice: Float = 250f,
    val rating: String = "4+"
)
