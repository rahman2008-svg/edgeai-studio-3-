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

package com.nexvora.ai.ui.common.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch

/**
 * A Composable function that displays a zoomable and pannable image.
 *
 * This function handles multi-touch gestures for zooming and panning. It's designed to be used
 * within a Pager to prevent the Pager from scrolling while the user is interacting with the image.
 */
@Composable
fun ZoomableImage(
  bitmap: ImageBitmap,
  modifier: Modifier = Modifier,
  minScale: Float = 1f,
  maxScale: Float = 3f,
  contentScale: ContentScale = ContentScale.Fit,
  pagerState: PagerState? = null,
  resetOnImageUpdate: Boolean = true,
  enabled: Boolean = true,
  twoFingerOnly: Boolean = false,
  onTransformed: (offsetX: Float, offsetY: Float, scale: Float) -> Unit = { _, _, _ -> },
) {
  val scale = remember { mutableFloatStateOf(1f) }
  val offsetX = remember { mutableFloatStateOf(0f) }
  val offsetY = remember { mutableFloatStateOf(0f) }
  val coroutineScope = rememberCoroutineScope()

  LaunchedEffect(bitmap) {
    if (resetOnImageUpdate) {
      scale.floatValue = 1f
      offsetX.floatValue = 0f
      offsetY.floatValue = 0f
    }
  }

  val gestureModifier =
    if (enabled) {
      Modifier.pointerInput(twoFingerOnly) { // Only apply if enabled is true
        // It uses the `pointerInput` modifier to detect gestures.
        //
        // When a user performs a pinch-to-zoom gesture, the `scale` state is updated.
        // Once the content is zoomed in (`scale.value > 1`), pan gestures are enabled, and the
        // `offsetX` and `offsetY` states are updated to move the content.
        //
        // To prevent a parent Pager component from scrolling horizontally during a pan gesture, the
        // `pagerState`'s scrolling is temporarily disabled and then re-enabled after the pan event.
        // If the content is zoomed back out to its original size, the scale and offsets are reset.
        awaitEachGesture {
          awaitFirstDown()
          do {
            val event = awaitPointerEvent()
            val isTwoFingerGesture = event.changes.size >= 2
            if ((twoFingerOnly && isTwoFingerGesture) || !twoFingerOnly) {
              scale.floatValue *= event.calculateZoom()
              scale.floatValue = max(min(scale.floatValue, maxScale), minScale)
              coroutineScope.launch { pagerState?.setScrolling(false) }
              val offset = event.calculatePan()
              offsetX.floatValue += offset.x
              offsetY.floatValue += offset.y

              // Consume the event so the parent Pager does not receive it
              if (twoFingerOnly) {
                event.changes.forEach { it.consume() }
              }

              coroutineScope.launch { pagerState?.setScrolling(true) }
              onTransformed(offsetX.floatValue, offsetY.floatValue, scale.floatValue)
            }
          } while (event.changes.any { it.pressed })
        }
      }
    } else {
      // Return an empty modifier if disabled, effectively disabling interaction
      Modifier
    }

  Box(
    contentAlignment = Alignment.Center,
    // `Color.Transparent` is intentional: this composable never imposes its own backdrop, so the
    // caller's surface / theme (light or dark) always shows through.
    modifier = modifier.background(Color.Transparent).then(gestureModifier),
  ) {
    Image(
      bitmap = bitmap,
      // No caller-supplied description is available at this layer; announce the interaction
      // affordance so screen reader users know the image can be zoomed.
      contentDescription = "Zoomable image",
      contentScale = contentScale,
      modifier =
        Modifier.align(Alignment.Center)
          .semantics {
            stateDescription = "Zoom level ${(scale.floatValue * 100).roundToInt()} percent"
          }
          .graphicsLayer {
            // Clamp once and reuse for both axes, since scaleX/scaleY are always equal here.
            val displayScale = scale.floatValue.coerceIn(minScale, maxScale)
            scaleX = displayScale
            scaleY = displayScale
            translationX = offsetX.floatValue
            translationY = offsetY.floatValue
          },
    )
  }
}

/**
 * An extension function on [PagerState] to temporarily disable or enable scrolling.
 *
 * This function uses a [MutatePriority.PreventUserInput] scroll block to ensure that no other
 * scrolls (like the user swiping) can happen while this block is active.
 */
suspend fun PagerState.setScrolling(value: Boolean) {
  scroll(scrollPriority = MutatePriority.PreventUserInput) {
    when (value) {
      true -> Unit
      else -> awaitCancellation()
    }
  }
}
