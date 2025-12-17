package com.gndy.camman.presentation.screens.photographer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.gndy.camman.domain.model.ExperienceLevel
import com.gndy.camman.presentation.theme.Gold
import com.gndy.camman.resources.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: EditProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showDiscardDialog by remember { mutableStateOf(false) }
    var showUrlInputDialog by remember { mutableStateOf<String?>(null) }
    var urlInput by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is EditProfileEvent.SaveSuccess -> {
                    scope.launch {
                        snackbarHostState.showSnackbar("Profile saved successfully!")
                    }
                    onNavigateBack()
                }

                is EditProfileEvent.ShowError -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(event.message)
                    }
                }

                is EditProfileEvent.NavigateBack -> {
                    onNavigateBack()
                }
            }
        }
    }

    // Discard changes dialog
    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Discard Changes?") },
            text = { Text("You have unsaved changes. Are you sure you want to discard them?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDiscardDialog = false
                        viewModel.discardChanges()
                        onNavigateBack()
                    }
                ) {
                    Text("Discard", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text("Keep Editing")
                }
            }
        )
    }

    // URL input dialog
    showUrlInputDialog?.let { type ->
        AlertDialog(
            onDismissRequest = {
                showUrlInputDialog = null
                urlInput = ""
            },
            title = {
                Text(
                    when (type) {
                        "profile" -> "Profile Image URL"
                        "cover" -> "Cover Image URL"
                        else -> "Add Portfolio Image URL"
                    }
                )
            },
            text = {
                OutlinedTextField(
                    value = urlInput,
                    onValueChange = { urlInput = it },
                    placeholder = { Text("https://example.com/image.jpg") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Gold,
                        cursorColor = Gold
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        when (type) {
                            "profile" -> viewModel.onProfileImageUrlChanged(urlInput)
                            "cover" -> viewModel.onCoverImageUrlChanged(urlInput)
                            "portfolio" -> viewModel.onPortfolioUrlAdded(urlInput)
                        }
                        showUrlInputDialog = null
                        urlInput = ""
                    },
                    enabled = urlInput.isNotBlank()
                ) {
                    Text("Add", color = Gold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showUrlInputDialog = null
                        urlInput = ""
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Profile",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (uiState.hasChanges) {
                                showDiscardDialog = true
                            } else {
                                onNavigateBack()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back)
                        )
                    }
                },
                actions = {
                    if (uiState.hasChanges) {
                        TextButton(
                            onClick = { viewModel.saveProfile() },
                            enabled = !uiState.isSaving
                        ) {
                            if (uiState.isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Gold,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = stringResource(Res.string.save),
                                    color = Gold,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Gold)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // ============ Profile Images Section ============
                ProfileImagesSection(
                    profileImageUrl = uiState.profileImageUrl,
                    coverImageUrl = uiState.coverImageUrl,
                    onProfileImageUrlClick = { showUrlInputDialog = "profile" },
                    onCoverImageUrlClick = { showUrlInputDialog = "cover" },
                    onClearProfileImage = { viewModel.clearProfileImage() },
                    onClearCoverImage = { viewModel.clearCoverImage() }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ============ Basic Info Section ============
                SectionCard(title = "Basic Information") {
                    EditTextField(
                        value = uiState.fullName,
                        onValueChange = viewModel::onFullNameChanged,
                        label = "Full Name *",
                        placeholder = "Enter your full name",
                        leadingIcon = Icons.Default.Person,
                        error = uiState.errors["fullName"]
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    EditTextField(
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChanged,
                        label = "Email Address *",
                        placeholder = "Enter your email",
                        leadingIcon = Icons.Default.Email,
                        error = uiState.errors["email"],
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    EditTextField(
                        value = uiState.phoneNumber,
                        onValueChange = viewModel::onPhoneChanged,
                        label = "Phone Number *",
                        placeholder = "Enter your phone number",
                        leadingIcon = Icons.Default.Phone,
                        error = uiState.errors["phone"],
                        keyboardType = KeyboardType.Phone
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ============ Professional Info Section ============
                SectionCard(title = "Professional Details") {
                    // Bio
                    OutlinedTextField(
                        value = uiState.bio,
                        onValueChange = viewModel::onBioChanged,
                        label = { Text("Bio *") },
                        placeholder = { Text("Tell clients about yourself...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Gold,
                            cursorColor = Gold
                        ),
                        isError = uiState.errors["bio"] != null,
                        supportingText = {
                            if (uiState.errors["bio"] != null) {
                                Text(
                                    uiState.errors["bio"]!!,
                                    color = MaterialTheme.colorScheme.error
                                )
                            } else {
                                Text("${uiState.bio.length}/500 characters")
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    EditTextField(
                        value = uiState.location,
                        onValueChange = viewModel::onLocationChanged,
                        label = "Location *",
                        placeholder = "City, State/Country",
                        leadingIcon = Icons.Default.LocationOn,
                        error = uiState.errors["location"]
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Specialties
                    SpecialtiesSection(
                        availableSpecialties = uiState.availableSpecialties,
                        selectedSpecialties = uiState.selectedSpecialties,
                        onSpecialtyToggled = viewModel::onSpecialtyToggled,
                        error = uiState.errors["specialties"]
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Experience Level
                    ExperienceLevelSection(
                        experienceLevels = uiState.experienceLevels,
                        selectedLevel = uiState.experienceLevel,
                        onLevelSelected = viewModel::onExperienceLevelSelected
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ============ Portfolio Section ============
                SectionCard(title = "Portfolio") {
                    Text(
                        text = "Add your best work URLs to attract clients",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Add URL button
                    OutlinedButton(
                        onClick = { showUrlInputDialog = "portfolio" },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Portfolio Image URL")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Portfolio images grid
                    if (uiState.portfolioUrls.isNotEmpty()) {
                        PortfolioImagesGrid(
                            urls = uiState.portfolioUrls,
                            onRemove = viewModel::onPortfolioUrlRemoved
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No portfolio images added yet",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ============ Pricing Section ============
                SectionCard(title = "Pricing & Availability") {
                    PricingSection(
                        startingPrice = uiState.startingPrice,
                        currency = uiState.currency,
                        currencies = uiState.currencies,
                        isAvailable = uiState.isAvailable,
                        onPriceChanged = viewModel::onStartingPriceChanged,
                        onCurrencySelected = viewModel::onCurrencySelected,
                        onAvailabilityChanged = viewModel::onAvailabilityChanged
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ============ Social Links Section ============
                SectionCard(title = "Social & Web Links") {
                    EditTextField(
                        value = uiState.website,
                        onValueChange = viewModel::onWebsiteChanged,
                        label = "Website",
                        placeholder = "https://yourwebsite.com",
                        leadingIcon = Icons.Default.Language
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    EditTextField(
                        value = uiState.instagram,
                        onValueChange = viewModel::onInstagramChanged,
                        label = "Instagram",
                        placeholder = "@yourusername",
                        leadingIcon = Icons.Default.Camera
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    EditTextField(
                        value = uiState.facebook,
                        onValueChange = viewModel::onFacebookChanged,
                        label = "Facebook",
                        placeholder = "facebook.com/yourpage",
                        leadingIcon = Icons.Default.Facebook
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Save Button
                Button(
                    onClick = { viewModel.saveProfile() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Gold,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = uiState.hasChanges && !uiState.isSaving
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.Black,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Save Changes",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ProfileImagesSection(
    profileImageUrl: String,
    coverImageUrl: String,
    onProfileImageUrlClick: () -> Unit,
    onCoverImageUrlClick: () -> Unit,
    onClearProfileImage: () -> Unit,
    onClearCoverImage: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        // Cover Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(Gold.copy(alpha = 0.2f))
                .clickable { onCoverImageUrlClick() }
        ) {
            if (coverImageUrl.isNotBlank()) {
                AsyncImage(
                    model = coverImageUrl,
                    contentDescription = "Cover",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.AddAPhoto,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = Gold
                        )
                        Text(
                            text = "Tap to add Cover Photo URL",
                            style = MaterialTheme.typography.bodySmall,
                            color = Gold
                        )
                    }
                }
            }

            // Edit/Remove buttons for cover
            if (coverImageUrl.isNotBlank()) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    IconButton(
                        onClick = onCoverImageUrlClick,
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onClearCoverImage,
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Profile Image
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp)
                .offset(y = 0.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(4.dp, MaterialTheme.colorScheme.surface, CircleShape)
                    .clickable { onProfileImageUrlClick() },
                contentAlignment = Alignment.Center
            ) {
                if (profileImageUrl.isNotBlank()) {
                    AsyncImage(
                        model = profileImageUrl,
                        contentDescription = "Profile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = Gold
                    )
                }
            }

            // Edit button for profile
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(32.dp)
                    .background(Gold, CircleShape)
                    .clickable { onProfileImageUrlClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = Color.Black,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Remove button for profile image
        if (profileImageUrl.isNotBlank()) {
            IconButton(
                onClick = onClearProfileImage,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 130.dp, bottom = 8.dp)
                    .size(28.dp)
                    .background(MaterialTheme.colorScheme.errorContainer, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove",
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Gold
            )
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
private fun EditTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Gold,
            cursorColor = Gold,
            errorBorderColor = MaterialTheme.colorScheme.error
        ),
        leadingIcon = {
            Icon(imageVector = leadingIcon, contentDescription = null)
        },
        isError = error != null,
        supportingText = error?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Next
        ),
        singleLine = true
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SpecialtiesSection(
    availableSpecialties: List<com.gndy.camman.domain.model.PhotographySpecialty>,
    selectedSpecialties: Set<String>,
    onSpecialtyToggled: (String) -> Unit,
    error: String?
) {
    Column {
        Text(
            text = "Specialties *",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            availableSpecialties.forEach { specialty ->
                val isSelected = selectedSpecialties.contains(specialty.displayName)
                FilterChip(
                    selected = isSelected,
                    onClick = { onSpecialtyToggled(specialty.displayName) },
                    label = { Text(specialty.displayName) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Gold,
                        selectedLabelColor = Color.Black
                    ),
                    leadingIcon = if (isSelected) {
                        { Icon(Icons.Default.Check, null, Modifier.size(16.dp)) }
                    } else null
                )
            }
        }
    }
}

@Composable
private fun ExperienceLevelSection(
    experienceLevels: List<ExperienceLevel>,
    selectedLevel: ExperienceLevel,
    onLevelSelected: (ExperienceLevel) -> Unit
) {
    Column {
        Text(
            text = "Experience Level",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(experienceLevels) { level ->
                val isSelected = level == selectedLevel
                Card(
                    modifier = Modifier
                        .clickable { onLevelSelected(level) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Gold.copy(alpha = 0.1f)
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = if (isSelected) {
                        androidx.compose.foundation.BorderStroke(2.dp, Gold)
                    } else null
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = level.displayName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = level.yearsRange,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PortfolioImagesGrid(
    urls: List<String>,
    onRemove: (Int) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(urls.size) { index ->
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                AsyncImage(
                    model = urls[index],
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Remove button
                IconButton(
                    onClick = { onRemove(index) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(24.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PricingSection(
    startingPrice: String,
    currency: String,
    currencies: List<String>,
    isAvailable: Boolean,
    onPriceChanged: (String) -> Unit,
    onCurrencySelected: (String) -> Unit,
    onAvailabilityChanged: (Boolean) -> Unit
) {
    var currencyExpanded by remember { mutableStateOf(false) }

    Column {
        // Price and Currency
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            OutlinedTextField(
                value = startingPrice,
                onValueChange = onPriceChanged,
                label = { Text("Starting Price") },
                placeholder = { Text("0.00") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Gold,
                    cursorColor = Gold
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                prefix = { Text("$") }
            )

            Spacer(modifier = Modifier.width(12.dp))

            ExposedDropdownMenuBox(
                expanded = currencyExpanded,
                onExpandedChange = { currencyExpanded = it },
                modifier = Modifier.width(110.dp)
            ) {
                OutlinedTextField(
                    value = currency,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = currencyExpanded)
                    },
                    modifier = Modifier.menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Gold
                    ),
                    singleLine = true
                )

                ExposedDropdownMenu(
                    expanded = currencyExpanded,
                    onDismissRequest = { currencyExpanded = false }
                ) {
                    currencies.forEach { curr ->
                        DropdownMenuItem(
                            text = { Text(curr) },
                            onClick = {
                                onCurrencySelected(curr)
                                currencyExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Availability Toggle
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Available for Bookings",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Show in client searches",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                Switch(
                    checked = isAvailable,
                    onCheckedChange = onAvailabilityChanged,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Gold,
                        checkedTrackColor = Gold.copy(alpha = 0.3f)
                    )
                )
            }
        }
    }
}
