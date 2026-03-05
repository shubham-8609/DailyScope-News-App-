package com.codeleg.dailyscope.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.codeleg.dailyscope.R
import com.codeleg.dailyscope.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
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
            setSupportActionBar(binding.toolbar)
    }
}