package com.androiddevs.RunTracker.ui.Fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.androiddevs.RunTracker.R
import com.androiddevs.RunTracker.ui.viewmodels.MainViewModel

class RunFragment: Fragment(R.layout.fragment_run) {

    private val viewModel : MainViewModel by viewModels()
}