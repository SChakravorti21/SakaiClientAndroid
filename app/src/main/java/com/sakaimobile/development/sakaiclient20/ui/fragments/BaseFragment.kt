package com.sakaimobile.development.sakaiclient20.ui.fragments

import androidx.lifecycle.Observer
import androidx.fragment.app.Fragment
import android.widget.Toast

import com.sakaimobile.development.sakaiclient20.ui.viewmodels.BaseViewModel

abstract class BaseFragment : Fragment() {
    fun initRefreshFailureListener(viewModel: BaseViewModel, onRefreshFailed: () -> Unit) {
        viewModel.errorState.observe(viewLifecycleOwner, Observer {
            Toast.makeText(context, "We had trouble reaching Sakai!", Toast.LENGTH_SHORT).show()
            onRefreshFailed()
        })
    }
}
