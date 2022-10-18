package com.androiddevs.RunTracker.ui.Fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.androiddevs.RunTracker.Other.Constants.KEY_NAME
import com.androiddevs.RunTracker.Other.Constants.KEY_WEIGHT
import com.androiddevs.RunTracker.R
import com.androiddevs.RunTracker.ui.viewmodels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_settings.*
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment: Fragment(R.layout.fragment_settings) {

    @Inject
    lateinit var sharedPref : SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadChangesFromSharedPref()
        btnApplyChanges.setOnClickListener{
            val success = applyChangesToSHaredPref()
            if(success){
                Snackbar.make(view,"Changes Saved!",Snackbar.LENGTH_LONG).show()
            }else{
                Snackbar.make(view,"Fill all details to continue",Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun loadChangesFromSharedPref(){
        val name = sharedPref.getString(KEY_NAME,"")
        val weight = sharedPref.getFloat(KEY_WEIGHT,80f)
        etName.setText(name)
        etWeight.setText(weight.toString())
    }

    private fun applyChangesToSHaredPref():Boolean{
        val name = etName.text.toString()
        val weight = etWeight.text.toString()
        if(name.isEmpty() || weight.isEmpty()){
            return false
        }
        sharedPref.edit()
            .putString(KEY_NAME,name)
            .putFloat(KEY_WEIGHT,weight.toFloat())
            .apply()
        val toolbarText = "Let's Go, ${name}!"
        requireActivity().tvToolbarTitle.text = toolbarText
        return true
    }


}