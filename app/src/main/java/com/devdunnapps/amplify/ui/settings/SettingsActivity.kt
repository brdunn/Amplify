package com.devdunnapps.amplify.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.Preferences
import com.devdunnapps.amplify.domain.models.ThemeConfig
import com.devdunnapps.amplify.ui.components.ListPreferenceCell
import com.devdunnapps.amplify.ui.components.ListPreferenceItem
import com.devdunnapps.amplify.ui.components.LoadingScreen
import com.devdunnapps.amplify.ui.components.StaticTextCell
import com.devdunnapps.amplify.ui.main.MainActivity
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    val viewModel: SettingsActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Mdc3Theme {
                SettingsScreen(
                    uiState = viewModel.uiState.collectAsState().value,
                    onNavigateUpClick = { onBackPressed() },
                    onChooseThemeClick = viewModel::changeTheme,
                    onSignOutClick = {  handleSignOut() }
                )
            }
        }
    }

    private fun handleSignOut() {
        MaterialAlertDialogBuilder(this)
            .setMessage(resources.getString(R.string.logout))
            .setMessage(resources.getString(R.string.sign_out_dialog_summary))
            .setNegativeButton(resources.getString(R.string.cancel)) { _, _ -> }
            .setPositiveButton(resources.getString(R.string.confirm)) { _, _ ->
                viewModel.signOut()

                val restartApp = Intent(this, MainActivity::class.java)
                restartApp.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(restartApp)
            }
            .show()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    uiState: SettingsScreenUIState,
    onNavigateUpClick: () -> Unit,
    onChooseThemeClick: (ThemeConfig) -> Unit,
    onSignOutClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateUpClick) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null)
                    }
                },
                title = { Text(text = stringResource(id = R.string.settings)) }
            )
        }
    ) { paddingValues ->
        when(uiState) {
            is SettingsScreenUIState.Loading -> LoadingScreen()
            is SettingsScreenUIState.Content -> SettingsScreenContent(
                preferences = uiState.preferences,
                onChooseThemeClick = onChooseThemeClick,
                onSignOutClick = onSignOutClick,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
private fun SettingsScreenContent(
    preferences: Preferences,
    onChooseThemeClick: (ThemeConfig) -> Unit,
    onSignOutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        item {
            val theme = preferences.themeConfig
            val darkModeTitles = stringArrayResource(R.array.theme_titles)
            val themeDescription = darkModeTitles[theme.ordinal]

            ListPreferenceCell(
                title = R.string.choose_theme_preference_title,
                description = themeDescription,
                items = listOf(
                    ListPreferenceItem(
                        title = stringResource(id = R.string.theme_system_default),
                        isSelected = theme == ThemeConfig.FOLLOW_SYSTEM,
                        value = ThemeConfig.FOLLOW_SYSTEM
                    ),
                    ListPreferenceItem(
                        title = stringResource(id = R.string.theme_light),
                        isSelected = theme == ThemeConfig.LIGHT,
                        value = ThemeConfig.LIGHT
                    ),
                    ListPreferenceItem(
                        title = stringResource(id = R.string.theme_dark),
                        isSelected = theme == ThemeConfig.DARK,
                        value = ThemeConfig.DARK
                    )
                ),
                onClick = { listPreferenceItem -> listPreferenceItem?.let { onChooseThemeClick(it.value as ThemeConfig) } }
            )
        }

        item {
            StaticTextCell(
                title = R.string.sign_out_preference_title,
                description = R.string.sign_out_preference_description,
                onClick = onSignOutClick
            )
        }
    }
}
