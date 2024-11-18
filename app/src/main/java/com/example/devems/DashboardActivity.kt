package com.example.devems

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.Toast

class DashboardActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var fabMain: FloatingActionButton
    private lateinit var fabCreateEvent: FloatingActionButton
    private lateinit var fabMaps: FloatingActionButton
    private lateinit var fabMenu: View
    private var isFabMenuOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        fabMain = findViewById(R.id.fabMain)
        fabCreateEvent = findViewById(R.id.fabCreateEvent)
        fabMaps = findViewById(R.id.fabMaps)
        fabMenu = findViewById(R.id.fabMenu)


        viewPager.adapter = ViewPagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when(position) {
                0 -> "Today"
                1 -> "All Events"
                2 -> "Completed"
                else -> null
            }
        }.attach()

        fabMain.setOnClickListener {
            toggleFabMenu()
        }

        fabCreateEvent.setOnClickListener {
            val intent = Intent(this, CreateEvent::class.java)
            startActivity(intent)
            toggleFabMenu()
        }

        fabMaps.setOnClickListener {
            val intent = Intent(this, UserNavigation::class.java)
            startActivity(intent)
            toggleFabMenu()
        }

        findViewById<ImageButton>(R.id.infoButton).setOnClickListener {
            showInfoDialog()
        }

        findViewById<ImageButton>(R.id.ratingButton).setOnClickListener {
            showRatingDialog()
        }

    }

    private fun toggleFabMenu() {
        if (isFabMenuOpen) {
            fabMenu.visibility = View.GONE
            isFabMenuOpen = false
        } else {
            fabMenu.visibility = View.VISIBLE
            isFabMenuOpen = true
        }
    }

    private fun showInfoDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_info, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("OK", null)
        builder.create().show()
    }

    private fun showRatingDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_rate, null)
        val ratingBar = dialogView.findViewById<RatingBar>(R.id.ratingBar)

        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)

        val dialog = builder.create()
        dialog.show()

        dialogView.findViewById<Button>(R.id.rateButton).setOnClickListener {
            val rating = ratingBar.rating
            val message = when (rating) {
                1f -> "We will try our best to impress you next time!"
                2f -> "Thank you! We appreciate your feedback."
                3f -> "Thanks for your feedback!"
                4f -> "Thank you! We are glad you liked it."
                5f -> "Thank you so much! We're thrilled to impress you!"
                else -> "Thank you for rating!"
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
    }

}
