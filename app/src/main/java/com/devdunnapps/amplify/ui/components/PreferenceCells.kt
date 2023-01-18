package com.devdunnapps.amplify.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.unit.dp
import com.devdunnapps.amplify.R

@Composable
fun ListPreferenceCell(
    @StringRes title: Int,
    description: String,
    items: List<ListPreferenceItem>,
    onClick: (ListPreferenceItem?) -> Unit
) {
    var isDialogVisible by remember { mutableStateOf(false) }

    if (isDialogVisible) {
        ListPreferenceDialog(
            title = stringResource(id = title),
            items = items,
            onDismissDialog = { isDialogVisible = false },
            onConfirmDialog = {
                isDialogVisible = false
                onClick(it)
            }
        )
    }

    BasicPreferenceCell(
        title = stringResource(id = title),
        description = description,
        onClick = {
            isDialogVisible = true
            onClick(null)
        }
    )
}

@Composable
fun StaticTextCell(
    @StringRes title: Int,
    @StringRes description: Int,
    onClick: () -> Unit
) {
    BasicPreferenceCell(
        title = stringResource(id = title),
        description = stringResource(id = description),
        onClick = onClick
    )
}

data class ListPreferenceItem(
    val title: String,
    val isSelected: Boolean,
    val value: Any
)

@Composable
private fun ListPreferenceDialog(
    title: String,
    items: List<ListPreferenceItem>,
    onDismissDialog: () -> Unit,
    onConfirmDialog: (ListPreferenceItem) -> Unit,

) {
    AlertDialog(
        onDismissRequest = onDismissDialog,
        confirmButton = {
            TextButton(onClick = onDismissDialog) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        title = {
            Text(text = title)
        },
        text = {
            LazyColumn {
                items(items) {
                    ListPreferenceDialogItem(
                        title = it.title,
                        isSelected = it.isSelected,
                        onClick = { onConfirmDialog(it) }
                    )
                }
            }
        }
    )
}

@Composable
private fun ListPreferenceDialogItem(title: String, isSelected: Boolean = false, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        RadioButton(selected = isSelected, onClick = onClick)

        Text(text = title, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun BasicPreferenceCell(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
