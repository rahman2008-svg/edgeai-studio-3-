/*
 * Copyright 2025 Google LLC
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

package com.nexvora.ai.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Chat
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

// Nexora AI brand colors (matches the Home Screen palette).
private val NexoraPrimary = Color(0xFF2563EB)
private val NexoraSecondary = Color(0xFF7C3AED)
private val NexoraSheetCorner = 28.dp

private data class NexoraFeature(val icon: ImageVector, val label: String)

private val NEXORA_FEATURES =
  listOf(
    NexoraFeature(Icons.Rounded.Storage, "Local AI Models"),
    NexoraFeature(Icons.Rounded.Chat, "AI Chat"),
    NexoraFeature(Icons.Rounded.CloudOff, "Offline Processing"),
    NexoraFeature(Icons.Rounded.Lock, "Privacy First"),
    NexoraFeature(Icons.Rounded.Bolt, "Fast Performance"),
    NexoraFeature(Icons.Rounded.Dashboard, "Smart Workspace"),
  )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobileActionsChallengeDialog(
  onDismiss: () -> Unit,
  onLoadModel: () -> Unit,
  onSendEmail: () -> Unit,
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    shape = RoundedCornerShape(topStart = NexoraSheetCorner, topEnd = NexoraSheetCorner),
    containerColor = MaterialTheme.colorScheme.surface,
  ) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
      visible = visible,
      enter =
        fadeIn(animationSpec = tween(350)) + slideInVertically(animationSpec = tween(350)) { it / 6 },
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        // Large AI logo/icon with a premium gradient background.
        Box(
          modifier =
            Modifier.size(72.dp)
              .clip(CircleShape)
              .background(Brush.linearGradient(colors = listOf(NexoraPrimary, NexoraSecondary))),
          contentAlignment = Alignment.Center,
        ) {
          Icon(
            imageVector = Icons.Rounded.AutoAwesome,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(36.dp),
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Premium gradient header title.
        Text(
          text = "Nexora AI",
          textAlign = TextAlign.Center,
          style =
            MaterialTheme.typography.headlineMedium.copy(
              fontWeight = FontWeight.Bold,
              brush = Brush.linearGradient(colors = listOf(NexoraPrimary, NexoraSecondary)),
            ),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "Private AI powered completely on your device",
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.titleSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text =
            "Experience powerful on-device AI with a modern interface. Manage local models, " +
              "chat with AI, explore intelligent tools, and enjoy a fast, secure, and private " +
              "AI experience without depending on cloud services.",
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Feature cards, two per row.
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          NEXORA_FEATURES.chunked(2).forEach { rowFeatures ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              rowFeatures.forEach { feature ->
                NexoraFeatureCard(feature = feature, modifier = Modifier.weight(1f))
              }
              // Keep the row width balanced if there's an odd number of cards.
              if (rowFeatures.size < 2) {
                Spacer(modifier = Modifier.weight(1f))
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action buttons: Material 3 Outlined + Filled.
        Row(
          modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          OutlinedButton(
            onClick = onSendEmail,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
          ) {
            Text("Learn More")
          }
          Button(
            onClick = onLoadModel,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NexoraPrimary),
          ) {
            Text("Load AI Model")
          }
        }
      }
    }
  }
}

@Composable
private fun NexoraFeatureCard(feature: NexoraFeature, modifier: Modifier = Modifier) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(14.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Box(
        modifier =
          Modifier.size(36.dp).clip(CircleShape).background(NexoraPrimary.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = feature.icon,
          contentDescription = null,
          tint = NexoraPrimary,
          modifier = Modifier.size(20.dp),
        )
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = feature.label,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurface,
      )
    }
  }
}
