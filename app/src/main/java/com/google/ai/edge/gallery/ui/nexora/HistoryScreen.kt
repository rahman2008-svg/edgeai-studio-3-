package com.nexvora.ai.ui.nexora

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class HistoryItem(
  val id: String,
  val title: String,
  val preview: String,
  val date: String,
  val type: String, // Chat, Image, Voice, Code, Document
  val icon: ImageVector,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
  onNavigateBack: () -> Unit,
  onItemClick: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedFilter by remember { mutableStateOf("All") }

  val historyList = remember {
    mutableStateListOf(
      HistoryItem(
        "1",
        "Gemma 4 Architecture Breakdown",
        "How do E2B and E4B handle multi-modal inputs locally?",
        "Today, 10:42 AM",
        "Chat",
        Icons.Rounded.ChatBubbleOutline
      ),
      HistoryItem(
        "2",
        "Kotlin Compose UI Layout Analysis",
        "Generated custom Glassmorphic card code for Nexora dashboard",
        "Yesterday",
        "Code",
        Icons.Rounded.Code
      ),
      HistoryItem(
        "3",
        "Futuristic AI Logo Vision Query",
        "Analyzed 3D neon emblem geometry and gradient color balance",
        "Jul 27",
        "Image",
        Icons.Rounded.Image
      ),
      HistoryItem(
        "4",
        "Voice Note Transcription & Summary",
        "Transcribed 5 minute meeting note with action item bullet points",
        "Jul 25",
        "Voice",
        Icons.Rounded.GraphicEq
      ),
      HistoryItem(
        "5",
        "LiteRT On-Device Performance Whitepaper",
        "Summarized top 3 key benchmarks on Android Snapdragon NPU",
        "Jul 22",
        "Document",
        Icons.Rounded.Description
      )
    )
  }

  val filters = listOf("All", "Chat", "Code", "Image", "Voice", "Document")

  val filteredItems = historyList.filter { item ->
    (selectedFilter == "All" || item.type == selectedFilter) &&
      (searchQuery.isEmpty() || item.title.contains(searchQuery, ignoreCase = true) || item.preview.contains(searchQuery, ignoreCase = true))
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            "Session History",
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
        .padding(horizontal = 20.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Search Box
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("Search previous prompts & sessions...") },
        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
      )

      // Filter Chips Row
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(filters) { filter ->
          FilterChip(
            selected = selectedFilter == filter,
            onClick = { selectedFilter = filter },
            label = { Text(filter) }
          )
        }
      }

      if (filteredItems.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              Icons.Rounded.History,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.outline,
              modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              "No sessions found",
              style = MaterialTheme.typography.titleMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      } else {
        LazyColumn(
          verticalArrangement = Arrangement.spacedBy(12.dp),
          modifier = Modifier.weight(1f)
        ) {
          items(filteredItems) { item ->
            HistoryCardItem(
              item = item,
              onClick = { onItemClick(item.id) },
              onDelete = { historyList.remove(item) }
            )
          }
        }
      }
    }
  }
}

@Composable
private fun HistoryCardItem(
  item: HistoryItem,
  onClick: () -> Unit,
  onDelete: () -> Unit,
) {
  NexoraGlassCard(
    modifier = Modifier.fillMaxWidth(),
    onClick = onClick
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            item.icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(22.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              item.title,
              style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface,
              maxLines = 1
            )
          }
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            item.preview,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            "${item.date} • ${item.type}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
          )
        }
      }

      IconButton(onClick = onDelete) {
        Icon(
          Icons.Rounded.DeleteOutline,
          contentDescription = "Delete",
          tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
        )
      }
    }
  }
}
