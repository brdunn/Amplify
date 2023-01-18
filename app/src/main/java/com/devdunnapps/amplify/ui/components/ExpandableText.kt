package com.devdunnapps.amplify.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.ui.theme.Theme

@Composable
fun ExpandableText(
    text: String?,
    modifier: Modifier = Modifier,
    collapsedLines: Int = 3
) {
    if (text.isNullOrBlank()) return

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var isExpanded by remember { mutableStateOf(false) }
        var displayExpandButton by remember { mutableStateOf(false) }

        Text(
            text = text,
            maxLines = if (isExpanded) Int.MAX_VALUE else collapsedLines,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .animateContentSize()
                .padding(horizontal = 16.dp),
            onTextLayout = {
                displayExpandButton = it.lineCount == collapsedLines && it.isLineEllipsized(collapsedLines - 1)
            }
        )

        if (isExpanded || displayExpandButton) {
            TextButton(
                onClick = { isExpanded = !isExpanded },
            ) {
                Text(
                    text = stringResource(id = if (isExpanded) R.string.collapse else R.string.expand)
                )
            }
        }
    }
}

@Preview
@Composable
fun ExpandableTextPreview() {
    Theme {
        Surface {
            ExpandableText(text = stringResource(id = R.string.sample_summary))
        }
    }
}
