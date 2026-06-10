package com.gndy.camman.presentation.screens.auth.authscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.MaterialTheme
import com.gndy.camman.presentation.screens.auth.authevents.ForgotPasswordUiEvent
import com.gndy.camman.presentation.screens.auth.authviewmodel.ForgotPasswordViewModel
import com.gndy.camman.presentation.theme.CamManColors
import com.gndy.camman.resources.*
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    viewModel: ForgotPasswordViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collectLatest { event ->
            when (event) {
                is ForgotPasswordUiEvent.NavigateBack -> onNavigateBack()
                is ForgotPasswordUiEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
                is ForgotPasswordUiEvent.ShowSuccess -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    val colorScheme = MaterialTheme.colorScheme
    val extendedColors = CamManColors.extended

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
                .padding(paddingValues)
                .widthIn(max = 480.dp)
        ) {
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(Res.string.back),
                        tint = extendedColors.textPrimary.copy(alpha = 0.8f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Main Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .padding(top = 24.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Content Section
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Title
                    Text(
                        text = stringResource(Res.string.reset_password),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = extendedColors.textPrimary,
                        lineHeight = 34.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Description
                    Text(
                        text = stringResource(Res.string.forgot_password_desc),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = extendedColors.textSecondary,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Email Field
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(Res.string.email_address),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = extendedColors.textSecondary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = uiState.email,
                            onValueChange = viewModel::onEmailChanged,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            placeholder = {
                                Text(
                                    text = stringResource(Res.string.enter_your_email),
                                    color = extendedColors.textSecondary.copy(alpha = 0.6f)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = stringResource(Res.string.email),
                                    tint = extendedColors.textSecondary.copy(alpha = 0.6f),
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = extendedColors.primaryAccent,
                                unfocusedBorderColor = extendedColors.border,
                                focusedContainerColor = colorScheme.surface.copy(alpha = 0.2f),
                                unfocusedContainerColor = colorScheme.surface.copy(alpha = 0.05f),
                                focusedTextColor = extendedColors.textPrimary,
                                unfocusedTextColor = extendedColors.textPrimary,
                                cursorColor = extendedColors.primaryAccent
                            ),
                            isError = uiState.emailError != null,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    viewModel.onResetPasswordClick()
                                }
                            )
                        )

                        if (uiState.emailError != null) {
                            Text(
                                text = uiState.emailError!!,
                                color = colorScheme.error,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Success Message
                    if (uiState.isEmailSent) {
                        MessageCard(
                            icon = Icons.Default.CheckCircle,
                            iconTint = extendedColors.success,
                            backgroundColor = extendedColors.success.copy(alpha = 0.1f),
                            title = stringResource(Res.string.email_sent),
                            titleColor = extendedColors.successLight,
                            message = stringResource(Res.string.check_inbox_instruction),
                            messageColor = extendedColors.successLight
                        )
                    }

                    // Error Message
                    if (uiState.error != null && !uiState.isEmailSent) {
                        MessageCard(
                            icon = Icons.Default.Error,
                            iconTint = colorScheme.error,
                            backgroundColor = colorScheme.errorContainer,
                            title = "Error",
                            titleColor = colorScheme.onErrorContainer,
                            message = uiState.error!!,
                            messageColor = colorScheme.onErrorContainer
                        )
                    }
                }

                // Send Reset Link Button
                Button(
                    onClick = viewModel::onResetPasswordClick,
                    enabled = !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = extendedColors.primaryAccent,
                        disabledContainerColor = extendedColors.primaryAccent.copy(alpha = 0.5f)
                    )
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = if (uiState.isEmailSent) 
                                stringResource(Res.string.back_to_sign_in) 
                            else 
                                stringResource(Res.string.send_reset_link),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageCard(
    icon: ImageVector,
    iconTint: Color,
    backgroundColor: Color,
    title: String,
    titleColor: Color,
    message: String,
    messageColor: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = titleColor
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = message,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = messageColor,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
