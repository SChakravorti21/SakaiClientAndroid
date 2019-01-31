package com.sakaimobile.development.sakaiclient20.ui.fragments


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

import com.sakaimobile.development.sakaiclient20.R
import com.sakaimobile.development.sakaiclient20.ui.activities.MainActivity
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.LoadingPageViewModel
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.ViewModelFactory
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_loading.*
import javax.inject.Inject
import android.view.animation.DecelerateInterpolator
import android.animation.ObjectAnimator



/**
 * A simple [Fragment] subclass.
 *
 */
class LoadingActivity : AppCompatActivity() {

    @Inject lateinit var viewModelFactory: ViewModelFactory
    private lateinit var loadingPageViewModel: LoadingPageViewModel
    private var hasShowedContent = false

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)
        this.loadingPageViewModel =
                ViewModelProviders.of(this, viewModelFactory)[LoadingPageViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()

        loadingPageViewModel.getRefreshProgress(true).observe(this, Observer { progress ->
            updateProgress(progress)
            // Use hasShowedContent to prevent double-loading the next page
            if(progress == LoadingPageViewModel.NUM_REFRESH_REQUESTS && !hasShowedContent) {
                hasShowedContent = true
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
    }

    private fun updateProgress(progress: Int?) {
        val displayProgress = progress?.times((100 / LoadingPageViewModel.NUM_REFRESH_REQUESTS)) ?: 0
        val animation = ObjectAnimator.ofInt(progressbar, "progress", displayProgress)
        animation.duration = 500 // 0.5 second
        animation.interpolator = DecelerateInterpolator()
        animation.start()
    }
}
