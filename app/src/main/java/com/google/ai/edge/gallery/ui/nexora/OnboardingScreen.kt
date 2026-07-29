package com.google.ai.edge.gallery.ui.nexora

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.ai.edge.gallery.R
import kotlin.random.Random

data class OnboardingPageData(
  val headline: String,
  val subtitle: String,
  val imageRes: Int,
)

@Composable
fun OnboardingScreen(
  onFinishOnboarding: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var currentPage by remember { mutableIntStateOf(0) }

  val pages = listOf(
    OnboardingPageData(
      headline = "Your Private AI Workspace",
      subtitle = "Run powerful AI models locally.\nChat, Code, Vision, Voice and Documents\nwithout compromising privacy.",
      imageRes = R.drawable.nexora_hologram_art_1785310074948
    ),
    OnboardingPageData(
      headline = "Multi-Modal Intelligence",
      subtitle = "Seamlessly analyze images, write code, translate languages, and process audio completely offline.",
      imageRes = R.drawable.nexora_welcome_hero_1785308843185
    ),
    OnboardingPageData(
      headline = "Next-Gen Gemma 4 Engine",
      subtitle = "Accelerated on-device LiteRT architecture built for low latency, high throughput, and zero latency.",
      imageRes = R.drawable.img_nexora_hero_1785307910713
    )
  )

  // Pulse & scale infinite transitions for animated top logo
  val infiniteTransition = rememberInfiniteTransition(label = "pulseLogo")
  val logoScale by infiniteTransition.animateFloat(
    initialValue = 0.95f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "logoScale"
  )

  val particleOffset by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(8000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "particleOffset"
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            Color(0xFF070B19),
            Color(0xFF0F172A),
            Color(0xFF020617)
          )
        )
      )
  ) {
    // Floating Particles Canvas Background
    Canvas(modifier = Modifier.fillMaxSize()) {
      val canvasWidth = size.width
      val canvasHeight = size.height
      repeat(20) { i ->
        val radius = ((i * 7) % 12 + 4).dp.toPx()
        val x = (canvasWidth * ((i * 17) % 100) / 100f)
        val y = ((canvasHeight * ((i * 23 + particleOffset.toInt()) % 100) / 100f))
        drawCircle(
          color = if (i % 2 == 0) Color(0xFF00F0FF).copy(alpha = 0.18f) else Color(0xFF8B5CF6).copy(alpha = 0.18f),
          radius = radius,
          center = Offset(x, y)
        )
      }
    }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Top: Animated Nexora AI Logo & Skip Button
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .scale(logoScale)
              .size(44.dp)
              .clip(CircleShape)
              .background(
                Brush.linearGradient(
                  colors = listOf(
                    Color(0xFF00F0FF),
                    Color(0xFF2563EB),
                    Color(0xFF7C3AED)
                  )
                )
              )
              .padding(2.dp),
            contentAlignment = Alignment.Center
          ) {
            Image(
              painter = painterResource(id = R.drawable.nexora_brand_logo_1785310056364),
              contentDescription = "Animated Nexora Logo",
              modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape),
              contentScale = ContentScale.Crop
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Text(
            "Nexora AI",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            ),
            color = Color.White
          )
        }

        if (currentPage < pages.size - 1) {
          TextButton(onClick = onFinishOnboarding) {
            Text(
              "Skip",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
              color = Color.White.copy(alpha = 0.7f)
            )
          }
        }
      }

      // Center Content & Artwork
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.Center
      ) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(260.dp)
            .clip(RoundedCornerShape(32.dp))
            .border(
              1.dp,
              Brush.linearGradient(
                colors = listOf(
                  Color(0xFF00F0FF).copy(alpha = 0.5f),
                  Color(0xFF8B5CF6).copy(alpha = 0.5f)
                )
              ),
              RoundedCornerShape(32.dp)
            )
        ) {
          AnimatedContent(
            targetState = currentPage,
            transitionSpec = {
              (fadeIn() + scaleIn(initialScale = 0.9f)).togetherWith(fadeOut() + scaleOut(targetScale = 1.1f))
            },
            label = "illustrationTransition"
          ) { pageIdx ->
            Image(
              painter = painterResource(id = pages[pageIdx].imageRes),
              contentDescription = "Nexora Artwork",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
          }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Headline and Subtitle
        AnimatedContent(
          targetState = currentPage,
          transitionSpec = {
            (fadeIn(animationSpec = tween(400)) + scaleIn(initialScale = 0.95f)).togetherWith(
              fadeOut(animationSpec = tween(200))
            )
          },
          label = "textTransition"
        ) { index ->
          val page = pages[index]
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 8.dp)
          ) {
            Text(
              text = page.headline,
              style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
              color = Color.White,
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
              text = page.subtitle,
              style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
              color = Color.White.copy(alpha = 0.8f),
              textAlign = TextAlign.Center
            )
          }
        }
      }

      // Bottom Page Indicator & Buttons
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
      ) {
        // Page Indicator
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(bottom = 24.dp)
        ) {
          repeat(pages.size) { index ->
            Box(
              modifier = Modifier
                .height(8.dp)
                .width(if (index == currentPage) 28.dp else 8.dp)
                .clip(CircleShape)
                .background(
                  if (index == currentPage)
                    Color(0xFF00F0FF)
                  else
                    Color.White.copy(alpha = 0.25f)
                )
            )
          }
        }

        // Action Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          if (currentPage > 0) {
            OutlinedButton(
              onClick = { currentPage-- },
              shape = RoundedCornerShape(20.dp),
              modifier = Modifier
                .weight(1f)
                .height(56.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
            ) {
              Text(
                "Back",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Color.White
              )
            }
          }

          Button(
            onClick = {
              if (currentPage < pages.size - 1) {
                currentPage++
              } else {
                onFinishOnboarding()
              }
            },
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFF2563EB),
              contentColor = Color.White
            ),
            modifier = Modifier
              .weight(2f)
              .height(56.dp)
          ) {
            Text(
              if (currentPage == pages.size - 1) "Get Started" else "Continue",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null)
          }
        }
      }
    }
  }
}

