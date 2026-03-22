package com.codeleg.dailyscope.ui.activity

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.codeleg.dailyscope.DailyScope
import com.codeleg.dailyscope.R
import com.codeleg.dailyscope.databinding.ActivityMainBinding
import com.codeleg.dailyscope.ui.viewmodel.MainViewModel
import com.codeleg.dailyscope.ui.viewmodel.MainViewModelFactory
import com.google.android.material.color.DynamicColors
import kotlinx.coroutines.launch
import kotlin.getValue

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainVM : MainViewModel by viewModels {
        val newsRepo = (application as DailyScope).newsRepository
        val settingsRepo = (application as DailyScope).settingsRepository
        MainViewModelFactory(newsRepo, settingsRepo)
    }
    private val notificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ granted ->
        if(granted){
            Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
            mainVM.setNotificationAllowed(true)
        } else {
            Toast.makeText(this, "Notification permission denied. You can enable it later in settings.", Toast.LENGTH_SHORT).show()
           mainVM.setNotificationAllowed(false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right,0)
            insets
        }

        manageToolbar()
        val navControler =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.findNavController()
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.searchFragment,
                R.id.bookmarkFragment,
                R.id.settingsFragment
            )
        )
        if (navControler != null) {
            binding.bottomNav.setupWithNavController(navControler)
            binding.toolbar.setupWithNavController(navControler, appBarConfiguration)
        } else Toast.makeText(this, "Navigation controller not found", Toast.LENGTH_SHORT).show()
        navControler?.addOnDestinationChangedListener { _, destination, _ ->
            with(binding) {
                bottomNav.visibility = android.view.View.VISIBLE
            when (destination.id) {
                    R.id.homeFragment -> {
                    toolbar.title = "Home"
                    toolbar.subtitle = "Latest news and updates"
                }
                    R.id.searchFragment -> {
                    toolbar.title = "Search News"
                    toolbar.subtitle = "Find news you care about"
                }
                    R.id.bookmarkFragment ->  {
                    toolbar.title = "Bookmark"
                    toolbar.subtitle = "Your saved articles"
                }
                    R.id.articleFragment -> {
                    toolbar.title = "Article"
                    toolbar.subtitle = "Read the full article"
                    bottomNav.visibility = android.view.View.GONE
                }
                    R.id.settingsFragment ->  {
                    toolbar.title = "Settings"
                    toolbar.subtitle = "Customize your experience"
                }

                }
            }
        }

        lifecycleScope.launch {
            mainVM.darkMode.collect { it ->
                if (it) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }
        lifecycleScope.launch {
            mainVM.materialYou.collect { it ->
                if (it) {
                    DynamicColors.applyToActivityIfAvailable(this@MainActivity)
                }
            }
        }
        lifecycleScope.launch {
            mainVM.requestNotificationPermission.collect {
                notificationPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
    }

    private fun manageToolbar() {
            setSupportActionBar(binding.toolbar)
    }
}