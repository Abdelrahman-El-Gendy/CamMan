package com.gndy.camman.presentation.screens.contact

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.gndy.camman.presentation.theme.Gold
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreen(
    onNavigateBack: () -> Unit,
    viewModel: ContactViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                ContactUiEvent.MessageSent -> { /* Show success snackbar */
                }

                is ContactUiEvent.ShowError -> { /* Show error snackbar */
                }

                is ContactUiEvent.OpenEmail -> { /* Open email app */
                }

                is ContactUiEvent.OpenPhone -> { /* Open phone dialer */
                }

                is ContactUiEvent.OpenUrl -> { /* Open browser */
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contact") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
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
                    .padding(16.dp)
            ) {
                // Profile Header
                uiState.profile?.let { profile ->
                    ProfileHeader(
                        name = profile.name,
                        imageUrl = profile.profileImageUrl,
                        location = profile.location
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Contact Actions
                    ContactActionsSection(
                        email = profile.email,
                        phone = profile.phone,
                        website = profile.website,
                        onEmailClick = viewModel::onEmailClick,
                        onPhoneClick = viewModel::onPhoneClick,
                        onWebsiteClick = viewModel::onWebsiteClick
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }

                HorizontalDivider()

                Spacer(modifier = Modifier.height(24.dp))

                // Contact Form
                ContactForm(
                    name = uiState.name,
                    email = uiState.email,
                    subject = uiState.subject,
                    message = uiState.message,
                    isSending = uiState.isSending,
                    onNameChanged = viewModel::onNameChanged,
                    onEmailChanged = viewModel::onEmailChanged,
                    onSubjectChanged = viewModel::onSubjectChanged,
                    onMessageChanged = viewModel::onMessageChanged,
                    onSendClick = viewModel::sendMessage
                )
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    name: String,
    imageUrl: String,
    location: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = name,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = location,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun ContactActionsSection(
    email: String,
    phone: String,
    website: String?,
    onEmailClick: () -> Unit,
    onPhoneClick: () -> Unit,
    onWebsiteClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ContactActionButton(
            icon = Icons.Default.Email,
            label = "Email",
            onClick = onEmailClick
        )

        ContactActionButton(
            icon = Icons.Default.Phone,
            label = "Call",
            onClick = onPhoneClick
        )

        if (website != null) {
            ContactActionButton(
                icon = Icons.Default.Language,
                label = "Website",
                onClick = onWebsiteClick
            )
        }
    }
}

@Composable
private fun ContactActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Surface(
            shape = CircleShape,
            color = Gold.copy(alpha = 0.1f),
            modifier = Modifier.size(56.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Gold,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun ContactForm(
    name: String,
    email: String,
    subject: String,
    message: String,
    isSending: Boolean,
    onNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onSubjectChanged: (String) -> Unit,
    onMessageChanged: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Column {
        Text(
            text = "Send a Message",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = onNameChanged,
            label = { Text("Your Name *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isSending
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChanged,
            label = { Text("Your Email *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isSending
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = subject,
            onValueChange = onSubjectChanged,
            label = { Text("Subject") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isSending
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = message,
            onValueChange = onMessageChanged,
            label = { Text("Message *") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4,
            maxLines = 6,
            enabled = !isSending
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSendClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Gold),
            enabled = !isSending
        ) {
            if (isSending) {
                CircularProgressIndicator(
                    color = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = null,
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Send Message", color = Color.Black)
            }
        }
    }
}
