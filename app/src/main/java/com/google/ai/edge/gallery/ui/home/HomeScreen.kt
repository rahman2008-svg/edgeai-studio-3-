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

package com.google.ai.edge.gallery.ui.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Chat
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.ai.edge.gallery.R
import com.google.ai.edge.gallery.data.BuiltInTaskId
import com.google.ai.edge.gallery.data.Category
import com.google.ai.edge.gallery.data.CategoryInfo
import com.google.ai.edge.gallery.data.Task
import com.google.ai.edge.gallery.ui.common.TaskIcon
import com.google.ai.edge.gallery.ui.common.tos.AppTosDialog
import com.google.ai.edge.gallery.ui.common.tos.TosViewModel
import com.google.ai.edge.gallery.ui.modelmanager.ModelManagerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "NexoraHomeScreen"

/** Navigation destination data */
private object HomeScreenDestination {
  @StringRes val titleRes = R.string.app_name
}

private val PREDEFINED_CATEGORY_ORDER = listOf(Category.LLM.id, Category.EXPERIMENTAL.id)

// Nexora AI brand colors.
private val NexoraPrimary = Color(0xFF2563EB)
private val NexoraSecondary = Color(0xFF7C3AED)
private val NexoraCorner = 24.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  modelManagerViewModel: ModelManagerViewModel,
  tosViewModel: TosViewModel,
  navigateToTaskScreen: (Task) -> Unit,
  onModelsClicked: () -> Unit,
  onNotificationsClicked: () -> Unit,
  enableAnimation: Boolean,
  modifier: Modifier = Modifier,
  gm4: Boolean = false,
) {
  val uiState by modelManagerViewModel.uiState.collectAsState()
  var showSettingsDialog by remember { mutableStateOf(false) }
  var showTosDialog by remember { mutableStateOf(!tosViewModel.getIsTosAccepted()) }
  val scope = rememberCoroutineScope()
  val context = LocalContext.current
  val isDevBuild = context.packageName.endsWith(".dev")

  val tasks = uiState.tasks

  val categoryMap: Map<String, CategoryInfo> =
    remember(tasks) { tasks.associateBy { it.category.id }.mapValues { it.value.category } }
  val sortedCategories =
    remember(categoryMap) {
      categoryMap.keys
        .toList()
        .sortedWith { a, b ->
          val indexA = PREDEFINED_CATEGORY_ORDER.indexOf(a)
          val indexB = PREDEFINED_CATEGORY_ORDER.indexOf(b)
          if (indexA != -1 && indexB != -1) {
            indexA.compareTo(indexB)
          } else if (indexA != -1) {
            -1
          } else if (indexB != -1) {
            1
          } else {
            val ca = categoryMap[a]!!
            val cb = categoryMap[b]!!
            getCategoryLabel(context, ca).compareTo(getCategoryLabel(context, cb))
          }
        }
        .map { categoryMap[it]!! }
    }

  // Chat shortcut tasks (used for FAB + Recent Chats + bottom nav). These come from the same
  // built-in task registry the original screen used — there's no chat-history store wired in
  // this file, so "Recent Chats" below is a shortcut list, not real conversation history yet.
  val chatTasks =
    remember(tasks) {
      listOfNotNull(
          modelManagerViewModel.getTaskById(BuiltInTaskId.LLM_CHAT),
          modelManagerViewModel.getTaskById(BuiltInTaskId.LLM_AGENT_CHAT),
        )
        .distinct()
    }
  val primaryChatTask = chatTasks.firstOrNull()

  var searchQuery by remember { mutableStateOf("") }

  if (!showTosDialog) {
    var loadingModelAllowlistDelayed by remember { mutableStateOf(false) }
    LaunchedEffect(uiState.loadingModelAllowlist) {
      if (uiState.loadingModelAllowlist) {
        delay(200)
        if (uiState.loadingModelAllowlist) {
          loadingModelAllowlistDelayed = true
        }
      } else {
        loadingModelAllowlistDelayed = false
      }
    }

    if (loadingModelAllowlistDelayed) {
      Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
      ) {
        CircularProgressIndicator(
          trackColor = MaterialTheme.colorScheme.surfaceVariant,
          strokeWidth = 3.dp,
          modifier = Modifier.padding(end = 8.dp).size(20.dp),
        )
        Text(
          stringResource(R.string.loading_model_list),
          style = MaterialTheme.typography.bodyMedium,
        )
      }
    }

    if (!loadingModelAllowlistDelayed && !uiState.loadingModelAllowlist) {
      val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

      val requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { _ -> }

      LaunchedEffect(Unit) {
        delay(2000)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
          if (
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
              PackageManager.PERMISSION_GRANTED
          ) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
          }
        }
      }

      BackHandler(drawerState.isOpen) { scope.launch { drawerState.close() } }

      ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
          ModalDrawerSheet {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(modifier = Modifier.fillMaxWidth()) {
                SquareDrawerItem(
                  label = stringResource(R.string.drawer_settings_label),
                  description = stringResource(R.string.drawer_settings_description),
                  icon = Icons.Rounded.Settings,
                  onClick = {
                    showSettingsDialog = true
                    scope.launch { drawerState.close() }
                  },
                  modifier = Modifier.weight(1f),
                  iconBrush = Brush.linearGradient(colors = listOf(NexoraPrimary, NexoraSecondary)),
                )
                Spacer(modifier = Modifier.width(16.dp))
                SquareDrawerItem(
                  label = stringResource(R.string.drawer_models_label),
                  description = stringResource(R.string.drawer_models_description),
                  icon = Icons.Rounded.Storage,
                  onClick = {
                    scope.launch { drawerState.close() }
                    scope.launch {
                      delay(50)
                      onModelsClicked()
                    }
                  },
                  modifier = Modifier.weight(1f),
                  iconBrush = Brush.linearGradient(colors = listOf(NexoraSecondary, NexoraPrimary)),
                )
              }
            }
          }
        },
        gesturesEnabled = drawerState.isOpen,
      ) {
        Scaffold(
          containerColor = MaterialTheme.colorScheme.background,
          floatingActionButton = {
            FloatingActionButton(
              onClick = { primaryChatTask?.let { navigateToTaskScreen(it) } },
              containerColor = NexoraPrimary,
              contentColor = Color.White,
              shape = RoundedCornerShape(NexoraCorner),
            ) {
              Icon(Icons.Rounded.Add, contentDescription = "New chat")
            }
          },
          bottomBar = {
            NexoraBottomNav(
              onHome = {},
              onChat = { primaryChatTask?.let { navigateToTaskScreen(it) } },
              onModels = onModelsClicked,
              onSettings = { showSettingsDialog = true },
            )
          },
        ) { innerPadding ->
          Box(
            modifier =
              Modifier.fillMaxSize()
                .background(
                  Brush.verticalGradient(
                    colors =
                      listOf(
                        NexoraPrimary.copy(alpha = 0.10f),
                        NexoraSecondary.copy(alpha = 0.06f),
                        MaterialTheme.colorScheme.background,
                      )
                  )
                )
                .padding(top = innerPadding.calculateTopPadding()),
          ) {
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
              NexoraHeader(
                onMenuClick = {
                  scope.launch { drawerState.apply { if (isClosed) open() else close() } }
                },
                onNotificationsClicked = onNotificationsClicked,
              )

              NexoraSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
              )

              QuickActionsRow(
                onNewChat = { primaryChatTask?.let { navigateToTaskScreen(it) } },
                onBrowseModels = onModelsClicked,
                onSettings = { showSettingsDialog = true },
                onNotifications = onNotificationsClicked,
              )

              StatisticsRow(
                taskCount = tasks.size,
                categoryCount = sortedCategories.size,
                modelCount = tasks.sumOf { it.models.size },
              )

              if (tasks.isNotEmpty()) {
                FeaturedModelsCarousel(
                  tasks = tasks.filter { it.newFeature }.ifEmpty { tasks.take(5) },
                  navigateToTaskScreen = navigateToTaskScreen,
                )
              }

              if (chatTasks.isNotEmpty()) {
                RecentChatsSection(chatTasks = chatTasks, navigateToTaskScreen = navigateToTaskScreen)
              }

              AIModelsSection(
                searchQuery = searchQuery,
                allTasks = tasks,
                sortedCategories = sortedCategories,
                tasksByCategories = uiState.tasksByCategory,
                navigateToTaskScreen = navigateToTaskScreen,
              )

              LibrarySummarySection(
                modelCount = tasks.sumOf { it.models.size },
                taskCount = tasks.size,
              )

              Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding() + 96.dp))
            }
          }
        }
      }
    }
  }

  if (showTosDialog) {
    AppTosDialog(
      onTosAccepted = {
        showTosDialog = false
        tosViewModel.acceptTos()
      }
    )
  }

  if (showSettingsDialog) {
    SettingsDialog(
      curThemeOverride = modelManagerViewModel.readThemeOverride(),
      curFirebaseAnalytics = modelManagerViewModel.readFirebaseAnalytics(),
      modelManagerViewModel = modelManagerViewModel,
      onDismissed = { showSettingsDialog = false },
    )
  }

  if (uiState.loadingModelAllowlistError.isNotEmpty()) {
    AlertDialog(
      icon = {
        Icon(
          Icons.Rounded.Error,
          contentDescription = stringResource(R.string.cd_error),
          tint = MaterialTheme.colorScheme.error,
        )
      },
      title = { Text(uiState.loadingModelAllowlistError) },
      text = { Text(stringResource(R.string.error_internet_connection)) },
      onDismissRequest = { modelManagerViewModel.loadModelAllowlist() },
      confirmButton = {
        TextButton(onClick = { modelManagerViewModel.loadModelAllowlist() }) {
          Text(stringResource(R.string.retry))
        }
      },
      dismissButton = {
        TextButton(onClick = { modelManagerViewModel.clearLoadModelAllowlistError() }) {
          Text(stringResource(R.string.cancel))
        }
      },
    )
  }
}

// ---------------------------------------------------------------------------
// Header: welcome text + Nexora AI branding + menu / notifications.
// ---------------------------------------------------------------------------
@Composable
private fun NexoraHeader(onMenuClick: () -> Unit, onNotificationsClicked: () -> Unit) {
  Row(
    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    IconButton(onClick = onMenuClick) { Icon(Icons.Rounded.Menu, contentDescription = "Menu") }
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
      Text(
        text = "Welcome back",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Text(
        text = "Nexora AI",
        style =
          MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Bold,
            brush = Brush.linearGradient(colors = listOf(NexoraPrimary, NexoraSecondary)),
          ),
      )
    }
    IconButton(onClick = onNotificationsClicked) {
      Icon(Icons.Rounded.Notifications, contentDescription = "Notifications")
    }
  }
}

// ---------------------------------------------------------------------------
// Search bar.
// ---------------------------------------------------------------------------
@Composable
private fun NexoraSearchBar(
  query: String,
  onQueryChange: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  OutlinedTextField(
    value = query,
    onValueChange = onQueryChange,
    modifier = modifier.fillMaxWidth(),
    placeholder = { Text("Search models, chats, tasks...") },
    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
    trailingIcon = {
      if (query.isNotEmpty()) {
        IconButton(onClick = { onQueryChange("") }) {
          Icon(Icons.Rounded.Close, contentDescription = "Clear")
        }
      }
    },
    singleLine = true,
    shape = RoundedCornerShape(NexoraCorner),
    colors =
      TextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
      ),
  )
}

// ---------------------------------------------------------------------------
// Quick actions.
// ---------------------------------------------------------------------------
@Composable
private fun QuickActionsRow(
  onNewChat: () -> Unit,
  onBrowseModels: () -> Unit,
  onSettings: () -> Unit,
  onNotifications: () -> Unit,
) {
  LazyRow(
    contentPadding = PaddingValues(horizontal = 20.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    modifier = Modifier.padding(bottom = 8.dp),
  ) {
    item { QuickActionChip("New Chat", Icons.Rounded.Chat, NexoraPrimary, onNewChat) }
    item { QuickActionChip("Models", Icons.Rounded.AutoAwesome, NexoraSecondary, onBrowseModels) }
    item { QuickActionChip("Settings", Icons.Rounded.Settings, NexoraPrimary, onSettings) }
    item { QuickActionChip("Alerts", Icons.Rounded.Notifications, NexoraSecondary, onNotifications) }
  }
}

@Composable
private fun QuickActionChip(
  label: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  color: Color,
  onClick: () -> Unit,
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier =
      Modifier.width(76.dp).clip(RoundedCornerShape(NexoraCorner)).clickable(onClick = onClick),
  ) {
    Box(
      modifier =
        Modifier.size(56.dp).clip(RoundedCornerShape(18.dp)).background(color.copy(alpha = 0.15f)),
      contentAlignment = Alignment.Center,
    ) {
      Icon(icon, contentDescription = label, tint = color)
    }
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      label,
      style = MaterialTheme.typography.labelMedium,
      color = MaterialTheme.colorScheme.onSurface,
      maxLines = 1,
    )
  }
}

// ---------------------------------------------------------------------------
// Statistics cards.
// ---------------------------------------------------------------------------
@Composable
private fun StatisticsRow(taskCount: Int, categoryCount: Int, modelCount: Int) {
  Row(
    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    StatCard(
      label = "Tasks",
      value = taskCount.toString(),
      icon = Icons.Rounded.BarChart,
      modifier = Modifier.weight(1f),
    )
    StatCard(
      label = "Categories",
      value = categoryCount.toString(),
      icon = Icons.Rounded.AutoAwesome,
      modifier = Modifier.weight(1f),
    )
    StatCard(
      label = "Models",
      value = modelCount.toString(),
      icon = Icons.Rounded.Storage,
      modifier = Modifier.weight(1f),
    )
  }
}

@Composable
private fun StatCard(
  label: String,
  value: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  modifier: Modifier = Modifier,
) {
  Card(
    modifier = modifier.clip(RoundedCornerShape(NexoraCorner)),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Icon(icon, contentDescription = null, tint = NexoraPrimary, modifier = Modifier.size(20.dp))
      Spacer(modifier = Modifier.height(6.dp))
      Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
      Text(
        label,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}

// ---------------------------------------------------------------------------
// Featured models carousel.
// ---------------------------------------------------------------------------
@Composable
private fun FeaturedModelsCarousel(tasks: List<Task>, navigateToTaskScreen: (Task) -> Unit) {
  Column(modifier = Modifier.padding(top = 12.dp)) {
    SectionTitle("Featured Models")
    LazyRow(
      contentPadding = PaddingValues(horizontal = 20.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      items(items = tasks, key = { it.id }) { task ->
        Card(
          modifier =
            Modifier.width(200.dp)
              .clip(RoundedCornerShape(NexoraCorner))
              .clickable { navigateToTaskScreen(task) },
          colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        ) {
          Box(
            modifier =
              Modifier.fillMaxWidth()
                .height(120.dp)
                .background(Brush.linearGradient(colors = listOf(NexoraPrimary, NexoraSecondary)))
                .padding(16.dp),
            contentAlignment = Alignment.BottomStart,
          ) {
            Column {
              Text(
                task.label,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
              )
              Text(
                pluralStringResource(
                  R.plurals.task_card_models_count,
                  task.models.size,
                  task.models.size,
                ),
                color = Color.White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodySmall,
              )
            }
          }
        }
      }
    }
  }
}

// ---------------------------------------------------------------------------
// Recent chats (shortcut-based — see note in HomeScreen()).
// ---------------------------------------------------------------------------
@Composable
private fun RecentChatsSection(chatTasks: List<Task>, navigateToTaskScreen: (Task) -> Unit) {
  Column(modifier = Modifier.padding(top = 20.dp)) {
    SectionTitle("Recent Chats", icon = Icons.Rounded.History)
    Column(
      modifier = Modifier.padding(horizontal = 20.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      for (task in chatTasks) {
        Card(
          modifier =
            Modifier.fillMaxWidth()
              .clip(RoundedCornerShape(NexoraCorner))
              .clickable { navigateToTaskScreen(task) },
          colors =
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        ) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            TaskIcon(task = task, width = 36.dp)
            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
              Text(
                task.label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
              )
              Text(
                "Tap to continue",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
          }
        }
      }
    }
  }
}

// ---------------------------------------------------------------------------
// AI Models section (search-aware; falls back to the original category browsing).
// ---------------------------------------------------------------------------
@Composable
private fun AIModelsSection(
  searchQuery: String,
  allTasks: List<Task>,
  sortedCategories: List<CategoryInfo>,
  tasksByCategories: Map<String, List<Task>>,
  navigateToTaskScreen: (Task) -> Unit,
) {
  Column(modifier = Modifier.padding(top = 20.dp)) {
    SectionTitle("AI Models", icon = Icons.Rounded.AutoAwesome)

    if (searchQuery.isNotBlank()) {
      val filtered = allTasks.filter { it.label.contains(searchQuery, ignoreCase = true) }
      Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        if (filtered.isEmpty()) {
          Text(
            "No models match \"$searchQuery\"",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        } else {
          for (task in filtered) {
            NexoraTaskCard(task = task, onClick = { navigateToTaskScreen(task) })
          }
        }
      }
    } else {
      var selectedIndex by remember { mutableIntStateOf(0) }
      if (sortedCategories.size > 1) {
        NexoraCategoryTabs(
          categories = sortedCategories,
          selectedIndex = selectedIndex,
          onSelected = { selectedIndex = it },
        )
      }
      val currentCategory = sortedCategories.getOrNull(selectedIndex)
      val currentTasks = currentCategory?.let { tasksByCategories[it.id] } ?: emptyList()
      Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        for (task in currentTasks) {
          NexoraTaskCard(task = task, onClick = { navigateToTaskScreen(task) })
        }
      }
    }
  }
}

@Composable
private fun NexoraCategoryTabs(
  categories: List<CategoryInfo>,
  selectedIndex: Int,
  onSelected: (Int) -> Unit,
) {
  val context = LocalContext.current
  LazyRow(
    contentPadding = PaddingValues(horizontal = 20.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    modifier = Modifier.padding(bottom = 12.dp),
  ) {
    items(items = categories) { category ->
      val index = categories.indexOf(category)
      Row(
        modifier =
          Modifier.height(36.dp)
            .clip(CircleShape)
            .background(
              if (selectedIndex != index) MaterialTheme.colorScheme.surfaceContainer
              else Color.Transparent
            )
            .background(
              if (selectedIndex == index)
                Brush.linearGradient(colors = listOf(NexoraPrimary, NexoraSecondary))
              else Brush.linearGradient(colors = listOf(Color.Transparent, Color.Transparent))
            )
            .clickable { onSelected(index) },
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          getCategoryLabel(context, category),
          modifier = Modifier.padding(horizontal = 16.dp),
          style = MaterialTheme.typography.labelLarge,
          color =
            if (selectedIndex == index) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
  }
}

@Composable
private fun NexoraTaskCard(task: Task, onClick: () -> Unit) {
  val modelCount by remember(task) {
    derivedStateOf {
      task.updateTrigger.value
      task.models.size
    }
  }
  Card(
    modifier =
      Modifier.fillMaxWidth().clip(RoundedCornerShape(NexoraCorner)).clickable(onClick = onClick),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        TaskIcon(task = task, width = 40.dp)
        Column(modifier = Modifier.padding(start = 16.dp)) {
          Text(task.label, style = MaterialTheme.typography.titleMedium)
          Text(
            pluralStringResource(R.plurals.task_card_models_count, modelCount, modelCount),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
      if (task.newFeature) {
        Box(
          modifier =
            Modifier.clip(RoundedCornerShape(8.dp))
              .background(NexoraSecondary.copy(alpha = 0.15f))
              .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Text("NEW", color = NexoraSecondary, style = MaterialTheme.typography.labelSmall)
        }
      }
    }
  }
}

// ---------------------------------------------------------------------------
// Library summary (honest stand-in for a "Downloads" section — see note above).
// ---------------------------------------------------------------------------
@Composable
private fun LibrarySummarySection(modelCount: Int, taskCount: Int) {
  Column(modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)) {
    SectionTitle("Your Library")
    Card(
      modifier =
        Modifier.fillMaxWidth().padding(horizontal = 20.dp).clip(RoundedCornerShape(NexoraCorner)),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(Icons.Rounded.Storage, contentDescription = null, tint = NexoraPrimary)
        Column(modifier = Modifier.padding(start = 12.dp)) {
          Text("$modelCount models across $taskCount tasks", style = MaterialTheme.typography.bodyMedium)
          Text(
            "Manage downloads from the Models screen",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
    }
  }
}

@Composable
private fun SectionTitle(
  title: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
) {
  Row(
    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    if (icon != null) {
      Icon(icon, contentDescription = null, tint = NexoraPrimary, modifier = Modifier.size(18.dp))
      Spacer(modifier = Modifier.width(6.dp))
    }
    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
  }
}

// ---------------------------------------------------------------------------
// Bottom navigation.
// ---------------------------------------------------------------------------
@Composable
private fun NexoraBottomNav(
  onHome: () -> Unit,
  onChat: () -> Unit,
  onModels: () -> Unit,
  onSettings: () -> Unit,
) {
  var selected by remember { mutableIntStateOf(0) }
  NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
    NavigationBarItem(
      selected = selected == 0,
      onClick = {
        selected = 0
        onHome()
      },
      icon = { Icon(Icons.Rounded.Home, contentDescription = "Home") },
      label = { Text("Home") },
    )
    NavigationBarItem(
      selected = selected == 1,
      onClick = {
        selected = 1
        onChat()
      },
      icon = { Icon(Icons.Rounded.Chat, contentDescription = "Chat") },
      label = { Text("Chat") },
    )
    NavigationBarItem(
      selected = selected == 2,
      onClick = {
        selected = 2
        onModels()
      },
      icon = { Icon(Icons.Rounded.AutoAwesome, contentDescription = "Models") },
      label = { Text("Models") },
    )
    NavigationBarItem(
      selected = selected == 3,
      onClick = {
        selected = 3
        onSettings()
      },
      icon = { Icon(Icons.Rounded.Settings, contentDescription = "Settings") },
      label = { Text("Settings") },
    )
  }
}

private fun getCategoryLabel(context: Context, category: CategoryInfo): String {
  val stringRes = category.labelStringRes
  val label = category.label
  if (stringRes != null) {
    return context.getString(stringRes)
  } else if (label != null) {
    return label
  }
  return context.getString(R.string.category_unlabeled)
}
