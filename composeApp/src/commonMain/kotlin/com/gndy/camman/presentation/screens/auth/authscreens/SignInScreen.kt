package com.gndy.camman.presentation.screens.auth.authscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.MaterialTheme
import com.gndy.camman.presentation.screens.auth.authevents.SignInUiEvent
import com.gndy.camman.presentation.screens.auth.authviewmodel.SignInViewModel
import com.gndy.camman.presentation.theme.CamManColors
import com.gndy.camman.resources.*
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SignInScreen(
    onNavigateToClientHome: () -> Unit,
    onNavigateToPhotographerHome: () -> Unit,
    onNavigateToRoleSelection: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateBack: (() -> Unit)? = null,
    onContinueAsGuest: (() -> Unit)? = null,
    viewModel: SignInViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collectLatest { event ->
            when (event) {
                is SignInUiEvent.NavigateToClientHome -> onNavigateToClientHome()
                is SignInUiEvent.NavigateToPhotographerHome -> onNavigateToPhotographerHome()
                is SignInUiEvent.NavigateToRoleSelection -> onNavigateToRoleSelection()
                is SignInUiEvent.NavigateToSignUp -> onNavigateToSignUp()
                is SignInUiEvent.NavigateToForgotPassword -> onNavigateToForgotPassword()
                is SignInUiEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
                is SignInUiEvent.ShowSuccess -> snackbarHostState.showSnackbar(event.message)
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
        ) {
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onNavigateBack != null) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = extendedColors.textPrimary
                        )
                    }
                }
            }

            // Main Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .widthIn(max = 480.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Brand Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(extendedColors.primaryAccent.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = extendedColors.primaryAccent,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Welcome Text
                Text(
                    text = stringResource(Res.string.welcome_back),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = extendedColors.textPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(Res.string.sign_in_to_continue),
                    fontSize = 16.sp,
                    color = extendedColors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.widthIn(max = 300.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Email Field
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.email),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = extendedColors.textSecondary
                    )

                    OutlinedTextField(
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        placeholder = {
                            Text(
                                text = "you@example.com",
                                color = extendedColors.textSecondary.copy(alpha = 0.7f)
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = extendedColors.primaryAccent,
                            unfocusedBorderColor = extendedColors.border,
                            focusedContainerColor = colorScheme.surface,
                            unfocusedContainerColor = colorScheme.surface,
                            focusedTextColor = extendedColors.textPrimary,
                            unfocusedTextColor = extendedColors.textPrimary,
                            cursorColor = extendedColors.primaryAccent
                        ),
                        isError = uiState.emailError != null,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )
                    
                    if (uiState.emailError != null) {
                        Text(
                            text = uiState.emailError!!,
                            color = colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Password Field
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.password),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = extendedColors.textSecondary
                    )

                    OutlinedTextField(
                        value = uiState.password,
                        onValueChange = viewModel::onPasswordChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        placeholder = {
                            Text(
                                text = stringResource(Res.string.enter_your_password),
                                color = extendedColors.textSecondary.copy(alpha = 0.7f)
                            )
                        },
                        singleLine = true,
                        visualTransformation = if (uiState.isPasswordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = viewModel::onTogglePasswordVisibility) {
                                Icon(
                                    imageVector = if (uiState.isPasswordVisible)
                                        Icons.Default.Visibility
                                    else
                                        Icons.Default.VisibilityOff,
                                    contentDescription = stringResource(Res.string.toggle_password_visibility),
                                    tint = extendedColors.textSecondary
                                )
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = extendedColors.primaryAccent,
                            unfocusedBorderColor = extendedColors.border,
                            focusedContainerColor = colorScheme.surface,
                            unfocusedContainerColor = colorScheme.surface,
                            focusedTextColor = extendedColors.textPrimary,
                            unfocusedTextColor = extendedColors.textPrimary,
                            cursorColor = extendedColors.primaryAccent
                        ),
                        isError = uiState.passwordError != null,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                viewModel.onSignInClick()
                            }
                        )
                    )
                    
                    if (uiState.passwordError != null) {
                        Text(
                            text = uiState.passwordError!!,
                            color = colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                }

                // Forgot Password
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = stringResource(Res.string.forgot_password),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = extendedColors.primaryAccent,
                        modifier = Modifier
                            .clickable { viewModel.onForgotPasswordClick() }
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                    )
                }

                // Error Message
                if (uiState.error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.error!!,
                        color = colorScheme.error,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Login Button
                Button(
                    onClick = viewModel::onSignInClick,
                    enabled = !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = extendedColors.primaryAccent,
                        disabledContainerColor = extendedColors.primaryAccent.copy(alpha = 0.5f)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = stringResource(Res.string.sign_in),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorScheme.onPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = extendedColors.border
                    )
                    Text(
                        text = "Or log in with",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = extendedColors.textSecondary,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = extendedColors.border
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Social Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Google Button
                    OutlinedButton(
                        onClick = { /* Handle Google login */ },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = colorScheme.surface,
                            contentColor = extendedColors.textPrimary
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, extendedColors.border)
                    ) {
                        Text(text = "G", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEA4335))
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "Google",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Facebook Button
                    OutlinedButton(
                        onClick = { /* Handle Facebook login */ },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = colorScheme.surface,
                            contentColor = extendedColors.textPrimary
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, extendedColors.border)
                    ) {
                        Text(text = "f", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1877F2))
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "Facebook",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Footer - Sign Up Link
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(Res.string.dont_have_account) + " ",
                        fontSize = 14.sp,
                        color = extendedColors.textSecondary
                    )
                    Text(
                        text = stringResource(Res.string.sign_up),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = extendedColors.primaryAccent,
                        modifier = Modifier.clickable { viewModel.onSignUpClick() }
                    )
                }

                // Continue as Guest option
                if (onContinueAsGuest != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(
                        onClick = onContinueAsGuest,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(Res.string.continue_browsing),
                            fontSize = 14.sp,
                            color = extendedColors.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
