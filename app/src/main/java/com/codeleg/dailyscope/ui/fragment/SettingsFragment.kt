package com.codeleg.dailyscope.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.codeleg.dailyscope.R
import com.codeleg.dailyscope.database.preference.SettingsDataStore
import com.codeleg.dailyscope.database.preference.settingsDataStore
import androidx.core.net.toUri

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val dataStore = SettingsDataStore(requireContext().settingsDataStore)
        preferenceManager.preferenceDataStore = dataStore
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        setupExternalLinks()

    }
    private fun setupExternalLinks() {

        val githubPref = findPreference<Preference>("github")
        val profilePref = findPreference<Preference>("developer_name")

        profilePref?.setOnPreferenceClickListener {

            val intent = Intent(
                Intent.ACTION_VIEW,
                "https://github.com/shubham-8609/".toUri()
            )

            startActivity(intent)
            true
        }

        githubPref?.setOnPreferenceClickListener {

            val intent = Intent(
                Intent.ACTION_VIEW,
                "https://github.com/shubham-8609/DailyScope-News-App-".toUri()
            )

            startActivity(intent)
            true
        }


        val contactPref = findPreference<Preference>("contact")

        contactPref?.setOnPreferenceClickListener {

            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = "mailto:shubhamguuat8609@email.com".toUri()
                putExtra(Intent.EXTRA_SUBJECT, "DailyScope Feedback")
            }

            startActivity(intent)
            true
        }
    }
}