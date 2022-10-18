package com.androiddevs.RunTracker.ui.Fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.androiddevs.RunTracker.Other.Constants.ACTION_PAUSE_SERVICE
import com.androiddevs.RunTracker.Other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.androiddevs.RunTracker.Other.Constants.ACTION_STOP_SERVICE
import com.androiddevs.RunTracker.Other.TrackingUtility
import com.androiddevs.RunTracker.R
import com.androiddevs.RunTracker.database.Run
import com.androiddevs.RunTracker.services.TrackingService
import com.androiddevs.RunTracker.ui.viewmodels.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tracking.*
import java.util.*
import javax.inject.Inject
import kotlin.math.round

typealias line = MutableList<LatLng>
typealias lines = MutableList<line>

@AndroidEntryPoint
class TrackingFragment: Fragment(R.layout.fragment_tracking) {

    private val viewModel : MainViewModel by viewModels()

    private var isTracking = false
    private var pathPoints = mutableListOf<line>()

    private var map:GoogleMap?=null

    private var curTimeInMillis=0L

    private var menu:Menu? = null

    @set:Inject
    var weight = 80f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)

        btnFinishRun.setOnClickListener {
            zoomOutCamera()
            endRunSaveToDB()
        }

        btnToggleRun.setOnClickListener {
            toggleRun()
        }
       mapView.getMapAsync {
           map = it
           addAllLines()
       }

        subscribeToObservers()

    }

    private fun subscribeToObservers(){
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })

        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoints=it
            addLatestLine()
            moveCameraToUser()
        })

        TrackingService.timeRunInMillis.observe(viewLifecycleOwner, Observer {
            curTimeInMillis=it
            val formattedTime = TrackingUtility.getFormattedTime(curTimeInMillis,true)
            tvTimer.setText(formattedTime)
        })
    }

    private fun toggleRun(){
        if(isTracking){
            menu?.getItem(0)?.isVisible=true
            sendCommandToTrackingService(ACTION_PAUSE_SERVICE)
        }else{
            sendCommandToTrackingService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.tracking_menu,menu)
        this.menu=menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if(curTimeInMillis > 0L){
            this.menu?.getItem(0)?.isVisible = true
        }
    }

    private fun showCancelRunDialog(){
        val dialog = MaterialAlertDialogBuilder(requireContext(),R.style.AlertDialogTheme)
            .setTitle("Cancel the Run")
            .setMessage("Are you sure to cancel the Run?")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("YES"){ _,_ ->
                stopRun()
            }
            .setNegativeButton("NO"){dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .create()
        dialog.show()
    }

    private fun stopRun(){
        sendCommandToTrackingService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
        when(item.itemId){
            R.id.cancelRun->{
                showCancelRunDialog()
            }
        }
    }

    private fun updateTracking(isTracking:Boolean){
       this.isTracking = isTracking
        if(!isTracking){
            btnToggleRun.text="START"
            btnFinishRun.visibility=View.VISIBLE
        }else{
            btnToggleRun.text="STOP"
            btnFinishRun.visibility=View.GONE
            menu?.getItem(0)?.isVisible=true
        }
    }

    private fun moveCameraToUser(){
        if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty())
        {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    15f
                )
            )
        }
    }

    private fun zoomOutCamera(){
        val bounds = LatLngBounds.Builder()
        for(polyline in pathPoints){
            for(pos in polyline){
                bounds.include(pos)
            }
        }

        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(bounds.build(),mapView.width,mapView.height,(mapView.height*0.05f).toInt())
        )

    }

    private fun endRunSaveToDB(){
        map?.snapshot { bmp->
            var distanceInMeters = 0
            for(polyline in pathPoints){
                distanceInMeters += TrackingUtility.calculatePolylineLength(polyline).toInt()
            }
            val avgSpeed = round((distanceInMeters/1000f)/(curTimeInMillis/1000f/60/60) * 10)/10f

            val dateTimeStamp = Calendar.getInstance().timeInMillis

            val caloriesBurned = ((distanceInMeters/1000f)*weight).toInt()

            val run = Run(bmp,dateTimeStamp,distanceInMeters,caloriesBurned,curTimeInMillis,avgSpeed)

            viewModel.insertRun(run)

            Snackbar.make(
                requireActivity().findViewById(R.id.rootView),
                "Your Run is Saved Successfully",
                Snackbar.LENGTH_LONG
            ).show()
            stopRun()
        }
    }

    private fun addAllLines(){
        for(line in pathPoints){
            val lineOptions = PolylineOptions()
                .color(Color.RED)
                .width(8f)
                .addAll(line)
            map?.addPolyline(lineOptions)
        }
    }

    private fun addLatestLine(){
        if(pathPoints.isNotEmpty() && pathPoints.last().size>1){
            val preLastLatLng = pathPoints.last()[pathPoints.last().size-2]
            val lastLatLng = pathPoints.last().last()

            val lineOptions = PolylineOptions()
                .color(Color.RED)
                .width(8f)
                .add(preLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(lineOptions)

        }
    }

    fun sendCommandToTrackingService(action:String) = Intent(requireContext(),
        TrackingService::class.java
    ).also {
        it.action = action
        requireContext().startService(it)
    }




    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }



    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        mapView.onDestroy()
//    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }


}