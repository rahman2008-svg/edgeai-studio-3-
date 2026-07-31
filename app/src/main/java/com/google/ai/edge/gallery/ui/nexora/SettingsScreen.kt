package com.nexvora.ai.ui.nexora

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.FolderZip
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  onNavigateBack: () -> Unit,
  onNavigateAbout: () -> Unit = {},
  modifier: Modifier = Modifier,
) {
  var darkTheme by remember { mutableStateOf(true) }
  var accentColorIndex by remember { mutableStateOf(0) } // 0: Neon Cyan, 1: Electric Blue, 2: Cyber Purple
  var gpuAcceleration by remember { mutableStateOf(true) }
  var offlineMode by remember { mutableStateOf(true) }
  var autoStorageClean by remember { mutableStateOf(true) }
  var cpuThreads by remember { mutableFloatStateOf(4f) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            "Nexora Dashboard & Settings",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
          )
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.background
        )
      )
    }
  ) { padding ->
    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(padding)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Text(
        "Control Center",
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp
        ),
        color = MaterialTheme.colorScheme.primary
      )

      // 1. Appearance Card
      DashboardCard(
        title = "Appearance",
        subtitle = "Theme, Accent Color, Wallpaper",
        icon = Icons.Rounded.Palette,
        iconGradient = listOf(Color(0xFF00F0FF), Color(0xFF2563EB))
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          SettingSwitchRow(
            title = "Futuristic Dark Mode",
            subtitle = "Enable high contrast OLED black theme",
            icon = Icons.Rounded.DarkMode,
            checked = darkTheme,
            onCheckedChange = { darkTheme = it }
          )

          Text(
            "Accent Color Palette",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )

          Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            ColorChipItem(
              label = "Neon Cyan",
              color = Color(0xFF00F0FF),
              selected = accentColorIndex == 0,
              onClick = { accentColorIndex = 0 },
              modifier = Modifier.weight(1f)
            )
            ColorChipItem(
              label = "Electric Blue",
              color = Color(0xFF2563EB),
              selected = accentColorIndex == 1,
              onClick = { accentColorIndex = 1 },
              modifier = Modifier.weight(1f)
            )
            ColorChipItem(
              label = "Cyber Purple",
              color = Color(0xFF8B5CF6),
              selected = accentColorIndex == 2,
              onClick = { accentColorIndex = 2 },
              modifier = Modifier.weight(1f)
            )
          }
        }
      }

      // 2. AI Models Card
      DashboardCard(
        title = "AI Models",
        subtitle = "Installed Models, Download Models, Storage",
        icon = Icons.Rounded.Psychology,
        iconGradient = listOf(Color(0xFF8B5CF6), Color(0xFFEC4899))
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          DashboardSubRow(
            icon = Icons.Rounded.AutoAwesome,
            title = "Installed Local Models",
            value = "3 Active Models (Gemma 4, LiteRT)"
          )
          DashboardSubRow(
            icon = Icons.Rounded.CloudDownload,
            title = "Available Cloud Models",
            value = "12 Models in Repository"
          )
          DashboardSubRow(
            icon = Icons.Rounded.Storage,
            title = "Model Storage Usage",
            value = "4.2 GB of 128 GB Used"
          )
        }
      }

      // 3. Downloads Card
      DashboardCard(
        title = "Downloads & Imports",
        subtitle = "Downloaded Models, Import GGUF, Import LiteRT",
        icon = Icons.Rounded.Download,
        iconGradient = listOf(Color(0xFF10B981), Color(0xFF059669))
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          DashboardSubRow(
            icon = Icons.Rounded.FolderZip,
            title = "Import Custom GGUF Weights",
            value = "Select local .gguf file from device"
          )
          DashboardSubRow(
            icon = Icons.Rounded.Build,
            title = "Import LiteRT Task Binary",
            value = "Load custom .tflite / .task binary"
          )
          SettingSwitchRow(
            title = "Auto-Clean Cache",
            subtitle = "Delete temporary download artifacts automatically",
            icon = Icons.Rounded.Storage,
            checked = autoStorageClean,
            onCheckedChange = { autoStorageClean = it }
          )
        }
      }

      // 4. Performance Card
      DashboardCard(
        title = "Performance Engine",
        subtitle = "Memory, NPU/GPU Acceleration, CPU Threads",
        icon = Icons.Rounded.Speed,
        iconGradient = listOf(Color(0xFFF59E0B), Color(0xFFEF4444))
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          SettingSwitchRow(
            title = "Hardware Acceleration (NPU/GPU)",
            subtitle = "Boost inference speed with Vulkan & OpenCL delegates",
            icon = Icons.Rounded.Memory,
            checked = gpuAcceleration,
            onCheckedChange = { gpuAcceleration = it }
          )

          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                "CPU Thread Allocation",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                "${cpuThreads.toInt()} Threads",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
              )
            }
            Slider(
              value = cpuThreads,
              onValueChange = { cpuThreads = it },
              valueRange = 1f..8f,
              steps = 6
            )
          }
        }
      }

      // 5. Privacy Card
      DashboardCard(
        title = "Privacy & Security",
        subtitle = "Permissions, 100% Offline Mode, Encryption",
        icon = Icons.Rounded.Shield,
        iconGradient = listOf(Color(0xFF06B6D4), Color(0xFF3B82F6))
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          SettingSwitchRow(
            title = "100% Offline Mode",
            subtitle = "Prevent all network calls to guarantee zero data leakage",
            icon = Icons.Rounded.Security,
            checked = offlineMode,
            onCheckedChange = { offlineMode = it }
          )
          DashboardSubRow(
            icon = Icons.Rounded.Shield,
            title = "Local Encrypted Database",
            value = "Room SQLite with AES-256 state protection"
          )
        }
      }

      // 6. About Nexora AI Card
      DashboardCard(
        title = "About Nexora AI",
        subtitle = "Version, Developer, Company, Licenses",
        icon = Icons.Rounded.Info,
        iconGradient = listOf(Color(0xFF6366F1), Color(0xFFA855F7)),
        onClick = onNavigateAbout
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          DashboardSubRow(
            icon = Icons.Rounded.Info,
            title = "Nexora AI Version",
            value = "v1.0.0 (Official Release)"
          )
          DashboardSubRow(
            icon = Icons.Rounded.Build,
            title = "Lead Developer",
            value = "Prince AR Abdur Rahman"
          )
          DashboardSubRow(
            icon = Icons.Rounded.AutoAwesome,
            title = "Publisher Company",
            value = "NexVora Lab's Ofc"
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun DashboardCard(
  title: String,
  subtitle: String,
  icon: ImageVector,
  iconGradient: List<Color>,
  onClick: (() -> Unit)? = null,
  content: @Composable () -> Unit,
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(28.dp))
      .background(
        Brush.linearGradient(
          colors = listOf(
            MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.85f),
            MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.95f)
          )
        )
      )
      .border(
        1.dp,
        Brush.linearGradient(
          colors = listOf(
            iconGradient.first().copy(alpha = 0.4f),
            iconGradient.last().copy(alpha = 0.15f)
          )
        ),
        RoundedCornerShape(28.dp)
      )
      .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
      .padding(18.dp)
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(Brush.linearGradient(iconGradient)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              icon,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(22.dp)
            )
          }

          Spacer(modifier = Modifier.width(14.dp))

          Column {
            Text(
              title,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              subtitle,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        if (onClick != null) {
          Icon(
            Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      content()
    }
  }
}

@Composable
private fun SettingSwitchRow(
  title: String,
  subtitle: String,
  icon: ImageVector,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.weight(1f)
    ) {
      Icon(
        icon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(20.dp)
      )
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(
          title,
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          subtitle,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = Color.White,
        checkedTrackColor = MaterialTheme.colorScheme.primary
      )
    )
  }
}

@Composable
private fun DashboardSubRow(
  icon: ImageVector,
  title: String,
  value: String,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.weight(1f)
    ) {
      Icon(
        icon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(18.dp)
      )
      Spacer(modifier = Modifier.width(10.dp))
      Text(
        title,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface
      )
    }
    Text(
      value,
      style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
      color = MaterialTheme.colorScheme.primary
    )
  }
}

@Composable
private fun ColorChipItem(
  label: String,
  color: Color,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .background(if (selected) color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface)
      .border(
        if (selected) 2.dp else 1.dp,
        if (selected) color else MaterialTheme.colorScheme.outlineVariant,
        RoundedCornerShape(14.dp)
      )
      .clickable { onClick() }
      .padding(vertical = 8.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(12.dp)
          .clip(CircleShape)
          .background(color)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        label,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}
