package com.mbds.bpst.parcoursnfc.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
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
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mbds.bpst.parcoursnfc.MainActivity
import com.mbds.bpst.parcoursnfc.R
import com.mbds.bpst.parcoursnfc.data.entities.Etape
import com.mbds.bpst.parcoursnfc.data.models.EtapeViewModel
import com.mbds.bpst.parcoursnfc.databinding.FragmentPlayBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.UnsupportedEncodingException


class PlayFragment : Fragment(), ActionNFC{

    private lateinit var binding: FragmentPlayBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var location: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var etapeViewModel: EtapeViewModel
    private var firstLoc = true
    private lateinit var viewModel: EtapeViewModel
    private var lastEtape: Etape? = null
    private var lastMarker:MarkerOptions? = null


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

        etapeViewModel = (activity as MainActivity).etapeViewModel
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
            if(lastEtape != null){
                val gmmIntentUri: Uri = Uri.parse("geo:${location.latitude},${location.longitude}?q=" + Uri.encode("${lastEtape!!.location.latitude},${lastEtape!!.location.longitude}"))
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                startActivity(mapIntent)
            }
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
        act.setMenuButtonVisibility(R.id.createItem, true)
        act.setMenuButtonVisibility(R.id.resetItem, true)
        act.onReset = {
            lifecycleScope.launch {
                withContext(Dispatchers.IO)
                {
                    val etapes = etapeViewModel.getAllEtapeByRead(true)
                    etapes.forEach { etape -> etapeViewModel.deleteEtape(etape) }
                    withContext(Dispatchers.Main){
                        lastEtape = null
                        googleMap.clear();
                        Toast.makeText(context, "Parcours remis à zero", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
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

        lifecycleScope.launch {
            withContext(Dispatchers.IO)
            {
                val etapes = etapeViewModel.getAllEtapeByRead(true)
                lastEtape = etapes.lastOrNull()
                withContext(Dispatchers.Main){
                    if(etapes.isNotEmpty()){
                        etapes.subList(0, etapes.lastIndex - 1).forEach { etape -> googleMap.addMarker(MarkerOptions().position(etape.location).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))) }
                        lastMarker = lastEtape?.location?.let { MarkerOptions().position(it).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)) }
                        googleMap.addMarker(lastMarker)

                        binding.description.text = lastEtape!!.indice
                    }
                }
            }
        }
    }

    override fun onNFC(tag: Tag, ndef: Ndef, rawMsgs: Array<Parcelable>?, context: Context) {
        var all = ""

        val ndefMessage = rawMsgs?.size?.let { arrayOfNulls<NdefMessage>(it) }!!
        for (i in rawMsgs.indices) {
            ndefMessage[i] = rawMsgs[i] as NdefMessage
            for (j in ndefMessage[i]!!.records.indices) {
                val ndefRecord = ndefMessage[i]!!.records[j]
                val payload = ndefRecord.payload
                try {
                    val type = ndefRecord.toMimeType()
                    if(type == "application/parcoursnfc"){
                        val recordTxt = String(
                                payload, 0,
                                payload.size,
                                charset("UTF-8"))
                        all += "$recordTxt||"
                    }
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }

                if(lastEtape == null ){
                    val etape = Etape("Depart", LatLng(location.latitude, location.longitude), true)
                    etapeViewModel.insert(etape)
                    googleMap.addMarker(MarkerOptions().position(etape.location).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
                }

                val splitedAll = all.split("||")

                var loc = when (splitedAll[0] == "")
                {
                    true -> {
                        lastEtape?.location
                    }
                    false -> {
                        val split = splitedAll[0].split(";")
                        LatLng(split[0].toDouble(), split[1].toDouble())
                    }
                }

                loc?.let {
                    lastEtape =  Etape(splitedAll[1], it, true)
                    etapeViewModel.insert(lastEtape!!)
                    lastMarker?.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    lastMarker = MarkerOptions().position(lastEtape!!.location).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    googleMap.addMarker(lastMarker)

                    binding.description.text = lastEtape!!.indice
                }

                //Toast.makeText(context, all, Toast.LENGTH_SHORT).show()
            }
        }
    }

}