/*
 * Copyright 2026 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.ai.edge.gallery.ui.home

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.ai.edge.gallery.R
import kotlinx.coroutines.delay

private const val DISMISS_DELAY_SECONDS = 5

@Composable
fun PromoScreenGm4(onDismiss: () -> Unit) {
  // Auto-dismiss timer — unchanged.
  LaunchedEffect(Unit) {
    delay(DISMISS_DELAY_SECONDS * 1000L)
    onDismiss()
  }

  // Soft entrance animation for the content.
  var visible by remember { mutableStateOf(false) }
  LaunchedEffect(Unit) { visible = true }

  val contentAlpha by
    animateFloatAsState(
      targetValue = if (visible) 1f else 0f,
      animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
      label = "promoContentAlpha",
    )
  val contentScale by
    animateFloatAsState(
      targetValue = if (visible) 1f else 0.92f,
      animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
      label = "promoContentScale",
    )

  // Gentle infinite glow pulse behind the brand icon.
  val infiniteTransition = rememberInfiniteTransition(label = "iconGlowTransition")
  val glowScale by
    infiniteTransition.animateFloat(
      initialValue = 1f,
      targetValue = 1.12f,
      animationSpec =
        infiniteRepeatable(
          animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
          repeatMode = RepeatMode.Reverse,
        ),
      label = "iconGlowScale",
    )

  val colorScheme = MaterialTheme.colorScheme
  val isDark = isSystemInDarkTheme()

  // Premium gradient background that adapts to Dynamic Color / dark & light theme.
  val backgroundBrush =
    Brush.verticalGradient(
      colors =
        if (isDark) {
          listOf(
            colorScheme.primary.copy(alpha = 0.35f),
            colorScheme.surface,
            colorScheme.background,
          )
        } else {
          listOf(
            colorScheme.primaryContainer.copy(alpha = 0.65f),
            colorScheme.surface,
            colorScheme.background,
          )
        }
    )

  Box(
    modifier = Modifier.fillMaxSize().background(brush = backgroundBrush),
    contentAlignment = Alignment.Center,
  ) {
    // Soft decorative background texture.
    val promoBg = ImageVector.vectorResource(R.drawable.gemma_promo_bg)
    Image(
      promoBg,
      contentDescription = null,
      modifier =
        Modifier.align(alignment = Alignment.TopCenter).graphicsLayer {
          alpha = if (isDark) 0.22f else 0.14f
          blendMode = BlendMode.Multiply
          translationY = promoBg.defaultHeight.toPx() * 0.2f
          scaleX = 2f
          scaleY = 2f
          rotationZ = -15.7f
        },
    )

    Column(
      modifier =
        Modifier.fillMaxWidth(0.92f)
          .widthIn(max = 440.dp)
          .padding(horizontal = 24.dp)
          .graphicsLayer {
            alpha = contentAlpha
            scaleX = contentScale
            scaleY = contentScale
          },
      verticalArrangement = Arrangement.spacedBy(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      // Glowing brand icon.
      Surface(
        modifier =
          Modifier.size(84.dp).graphicsLayer {
            scaleX = glowScale
            scaleY = glowScale
          },
        shape = CircleShape,
        color = colorScheme.primary.copy(alpha = if (isDark) 0.22f else 0.16f),
        tonalElevation = 0.dp,
      ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
          Image(
            ImageVector.vectorResource(R.drawable.gemini_star),
            contentDescription = null,
            modifier = Modifier.size(40.dp),
          )
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      // Subtitle / eyebrow text.
      Text(
        "YOUR PRIVATE AI ASSISTANT",
        style =
          MaterialTheme.typography.labelLarge.copy(
            fontSize = 13.sp,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.SemiBold,
          ),
        color = colorScheme.primary,
        textAlign = TextAlign.Center,
      )

      // Title.
      Text(
        "Welcome to Nexora AI",
        style =
          MaterialTheme.typography.headlineLarge.copy(
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 38.sp,
          ),
        color = colorScheme.onBackground,
        textAlign = TextAlign.Center,
      )

      // Description card.
      Card(
        shape = RoundedCornerShape(24.dp),
        colors =
          CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant.copy(alpha = if (isDark) 0.55f else 0.7f)
          ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
      ) {
        Text(
          "Experience fast, intelligent, and completely private on-device AI. Chat with " +
            "advanced AI models, explore powerful tools, and enjoy a beautiful modern " +
            "interface built for productivity.",
          style =
            MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp, lineHeight = 22.sp),
          textAlign = TextAlign.Center,
          color = colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(20.dp),
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Primary call-to-action button.
      Button(
        onClick = onDismiss,
        modifier = Modifier.fillMaxWidth(0.75f).height(52.dp),
        shape = RoundedCornerShape(28.dp),
        colors =
          ButtonDefaults.buttonColors(
            containerColor = colorScheme.primary,
            contentColor = colorScheme.onPrimary,
          ),
      ) {
        Text(
          "Get Started",
          style =
            MaterialTheme.typography.titleMedium.copy(
              fontSize = 16.sp,
              fontWeight = FontWeight.SemiBold,
            ),
        )
      }
    }
  }
}
