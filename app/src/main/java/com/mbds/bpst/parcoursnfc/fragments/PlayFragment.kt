package com.mbds.bpst.parcoursnfc.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.nfc.NdefMessage
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.os.Looper
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.mbds.bpst.parcoursnfc.MainActivity
import com.mbds.bpst.parcoursnfc.R
import com.mbds.bpst.parcoursnfc.data.models.EtapeViewModel
import com.mbds.bpst.parcoursnfc.databinding.FragmentPlayBinding
import java.io.UnsupportedEncodingException
import kotlin.experimental.and


class PlayFragment : Fragment(), ActionNFC{

    private lateinit var binding: FragmentPlayBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var location: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var firstLoc = true
    private lateinit var viewModel: EtapeViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = context?.let { LocationServices.getFusedLocationProviderClient(it) }!!

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                location = locationResult.lastLocation
                if(firstLoc){
                    firstLoc = false
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(location.latitude, location.longitude)))
                }
            }
        }

        locationRequest = LocationRequest.create()?.apply {
            interval = 5000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = (activity as MainActivity).etapeViewModel
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

        var act = activity as MainActivity
        act.setMenuCreateButtonVisibility(true)

    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        this.googleMap = googleMap
        this.googleMap.moveCamera(CameraUpdateFactory.zoomTo(17.0f))
        this.googleMap.isMyLocationEnabled = true
    }

    override fun onNFC(tag: Tag, ndef: Ndef, rawMsgs: Array<Parcelable>?, context: Context) {
        var all = ""

        val ndefMessage = rawMsgs?.size?.let { arrayOfNulls<NdefMessage>(it) }!!
        for (i in rawMsgs.indices) {
            ndefMessage[i] = rawMsgs[i] as NdefMessage
            for (j in ndefMessage[i]!!.records.indices) {
                val ndefRecord = ndefMessage[i]!!.records[j]
                val payload = ndefRecord.payload
                val languageSize: Int = (payload[0] and 51.toByte()).toInt()
                try {
                    val type = ndefRecord.toMimeType()
                    val recordTxt = String(
                        payload, 0,
                        payload.size,
                        charset("UTF-8"))
                    all += recordTxt
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }

                Toast.makeText(context, all, Toast.LENGTH_SHORT).show()
            }
        }
    }

}