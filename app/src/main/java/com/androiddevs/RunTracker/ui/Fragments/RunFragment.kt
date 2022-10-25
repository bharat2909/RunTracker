package com.androiddevs.RunTracker.ui.Fragments

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevs.RunTracker.repositories.SortType
import com.androiddevs.RunTracker.Other.TrackingUtility
import com.androiddevs.RunTracker.R
import com.androiddevs.RunTracker.adapters.RunAdapter
import com.androiddevs.RunTracker.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_run.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class RunFragment: Fragment(R.layout.fragment_run), EasyPermissions.PermissionCallbacks {

    private val viewModel : MainViewModel by viewModels()

    private lateinit var runAdapter: RunAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermissions()
        setUpRecyclerView()

        when(viewModel.sortType){
            SortType.DATE->spFilter.setSelection(0)
            SortType.TIME->spFilter.setSelection(1)
            SortType.DISTANCE->spFilter.setSelection(2)
            SortType.SPEED->spFilter.setSelection(3)
            SortType.CALORIESBURNED->spFilter.setSelection(4)
        }


        spFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{

//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
//                when(pos){
//                    0 -> viewModel.sortRuns(SortType.DATE)
//                    1 -> viewModel.sortRuns(SortType.TIME)
//                    2 -> viewModel.sortRuns(SortType.DISTANCE)
//                    3 -> viewModel.sortRuns(SortType.SPEED)
//                    4 -> viewModel.sortRuns(SortType.CALORIESBURNED)
//                }
//            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                when(pos){
                    0 -> viewModel.setSortType(SortType.DATE)
                    1 -> viewModel.setSortType(SortType.TIME)
                    2 -> viewModel.setSortType(SortType.DISTANCE)
                    3 -> viewModel.setSortType(SortType.SPEED)
                    4 -> viewModel.setSortType(SortType.CALORIESBURNED)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        viewModel.runs.observe(viewLifecycleOwner, Observer {
            runAdapter.submitList(it)
        })


        fab.setOnClickListener {
            findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
        }


    }

    private fun setUpRecyclerView(){
        rvRuns.apply {
            runAdapter = RunAdapter()
            adapter = runAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }



    private fun requestPermissions(){
        if(TrackingUtility.hasPermissions(requireContext())){
            return
        }
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            EasyPermissions.requestPermissions(
                this,
                "You Need to accept Location Permissions to use this app.",
                0,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }else{
            EasyPermissions.requestPermissions(
                this,
                "You Need to accept Location Permissions to use this app.",
                0,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            AppSettingsDialog.Builder(this).build().show()
        }else{
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }
}