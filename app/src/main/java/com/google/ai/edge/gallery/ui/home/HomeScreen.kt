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

// import androidx.compose.ui.tooling.preview.Preview
// import com.google.ai.edge.gallery.ui.theme.GalleryTheme
// import com.google.ai.edge.gallery.ui.preview.PreviewModelManagerViewModel
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ListAlt
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.ai.edge.gallery.GalleryTopAppBar
import com.google.ai.edge.gallery.R
import com.google.ai.edge.gallery.data.AppBarAction
import com.google.ai.edge.gallery.data.AppBarActionType
import com.google.ai.edge.gallery.data.BuiltInTaskId
import com.google.ai.edge.gallery.data.Category
import com.google.ai.edge.gallery.data.CategoryInfo
import com.google.ai.edge.gallery.data.Task
import com.google.ai.edge.gallery.ui.common.RevealingText
import com.google.ai.edge.gallery.ui.common.SwipingText
import com.google.ai.edge.gallery.ui.common.TaskIcon
import com.google.ai.edge.gallery.ui.common.buildTrackableUrlAnnotatedString
import com.google.ai.edge.gallery.ui.common.rememberDelayedAnimationProgress
import com.google.ai.edge.gallery.ui.common.tos.AppTosDialog
import com.google.ai.edge.gallery.ui.common.tos.TosViewModel
import com.google.ai.edge.gallery.ui.modelmanager.ModelManagerViewModel
import com.google.ai.edge.gallery.ui.theme.customColors
import com.google.ai.edge.gallery.ui.theme.homePageTitleStyle
import androidx.compose.foundation.border
import com.google.ai.edge.gallery.ui.nexora.NexoraGlassCard
import androidx.compose.material.icons.rounded.Article
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Newspaper
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.AssistChip
import androidx.compose.foundation.lazy.items
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "AGHomeScreen"
private const val TASK_COUNT_ANIMATION_DURATION = 250
private const val ANIMATION_INIT_DELAY = 0L
private const val TOP_APP_BAR_ANIMATION_DURATION = 600
private const val TITLE_FIRST_LINE_ANIMATION_DURATION = 600
private const val TITLE_SECOND_LINE_ANIMATION_DURATION = 600
private const val TITLE_SECOND_LINE_ANIMATION_DURATION2 = 800
private const val TITLE_SECOND_LINE_ANIMATION_START =
  ANIMATION_INIT_DELAY + (TITLE_FIRST_LINE_ANIMATION_DURATION * 0.5).toInt()
private const val TASK_LIST_ANIMATION_START = TITLE_SECOND_LINE_ANIMATION_START + 110
private const val TASK_CARD_ANIMATION_DELAY_OFFSET = 100
private const val TASK_CARD_ANIMATION_DURATION = 600
private const val CONTENT_COMPOSABLES_ANIMATION_DURATION = 1200
private const val CONTENT_COMPOSABLES_OFFSET_Y = 16

/** Navigation destination data */
private object HomeScreenDestination {
  @StringRes val titleRes = R.string.app_name
}

private val PREDEFINED_CATEGORY_ORDER = listOf(Category.LLM.id, Category.EXPERIMENTAL.id)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  modelManagerViewModel: ModelManagerViewModel,
  tosViewModel: TosViewModel,
  navigateToTaskScreen: (Task) -> Unit,
  onModelsClicked: () -> Unit,
  onNotificationsClicked: () -> Unit,
  onNavigateProfile: () -> Unit,
  onNavigateSettings: () -> Unit,
  onNavigateHistory: () -> Unit,
  onNavigateStudio: () -> Unit,
  onNavigateDocuments: () -> Unit,
  onNavigateTranslate: () -> Unit,
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

  var tasks = uiState.tasks

  val categoryMap: Map<String, CategoryInfo> =
    remember(tasks) { tasks.associateBy { it.category.id }.mapValues { it.value.category } }
  val sortedCategories =
    remember(categoryMap) {
      categoryMap.keys
        .toList()
        .sortedWith { a, b ->
          val indexA = PREDEFINED_CATEGORY_ORDER.indexOf(a)
          val indexB = PREDEFINED_CATEGORY_ORDER.indexOf(b)
          // Check if both categories are in the predefined order
          if (indexA != -1 && indexB != -1) {
            indexA.compareTo(indexB)
          }
          // Check if only category 'a' is in the predefined order
          else if (indexA != -1) {
            -1
          }
          // Check if only category 'b' is in the predefined order
          else if (indexB != -1) {
            1
          }
          // If neither is in the predefined order, sort by label
          else {
            val ca = categoryMap[a]!!
            val cb = categoryMap[b]!!
            val caLabel = getCategoryLabel(context = context, category = ca)
            val cbLabel = getCategoryLabel(context = context, category = cb)
            caLabel.compareTo(cbLabel)
          }
        }
        .map { categoryMap[it]!! }
    }

  // Show home screen content when TOS has been accepted.
  if (!showTosDialog) {
    // The code below manages the display of the model allowlist loading indicator with a debounced
    // delay. It ensures that a progress indicator is only shown if the loading operation
    // (represented by `uiState.loadingModelAllowlist`) takes longer than 200 milliseconds.
    // If the loading completes within 200ms, the indicator is never shown,
    // preventing a "flicker" and improving the perceived responsiveness of the UI.
    // The `loadingModelAllowlistDelayed` state is used to control the actual
    // visibility of the indicator based on this debounced logic.
    var loadingModelAllowlistDelayed by remember { mutableStateOf(false) }
    // This effect runs whenever uiState.loadingModelAllowlist changes
    LaunchedEffect(uiState.loadingModelAllowlist) {
      if (uiState.loadingModelAllowlist) {
        // If loading starts, wait for 200ms
        delay(200)
        // After 200ms, check if loadingModelAllowlist is still true
        if (uiState.loadingModelAllowlist) {
          loadingModelAllowlistDelayed = true
        }
      } else {
        // If loading finishes, immediately hide the indicator
        loadingModelAllowlistDelayed = false
      }
    }

    // Label and spinner to show when in the process of loading model allowlist.
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
    // Main UI when allowlist is done loading.
    if (!loadingModelAllowlistDelayed && !uiState.loadingModelAllowlist) {
      val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

      val requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
          isGranted: Boolean ->
          if (isGranted) {
            // FCM SDK (and your app) can post notifications.
          }
        }

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

      // Close the menu when back button is pressed.
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
                  iconBrush =
                    linearGradient(
                      colors =
                        listOf(
                          MaterialTheme.customColors.taskBgGradientColors[2][0],
                          MaterialTheme.customColors.taskBgGradientColors[2][1],
                        )
                    ),
                )
                Spacer(modifier = Modifier.width(16.dp))
                SquareDrawerItem(
                  label = stringResource(R.string.drawer_models_label),
                  description = stringResource(R.string.drawer_models_description),
                  icon = Icons.AutoMirrored.Rounded.ListAlt,
                  onClick = {
                    scope.launch { drawerState.close() }
                    scope.launch {
                      delay(50)
                      onModelsClicked()
                    }
                  },
                  modifier = Modifier.weight(1f),
                  iconBrush =
                    linearGradient(
                      colors =
                        listOf(
                          MaterialTheme.customColors.taskBgGradientColors[1][0],
                          MaterialTheme.customColors.taskBgGradientColors[1][1],
                        )
                    ),
                )
              }
              Spacer(modifier = Modifier.height(16.dp))
              Row(modifier = Modifier.fillMaxWidth()) {
              }
            }
          }
        },
        gesturesEnabled = drawerState.isOpen,
      ) {
        Scaffold(
          containerColor = MaterialTheme.colorScheme.background,
          topBar = {
            // Top bar animation:
            //
            // Fade in and move down at the same time.
            val progress =
              if (!enableAnimation) 1f
              else
                rememberDelayedAnimationProgress(
                  initialDelay = ANIMATION_INIT_DELAY - 50,
                  animationDurationMs = TOP_APP_BAR_ANIMATION_DURATION,
                  animationLabel = "top bar",
                )
            Box(
              modifier =
                Modifier.graphicsLayer {
                  alpha = progress
                  translationY = ((-16).dp * (1 - progress)).toPx()
                }
            ) {
              GalleryTopAppBar(
                title = stringResource(HomeScreenDestination.titleRes),
                leftAction =
                  AppBarAction(
                    actionType = AppBarActionType.MENU,
                    actionFn = {
                      scope.launch { drawerState.apply { if (isClosed) open() else close() } }
                    },
                  ),
              )
            }
          },
        ) { innerPadding ->
          // Outer box for coloring the background edge to edge.
          Box(
            contentAlignment = Alignment.TopCenter,
            modifier =
              Modifier.fillMaxSize()
                .background(
                  if (gm4) {
                    MaterialTheme.colorScheme.surface
                  } else {
                    MaterialTheme.colorScheme.surfaceContainer
                  }
                ),
          ) {
            // Inner box to hold content.
            Box(
              contentAlignment = Alignment.TopCenter,
              modifier =
                Modifier.fillMaxSize()
                  .padding(top = innerPadding.calculateTopPadding())
                  .verticalScroll(rememberScrollState()),
            ) {
              // Background star at top.
              if (gm4) {
                val progress =
                  if (!enableAnimation) {
                    1f
                  } else {
                    rememberDelayedAnimationProgress(
                      initialDelay = ANIMATION_INIT_DELAY,
                      animationDurationMs = 2000,
                      animationLabel = "bg star",
                    )
                  }
                val configuration = LocalConfiguration.current
                val screenWidth = configuration.screenWidthDp.dp
                val targetWidth = screenWidth * 1.5f
                Image(
                  painter = painterResource(id = R.drawable.bg_star),
                  contentDescription = null,
                  modifier =
                    Modifier.requiredWidth(targetWidth)
                      .blur(radius = 35.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                      .offset(x = screenWidth * 0.25f, y = -screenWidth * 0.1f)
                      .graphicsLayer {
                        rotationZ = (1f - progress) * 40f
                        scaleX = 0.4f + 0.6f * progress
                        scaleY = 0.4f + 0.6f * progress
                        alpha = progress * 2f
                      },
                  contentScale = ContentScale.Crop,
                  colorFilter = ColorFilter.tint(MaterialTheme.customColors.bgStarColor),
                )
              }

              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
              ) {
                // New Home Header: Welcome Back
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                ) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      modifier = Modifier.clickable { onNavigateProfile() }
                    ) {
                      Box(contentAlignment = Alignment.BottomEnd) {
                        Image(
                          painter = painterResource(id = R.drawable.img_nexora_avatar_1785307944144),
                          contentDescription = "Profile",
                          modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                          contentScale = ContentScale.Crop
                        )
                        Box(
                          modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF22C55E))
                            .border(2.dp, MaterialTheme.colorScheme.background, CircleShape)
                        )
                      }
                      Spacer(modifier = Modifier.width(12.dp))
                      Column {
                        Text(
                          "Welcome Back 👋",
                          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                          color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                          "Power your productivity with private on-device AI.",
                          style = MaterialTheme.typography.bodySmall,
                          color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                      }
                    }

                    Row {
                      IconButton(onClick = onNotificationsClicked) {
                        Icon(Icons.Rounded.Notifications, contentDescription = "Notifications")
                      }
                      IconButton(onClick = onNavigateSettings) {
                        Icon(Icons.Rounded.Settings, contentDescription = "Settings")
                      }
                    }
                  }
                }

                // Animated Real-time Search Box
                var searchQuery by remember { mutableStateOf("") }
                OutlinedTextField(
                  value = searchQuery,
                  onValueChange = { searchQuery = it },
                  placeholder = { Text("Search AI chat, vision, voice, code, docs...") },
                  leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                  shape = RoundedCornerShape(24.dp),
                  modifier = Modifier.fillMaxWidth()
                )

                // Quick Action Chips
                LazyRow(
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  item {
                    AssistChip(
                      onClick = { modelManagerViewModel.getTaskById(BuiltInTaskId.LLM_CHAT)?.let { navigateToTaskScreen(it) } },
                      label = { Text("⚡ Fast Summarize") }
                    )
                  }
                  item {
                    AssistChip(
                      onClick = { modelManagerViewModel.getTaskById(BuiltInTaskId.LLM_ASK_IMAGE)?.let { navigateToTaskScreen(it) } },
                      label = { Text("🔮 Analyze Image") }
                    )
                  }
                  item {
                    AssistChip(
                      onClick = { onNavigateStudio() },
                      label = { Text("💻 Generate Code") }
                    )
                  }
                  item {
                    AssistChip(
                      onClick = { modelManagerViewModel.getTaskById(BuiltInTaskId.LLM_ASK_AUDIO)?.let { navigateToTaskScreen(it) } },
                      label = { Text("🎙️ Voice AI") }
                    )
                  }
                }

                // Main Sections / Feature Grid (12 Cards)
                Text(
                  "Main AI Hub",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                  // Row 1
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                  ) {
                    FeatureCardItem(
                      title = "AI Chat",
                      subtitle = "Natural conversations",
                      icon = Icons.Rounded.ChatBubbleOutline,
                      gradientColors = listOf(Color(0xFF00F0FF), Color(0xFF2563EB)),
                      onClick = { modelManagerViewModel.getTaskById(BuiltInTaskId.LLM_CHAT)?.let { navigateToTaskScreen(it) } },
                      modifier = Modifier.weight(1f)
                    )
                    FeatureCardItem(
                      title = "Vision AI",
                      subtitle = "Analyze Images",
                      icon = Icons.Rounded.Image,
                      gradientColors = listOf(Color(0xFF8B5CF6), Color(0xFFEC4899)),
                      onClick = { modelManagerViewModel.getTaskById(BuiltInTaskId.LLM_ASK_IMAGE)?.let { navigateToTaskScreen(it) } },
                      modifier = Modifier.weight(1f)
                    )
                  }

                  // Row 2
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                  ) {
                    FeatureCardItem(
                      title = "AI Voice",
                      subtitle = "Speech Assistant",
                      icon = Icons.Rounded.GraphicEq,
                      gradientColors = listOf(Color(0xFF10B981), Color(0xFF059669)),
                      onClick = { modelManagerViewModel.getTaskById(BuiltInTaskId.LLM_ASK_AUDIO)?.let { navigateToTaskScreen(it) } },
                      modifier = Modifier.weight(1f)
                    )
                    FeatureCardItem(
                      title = "Code Studio",
                      subtitle = "Generate Code",
                      icon = Icons.Rounded.Code,
                      gradientColors = listOf(Color(0xFFF59E0B), Color(0xFFEF4444)),
                      onClick = { onNavigateStudio() },
                      modifier = Modifier.weight(1f)
                    )
                  }

                  // Row 3
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                  ) {
                    FeatureCardItem(
                      title = "Translator",
                      subtitle = "Multi-language AI",
                      icon = Icons.Rounded.Translate,
                      gradientColors = listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)),
                      onClick = onNavigateTranslate,
                      modifier = Modifier.weight(1f)
                    )
                    FeatureCardItem(
                      title = "Document AI",
                      subtitle = "PDF & Doc Parsing",
                      icon = Icons.Rounded.Description,
                      gradientColors = listOf(Color(0xFF06B6D4), Color(0xFF0891B2)),
                      onClick = onNavigateDocuments,
                      modifier = Modifier.weight(1f)
                    )
                  }

                  // Row 4
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                  ) {
                    FeatureCardItem(
                      title = "Writing Assistant",
                      subtitle = "Smart Content",
                      icon = Icons.Rounded.AutoAwesome,
                      gradientColors = listOf(Color(0xFFA855F7), Color(0xFF7C3AED)),
                      onClick = { modelManagerViewModel.getTaskById(BuiltInTaskId.LLM_CHAT)?.let { navigateToTaskScreen(it) } },
                      modifier = Modifier.weight(1f)
                    )
                    FeatureCardItem(
                      title = "Summarizer",
                      subtitle = "Instant Text Summary",
                      icon = Icons.Rounded.Article,
                      gradientColors = listOf(Color(0xFFF43F5E), Color(0xFFE11D48)),
                      onClick = { modelManagerViewModel.getTaskById(BuiltInTaskId.LLM_CHAT)?.let { navigateToTaskScreen(it) } },
                      modifier = Modifier.weight(1f)
                    )
                  }

                  // Row 5
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                  ) {
                    FeatureCardItem(
                      title = "Offline Models",
                      subtitle = "On-Device Engine",
                      icon = Icons.Rounded.Psychology,
                      gradientColors = listOf(Color(0xFF14B8A6), Color(0xFF0D9488)),
                      onClick = onModelsClicked,
                      modifier = Modifier.weight(1f)
                    )
                    FeatureCardItem(
                      title = "History",
                      subtitle = "Past AI Sessions",
                      icon = Icons.Rounded.History,
                      gradientColors = listOf(Color(0xFF6366F1), Color(0xFF4338CA)),
                      onClick = onNavigateHistory,
                      modifier = Modifier.weight(1f)
                    )
                  }

                  // Row 6
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                  ) {
                    FeatureCardItem(
                      title = "Downloads",
                      subtitle = "Model Weights",
                      icon = Icons.Rounded.Download,
                      gradientColors = listOf(Color(0xFF0284C7), Color(0xFF0369A1)),
                      onClick = onModelsClicked,
                      modifier = Modifier.weight(1f)
                    )
                    FeatureCardItem(
                      title = "Smart Tools",
                      subtitle = "Benchmarks & Extra",
                      icon = Icons.Rounded.Build,
                      gradientColors = listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9)),
                      onClick = onNavigateSettings,
                      modifier = Modifier.weight(1f)
                    )
                  }
                }

                // Recent Activity Section
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  "Recent Activity",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )

                NexoraGlassCard(
                  modifier = Modifier.fillMaxWidth(),
                  onClick = { modelManagerViewModel.getTaskById(BuiltInTaskId.LLM_CHAT)?.let { navigateToTaskScreen(it) } }
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Box(
                        modifier = Modifier
                          .size(38.dp)
                          .clip(CircleShape)
                          .background(Color(0xFF00F0FF).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                      ) {
                        Icon(Icons.Rounded.ChatBubbleOutline, contentDescription = null, tint = Color(0xFF00F0FF))
                      }
                      Spacer(modifier = Modifier.width(12.dp))
                      Column {
                        Text(
                          "Explain Quantum Entanglement simply",
                          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                          color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                          "AI Chat • 12 mins ago",
                          style = MaterialTheme.typography.labelSmall,
                          color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                      }
                    }
                    Text(
                      "Completed",
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                      color = Color(0xFF22C55E)
                    )
                  }
                }

                NexoraGlassCard(
                  modifier = Modifier.fillMaxWidth(),
                  onClick = { onNavigateStudio() }
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Box(
                        modifier = Modifier
                          .size(38.dp)
                          .clip(CircleShape)
                          .background(Color(0xFFF59E0B).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                      ) {
                        Icon(Icons.Rounded.Code, contentDescription = null, tint = Color(0xFFF59E0B))
                      }
                      Spacer(modifier = Modifier.width(12.dp))
                      Column {
                        Text(
                          "Generate Kotlin Coroutines Flow example",
                          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                          color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                          "Code Studio • 1 hour ago",
                          style = MaterialTheme.typography.labelSmall,
                          color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                      }
                    }
                    Text(
                      "Saved",
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                      color = MaterialTheme.colorScheme.primary
                    )
                  }
                }

                // Suggested AI Tools Section
                Text(
                  "Suggested AI Workflows",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  NexoraGlassCard(
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateDocuments() }
                  ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                      Icon(Icons.Rounded.Description, contentDescription = null, tint = Color(0xFF06B6D4))
                      Spacer(modifier = Modifier.height(8.dp))
                      Text(
                        "Summarize 10-page PDF",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                      )
                      Text(
                        "Instant offline extraction",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                  }

                  NexoraGlassCard(
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTranslate() }
                  ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                      Icon(Icons.Rounded.Translate, contentDescription = null, tint = Color(0xFF3B82F6))
                      Spacer(modifier = Modifier.height(8.dp))
                      Text(
                        "Translate Document",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                      )
                      Text(
                        "50+ Languages offline",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                  }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 1. ⭐ Recent Chats
                Text(
                  "⭐ Recent Chats",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                  RecentChatCardItem(
                    title = "Quantum Physics & Relativity Overview 🌌",
                    time = "10 mins ago",
                    snippet = "Explain light wave-particle duality simply...",
                    onClick = { modelManagerViewModel.getTaskById(BuiltInTaskId.LLM_CHAT)?.let { navigateToTaskScreen(it) } }
                  )
                  RecentChatCardItem(
                    title = "Kotlin Jetpack Compose Motion Layout 📱",
                    time = "1 hour ago",
                    snippet = "How to implement smooth spring physics for glassmorphism...",
                    onClick = { modelManagerViewModel.getTaskById(BuiltInTaskId.LLM_CHAT)?.let { navigateToTaskScreen(it) } }
                  )
                  RecentChatCardItem(
                    title = "System Architecture for Local AI ⚡",
                    time = "Yesterday",
                    snippet = "Compare LiteRT v2 with Vulkan GPU delegates...",
                    onClick = { modelManagerViewModel.getTaskById(BuiltInTaskId.LLM_CHAT)?.let { navigateToTaskScreen(it) } }
                  )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 2. ⚡ Quick Actions
                Text(
                  "⚡ Quick Actions",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )

                LazyRow(
                  horizontalArrangement = Arrangement.spacedBy(10.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  item {
                    QuickActionGlassChip(
                      icon = Icons.Rounded.Code,
                      label = "Debug Kotlin Code",
                      onClick = { onNavigateStudio() }
                    )
                  }
                  item {
                    QuickActionGlassChip(
                      icon = Icons.Rounded.Description,
                      label = "Summarize PDF",
                      onClick = { onNavigateDocuments() }
                    )
                  }
                  item {
                    QuickActionGlassChip(
                      icon = Icons.Rounded.Translate,
                      label = "Translate Document",
                      onClick = { onNavigateTranslate() }
                    )
                  }
                  item {
                    QuickActionGlassChip(
                      icon = Icons.Rounded.GraphicEq,
                      label = "Voice Scribe",
                      onClick = { modelManagerViewModel.getTaskById(BuiltInTaskId.LLM_ASK_AUDIO)?.let { navigateToTaskScreen(it) } }
                    )
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 3. 💡 Suggested Prompts
                Text(
                  "💡 Suggested Prompts",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                  SuggestedPromptCard(
                    prompt = "🤖 Draft a clean Kotlin Coroutines Flow repository class",
                    category = "Coding",
                    onClick = { modelManagerViewModel.getTaskById(BuiltInTaskId.LLM_CHAT)?.let { navigateToTaskScreen(it) } }
                  )
                  SuggestedPromptCard(
                    prompt = "🖼️ What are the key objects and text in this image?",
                    category = "Vision AI",
                    onClick = { modelManagerViewModel.getTaskById(BuiltInTaskId.LLM_ASK_IMAGE)?.let { navigateToTaskScreen(it) } }
                  )
                  SuggestedPromptCard(
                    prompt = "📝 Summarize meeting notes into 3 concise action items",
                    category = "Productivity",
                    onClick = { modelManagerViewModel.getTaskById(BuiltInTaskId.LLM_CHAT)?.let { navigateToTaskScreen(it) } }
                  )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 4. 📊 Usage Overview
                Text(
                  "📊 Usage Overview",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  UsageStatBox(
                    count = "18",
                    label = "Chats Today",
                    icon = Icons.Rounded.ChatBubbleOutline,
                    color = Color(0xFF00F0FF),
                    modifier = Modifier.weight(1f)
                  )
                  UsageStatBox(
                    count = "7",
                    label = "Images Analyzed",
                    icon = Icons.Rounded.Image,
                    color = Color(0xFF8B5CF6),
                    modifier = Modifier.weight(1f)
                  )
                }

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  UsageStatBox(
                    count = "4",
                    label = "Voice Requests",
                    icon = Icons.Rounded.GraphicEq,
                    color = Color(0xFF10B981),
                    modifier = Modifier.weight(1f)
                  )
                  UsageStatBox(
                    count = "12",
                    label = "Docs Processed",
                    icon = Icons.Rounded.Description,
                    color = Color(0xFFF59E0B),
                    modifier = Modifier.weight(1f)
                  )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 5. 📥 Downloaded Models
                Text(
                  "📥 Downloaded Models",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )

                NexoraGlassCard(
                  modifier = Modifier.fillMaxWidth(),
                  onClick = onModelsClicked
                ) {
                  Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                          modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF8B5CF6).copy(alpha = 0.2f)),
                          contentAlignment = Alignment.Center
                        ) {
                          Icon(Icons.Rounded.Psychology, contentDescription = null, tint = Color(0xFF8B5CF6))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                          Text(
                            "Gemma 4 2B + LiteRT Vision",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                          )
                          Text(
                            "2 Models Active • 100% Offline Mode",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                          )
                        }
                      }
                      Box(
                        modifier = Modifier
                          .clip(RoundedCornerShape(12.dp))
                          .background(Color(0xFF22C55E).copy(alpha = 0.2f))
                          .padding(horizontal = 10.dp, vertical = 4.dp)
                      ) {
                        Text(
                          "Ready",
                          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                          color = Color(0xFF22C55E)
                        )
                      }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Text(
                        "Storage Used: 4.2 GB of 128 GB",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                      Text(
                        "Manage Models ›",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                      )
                    }
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 6. ❤️ Favorites
                Text(
                  "❤️ Favorites",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  FavoriteToolCard(
                    title = "Code Studio",
                    subtitle = "Pinned Tool",
                    icon = Icons.Rounded.Code,
                    color = Color(0xFFF59E0B),
                    onClick = onNavigateStudio,
                    modifier = Modifier.weight(1f)
                  )
                  FavoriteToolCard(
                    title = "Vision AI",
                    subtitle = "Pinned Tool",
                    icon = Icons.Rounded.Image,
                    color = Color(0xFF8B5CF6),
                    onClick = { modelManagerViewModel.getTaskById(BuiltInTaskId.LLM_ASK_IMAGE)?.let { navigateToTaskScreen(it) } },
                    modifier = Modifier.weight(1f)
                  )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 7. 📰 AI News & Tips
                Text(
                  "📰 AI News & Tips",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )

                NexoraGlassCard(
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                  ) {
                    NewsTipRow(
                      badge = "PRO TIP",
                      badgeColor = Color(0xFF00F0FF),
                      text = "Run Gemma 4 100% offline on your device with zero cloud latency."
                    )
                    NewsTipRow(
                      badge = "FEATURE",
                      badgeColor = Color(0xFF8B5CF6),
                      text = "LiteRT Vision engine now supports multi-object analysis & OCR."
                    )
                    NewsTipRow(
                      badge = "RELEASE",
                      badgeColor = Color(0xFF10B981),
                      text = "Nexora v1.0 released with Vulkan & OpenCL NPU acceleration."
                    )
                  }
                }

                Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding() + 20.dp))
              }
            }

            // Gradient overlay at the bottom.
            Box(
              modifier =
                Modifier.fillMaxWidth()
                  .height(innerPadding.calculateBottomPadding())
                  .background(
                    Brush.verticalGradient(
                      colors = listOf(Color.Transparent, MaterialTheme.colorScheme.surfaceContainer)
                    )
                  )
                  .align(Alignment.BottomCenter)
            )
          }
        }
      }
    }
  }

  // Show TOS dialog for users to accept.
  if (showTosDialog) {
    AppTosDialog(
      onTosAccepted = {
        showTosDialog = false
        tosViewModel.acceptTos()
      }
    )
  }

  // Settings dialog.
  if (showSettingsDialog) {
    SettingsDialog(
      curThemeOverride = modelManagerViewModel.readThemeOverride(),
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
      text = { Text("Please check your internet connection and try again later.") },
      onDismissRequest = { modelManagerViewModel.loadModelAllowlist() },
      confirmButton = {
        TextButton(onClick = { modelManagerViewModel.loadModelAllowlist() }) { Text("Retry") }
      },
      dismissButton = {
        TextButton(onClick = { modelManagerViewModel.clearLoadModelAllowlistError() }) {
          Text("Cancel")
        }
      },
    )
  }
}

@Composable
private fun AppTitle(enableAnimation: Boolean) {
  val firstLineText = stringResource(R.string.app_name_first_part)
  val secondLineText = stringResource(R.string.app_name_second_part)
  val titleColor = MaterialTheme.customColors.appTitleGradientColors[1]
  val screenWidthInDp = LocalConfiguration.current.screenWidthDp.dp
  val fontSize = with(LocalDensity.current) { (screenWidthInDp.toPx() * 0.12f).toSp() }
  val titleStyle = homePageTitleStyle.copy(fontSize = fontSize, lineHeight = fontSize)

  // First line text "Google AI" and its animation.
  //
  // The animation starts with the first line of text swiping in from left to right, progressively
  // revealing itself in the title color (blue). Then, after a brief delay, the exact same text, but
  // in the onSurface color (which is black in light mode), begins its own left-to-right swiping
  // animation. This second animation is positioned directly on top of the first, appearing just as
  // the initial reveal is finishing or has just completed, creating a layered and dynamic visual
  // effect.
  Box(modifier = Modifier.clearAndSetSemantics {}) {
    var delay = ANIMATION_INIT_DELAY
    if (enableAnimation) {
      SwipingText(
        text = firstLineText,
        style = titleStyle,
        color = titleColor,
        animationDelay = delay,
        animationDurationMs = TITLE_FIRST_LINE_ANIMATION_DURATION,
      )
      delay += (TITLE_FIRST_LINE_ANIMATION_DURATION * 0.3).toLong()
    }
    SwipingText(
      text = firstLineText,
      style = titleStyle,
      color = MaterialTheme.colorScheme.onSurface,
      animationDelay = if (enableAnimation) delay else 0,
      animationDurationMs = if (enableAnimation) TITLE_FIRST_LINE_ANIMATION_DURATION else 0,
    )
  }
  // Second line text "Edge Gallery" and its animation.
  //
  // The initial animation is the same as the first line text. Right before it is done, the final
  // text with a gradient is revealed.
  Box(modifier = Modifier.clearAndSetSemantics {}) {
    var delay = TITLE_SECOND_LINE_ANIMATION_START
    if (enableAnimation) {
      SwipingText(
        text = secondLineText,
        style = titleStyle,
        color = titleColor,
        modifier = Modifier.offset(y = (-16).dp),
        animationDelay = delay,
        animationDurationMs = TITLE_SECOND_LINE_ANIMATION_DURATION,
      )
      delay += (TITLE_SECOND_LINE_ANIMATION_DURATION * 0.3).toInt()
      SwipingText(
        text = secondLineText,
        style = titleStyle,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.offset(y = (-16).dp),
        animationDelay = delay,
        animationDurationMs = TITLE_SECOND_LINE_ANIMATION_DURATION,
      )
      delay += (TITLE_SECOND_LINE_ANIMATION_DURATION * 0.6).toInt()
    }
    RevealingText(
      text = secondLineText,
      style =
        titleStyle.copy(
          brush = linearGradient(colors = MaterialTheme.customColors.appTitleGradientColors)
        ),
      modifier = Modifier.offset(x = (-16).dp, y = (-16).dp),
      animationDelay = if (enableAnimation) delay else 0,
      animationDurationMs = if (enableAnimation) TITLE_SECOND_LINE_ANIMATION_DURATION2 else 0,
    )
  }
}

@Composable
fun AppTitleGm4(enableAnimation: Boolean) {
  val text1 = "NexUs"
  val text2 = "AI Assistant"
  val annotatedText = buildAnnotatedString {
    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) { append(text1) }
    append(" ")
    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) { append(text2) }
  }

  RevealingText(
    text = "",
    annotatedText = annotatedText,
    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Medium),
    animationDelay = 0,
    animationDurationMs =
      if (enableAnimation) {
        (TITLE_FIRST_LINE_ANIMATION_DURATION + TITLE_SECOND_LINE_ANIMATION_DURATION)
      } else {
        0
      },
    extraTextPadding = 0.dp,
  )
}

@Composable
private fun IntroText(enableAnimation: Boolean, gm4: Boolean) {
  val litertUrl = "https://huggingface.co/litert-community"

  // Intro text animation:
  //
  // fade in + slide up.
  val progress =
    if (!enableAnimation) {
      1f
    } else {
      rememberDelayedAnimationProgress(
        initialDelay = TITLE_SECOND_LINE_ANIMATION_START,
        animationDurationMs = CONTENT_COMPOSABLES_ANIMATION_DURATION,
        animationLabel = "intro text animation",
      )
    }

  val introText = buildAnnotatedString {
    val gemma4Url = "https://ai.google.dev/gemma"
    if (gm4) {
      append("Discover the power of on-device AI models from the ")
      append(buildTrackableUrlAnnotatedString(url = litertUrl, linkText = "LiteRT community"))
      append(", featuring the all-new ")
      append(buildTrackableUrlAnnotatedString(url = gemma4Url, linkText = "Gemma 4"))
      append(".")
    } else {
      append("${stringResource(R.string.app_intro)} ")
      append(
        buildTrackableUrlAnnotatedString(
          url = litertUrl,
          linkText = stringResource(R.string.litert_community_label),
        )
      )
    }
  }
  Text(
    introText,
    style = MaterialTheme.typography.bodyMedium,
    modifier =
      Modifier.graphicsLayer {
        alpha = progress
        translationY = (CONTENT_COMPOSABLES_OFFSET_Y.dp * (1 - progress)).toPx()
      },
  )
}

@Composable
private fun TryGm4IntroText(enableAnimation: Boolean) {
  // fade in + slide up.
  val progress =
    if (!enableAnimation) {
      1f
    } else {
      rememberDelayedAnimationProgress(
        initialDelay = TITLE_SECOND_LINE_ANIMATION_START,
        animationDurationMs = CONTENT_COMPOSABLES_ANIMATION_DURATION,
        animationLabel = "intro text animation",
      )
    }
  Row(
    modifier =
      Modifier.padding(top = 24.dp).graphicsLayer {
        alpha = progress
        translationY = (CONTENT_COMPOSABLES_OFFSET_Y.dp * (1 - progress)).toPx()
      },
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Icon(
      ImageVector.vectorResource(R.drawable.gemma_logo),
      contentDescription = null,
      modifier = Modifier.size(24.dp),
      tint = MaterialTheme.colorScheme.primary,
    )
    Text(
      text = "Try Gemma 4 today",
      style =
        MaterialTheme.typography.headlineSmall.copy(
          fontWeight = FontWeight.Medium,
          fontSize = 20.sp,
          lineHeight = 24.sp,
        ),
      color = MaterialTheme.colorScheme.onSurface,
    )
  }

  Text(
    "Gemma 4 E2B & E4B are here! Try them in AI Chat, Agent Skills, or the use cases below.",
    style = MaterialTheme.typography.bodyMedium,
    modifier =
      Modifier.graphicsLayer {
        alpha = progress
        translationY = (CONTENT_COMPOSABLES_OFFSET_Y.dp * (1 - progress)).toPx()
      },
  )
}

@Composable
private fun CategoryTabHeader(
  sortedCategories: List<CategoryInfo>,
  selectedIndex: Int,
  enableAnimation: Boolean,
  onCategorySelected: (Int) -> Unit,
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val listState = rememberLazyListState()

  val progress =
    if (!enableAnimation) 1f
    else
      rememberDelayedAnimationProgress(
        initialDelay = TASK_LIST_ANIMATION_START,
        animationDurationMs = CONTENT_COMPOSABLES_ANIMATION_DURATION,
        animationLabel = "task card animation",
      )

  LazyRow(
    state = listState,
    modifier =
      Modifier.fillMaxWidth().padding(bottom = 32.dp).graphicsLayer {
        alpha = progress
        translationY = (CONTENT_COMPOSABLES_OFFSET_Y.dp * (1 - progress)).toPx()
      },
    horizontalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    item(key = "spacer_start") { Spacer(modifier = Modifier.width(8.dp)) }
    itemsIndexed(items = sortedCategories) { index, category ->
      Row(
        modifier =
          Modifier.height(40.dp)
            .clip(CircleShape)
            .background(
              color =
                if (selectedIndex == index) MaterialTheme.customColors.tabHeaderBgColor
                else Color.Transparent
            )
            .clickable {
              onCategorySelected(index)

              // Scroll to clicked item when the item is not fully inside view.
              scope.launch {
                val visibleItems = listState.layoutInfo.visibleItemsInfo
                val targetItem = visibleItems.find {
                  // +1 because the first item is the item keyed at spacer_start.
                  it.index == index + 1
                }
                if (
                  targetItem == null ||
                    targetItem.offset < 0 ||
                    targetItem.offset + targetItem.size > listState.layoutInfo.viewportSize.width
                ) {
                  listState.animateScrollToItem(index = index)
                }
              }
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
      ) {
        Text(
          getCategoryLabel(context = context, category = category),
          modifier = Modifier.padding(horizontal = 16.dp),
          style = MaterialTheme.typography.labelLarge,
          color =
            if (selectedIndex == index) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
    item(key = "spacer_end") { Spacer(modifier = Modifier.width(8.dp)) }
  }
}

@Composable
private fun TaskList(
  modelManagerViewModel: ModelManagerViewModel,
  pagerState: PagerState,
  sortedCategories: List<CategoryInfo>,
  tasksByCategories: Map<String, List<Task>>,
  enableAnimation: Boolean,
  navigateToTaskScreen: (Task) -> Unit,
  gm4: Boolean = false,
  grid: Boolean = false,
) {
  // Model list animation:
  //
  // 1.  Slide Up: The entire column of task cards translates upwards,
  // 2.  Fade in one by one: The task card fade in one by one. See TaskCard for details.
  val progress =
    if (!enableAnimation) 1f
    else
      rememberDelayedAnimationProgress(
        initialDelay = TASK_LIST_ANIMATION_START,
        animationDurationMs = CONTENT_COMPOSABLES_ANIMATION_DURATION,
        animationLabel = "task card animation",
      )

  // Tracks when the initial animation is done.
  //
  var initialAnimationDone by remember { mutableStateOf(false) }
  LaunchedEffect(Unit) {
    // Use 5 iterations to make sure all visible task cards are animated.
    delay(((TASK_CARD_ANIMATION_DURATION + TASK_CARD_ANIMATION_DELAY_OFFSET) * 5).toLong())
    initialAnimationDone = true
  }

  // The highlighted tiles at the top.
  if (gm4) {
    Column(
      verticalArrangement = Arrangement.spacedBy(10.dp),
      modifier =
        Modifier.padding(horizontal = 24.dp).graphicsLayer {
          alpha = progress
          translationY = (CONTENT_COMPOSABLES_OFFSET_Y.dp * (1 - progress)).toPx()
        },
    ) {
      val chatToDescription =
        mapOf(
          BuiltInTaskId.LLM_CHAT to "Chat with the latest Gemma 4 model today",
          // use "\u00a0" to make sure the word before and after it should always be together when
          // wrapping lines.
          BuiltInTaskId.LLM_AGENT_CHAT to "Have Gemma 4 complete agentic tasks for\u00A0you",
        )
      for (task in
        listOf(
          modelManagerViewModel.getTaskById(BuiltInTaskId.LLM_CHAT)!!,
          modelManagerViewModel.getTaskById(BuiltInTaskId.LLM_AGENT_CHAT)!!,
        )) {
        TaskCard(
          task = task,
          index = 0,
          animate = !initialAnimationDone && enableAnimation,
          onClick = { navigateToTaskScreen(task) },
          modifier = Modifier.fillMaxWidth(),
          description = chatToDescription[task.id]!!,
        )
      }

      Text(
        text = "Explore other use cases",
        style =
          MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            lineHeight = 24.sp,
          ),
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(top = 22.dp, bottom = 16.dp),
      )
    }
  }

  HorizontalPager(
    state = pagerState,
    verticalAlignment = Alignment.Top,
    contentPadding = PaddingValues(horizontal = 20.dp),
  ) { pageIndex ->
    val tasks = tasksByCategories[sortedCategories[pageIndex].id]!!
    if (grid) {
      Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier =
          Modifier.fillMaxWidth().padding(4.dp).graphicsLayer {
            translationY = (CONTENT_COMPOSABLES_OFFSET_Y.dp * (1 - progress)).toPx()
          },
      ) {
        for (i in tasks.indices step 2) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
          ) {
            // First item in the row
            TaskCard(
              task = tasks[i],
              index = i,
              animate =
                (pageIndex == 0 || pageIndex == 1) && !initialAnimationDone && enableAnimation,
              onClick = { navigateToTaskScreen(tasks[i]) },
              modifier = Modifier.weight(1f),
              square = true,
            )

            // Second item in the row, if it exists
            if (i + 1 < tasks.size) {
              TaskCard(
                task = tasks[i + 1],
                index = i + 1,
                animate =
                  (pageIndex == 0 || pageIndex == 1) && !initialAnimationDone && enableAnimation,
                onClick = { navigateToTaskScreen(tasks[i + 1]) },
                modifier = Modifier.weight(1f),
                square = true,
              )
            } else {
              // Add a spacer to fill the remaining space if there's only one item in the last row
              Spacer(modifier = Modifier.weight(1f))
            }
          }
        }
      }
    } else {
      Column(
        modifier =
          Modifier.fillMaxWidth().padding(4.dp).graphicsLayer {
            translationY = (CONTENT_COMPOSABLES_OFFSET_Y.dp * (1 - progress)).toPx()
          },
        verticalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        for ((index, task) in tasks.withIndex()) {
          TaskCard(
            task = task,
            index = index,
            animate =
              (pageIndex == 0 || pageIndex == 1) && !initialAnimationDone && enableAnimation,
            onClick = { navigateToTaskScreen(task) },
            modifier = Modifier.fillMaxWidth(),
            square = false,
          )
        }
      }
    }
  }
}

@Composable
private fun TaskCard(
  task: Task,
  index: Int,
  animate: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  description: String = "",
  square: Boolean = false,
) {
  // Observes the model count and updates the model count label with a fade-in/fade-out animation
  // whenever the count changes.
  val modelCount by remember {
    derivedStateOf {
      val trigger = task.updateTrigger.value
      if (trigger >= 0) {
        task.models.size
      } else {
        0
      }
    }
  }
  val modelCountLabel by remember {
    derivedStateOf {
      when (modelCount) {
        1 -> "1 Model"
        else -> "%d Models".format(modelCount)
      }
    }
  }
  var curModelCountLabel by remember { mutableStateOf("") }
  var modelCountLabelVisible by remember { mutableStateOf(true) }

  LaunchedEffect(modelCountLabel) {
    if (curModelCountLabel.isEmpty()) {
      curModelCountLabel = modelCountLabel
    } else {
      modelCountLabelVisible = false
      delay(TASK_COUNT_ANIMATION_DURATION.toLong())
      curModelCountLabel = modelCountLabel
      modelCountLabelVisible = true
    }
  }

  // Task card animation:
  //
  // This animation makes the task cards appear with a delayed fade-in effect. Each card will become
  // visible sequentially, starting after an initial delay and then with an additional offset for
  // subsequent cards.
  val progress =
    if (animate)
      rememberDelayedAnimationProgress(
        initialDelay = TASK_LIST_ANIMATION_START + index * TASK_CARD_ANIMATION_DELAY_OFFSET,
        animationDurationMs = TASK_CARD_ANIMATION_DURATION,
        animationLabel = "task card animation",
      )
    else 1f

  val cbTask = stringResource(R.string.cd_task_card, task.label, task.models.size)
  Card(
    modifier =
      modifier
        .clip(RoundedCornerShape(24.dp))
        .clickable(onClick = onClick)
        .graphicsLayer { alpha = progress }
        .semantics { contentDescription = cbTask },
    colors =
      CardDefaults.cardColors(
        containerColor =
          if (description.isNotEmpty() || square) {
            MaterialTheme.colorScheme.surfaceContainer
          } else {

            MaterialTheme.customColors.taskCardBgColor
          }
      ),
  ) {
    if (square) {
      Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        TaskIcon(task = task, width = 40.dp)
        Column() {
          Text(
            curModelCountLabel,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
            modifier = Modifier.clearAndSetSemantics {},
          )
          Text(
            task.label,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
          )
          Text(
            task.shortDescription,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp, lineHeight = 14.sp),
            modifier = Modifier.clearAndSetSemantics {},
            minLines = 2,
            maxLines = 2,
            autoSize =
              TextAutoSize.StepBased(minFontSize = 8.sp, maxFontSize = 12.sp, stepSize = 1.sp),
          )
        }
      }
    } else {
      Row(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        if (description.isNotEmpty()) {
          // Icon.
          TaskIcon(task = task, width = 40.dp)

          // Title and description.
          Column(modifier = Modifier.weight(1f).padding(start = 16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Text(
                task.label,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
              )
              if (task.newFeature) {
                Box(
                  modifier =
                    Modifier.offset(y = (-6).dp, x = 6.dp)
                      .clip(RoundedCornerShape(8.dp))
                      .background(MaterialTheme.customColors.newFeatureContainerColor)
                      .padding(horizontal = 12.dp)
                      .height(26.dp),
                  contentAlignment = Alignment.Center,
                ) {
                  Text(
                    "New",
                    color = MaterialTheme.customColors.newFeatureTextColor,
                    style = MaterialTheme.typography.labelLarge,
                  )
                }
              }
            }
            Text(
              description,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              style =
                MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp, lineHeight = 15.sp),
              modifier = Modifier.clearAndSetSemantics {},
            )
          }
        } else {
          // Title and model count
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                task.label,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
              )
              if (task.experimental) {
                Icon(
                  painter = painterResource(R.drawable.ic_experiment),
                  contentDescription = "Experimental",
                  modifier = Modifier.size(20.dp).padding(start = 4.dp),
                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
            }
            Text(
              curModelCountLabel,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              style = MaterialTheme.typography.bodyMedium,
              modifier = Modifier.clearAndSetSemantics {},
            )
          }

          // Icon.
          TaskIcon(task = task, width = 40.dp)
        }
      }
    }
  }
}

@Composable
private fun QuickToolItem(
  icon: ImageVector,
  label: String,
  onClick: () -> Unit,
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .clip(RoundedCornerShape(16.dp))
      .clickable(onClick = onClick)
      .padding(8.dp)
  ) {
    Box(
      modifier = Modifier
        .size(52.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.primaryContainer),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = Modifier.size(26.dp)
      )
    }
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
      color = MaterialTheme.colorScheme.onSurface
    )
  }
}

@Composable
private fun FeatureCardItem(
  title: String,
  subtitle: String,
  icon: ImageVector,
  gradientColors: List<Color>,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(22.dp))
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
            gradientColors.first().copy(alpha = 0.35f),
            gradientColors.last().copy(alpha = 0.15f)
          )
        ),
        RoundedCornerShape(22.dp)
      )
      .clickable { onClick() }
      .padding(14.dp)
  ) {
    Column {
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(CircleShape)
          .background(Brush.linearGradient(gradientColors)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          icon,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        title,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )

      Text(
        subtitle,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1
      )
    }
  }
}

@Composable
private fun RecentChatCardItem(
  title: String,
  time: String,
  snippet: String,
  onClick: () -> Unit
) {
  NexoraGlassCard(
    modifier = Modifier.fillMaxWidth(),
    onClick = onClick
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            Icons.Rounded.ChatBubbleOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            title,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            snippet,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
          )
        }
      }
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        time,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary
      )
    }
  }
}

@Composable
private fun QuickActionGlassChip(
  icon: ImageVector,
  label: String,
  onClick: () -> Unit
) {
  NexoraGlassCard(
    onClick = onClick
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        icon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(18.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        label,
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurface
      )
    }
  }
}

@Composable
private fun SuggestedPromptCard(
  prompt: String,
  category: String,
  onClick: () -> Unit
) {
  NexoraGlassCard(
    modifier = Modifier.fillMaxWidth(),
    onClick = onClick
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          category.uppercase(),
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          prompt,
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
          color = MaterialTheme.colorScheme.onSurface
        )
      }
      Icon(
        Icons.Rounded.ChevronRight,
        contentDescription = "Run Prompt",
        tint = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@Composable
private fun UsageStatBox(
  count: String,
  label: String,
  icon: ImageVector,
  color: Color,
  modifier: Modifier = Modifier
) {
  NexoraGlassCard(modifier = modifier) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        }
        Text(
          count,
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        label,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@Composable
private fun FavoriteToolCard(
  title: String,
  subtitle: String,
  icon: ImageVector,
  color: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  NexoraGlassCard(
    modifier = modifier,
    onClick = onClick
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(CircleShape)
          .background(color.copy(alpha = 0.18f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(
          title,
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          subtitle,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}

@Composable
private fun NewsTipRow(
  badge: String,
  badgeColor: Color,
  text: String
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.fillMaxWidth()
  ) {
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(8.dp))
        .background(badgeColor.copy(alpha = 0.2f))
        .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
      Text(
        badge,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = badgeColor
      )
    }
    Spacer(modifier = Modifier.width(10.dp))
    Text(
      text,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurface
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
