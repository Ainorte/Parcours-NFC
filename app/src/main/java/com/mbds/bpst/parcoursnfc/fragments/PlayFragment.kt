package com.mbds.bpst.parcoursnfc.fragments

import android.annotation.SuppressLint
import android.icu.text.Transliterator
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mbds.bpst.parcoursnfc.MainActivity
import com.mbds.bpst.parcoursnfc.R
import com.mbds.bpst.parcoursnfc.databinding.FragmentPlayBinding


class PlayFragment : Fragment(){

    private lateinit var binding: FragmentPlayBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var location: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var firstLoc = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = context?.let { LocationServices.getFusedLocationProviderClient(it) }!!

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                location = locationResult.lastLocation

                var position = LatLng(location.latitude, location.longitude)

                googleMap.clear()
                if(firstLoc){
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(position))
                    firstLoc = false;
                }
                googleMap.addMarker(MarkerOptions().position(position))
            }
        }

        locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayBinding.inflate(inflater, container, false)
        var mapFragment =  childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(callback)

        binding.navigateButton.setOnClickListener {
            //TODO : Intent navigation
        }

        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
         fusedLocationClient.requestLocationUpdates(
             locationRequest,
             locationCallback,
             Looper.getMainLooper())
        activity?.title = "Parcours NFC"
        (activity as MainActivity).setMenuCreateButtonVisibility(true)
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private val callback = OnMapReadyCallback { googleMap ->
        this.googleMap = googleMap
        this.googleMap.moveCamera(CameraUpdateFactory.zoomTo(15.0f))
    }

}