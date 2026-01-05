package com.gndy.camman.presentation.screens.auth.authscreens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gndy.camman.domain.model.UserType
import com.gndy.camman.presentation.screens.auth.authevents.SignUpUiEvent
import com.gndy.camman.presentation.screens.auth.authviewmodel.SignUpViewModel
import com.gndy.camman.resources.*
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

// Dark Theme Colors
private val BackgroundDark = Color(0xFF0D1117)
private val SurfaceDark = Color(0xFF161B22)
private val PrimaryBlue = Color(0xFF4D76FD)
private val TextPrimary = Color(0xFFE6EDF3)
private val TextSecondary = Color(0xFF848D97)
private val BorderColor = Color(0xFF30363D)
private val ErrorColor = Color(0xFFE53935)
private val SuccessGreen = Color(0xFF22C55E)

// Input field height
private val InputFieldHeight = 56.dp

@Composable
fun SignUpScreen(
    onNavigateToSignIn: () -> Unit,
    viewModel: SignUpViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collectLatest { event ->
            when (event) {
                is SignUpUiEvent.NavigateToSignIn -> onNavigateToSignIn()
                is SignUpUiEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
                is SignUpUiEvent.ShowSuccess -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BackgroundDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
                .padding(paddingValues)
        ) {
            // Top Bar with Back Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateToSignIn,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(Res.string.back),
                        tint = TextPrimary
                    )
                }
            }

            // Scrollable Content - includes everything
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .widthIn(max = 480.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Section
                Text(
                    text = stringResource(Res.string.create_account),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(Res.string.join_camman_portfolio),
                    fontSize = 15.sp,
                    color = TextSecondary,
                    lineHeight = 22.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // User Type Selection Section
                Text(
                    text = "I am a",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )

                // Responsive User Type Cards
                UserTypeSelectionCards(
                    selectedUserType = uiState.selectedUserType,
                    onUserTypeSelected = viewModel::onUserTypeSelected
                )

                // User Type Error
                if (uiState.userTypeError != null) {
                    Text(
                        text = uiState.userTypeError!!,
                        color = ErrorColor,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Full Name Field
                InputFieldWithLabel(
                    label = stringResource(Res.string.full_name),
                    value = uiState.displayName,
                    onValueChange = viewModel::onDisplayNameChanged,
                    placeholder = "John Doe",
                    error = uiState.displayNameError,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                    onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Email Field
                InputFieldWithLabel(
                    label = stringResource(Res.string.email_address),
                    value = uiState.email,
                    onValueChange = viewModel::onEmailChanged,
                    placeholder = "name@example.com",
                    error = uiState.emailError,
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                    onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password Field
                PasswordFieldWithLabel(
                    label = stringResource(Res.string.password),
                    value = uiState.password,
                    onValueChange = viewModel::onPasswordChanged,
                    placeholder = "••••••••",
                    error = uiState.passwordError,
                    isVisible = uiState.isPasswordVisible,
                    onToggleVisibility = viewModel::onTogglePasswordVisibility,
                    hint = "At least 6 characters with letters and numbers",
                    imeAction = ImeAction.Next,
                    onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Confirm Password Field
                PasswordFieldWithLabel(
                    label = stringResource(Res.string.confirm_password),
                    value = uiState.confirmPassword,
                    onValueChange = viewModel::onConfirmPasswordChanged,
                    placeholder = "••••••••",
                    error = uiState.confirmPasswordError,
                    isVisible = uiState.isConfirmPasswordVisible,
                    onToggleVisibility = viewModel::onToggleConfirmPasswordVisibility,
                    imeAction = ImeAction.Done,
                    onImeAction = {
                        focusManager.clearFocus()
                        viewModel.onSignUpClick()
                    }
                )

                // General Error Message
                if (uiState.error != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = uiState.error!!,
                        color = ErrorColor,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = ErrorColor.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Sign Up Button
                Button(
                    onClick = viewModel::onSignUpClick,
                    enabled = !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue,
                        disabledContainerColor = PrimaryBlue.copy(alpha = 0.5f)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = stringResource(Res.string.sign_up),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Divider with "Or sign up with"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = BorderColor
                    )
                    Text(
                        text = "Or sign up with",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = BorderColor
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Social Login Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                ) {
                    // Google
                    SocialLoginButton(
                        text = "G",
                        textColor = Color(0xFFEA4335),
                        onClick = { /* TODO: Google OAuth */ }
                    )

                    // Apple
                    SocialLoginButton(
                        text = "",
                        textColor = TextPrimary,
                        onClick = { /* TODO: Apple OAuth */ }
                    )

                    // Facebook
                    SocialLoginButton(
                        text = "f",
                        textColor = Color(0xFF1877F2),
                        onClick = { /* TODO: Facebook OAuth */ }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Footer - Already have an account?
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(Res.string.already_have_account) + " ",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = stringResource(Res.string.sign_in),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBlue,
                        modifier = Modifier.clickable { viewModel.onSignInClick() }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

/**
 * Responsive container for user type selection cards
 * - On narrow screens (< 340dp): Cards stack vertically
 * - On wider screens: Cards display side by side
 */
@Composable
private fun UserTypeSelectionCards(
    selectedUserType: UserType?,
    onUserTypeSelected: (UserType) -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth()
    ) {
        val isCompact = maxWidth < 340.dp
        
        if (isCompact) {
            // Vertical layout for narrow screens
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                UserTypeCard(
                    modifier = Modifier.fillMaxWidth(),
                    title = "Client",
                    description = "Browse and book professional photographers for your events",
                    icon = Icons.Default.Person,
                    isSelected = selectedUserType == UserType.USER,
                    onClick = { onUserTypeSelected(UserType.USER) },
                    isCompactMode = false
                )

                UserTypeCard(
                    modifier = Modifier.fillMaxWidth(),
                    title = "Photographer",
                    description = "Showcase your portfolio and connect with clients",
                    icon = Icons.Default.CameraAlt,
                    isSelected = selectedUserType == UserType.PHOTOGRAPHER,
                    onClick = { onUserTypeSelected(UserType.PHOTOGRAPHER) },
                    isCompactMode = false
                )
            }
        } else {
            // Horizontal layout for wider screens
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                UserTypeCard(
                    modifier = Modifier.weight(1f),
                    title = "Client",
                    description = "Book photographers",
                    icon = Icons.Default.Person,
                    isSelected = selectedUserType == UserType.USER,
                    onClick = { onUserTypeSelected(UserType.USER) },
                    isCompactMode = true
                )

                UserTypeCard(
                    modifier = Modifier.weight(1f),
                    title = "Photographer",
                    description = "Offer services",
                    icon = Icons.Default.CameraAlt,
                    isSelected = selectedUserType == UserType.PHOTOGRAPHER,
                    onClick = { onUserTypeSelected(UserType.PHOTOGRAPHER) },
                    isCompactMode = true
                )
            }
        }
    }
}

/**
 * Adaptive user type selection card
 * @param isCompactMode When true, uses vertical layout with smaller content
 *                      When false, uses horizontal layout with full descriptions
 */
@Composable
private fun UserTypeCard(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    isCompactMode: Boolean = true
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) PrimaryBlue else BorderColor,
        animationSpec = spring(),
        label = "borderColor"
    )
    
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) PrimaryBlue.copy(alpha = 0.1f) else SurfaceDark,
        animationSpec = spring(),
        label = "backgroundColor"
    )
    
    val borderWidth by animateDpAsState(
        targetValue = if (isSelected) 2.dp else 1.dp,
        animationSpec = spring(),
        label = "borderWidth"
    )

    val iconSize = if (isCompactMode) 44.dp else 52.dp
    val innerIconSize = if (isCompactMode) 24.dp else 28.dp

    Box(
        modifier = modifier
            .then(
                if (isCompactMode) {
                    Modifier.height(130.dp)
                } else {
                    Modifier.height(IntrinsicSize.Min)
                }
            )
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(borderWidth, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        if (isCompactMode) {
            // Compact vertical layout
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Icon Container
                Box(
                    modifier = Modifier
                        .size(iconSize)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) PrimaryBlue.copy(alpha = 0.2f)
                            else BorderColor.copy(alpha = 0.3f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = if (isSelected) PrimaryBlue else TextSecondary,
                        modifier = Modifier.size(innerIconSize)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) TextPrimary else TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = TextSecondary.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp,
                    maxLines = 2
                )
            }
        } else {
            // Expanded horizontal layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                // Icon Container
                Box(
                    modifier = Modifier
                        .size(iconSize)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) PrimaryBlue.copy(alpha = 0.2f)
                            else BorderColor.copy(alpha = 0.3f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = if (isSelected) PrimaryBlue else TextSecondary,
                        modifier = Modifier.size(innerIconSize)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSelected) TextPrimary else TextSecondary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = description,
                        fontSize = 13.sp,
                        color = TextSecondary.copy(alpha = 0.8f),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Selection Indicator (checkmark)
        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✓",
                    fontSize = 13.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun InputFieldWithLabel(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    error: String?,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    onImeAction: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(InputFieldHeight),
            placeholder = {
                Text(
                    text = placeholder,
                    color = TextSecondary.copy(alpha = 0.5f),
                    fontSize = 15.sp
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = BorderColor,
                focusedContainerColor = SurfaceDark,
                unfocusedContainerColor = SurfaceDark,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = PrimaryBlue,
                errorBorderColor = ErrorColor
            ),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
            isError = error != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onNext = { onImeAction() },
                onDone = { onImeAction() }
            )
        )

        if (error != null) {
            Text(
                text = error,
                color = ErrorColor,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}

@Composable
private fun PasswordFieldWithLabel(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    error: String?,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    hint: String? = null,
    imeAction: ImeAction,
    onImeAction: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(InputFieldHeight),
            placeholder = {
                Text(
                    text = placeholder,
                    color = TextSecondary.copy(alpha = 0.5f),
                    fontSize = 15.sp
                )
            },
            visualTransformation = if (isVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(
                    onClick = onToggleVisibility,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = if (isVisible)
                            Icons.Default.Visibility
                        else
                            Icons.Default.VisibilityOff,
                        contentDescription = "Toggle password visibility",
                        tint = TextSecondary
                    )
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = BorderColor,
                focusedContainerColor = SurfaceDark,
                unfocusedContainerColor = SurfaceDark,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = PrimaryBlue,
                errorBorderColor = ErrorColor
            ),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
            isError = error != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onNext = { onImeAction() },
                onDone = { onImeAction() }
            )
        )

        if (hint != null && error == null) {
            Text(
                text = hint,
                color = TextSecondary.copy(alpha = 0.7f),
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }

        if (error != null) {
            Text(
                text = error,
                color = ErrorColor,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}

@Composable
private fun SocialLoginButton(
    text: String,
    textColor: Color,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(56.dp)
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
            .background(SurfaceDark, RoundedCornerShape(12.dp))
    ) {
        Text(
            text = text,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
