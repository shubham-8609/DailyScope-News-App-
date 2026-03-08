package com.codeleg.dailyscope.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
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
        setupChangeListeners()
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
                data = "mailto:shubhamgupta8609@email.com".toUri()
                putExtra(Intent.EXTRA_SUBJECT, "DailyScope Feedback")
            }

            startActivity(intent)
            true
        }
    }


    private fun setupChangeListeners() {

        val keys = listOf(
            "country",
            "language",
            "headlines_only",
            "max_news_per_cluster",
            "news_date",
            "sync",
            "attachment"
        )

        keys.forEach { key ->

            val pref = findPreference<Preference>(key)

            pref?.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, _ ->

                    Toast.makeText(
                        requireContext(),
                        "Changes will take effect after restart",
                        Toast.LENGTH_SHORT
                    ).show()

                    true // allow change
                }
        }
        }
}