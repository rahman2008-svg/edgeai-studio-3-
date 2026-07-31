/*
 * Copyright © 2026 NexVora Lab's Ofc
 *
 * Developed by:
 * Prince AR Abdur Rahman
 *
 * Application:
 * Nexora AI
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddComment
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nexvora.ai.R

@Composable
fun ChatHistorySideSheetContent(
  history: List<com.nexvora.ai.proto.ChatSessionProto>,
  onHistoryItemClicked: (String) -> Unit,
  onHistoryItemDeleted: (String) -> Unit,
  onHistoryItemsDeleteAll: () -> Unit,
  onNewChatClicked: () -> Unit,
  onDismissed: () -> Unit,
) {
  var showConfirmDeleteDialog by remember { mutableStateOf(false) }
  var itemToDelete by remember { mutableStateOf<String?>(null) }

  Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
    // Top Row: Title and Close button
    Row(
      modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        stringResource(R.string.chat_history_title),
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
      )
      IconButton(onClick = onDismissed) {
        Icon(Icons.Rounded.Close, contentDescription = stringResource(R.string.cd_close_icon))
      }
    }

    // "+ New chat" — premium, full-width primary action.
    Button(
      onClick = onNewChatClicked,
      modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
      shape = RoundedCornerShape(16.dp),
      colors =
        ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer,
          contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
      contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 14.dp),
    ) {
      Icon(Icons.Rounded.AddComment, contentDescription = null, modifier = Modifier.size(20.dp))
      Spacer(modifier = Modifier.size(10.dp))
      Text(stringResource(R.string.new_chat), style = MaterialTheme.typography.titleSmall)
    }

    if (history.isEmpty()) {
      // Modern empty state.
      Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            modifier = Modifier.size(72.dp),
          ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
              Icon(
                imageVector = Icons.Rounded.ChatBubbleOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp),
              )
            }
          }
          Spacer(modifier = Modifier.height(20.dp))
          Text(
            "No conversations yet",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            textAlign = TextAlign.Center,
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            "Start a new AI conversation.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
          )
        }
      }
    } else {
      // Subheading Row: Chat history and Clear all button
      Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          stringResource(R.string.chat_history_title),
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        TextButton(
          onClick = { showConfirmDeleteDialog = true },
          colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
        ) {
          Icon(
            Icons.Rounded.DeleteSweep,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.size(6.dp))
          Text(stringResource(R.string.clear_all))
        }
      }

      // History list — premium conversation cards.
      LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(history) { session ->
          Surface(
            onClick = { onHistoryItemClicked(session.sessionId) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 1.dp,
          ) {
            Row(
              modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp, horizontal = 16.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(36.dp),
              ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                  Icon(
                    imageVector = Icons.Rounded.ChatBubbleOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(18.dp),
                  )
                }
              }
              Text(
                session.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
              )
              IconButton(
                onClick = { itemToDelete = session.sessionId },
                modifier = Modifier.size(36.dp),
              ) {
                Icon(
                  Icons.Rounded.DeleteOutline,
                  contentDescription = stringResource(R.string.cd_delete_input_history_entry_icon),
                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(20.dp),
                )
              }
            }
          }
        }
      }
    }

    // Footer.
    Row(
      modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        "Powered by Nexora AI",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }

  if (showConfirmDeleteDialog) {
    AlertDialog(
      onDismissRequest = { showConfirmDeleteDialog = false },
      shape = RoundedCornerShape(24.dp),
      icon = {
        Icon(Icons.Rounded.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
      },
      title = {
        Text(
          stringResource(R.string.clear_history_dialog_title),
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        )
      },
      text = {
        Text(
          stringResource(R.string.clear_history_dialog_content),
          style = MaterialTheme.typography.bodyMedium,
        )
      },
      confirmButton = {
        Button(
          onClick = {
            showConfirmDeleteDialog = false
            onHistoryItemsDeleteAll()
          },
          shape = RoundedCornerShape(14.dp),
          colors =
            ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.error,
              contentColor = MaterialTheme.colorScheme.onError,
            ),
        ) {
          Text(stringResource(R.string.ok))
        }
      },
      dismissButton = {
        TextButton(onClick = { showConfirmDeleteDialog = false }) {
          Text(stringResource(R.string.cancel))
        }
      },
    )
  }

  if (itemToDelete != null) {
    AlertDialog(
      onDismissRequest = { itemToDelete = null },
      shape = RoundedCornerShape(24.dp),
      icon = {
        Icon(Icons.Rounded.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
      },
      title = {
        Text(
          stringResource(R.string.clear_history_dialog_title),
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        )
      },
      text = {
        Text(
          stringResource(R.string.clear_history_dialog_content),
          style = MaterialTheme.typography.bodyMedium,
        )
      },
      confirmButton = {
        Button(
          onClick = {
            val toDel = itemToDelete
            itemToDelete = null
            if (toDel != null) {
              onHistoryItemDeleted(toDel)
            }
          },
          shape = RoundedCornerShape(14.dp),
          colors =
            ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.error,
              contentColor = MaterialTheme.colorScheme.onError,
            ),
        ) {
          Text(stringResource(R.string.ok))
        }
      },
      dismissButton = {
        TextButton(onClick = { itemToDelete = null }) { Text(stringResource(R.string.cancel)) }
      },
    )
  }
}
