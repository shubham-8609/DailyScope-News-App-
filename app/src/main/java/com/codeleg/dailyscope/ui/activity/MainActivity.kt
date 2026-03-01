package com.codeleg.dailyscope.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.codeleg.dailyscope.R
import com.codeleg.dailyscope.database.network.RetrofitInstance
import com.codeleg.dailyscope.database.repository.NewsRepository
import com.codeleg.dailyscope.databinding.ActivityMainBinding
import com.codeleg.dailyscope.ui.viewmodel.MainViewModel
import com.codeleg.dailyscope.ui.viewmodel.MainViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val newsRepo = NewsRepository(RetrofitInstance.newsApi)
    private val mainVM: MainViewModel by viewModels {
        MainViewModelFactory(newsRepo)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.nav_host_fragment)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        manageToolbar()
        val navControler =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.findNavController()
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.categoryFragment,
                R.id.bookmarkFragment
            )
        )
        if (navControler != null) {
            binding.bottomNav.setupWithNavController(navControler)
            binding.toolbar.setupWithNavController(navControler, appBarConfiguration)
        } else Toast.makeText(this, "Navigation controller not found", Toast.LENGTH_SHORT).show()
        navControler?.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> binding.toolbar.title = "Home"
                R.id.categoryFragment -> binding.toolbar.title = "Category"
                R.id.bookmarkFragment -> binding.toolbar.title = "Bookmark"
                R.id.articleFragment -> binding.toolbar.title = "Article"
            }
        }


    }


    private fun manageToolbar() {
        lifecycleScope.launch {
            delay(200) // Simulate loading time
            binding.toolbar.visibility = View.VISIBLE
            setSupportActionBar(binding.toolbar)

        }
    }
}