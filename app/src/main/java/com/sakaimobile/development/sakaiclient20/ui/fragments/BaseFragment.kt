package com.sakaimobile.development.sakaiclient20.ui.fragments

import android.arch.lifecycle.Observer
import android.support.v4.app.Fragment
import android.widget.Toast

import com.sakaimobile.development.sakaiclient20.ui.viewmodels.BaseViewModel
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.SakaiErrorState

abstract class BaseFragment : Fragment() {
    fun initRefreshFailureListener(viewModel: BaseViewModel, onRefreshFailed: () -> Unit) {
        viewModel.errorState.observe(viewLifecycleOwner, Observer { error ->
            val message = when(error) {
                SakaiErrorState.SESSION_EXPIRED -> "Your Sakai session has expired, please restart the app."
                SakaiErrorState.FAILURE, null -> "We had trouble reaching Sakai!"
            }

            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            onRefreshFailed()
        })
    }
}
