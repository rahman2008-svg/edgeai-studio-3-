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
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SquareDrawerItem(
  label: String,
  description: String,
  icon: ImageVector,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  iconBrush: Brush? = null,
) {
  val colorScheme = MaterialTheme.colorScheme
  val shape = RoundedCornerShape(28.dp)

  // Track the press state to drive the scale / elevation / border animations below.
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  // Subtle "press" scale animation — a soft, bouncy squeeze on tap.
  val pressScale by
    animateFloatAsState(
      targetValue = if (isPressed) 0.96f else 1f,
      animationSpec =
        spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
      label = "squareDrawerItemPressScale",
    )

  // Smooth elevation animation — the card lifts when idle and settles when pressed.
  val elevation by
    animateDpAsState(
      targetValue = if (isPressed) 2.dp else 8.dp,
      animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
      label = "squareDrawerItemElevation",
    )

  // Border glows slightly brighter while pressed for tactile feedback.
  val borderAlpha by
    animateFloatAsState(
      targetValue = if (isPressed) 0.9f else 0.4f,
      animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
      label = "squareDrawerItemBorderAlpha",
    )

  // Glassmorphism-inspired gradient surface. Built entirely from MaterialTheme.colorScheme so it
  // automatically follows Dynamic Color and Dark / Light mode.
  val backgroundBrush =
    Brush.linearGradient(
      colors =
        listOf(
          colorScheme.surfaceContainerHigh.copy(alpha = 0.92f),
          colorScheme.surfaceContainerLow.copy(alpha = 0.78f),
          colorScheme.primaryContainer.copy(alpha = 0.16f),
        )
    )

  val borderBrush =
    Brush.linearGradient(
      colors =
        listOf(
          colorScheme.primary.copy(alpha = borderAlpha),
          colorScheme.outlineVariant.copy(alpha = borderAlpha * 0.5f),
        )
    )

  // Soft tonal container behind the icon. Independent of `iconBrush`, which continues to control
  // the icon's own tint exactly as before.
  val iconContainerBrush =
    Brush.linearGradient(
      colors =
        listOf(colorScheme.primary.copy(alpha = 0.20f), colorScheme.tertiary.copy(alpha = 0.16f))
    )

  Column(
    modifier =
      modifier
        .aspectRatio(1f)
        .graphicsLayer {
          scaleX = pressScale
          scaleY = pressScale
        }
        .shadow(
          elevation = elevation,
          shape = shape,
          clip = false,
          ambientColor = colorScheme.primary.copy(alpha = 0.22f),
          spotColor = colorScheme.primary.copy(alpha = 0.28f),
        )
        .clip(shape)
        .background(brush = backgroundBrush)
        .border(width = 1.4.dp, brush = borderBrush, shape = shape)
        .clickable(
          interactionSource = interactionSource,
          indication = ripple(color = colorScheme.primary),
          onClickLabel = label,
          role = Role.Button,
          onClick = onClick,
        )
        .semantics(mergeDescendants = true) {}
  ) {
    Column(
      verticalArrangement = Arrangement.SpaceBetween,
      horizontalAlignment = Alignment.Start,
      modifier = Modifier.padding(20.dp).fillMaxSize(),
    ) {
      // Larger icon presented inside a rounded, gradient-tinted container.
      Box(
        modifier =
          Modifier.size(52.dp).clip(RoundedCornerShape(16.dp)).background(brush = iconContainerBrush),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          icon,
          contentDescription = null,
          modifier =
            Modifier.size(30.dp)
              .then(
                if (iconBrush != null) {
                  Modifier.graphicsLayer(
                      // Required for some devices to blend correctly
                      alpha = 0.99f
                    )
                    .drawWithContent {
                      // Draws the icon first
                      drawContent()
                      // Masks the brush to the icon's shape
                      drawRect(brush = iconBrush, blendMode = BlendMode.SrcIn)
                    }
                } else {
                  Modifier
                }
              ),
        )
      }

      Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(
          label,
          color = colorScheme.onSurface,
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
          maxLines = 2,
        )
        Text(
          description,
          color = colorScheme.onSurfaceVariant,
          style = MaterialTheme.typography.bodySmall,
          maxLines = 2,
          autoSize =
            TextAutoSize.StepBased(minFontSize = 8.sp, maxFontSize = 12.sp, stepSize = 1.sp),
        )
      }
    }
  }
}
