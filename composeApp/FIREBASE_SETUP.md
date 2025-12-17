# Firebase Authentication Setup Guide

This guide will help you set up Firebase Authentication for the CamMan Photographer Portfolio App.

## Prerequisites

1. A Google account
2. Firebase project (or willingness to create one)

## Step 1: Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project"
3. Enter project name: `CamMan` (or your preferred name)
4. Enable/disable Google Analytics as needed
5. Click "Create project"

## Step 2: Enable Authentication

1. In Firebase Console, go to **Build > Authentication**
2. Click **Get Started**
3. Go to **Sign-in method** tab
4. Enable **Email/Password** provider
5. (Optional) Enable other providers as needed

## Step 3: Add Android App

1. In Firebase Console, click **Project Settings** (gear icon)
2. Under "Your apps", click **Add app** > **Android**
3. Enter package name: `com.gndy.camman`
4. Enter app nickname: `CamMan Android`
5. (Optional) Add SHA-1 for Google Sign-In
6. Click **Register app**
7. Download `google-services.json`
8. Place `google-services.json` in: `composeApp/src/androidMain/`

## Step 4: Add iOS App (Optional)

1. In Firebase Console, click **Add app** > **iOS**
2. Enter bundle ID: `com.gndy.camman`
3. Enter app nickname: `CamMan iOS`
4. Click **Register app**
5. Download `GoogleService-Info.plist`
6. Place `GoogleService-Info.plist` in your iOS project's root directory

## Step 5: Sync Gradle

1. Open the project in Android Studio
2. Click **File > Sync Project with Gradle Files**
3. Wait for sync to complete

## Step 6: Run the App

1. Build and run the app
2. Test Sign Up with a new email
3. Test Sign In with the created account
4. Test Forgot Password functionality

## Project Structure (Auth Related)

```
composeApp/src/
├── commonMain/kotlin/com/gndy/camman/
│   ├── domain/
│   │   ├── model/AuthUser.kt          # Auth domain models
│   │   ├── repository/AuthRepository.kt
│   │   └── usecase/auth/
│   │       ├── SignInUseCase.kt
│   │       ├── SignUpUseCase.kt
│   │       ├── SignOutUseCase.kt
│   │       ├── GetAuthStateUseCase.kt
│   │       └── ResetPasswordUseCase.kt
│   ├── data/
│   │   └── repository/AuthRepositoryImpl.kt
│   └── presentation/screens/auth/
│       ├── AuthViewModel.kt           # Contains all auth ViewModels
│       ├── SignInScreen.kt
│       ├── SignUpScreen.kt
│       └── ForgotPasswordScreen.kt
└── androidMain/
    └── google-services.json           # Place your file here
```

## Authentication Flow

```
App Launch
    ↓
Splash Screen
    ↓
Check Auth State
    ↓
┌─────────────┬─────────────┐
│ Logged In   │ Not Logged  │
│     ↓       │      ↓      │
│ Home Screen │ Sign In     │
└─────────────┴─────────────┘
```

## Features

- ✅ Email/Password Sign In
- ✅ Email/Password Sign Up
- ✅ Password Reset via Email
- ✅ Email Verification (sent on sign up)
- ✅ Auto-login on app restart
- ✅ Sign Out functionality
- ✅ Input validation (email format, password strength)
- ✅ Error handling with user-friendly messages

## Security Notes

- Passwords must be at least 6 characters
- Passwords must contain at least one letter and one number
- Email verification is sent automatically on sign up
- Users can sign in before verifying email, but you can add verification checks

## Troubleshooting

### "No Firebase app" error

- Ensure `google-services.json` is in the correct location
- Sync Gradle after adding the file

### Sign in fails with "network error"

- Check internet connection
- Verify Firebase project is set up correctly

### "Email already in use"

- The email is already registered
- Use "Forgot Password" to reset or use different email

## Dependencies Used

```kotlin
// Firebase (GitLive KMP SDK)
implementation("dev.gitlive:firebase-auth:2.1.0")
implementation("dev.gitlive:firebase-common:2.1.0")
```

These are Kotlin Multiplatform compatible Firebase libraries.
