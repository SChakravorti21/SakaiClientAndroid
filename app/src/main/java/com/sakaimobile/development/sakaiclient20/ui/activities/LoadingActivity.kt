package com.sakaimobile.development.sakaiclient20.ui.activities


import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.sakaimobile.development.sakaiclient20.R
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.LoadingPageViewModel
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.ViewModelFactory
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_loading.*
import javax.inject.Inject
import android.view.animation.DecelerateInterpolator
import android.animation.ObjectAnimator
import android.widget.Toast


/**
 * A simple [Fragment] subclass.
 *
 */
class LoadingActivity : AppCompatActivity() {

    @Inject lateinit var viewModelFactory: ViewModelFactory
    private lateinit var loadingPageViewModel: LoadingPageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        this.loadingPageViewModel =
                ViewModelProviders.of(this, viewModelFactory)[LoadingPageViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()

        // Just in case network connectivity changes as refresh is occurring,
        // make sure the user is notified of this.
        loadingPageViewModel.errorState.observe(this, Observer {
            Toast.makeText(this, "We had trouble reaching Sakai, please restart this application", Toast.LENGTH_LONG).show()
        })

        loadingPageViewModel.getRefreshProgress(true).observe(this, Observer { progress ->
            updateProgress(progress)
            // Use hasShowedContent to prevent double-loading the next page
            // If progress equals NUM_REFRESH_REQUESTS then we finished loading everything
            if(progress == LoadingPageViewModel.NUM_REFRESH_REQUESTS) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
    }

    private fun updateProgress(progress: Int?) {
        // Create an animation for smooth update of progress bar
        val displayProgress = progress?.times((100 / LoadingPageViewModel.NUM_REFRESH_REQUESTS)) ?: 0
        val animation = ObjectAnimator.ofInt(progressbar, "progress", displayProgress)
        animation.duration = 500 // 0.5 second
        animation.interpolator = DecelerateInterpolator()
        animation.start()
    }
}
