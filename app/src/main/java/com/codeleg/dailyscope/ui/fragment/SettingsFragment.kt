package com.codeleg.dailyscope.ui.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.codeleg.dailyscope.R
import com.codeleg.dailyscope.database.preference.SettingsDataStore
import com.codeleg.dailyscope.database.preference.settingsDataStore
import androidx.core.net.toUri
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.preference.SwitchPreferenceCompat
import com.codeleg.dailyscope.DailyScope
import com.codeleg.dailyscope.ui.viewmodel.MainViewModel
import com.codeleg.dailyscope.ui.viewmodel.MainViewModelFactory
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
    private  var clearCachePref : Preference? = null
    private  var deleteNewsPref : Preference? = null
    private val mainVM: MainViewModel by activityViewModels {
        val newsRepo = (requireActivity().application as DailyScope).newsRepository
        MainViewModelFactory(newsRepo , settingsRepo)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val dataStore = SettingsDataStore(requireContext().settingsDataStore)
        preferenceManager.preferenceDataStore = dataStore
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) findPreference<Preference>("material_you_enable")?.isVisible = true
        setupChangeListeners()
        setupExternalLinks()
         deleteNewsPref = findPreference("delete_all_news")
        deleteNewsPref?.setOnPreferenceClickListener {
            showDeleteConfirmation("Delete All News" , "This will permanently delete all stored news articles." , ::deleteAllNews )
            true
        }

         clearCachePref = findPreference("clear_cache")
        clearCachePref?.setOnPreferenceClickListener {
            showDeleteConfirmation("Clear cache ? " , "This will clear all cached news images and cannot be undone." , ::deleteCachedImages)
            true
        }
       showStorageInfo()
        findPreference<SwitchPreferenceCompat>("notifications_enabled")?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue as Boolean) {
                mainVM.onNotificationPreferenceChanged(true)
                false
            }else{
                mainVM.setNotificationAllowed(false)
                 findPreference<SwitchPreferenceCompat>("fetched_news_notification")?.isChecked = false
                findPreference<SwitchPreferenceCompat>("breaking_news_notification")?.isChecked = false
                true
            }
        }
        lifecycleScope.launch{
            mainVM.notificationAllowed.collect { findPreference<SwitchPreferenceCompat>("notifications_enabled")?.isChecked = it }
        }
    }

    private fun showStorageInfo() {
        lifecycleScope.launch(Dispatchers.IO) {
            val cacheDir = File(requireContext().cacheDir, "image_manager_disk_cache")
            val size = settingsRepo.findCacheSize(cacheDir)
            val totalNewsCount = mainVM.getTotalNewsCount()
            val newsSummary = if(totalNewsCount==0) "No news stored" else "$totalNewsCount articles stored"
            withContext(Dispatchers.Main) {
                view?.post {
                    clearCachePref?.summary = "Current cache size: $size"
                    deleteNewsPref?.summary = newsSummary
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().addMenuProvider(object : MenuProvider{
            override fun onCreateMenu(
                menu: Menu,
                menuInflater: MenuInflater
            ) {
                menu.clear()
                menuInflater.inflate(com.codeleg.dailyscope.R.menu.settings_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when(menuItem.itemId){
                    R.id.option_refresh -> {

                        true
                    }

                    else -> true
                }
            }

        }, viewLifecycleOwner , Lifecycle.State.RESUMED)
        super.onViewCreated(view, savedInstanceState)
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
            "headlines_only",
            "max_news_per_cluster",
            "news_date",
            "sync",
            "material_you_enable",
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

                    true
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
            Toast.makeText(requireContext(), "All news articles have been deleted.", Toast.LENGTH_SHORT).show()

        }
    }

    private fun deleteCachedImages() {
        lifecycleScope.launch {
            mainVM.clearCachedImages(requireContext())
            Toast.makeText(requireContext(), "Cached images have been cleared.", Toast.LENGTH_SHORT).show()
            showStorageInfo()
        }
    }


}