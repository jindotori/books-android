package com.books.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.books.R
import com.books.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        private const val CLOSE_TIMEOUT = 2000
    }

    private var onBackPressedTime = 0L
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private val navController: NavController by lazy {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHostFragment.navController
    }

    override fun onBackPressed() {
        when (navController.currentDestination?.id) {
            R.id.detailFragment -> navController.popBackStack()
            else -> delayFinish()
        }
    }

    private fun delayFinish() {
        when (System.currentTimeMillis() > onBackPressedTime + CLOSE_TIMEOUT) {
            true -> {
                onBackPressedTime = System.currentTimeMillis()
                Toast.makeText(applicationContext, R.string.noti_exit, Toast.LENGTH_SHORT)
                    .show()
            }

            false -> {
                finish()
            }
        }
    }
}