package com.bphilip.botree.ui.post_meditation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.ShareActionProvider
import androidx.core.view.MenuItemCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bphilip.botree.*
import com.bphilip.botree.ui.reflections.NewReflectionActivity
import com.bphilip.botree.ui.reflections.ReflectionsViewModel

class PostMeditation : AppCompatActivity() {

    private lateinit var postMeditationViewModel: PostMeditationViewModel
    private val newWordActivityRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.nav_default_pop_enter_anim, R.anim.nav_default_pop_exit_anim)
        setContentView(R.layout.activity_post_meditation)

        postMeditationViewModel =
            ViewModelProviders.of(this).get(PostMeditationViewModel::class.java)

        // Change the textView.
        val textView: TextView = findViewById(R.id.time_meditated_for)
        postMeditationViewModel.text.observe(this, Observer {
            textView.text = it
        })

        if (postMeditationViewModel.pullValue) {
            postMeditationViewModel.meditatedFor = intent.getLongExtra(EXTRA_TIMER, 600000)
            postMeditationViewModel.pullValue = false
        }

        // Update the PostMeditation Text to be whatever the user has meditated for.
        postMeditationViewModel.text.value =
            Utility.timeFormatter(postMeditationViewModel.meditatedFor, applicationContext)

        val endSessionButton : Button = findViewById(R.id.button_end_session)
        endSessionButton.setOnClickListener { onSupportNavigateUp() }

        val newReflectionButton : Button = findViewById(R.id.button_add_reflection)
        newReflectionButton.setOnClickListener {
            // Start the new meditation view.
            Intent(applicationContext, NewReflectionActivity::class.java).also { intent ->
                startActivityForResult(intent, newWordActivityRequestCode)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Only need reflectionsViewModel for storing reflections that return to the PostMeditation
        // screen.
        val reflectionsViewModel = ViewModelProviders.of(this).get(ReflectionsViewModel::class.java)

        Utility.createReflectionFromIntent(
            requestCode,
            resultCode,
            data,
            applicationContext,
            reflectionsViewModel
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        /**
         * Create the sharing menu.
         */
        menuInflater.inflate(R.menu.menu_post_meditation, menu)

        val shareItem: MenuItem = menu.findItem(R.id.action_share)
        val shareActionProvider: ShareActionProvider = MenuItemCompat.getActionProvider(shareItem) as ShareActionProvider

        // Create the sharing intent.
        val sharingIntent: Intent = Intent(Intent.ACTION_SEND)
            .setType("text/plain")
            .putExtra(Intent.EXTRA_SUBJECT, "\n\n")
            .putExtra(Intent.EXTRA_TEXT, getString(R.string.share_post_meditation, postMeditationViewModel.text.value))

        shareActionProvider.setShareIntent(sharingIntent)

        return true
    }


}
