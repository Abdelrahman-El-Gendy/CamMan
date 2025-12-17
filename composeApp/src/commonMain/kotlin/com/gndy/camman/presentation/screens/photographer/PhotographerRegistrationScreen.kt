package com.gndy.camman.presentation.screens.photographer

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoCamera
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.gndy.camman.domain.model.ExperienceLevel
import com.gndy.camman.domain.model.PhotographySpecialty
import com.gndy.camman.domain.model.RegistrationStep
import com.gndy.camman.presentation.theme.Gold
import com.gndy.camman.resources.*
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotographerRegistrationScreen(
    onRegistrationComplete: () -> Unit,
    onBack: () -> Unit,
    viewModel: PhotographerRegistrationViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is PhotographerRegistrationEvent.RegistrationSuccess -> {
                    onRegistrationComplete()
                }

                is PhotographerRegistrationEvent.ShowError -> {
                    // Show snackbar or toast
                }

                is PhotographerRegistrationEvent.NavigateToDashboard -> {
                    onRegistrationComplete()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Create Your Profile",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (uiState.currentStep == RegistrationStep.BASIC_INFO) {
                            onBack()
                        } else {
                            viewModel.onPreviousStep()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Progress indicator
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                // Calculate step number and progress from collected state
                val stepNumber = when (uiState.currentStep) {
                    RegistrationStep.BASIC_INFO -> 1
                    RegistrationStep.PROFESSIONAL -> 2
                    RegistrationStep.PORTFOLIO -> 3
                    RegistrationStep.PRICING -> 4
                    RegistrationStep.REVIEW -> 5
                }
                val stepProgress = when (uiState.currentStep) {
                    RegistrationStep.BASIC_INFO -> 0.2f
                    RegistrationStep.PROFESSIONAL -> 0.4f
                    RegistrationStep.PORTFOLIO -> 0.6f
                    RegistrationStep.PRICING -> 0.8f
                    RegistrationStep.REVIEW -> 1.0f
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Step $stepNumber of 5",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = getStepTitle(uiState.currentStep),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Gold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { stepProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Gold,
                    trackColor = Gold.copy(alpha = 0.2f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Step Content with animation
            AnimatedContent(
                targetState = uiState.currentStep,
                transitionSpec = {
                    if (targetState.ordinal > initialState.ordinal) {
                        slideInHorizontally { it } + fadeIn() togetherWith
                                slideOutHorizontally { -it } + fadeOut()
                    } else {
                        slideInHorizontally { -it } + fadeIn() togetherWith
                                slideOutHorizontally { it } + fadeOut()
                    }
                },
                modifier = Modifier.weight(1f)
            ) { step ->
                when (step) {
                    RegistrationStep.BASIC_INFO -> BasicInfoStep(
                        uiState = uiState,
                        viewModel = viewModel
                    )

                    RegistrationStep.PROFESSIONAL -> ProfessionalInfoStep(
                        uiState = uiState,
                        viewModel = viewModel
                    )

                    RegistrationStep.PORTFOLIO -> PortfolioStep(
                        uiState = uiState,
                        viewModel = viewModel
                    )

                    RegistrationStep.PRICING -> PricingStep(
                        uiState = uiState,
                        viewModel = viewModel
                    )

                    RegistrationStep.REVIEW -> ReviewStep(
                        uiState = uiState,
                        viewModel = viewModel
                    )
                }
            }

            // Navigation buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (uiState.currentStep != RegistrationStep.BASIC_INFO) {
                    OutlinedButton(
                        onClick = { viewModel.onPreviousStep() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(Res.string.previous))
                    }
                }

                Button(
                    onClick = { viewModel.onNextStep() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Gold,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.Black,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = if (uiState.currentStep == RegistrationStep.REVIEW)
                                "Complete Registration" else stringResource(Res.string.next),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = if (uiState.currentStep == RegistrationStep.REVIEW)
                                Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BasicInfoStep(
    uiState: PhotographerRegistrationUiState,
    viewModel: PhotographerRegistrationViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "Let's start with your basic information",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "This information will help clients contact you",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Full Name
        RegistrationTextField(
            value = uiState.fullName,
            onValueChange = viewModel::onFullNameChanged,
            label = "Full Name *",
            placeholder = "Enter your full name",
            leadingIcon = Icons.Default.Person,
            error = uiState.errors["fullName"],
            keyboardType = KeyboardType.Text
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Email
        RegistrationTextField(
            value = uiState.email,
            onValueChange = viewModel::onEmailChanged,
            label = "Email Address *",
            placeholder = "Enter your email",
            leadingIcon = Icons.Default.Email,
            error = uiState.errors["email"],
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Phone
        RegistrationTextField(
            value = uiState.phoneNumber,
            onValueChange = viewModel::onPhoneChanged,
            label = "Phone Number *",
            placeholder = "Enter your phone number",
            leadingIcon = Icons.Default.Phone,
            error = uiState.errors["phone"],
            keyboardType = KeyboardType.Phone
        )
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ProfessionalInfoStep(
    uiState: PhotographerRegistrationUiState,
    viewModel: PhotographerRegistrationViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "Tell us about your work",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Help clients understand your expertise",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Bio
        OutlinedTextField(
            value = uiState.bio,
            onValueChange = viewModel::onBioChanged,
            label = { Text("Bio *") },
            placeholder = { Text("Tell clients about yourself and your photography style...") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4,
            maxLines = 6,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Gold,
                cursorColor = Gold
            ),
            isError = uiState.errors["bio"] != null,
            supportingText = {
                if (uiState.errors["bio"] != null) {
                    Text(
                        text = uiState.errors["bio"]!!,
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Text("${uiState.bio.length}/500 characters")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Location
        RegistrationTextField(
            value = uiState.location,
            onValueChange = viewModel::onLocationChanged,
            label = "Location *",
            placeholder = "City, State/Country",
            leadingIcon = Icons.Default.LocationOn,
            error = uiState.errors["location"]
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Specialties
        Text(
            text = "Photography Specialties *",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        if (uiState.errors["specialties"] != null) {
            Text(
                text = uiState.errors["specialties"]!!,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            uiState.availableSpecialties.forEach { specialty ->
                val isSelected = uiState.selectedSpecialties.contains(specialty.displayName)
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.onSpecialtyToggled(specialty.displayName) },
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

        Spacer(modifier = Modifier.height(24.dp))

        // Experience Level
        Text(
            text = "Experience Level",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            uiState.experienceLevels.forEach { level ->
                ExperienceLevelCard(
                    level = level,
                    isSelected = uiState.experienceLevel == level,
                    onClick = { viewModel.onExperienceLevelSelected(level) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ExperienceLevelCard(
    level: ExperienceLevel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Gold.copy(alpha = 0.1f)
            else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, Gold)
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = level.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = level.yearsRange,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Gold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PortfolioStep(
    uiState: PhotographerRegistrationUiState,
    viewModel: PhotographerRegistrationViewModel
) {
    var newPortfolioUrl by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "Showcase your work",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Add images to attract more clients",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Profile Image URL
        Text(
            text = "Profile Image URL",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.profileImageUrl,
            onValueChange = viewModel::onProfileImageUrlChanged,
            placeholder = { Text("https://example.com/your-photo.jpg") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Gold,
                cursorColor = Gold
            ),
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = null)
            },
            singleLine = true
        )

        // Preview profile image
        AnimatedVisibility(visible = uiState.profileImageUrl.isNotBlank()) {
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = uiState.profileImageUrl,
                    contentDescription = "Profile preview",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Cover Image URL
        Text(
            text = "Cover Image URL",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.coverImageUrl,
            onValueChange = viewModel::onCoverImageUrlChanged,
            placeholder = { Text("https://example.com/cover-photo.jpg") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Gold,
                cursorColor = Gold
            ),
            leadingIcon = {
                Icon(Icons.Default.Image, contentDescription = null)
            },
            singleLine = true
        )

        // Preview cover image
        AnimatedVisibility(visible = uiState.coverImageUrl.isNotBlank()) {
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                AsyncImage(
                    model = uiState.coverImageUrl,
                    contentDescription = "Cover preview",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Portfolio URLs
        Text(
            text = "Portfolio Images",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Add URLs to your best work samples",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Add new portfolio URL
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newPortfolioUrl,
                onValueChange = { newPortfolioUrl = it },
                placeholder = { Text("Add portfolio image URL") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Gold,
                    cursorColor = Gold
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (newPortfolioUrl.isNotBlank()) {
                        viewModel.onPortfolioUrlAdded(newPortfolioUrl)
                        newPortfolioUrl = ""
                    }
                },
                modifier = Modifier
                    .size(56.dp)
                    .background(Gold, RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // List of added portfolio URLs
        uiState.portfolioUrls.forEachIndexed { index, url ->
            PortfolioUrlItem(
                url = url,
                onRemove = { viewModel.onPortfolioUrlRemoved(index) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun PortfolioUrlItem(
    url: String,
    onRemove: () -> Unit
) {
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
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = url,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = url,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PricingStep(
    uiState: PhotographerRegistrationUiState,
    viewModel: PhotographerRegistrationViewModel
) {
    var currencyExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "Set your pricing",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Let clients know your starting rates",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Starting Price
        Text(
            text = "Starting Price",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            OutlinedTextField(
                value = uiState.startingPrice,
                onValueChange = viewModel::onStartingPriceChanged,
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
                modifier = Modifier.width(120.dp)
            ) {
                OutlinedTextField(
                    value = uiState.currency,
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
                    uiState.currencies.forEach { currency ->
                        DropdownMenuItem(
                            text = { Text(currency) },
                            onClick = {
                                viewModel.onCurrencySelected(currency)
                                currencyExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Availability
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
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Let clients know you're accepting new bookings",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                Switch(
                    checked = uiState.isAvailable,
                    onCheckedChange = viewModel::onAvailabilityChanged,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Gold,
                        checkedTrackColor = Gold.copy(alpha = 0.3f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Social Links
        Text(
            text = "Social & Web Links (Optional)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        RegistrationTextField(
            value = uiState.website,
            onValueChange = viewModel::onWebsiteChanged,
            label = "Website",
            placeholder = "https://yourwebsite.com",
            leadingIcon = Icons.Default.Language
        )

        Spacer(modifier = Modifier.height(12.dp))

        RegistrationTextField(
            value = uiState.instagram,
            onValueChange = viewModel::onInstagramChanged,
            label = "Instagram",
            placeholder = "@yourusername",
            leadingIcon = Icons.Default.Camera
        )

        Spacer(modifier = Modifier.height(12.dp))

        RegistrationTextField(
            value = uiState.facebook,
            onValueChange = viewModel::onFacebookChanged,
            label = "Facebook",
            placeholder = "facebook.com/yourpage",
            leadingIcon = Icons.Default.Facebook
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ReviewStep(
    uiState: PhotographerRegistrationUiState,
    viewModel: PhotographerRegistrationViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "Review your profile",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Make sure everything looks good before submitting",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Profile Preview Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column {
                // Cover Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(Gold.copy(alpha = 0.2f))
                ) {
                    if (uiState.coverImageUrl.isNotBlank()) {
                        AsyncImage(
                            model = uiState.coverImageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Profile Image
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 16.dp)
                            .offset(y = 40.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(4.dp, MaterialTheme.colorScheme.surface, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (uiState.profileImageUrl.isNotBlank()) {
                                AsyncImage(
                                    model = uiState.profileImageUrl,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 48.dp, bottom = 16.dp)
                ) {
                    Text(
                        text = uiState.fullName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = uiState.location,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = uiState.bio,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Specialties
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        uiState.selectedSpecialties.take(3).forEach { specialty ->
                            Box(
                                modifier = Modifier
                                    .background(Gold.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = specialty,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Gold
                                )
                            }
                        }
                    }

                    if (uiState.startingPrice.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Starting from $${uiState.startingPrice} ${uiState.currency}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Gold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Summary sections
        ReviewSection(
            title = "Contact Information",
            onEdit = { viewModel.goToStep(RegistrationStep.BASIC_INFO) }
        ) {
            ReviewItem("Email", uiState.email)
            ReviewItem("Phone", uiState.phoneNumber)
        }

        Spacer(modifier = Modifier.height(16.dp))

        ReviewSection(
            title = "Professional Details",
            onEdit = { viewModel.goToStep(RegistrationStep.PROFESSIONAL) }
        ) {
            ReviewItem("Experience", uiState.experienceLevel.displayName)
            ReviewItem("Specialties", uiState.selectedSpecialties.joinToString(", "))
        }

        Spacer(modifier = Modifier.height(16.dp))

        ReviewSection(
            title = "Portfolio",
            onEdit = { viewModel.goToStep(RegistrationStep.PORTFOLIO) }
        ) {
            ReviewItem("Portfolio Images", "${uiState.portfolioUrls.size} images added")
        }

        Spacer(modifier = Modifier.height(16.dp))

        ReviewSection(
            title = "Pricing & Availability",
            onEdit = { viewModel.goToStep(RegistrationStep.PRICING) }
        ) {
            ReviewItem(
                "Starting Price",
                if (uiState.startingPrice.isNotBlank()) "$${uiState.startingPrice} ${uiState.currency}" else "Not set"
            )
            ReviewItem("Available for Bookings", if (uiState.isAvailable) "Yes" else "No")
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ReviewSection(
    title: String,
    onEdit: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Gold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            content()
        }
    }
}

@Composable
private fun ReviewItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun RegistrationTextField(
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
            Icon(
                imageVector = leadingIcon,
                contentDescription = null
            )
        },
        isError = error != null,
        supportingText = {
            if (error != null) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Next
        ),
        singleLine = true
    )
}

private fun getStepTitle(step: RegistrationStep): String {
    return when (step) {
        RegistrationStep.BASIC_INFO -> "Basic Info"
        RegistrationStep.PROFESSIONAL -> "Professional"
        RegistrationStep.PORTFOLIO -> "Portfolio"
        RegistrationStep.PRICING -> "Pricing"
        RegistrationStep.REVIEW -> "Review"
    }
}
