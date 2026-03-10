package com.codeleg.dailyscope.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.codeleg.dailyscope.R
import com.codeleg.dailyscope.database.preference.SettingsDataStore
import com.codeleg.dailyscope.database.preference.settingsDataStore
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.codeleg.dailyscope.DailyScope
import com.codeleg.dailyscope.ui.viewmodel.MainViewModel
import com.codeleg.dailyscope.ui.viewmodel.MainViewModelFactory
import com.codeleg.dailyscope.utils.showWarningToast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.getValue

class SettingsFragment : PreferenceFragmentCompat() {

    private val settingsRepo by lazy {
        (requireActivity().application as DailyScope).settingsRepository
    }
    private val mainVM: MainViewModel by activityViewModels {
        val newsRepo = (requireActivity().application as DailyScope).newsRepository
        MainViewModelFactory(newsRepo)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val dataStore = SettingsDataStore(requireContext().settingsDataStore)
        preferenceManager.preferenceDataStore = dataStore
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        setupChangeListeners()
        setupExternalLinks()
        val deleteNewsPref = findPreference<Preference>("delete_all_news")
        deleteNewsPref?.setOnPreferenceClickListener {
            showDeleteConfirmation("Delete All News" , "This will permanently delete all stored news articles." , ::deleteAllNews )
            true
        }
        val clearCachePref = findPreference<Preference>("clear_cache")
        clearCachePref?.setOnPreferenceClickListener {
            showDeleteConfirmation("Clear cache ? " , "This will clear all cached news images and cannot be undone." , ::deleteCachedImages)
            true
        }
        lifecycleScope.launch(Dispatchers.IO) {
            val cacheDir = File(requireContext().cacheDir, "image_manager_disk_cache")
            val size = settingsRepo.findCacheSize(cacheDir)
            withContext(Dispatchers.Main){clearCachePref?.summary = " Current cache size: $size"}
        }

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

    private fun showDeleteConfirmation(title: String , message: String , onConfirm: () -> Unit ) {

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Delete") { _, _ ->
                onConfirm()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteAllNews() {

        lifecycleScope.launch {
            mainVM.clearNewsDB()
            requireActivity().showWarningToast(requireActivity() , "All news articles have been deleted.")

        }
    }

    private fun deleteCachedImages() {
        lifecycleScope.launch {
            mainVM.clearCachedImages(requireContext())
            requireActivity().showWarningToast(requireActivity() , "Cached images have been cleared.")
        }
    }


}