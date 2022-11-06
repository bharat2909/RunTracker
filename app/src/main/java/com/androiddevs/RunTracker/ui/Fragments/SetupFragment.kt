package com.androiddevs.RunTracker.ui.Fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.androiddevs.RunTracker.Other.Constants.FIRST_TIME_TOGGLE
import com.androiddevs.RunTracker.Other.Constants.KEY_NAME
import com.androiddevs.RunTracker.Other.Constants.KEY_WEIGHT
import com.androiddevs.RunTracker.R
import com.androiddevs.RunTracker.ui.viewmodels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_setup.*
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment: Fragment(R.layout.fragment_setup) {

    private val viewModel : MainViewModel by viewModels()



    @set:Inject
    var isFirstTImeToggle = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!isFirstTImeToggle){
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.setupFragment,true)
                .build()

            findNavController().navigate(R.id.action_setupFragment_to_runFragment,savedInstanceState,navOptions)
        }

        tvContinue.setOnClickListener {
            val success = writeDataToSharedPref()
            if(success){
                findNavController().navigate(R.id.action_setupFragment_to_runFragment)
            }else{
                Snackbar.make(requireView(),"Please Enter all Details to Continue",Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun writeDataToSharedPref():Boolean{
        val name = etName.text.toString()
        val weight = etWeight.text.toString()
        if(name.isEmpty() || weight.isEmpty()){
            return false
        }
        viewModel.updateSharedPref(name,weight)

        val toolbarText = "Let's Go, ${name}!"
        requireActivity().tvToolbarTitle.text = toolbarText
        return true
    }


}