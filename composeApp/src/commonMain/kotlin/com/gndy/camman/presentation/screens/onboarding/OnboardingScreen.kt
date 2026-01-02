package com.gndy.camman.presentation.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gndy.camman.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

// Design System Colors
private val PrimaryBlue = Color(0xFF1173D4)
private val PrimaryBlueLight = Color(0xFF3D8FE0)
private val BackgroundDark = Color(0xFF101922)
private val TextWhite = Color.White
private val TextWhite90 = Color.White.copy(alpha = 0.9f)
private val IndicatorInactive = Color.White.copy(alpha = 0.5f)

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    onSkip: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // Horizontal Pager for swiping between pages
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            OnboardingPageContent(
                page = onboardingPages[page]
            )
        }

        // Overlay content (Skip button, text, indicators, CTA)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp)
        ) {
            // Top App Bar with Skip button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Placeholder for balance
                Spacer(modifier = Modifier.size(48.dp))

                TextButton(onClick = onSkip) {
                    Text(
                        text = stringResource(Res.string.skip),
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Spacer to push content to bottom
            Spacer(modifier = Modifier.weight(1f))

            // Bottom Content
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animated Title
                AnimatedContent(
                    targetState = pagerState.currentPage,
                    transitionSpec = {
                        (slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(400, easing = FastOutSlowInEasing)
                        ) + fadeIn(animationSpec = tween(400))) togetherWith
                                (slideOutVertically(
                                    targetOffsetY = { -it },
                                    animationSpec = tween(400, easing = FastOutSlowInEasing)
                                ) + fadeOut(animationSpec = tween(200)))
                    },
                    label = "title_animation"
                ) { currentPage ->
                    Text(
                        text = stringResource(onboardingPages[currentPage].titleRes),
                        color = TextWhite,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 42.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Animated Description
                AnimatedContent(
                    targetState = pagerState.currentPage,
                    transitionSpec = {
                        (slideInVertically(
                            initialOffsetY = { it / 2 },
                            animationSpec = tween(500, delayMillis = 100, easing = FastOutSlowInEasing)
                        ) + fadeIn(animationSpec = tween(400, delayMillis = 100))) togetherWith
                                (slideOutVertically(
                                    targetOffsetY = { -it / 2 },
                                    animationSpec = tween(300)
                                ) + fadeOut(animationSpec = tween(200)))
                    },
                    label = "description_animation"
                ) { currentPage ->
                    Text(
                        text = stringResource(onboardingPages[currentPage].descriptionRes),
                        color = TextWhite90,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp,
                        modifier = Modifier.widthIn(max = 400.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Page Indicators
                PageIndicator(
                    pageCount = onboardingPages.size,
                    currentPage = pagerState.currentPage
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Animated CTA Button
                AnimatedButton(
                    text = if (pagerState.currentPage == onboardingPages.size - 1)
                        stringResource(Res.string.get_started)
                    else
                        stringResource(Res.string.continue_button),
                    onClick = {
                        if (pagerState.currentPage == onboardingPages.size - 1) {
                            onComplete()
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(
                                    page = pagerState.currentPage + 1,
                                    animationSpec = tween(
                                        durationMillis = 500,
                                        easing = FastOutSlowInEasing
                                    )
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun AnimatedButton(
    text: String,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "button_scale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 8.dp,
        animationSpec = tween(100),
        label = "button_elevation"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale)
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(28.dp),
                ambientColor = PrimaryBlue.copy(alpha = 0.3f),
                spotColor = PrimaryBlue.copy(alpha = 0.5f)
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                        onClick()
                    }
                )
            },
        shape = RoundedCornerShape(28.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            PrimaryBlue,
                            PrimaryBlueLight,
                            PrimaryBlue
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Animated text change
            AnimatedContent(
                targetState = text,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(200)) +
                            slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(300)
                            )) togetherWith
                            (fadeOut(animationSpec = tween(150)) +
                                    slideOutVertically(
                                        targetOffsetY = { -it },
                                        animationSpec = tween(200)
                                    ))
                },
                label = "button_text_animation"
            ) { buttonText ->
                Text(
                    text = buttonText,
                    color = TextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingPage
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Full-screen background image from drawable resource
        Image(
            painter = painterResource(page.image),
            contentDescription = "Onboarding background image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient overlay (from bottom black/80 to top black/20)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.2f),  // Top
                            Color.Black.copy(alpha = 0.4f),  // Middle
                            Color.Black.copy(alpha = 0.8f)   // Bottom
                        )
                    )
                )
        )
    }
}

@Composable
private fun PageIndicator(
    pageCount: Int,
    currentPage: Int
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage

            // Animated width for pill effect on selected indicator
            val width by animateDpAsState(
                targetValue = if (isSelected) 24.dp else 8.dp,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "indicator_width"
            )

            // Animated alpha
            val alpha by animateFloatAsState(
                targetValue = if (isSelected) 1f else 0.5f,
                animationSpec = tween(300),
                label = "indicator_alpha"
            )

            Box(
                modifier = Modifier
                    .size(width = width, height = 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) PrimaryBlue else Color.White.copy(alpha = alpha)
                    )
            )
        }
    }
}
