package com.devdunnapps.amplify.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.ui.main.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsFragment : PreferenceFragmentCompat() {

    val viewModel: SettingsFragmentViewModel by viewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        return when (preference.key) {
            "reset_preferences" -> {
                showResetAllPreferences()
                true
            }
            else -> false
        }
    }

    private fun showResetAllPreferences() {
        MaterialAlertDialogBuilder(requireActivity())
            .setMessage("Reset Preferences")
            .setMessage(resources.getString(R.string.reset_preferences_dialog_summary))
            .setNegativeButton("Cancel") { _, _ -> }
            .setPositiveButton("Confirm") { _, _ ->
                val darkModeValues = resources.getStringArray(R.array.theme_values)
                this.preferenceManager.sharedPreferences!!.edit().putString("theme", darkModeValues[2]).commit()

                viewModel.clearAllPreferences()

                val restartApp = Intent(requireActivity(), MainActivity::class.java)
                restartApp.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(restartApp)
            }
            .show()
    }
}
