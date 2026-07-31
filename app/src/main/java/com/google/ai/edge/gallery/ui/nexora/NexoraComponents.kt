package com.nexvora.ai.ui.nexora

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexvora.ai.ui.theme.customColors

/**
 * Reusable Glassmorphism Card with gradient borders and rounded corners.
 */
@Composable
fun NexoraGlassCard(
  modifier: Modifier = Modifier,
  cornerRadius: Dp = 24.dp,
  onClick: (() -> Unit)? = null,
  content: @Composable () -> Unit,
) {
  val shape = RoundedCornerShape(cornerRadius)
  val borderBrush = Brush.linearGradient(
    colors = listOf(
      MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
      MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
      Color.Transparent
    )
  )

  Surface(
    modifier = modifier
      .clip(shape)
      .border(width = 1.dp, brush = borderBrush, shape = shape)
      .then(
        if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
      ),
    shape = shape,
    color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.75f),
    tonalElevation = 6.dp,
  ) {
    content()
  }
}

/**
 * Shimmer modifier for skeleton loading states.
 */
@Composable
fun Modifier.nexoraShimmer(): Modifier {
  val transition = rememberInfiniteTransition(label = "shimmer")
  val translateAnim by transition.animateFloat(
    initialValue = 0f,
    targetValue = 1000f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "shimmerTranslation"
  )

  val shimmerColors = listOf(
    MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f),
    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
    MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f),
  )

  val brush = Brush.linearGradient(
    colors = shimmerColors,
    start = Offset(translateAnim - 200f, translateAnim - 200f),
    end = Offset(translateAnim, translateAnim)
  )

  return this.background(brush)
}

/**
 * Skeleton card placeholder used during loading operations.
 */
@Composable
fun NexoraSkeletonCard(modifier: Modifier = Modifier) {
  NexoraGlassCard(modifier = modifier) {
    Column(modifier = Modifier.padding(16.dp)) {
      Box(
        modifier = Modifier
          .fillMaxWidth(0.4f)
          .height(16.dp)
          .clip(RoundedCornerShape(8.dp))
          .nexoraShimmer()
      )
      Spacer(modifier = Modifier.height(12.dp))
      Box(
        modifier = Modifier
          .fillMaxWidth(0.85f)
          .height(12.dp)
          .clip(RoundedCornerShape(6.dp))
          .nexoraShimmer()
      )
      Spacer(modifier = Modifier.height(8.dp))
      Box(
        modifier = Modifier
          .fillMaxWidth(0.6f)
          .height(12.dp)
          .clip(RoundedCornerShape(6.dp))
          .nexoraShimmer()
      )
    }
  }
}

/**
 * Dynamic Island style action panel at bottom of the Home screen.
 */
@Composable
fun NexoraDynamicIsland(
  onActionClick: (String) -> Unit,
  onFabClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val shape = RoundedCornerShape(32.dp)
  val borderBrush = Brush.linearGradient(
    colors = listOf(
      MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
      MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f)
    )
  )

  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 12.dp),
    contentAlignment = Alignment.Center
  ) {
    Surface(
      shape = shape,
      color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.92f),
      tonalElevation = 12.dp,
      modifier = Modifier
        .clip(shape)
        .border(width = 1.dp, brush = borderBrush, shape = shape)
    ) {
      Row(
        modifier = Modifier
          .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        DynamicIslandItem(
          icon = Icons.Rounded.ChatBubbleOutline,
          label = "Chat",
          onClick = { onActionClick("ai_chat") }
        )
        DynamicIslandItem(
          icon = Icons.Rounded.Image,
          label = "Vision",
          onClick = { onActionClick("ai_image") }
        )
        DynamicIslandItem(
          icon = Icons.Rounded.GraphicEq,
          label = "Voice",
          onClick = { onActionClick("ai_voice") }
        )
        DynamicIslandItem(
          icon = Icons.Rounded.Code,
          label = "Code",
          onClick = { onActionClick("ai_code") }
        )
        DynamicIslandItem(
          icon = Icons.Rounded.Translate,
          label = "Translate",
          onClick = { onActionClick("ai_translate") }
        )

        Spacer(modifier = Modifier.width(8.dp))

        FloatingActionButton(
          onClick = onFabClick,
          shape = CircleShape,
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary,
          elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
          modifier = Modifier.size(44.dp)
        ) {
          Icon(
            imageVector = Icons.Rounded.AutoAwesome,
            contentDescription = "Nexora AI Assistant",
            modifier = Modifier.size(22.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun DynamicIslandItem(
  icon: ImageVector,
  label: String,
  onClick: () -> Unit,
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
    modifier = Modifier
      .clip(CircleShape)
      .clickable(onClick = onClick)
      .padding(horizontal = 8.dp, vertical = 4.dp)
  ) {
    Icon(
      imageVector = icon,
      contentDescription = label,
      tint = MaterialTheme.colorScheme.onSurface,
      modifier = Modifier.size(20.dp)
    )
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}
