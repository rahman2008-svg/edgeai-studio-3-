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

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
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
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.nexvora.ai.BuildConfig
import com.nexvora.ai.common.getJsonResponse
import kotlin.math.max
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "AGNewReleaseNotifi"
private const val REPO = "google-ai-edge/gallery"

// Nexora AI brand colors (matches the rest of the app's premium redesign).
private val NexoraPrimary = Color(0xFF2563EB)
private val NexoraSecondary = Color(0xFF7C3AED)
private val NexoraCardCorner = 24.dp

data class ReleaseInfo(val html_url: String, val tag_name: String)

@Composable
fun NewReleaseNotification() {
  var newReleaseVersion by remember { mutableStateOf("") }
  var newReleaseUrl by remember { mutableStateOf("") }
  val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
  val coroutineScope = rememberCoroutineScope()
  val uriHandler = LocalUriHandler.current

  DisposableEffect(lifecycleOwner) {
    // Create a LifecycleEventObserver to listen for specific lifecycle events.
    val observer = LifecycleEventObserver { _, event ->
      // Log or perform actions based on the lifecycle event.
      when (event) {
        Lifecycle.Event.ON_RESUME -> {
          coroutineScope.launch {
            withContext(Dispatchers.IO) {
              Log.d(TAG, "Checking for new release...")
              val info =
                getJsonResponse<ReleaseInfo>("https://api.github.com/repos/$REPO/releases/latest")
              if (info != null) {
                val curRelease = BuildConfig.VERSION_NAME
                val newRelease = info.jsonObj.tag_name
                val isNewer = isNewerRelease(currentRelease = curRelease, newRelease = newRelease)
                Log.d(TAG, "curRelease: $curRelease, newRelease: $newRelease, isNewer: $isNewer")
                if (isNewer) {
                  newReleaseVersion = newRelease
                  newReleaseUrl = info.jsonObj.html_url
                }
              }
            }
          }
        }

        else -> {}
      }
    }

    lifecycleOwner.lifecycle.addObserver(observer)

    onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
  }

  AnimatedVisibility(
    visible = newReleaseVersion.isNotEmpty(),
    enter = fadeIn() + expandVertically(),
  ) {
    Card(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
      shape = RoundedCornerShape(NexoraCardCorner),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
      Box(
        modifier =
          Modifier.fillMaxWidth()
            .background(
              Brush.linearGradient(
                colors =
                  listOf(NexoraPrimary.copy(alpha = 0.12f), NexoraSecondary.copy(alpha = 0.12f))
              )
            )
      ) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            // AI update icon.
            Box(
              modifier =
                Modifier.size(48.dp)
                  .clip(CircleShape)
                  .background(Brush.linearGradient(colors = listOf(NexoraPrimary, NexoraSecondary))),
              contentAlignment = Alignment.Center,
            ) {
              Icon(
                imageVector = Icons.Rounded.SystemUpdate,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(26.dp),
              )
            }

            Column(modifier = Modifier.padding(start = 14.dp)) {
              Text(
                text = "\uD83D\uDE80 Nexora AI Update Available",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
              )
              Text(
                text = "A newer version of Nexora AI is ready to install.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text =
              "Enjoy new AI models, performance improvements, bug fixes, UI enhancements, " +
                "and exciting new features.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
          )

          Spacer(modifier = Modifier.height(18.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            OutlinedButton(
              onClick = { uriHandler.openUri(newReleaseUrl) },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(16.dp),
            ) {
              Text("Release Notes")
            }
            Button(
              onClick = { uriHandler.openUri(newReleaseUrl) },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(16.dp),
              colors = ButtonDefaults.buttonColors(containerColor = NexoraPrimary),
            ) {
              Text("Update Now")
            }
          }
        }
      }
    }
  }
}

private fun isNewerRelease(currentRelease: String, newRelease: String): Boolean {
  // Split the version strings into their individual components (e.g., "0.9.0" -> ["0", "9", "0"])
  val currentComponents = currentRelease.split('.').map { it.toIntOrNull() ?: 0 }
  val newComponents = newRelease.split('.').map { it.toIntOrNull() ?: 0 }

  // Determine the maximum number of components to iterate through
  val maxComponents = max(currentComponents.size, newComponents.size)

  // Iterate through the components from left to right (major, minor, patch, etc.)
  for (i in 0 until maxComponents) {
    val currentComponent = currentComponents.getOrElse(i) { 0 }
    val newComponent = newComponents.getOrElse(i) { 0 }

    if (newComponent > currentComponent) {
      return true
    } else if (newComponent < currentComponent) {
      return false
    }
  }

  return false
}
