package com.gndy.camman

import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController {
    App()
}

fun initializeKoin() {
    com.gndy.camman.di.initKoin()
}
