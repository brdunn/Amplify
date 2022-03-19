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
            "sign_out" -> {
                signOut()
                true
            }
            else -> false
        }
    }

    private fun signOut() {
        MaterialAlertDialogBuilder(requireActivity())
            .setMessage("Logout")
            .setMessage(resources.getString(R.string.sign_out_dialog_summary))
            .setNegativeButton("Cancel") { _, _ -> }
            .setPositiveButton("Confirm") { _, _ ->
                viewModel.signOut()

                val restartApp = Intent(requireActivity(), MainActivity::class.java)
                restartApp.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(restartApp)
            }
            .show()
    }
}
