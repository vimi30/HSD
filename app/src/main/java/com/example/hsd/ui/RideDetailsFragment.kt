package com.example.hsd.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.hsd.R
import com.example.hsd.databinding.FragmentRideDetailsBinding
import com.example.hsd.model.Ride
import com.example.hsd.ui.adapter.RidesAdapter
import com.example.hsd.utils.Constant
import com.example.hsd.utils.Constant.MAP_VIEW_BUNDLE_KEY
import com.example.hsd.utils.SetupUtil
import com.example.hsd.utils.SetupUtil.setupRv
import com.example.hsd.viewmodel.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import java.util.*


class RideDetailsFragment : Fragment(),OnMapReadyCallback {



    private lateinit var binding: FragmentRideDetailsBinding
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var currentRide : Ride
    private lateinit var waypointAdapter : RidesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRideDetailsBinding.inflate(layoutInflater)

        initMap(savedInstanceState)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentRide = viewModel.clicked!!
        setupRecyclerView()
        setRideDetails()
    }


    private fun initMap(savedInstanceState: Bundle?){
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY)
        }

        binding.map.apply {
            onCreate(mapViewBundle)
            getMapAsync(this@RideDetailsFragment)
        }
    }

    private fun setRideDetails() {

        binding.apply {

            val rideStartDate = SetupUtil.convertToDate(currentRide.startsAt)
            val rideEndDate = SetupUtil.convertToDate(currentRide.endsAt)

            date.text = root.context.getString(R.string.date,SetupUtil.getMonthAndDay(rideStartDate))

            val fromToTimeText = getString(R.string.startEndTime,SetupUtil.getTimeFromDate(rideStartDate),SetupUtil.getTimeFromDate(rideEndDate))

            FromToTime.text = SetupUtil.getSpannedText(fromToTimeText)

            val fare: Double = currentRide.estimatedEarningsCents/100.toDouble()
            rideFare.text = root.context.getString(R.string.fare,fare)

            showOrHideSeriesCard()

            waypointAdapter.locationList = currentRide.orderedWaypoints

            tripId.text = root.context.getString(R.string.TripDescription,
            currentRide.tripId,currentRide.estimatedRideMiles,currentRide.estimatedRideMinutes)
        }

    }

    private fun showOrHideSeriesCard() {
        if(currentRide.inSeries){
            binding.seriesCard.visibility = View.VISIBLE
        }else{
            binding.seriesCard.visibility = View.GONE
        }
    }

    private fun setupRecyclerView() {
        waypointAdapter = RidesAdapter(null,Constant.WAYPOINT)
        setupRv(binding.waypointRv,waypointAdapter)
    }

    override fun onMapReady(p0: GoogleMap) {

        val pickup = LatLng(
            currentRide.orderedWaypoints[0].location.lat,
            currentRide.orderedWaypoints[0].location.lng
        )

        val dropOff =  LatLng(
            currentRide.orderedWaypoints[currentRide.orderedWaypoints.size-1].location.lat,
            currentRide.orderedWaypoints[currentRide.orderedWaypoints.size-1].location.lng
        )

        p0.addMarker(MarkerOptions().position(pickup).title(currentRide.orderedWaypoints[0].location.address)
            .icon(bitmapDescriptorFromVector(requireActivity(),R.drawable.pickup_marker)))

        p0.addMarker(MarkerOptions().position(dropOff).title(currentRide.orderedWaypoints[currentRide.orderedWaypoints.size-1].location.address)
            .icon(bitmapDescriptorFromVector(requireActivity(),R.drawable.dropoff_marker)))

        val cameraPosition = CameraPosition.Builder()
            .bearing(15.0f)
            .tilt(0.0f)
            .target(midPoint(pickup,dropOff))
            .zoom(9.5f)
            .build()

        p0.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))


    }

    private fun midPoint(pickup: LatLng, dropOff: LatLng): LatLng {

        return LatLng(
            (pickup.latitude+dropOff.latitude)/2,
            (pickup.longitude+dropOff.longitude)/2
        )

    }

    override fun onStart() {
        super.onStart()
        binding.map.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.map.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.map.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.map.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle: Bundle? = outState.getBundle(MAP_VIEW_BUNDLE_KEY)

        if(mapViewBundle==null){
            mapViewBundle = Bundle()
            outState.putBundle(MAP_VIEW_BUNDLE_KEY,mapViewBundle)
        }
        binding.map.onSaveInstanceState(mapViewBundle)
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }
}