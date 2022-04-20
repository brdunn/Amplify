package com.devdunnapps.amplify.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devdunnapps.amplify.R
import com.google.android.material.composethemeadapter3.Mdc3Theme

@Composable
fun ExpandableText(
    text: String?,
    collapsedLines: Int = 3
) {
    if (text == null || text.isEmpty()) return;

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var isExpanded by remember { mutableStateOf(false) }
        Text(
            text = text,
            maxLines = if (isExpanded) Int.MAX_VALUE else collapsedLines,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.animateContentSize().padding(horizontal = 16.dp)
        )
        TextButton(
            onClick = { isExpanded = !isExpanded },
        ) {
            Text(
                text = if (isExpanded) "Collapse" else "Expand"
            )
        }
    }
}

@Preview
@Composable
fun ExpandableTextPreview() {
    Mdc3Theme {
        Surface {
            ExpandableText(text = stringResource(id = R.string.sample_summary))
        }
    }
}
