package com.mbds.bpst.parcoursnfc.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.nfc.FormatException
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.os.Looper
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mbds.bpst.parcoursnfc.MainActivity
import com.mbds.bpst.parcoursnfc.R
import com.mbds.bpst.parcoursnfc.data.entities.Etape
import com.mbds.bpst.parcoursnfc.data.models.EtapeViewModel
import com.mbds.bpst.parcoursnfc.databinding.FragmentCreateBinding
import kotlinx.coroutines.*
import java.io.IOException
import java.nio.charset.Charset
import java.time.Instant


class CreateFragment : Fragment(), ActionNFC {

    private lateinit var binding: FragmentCreateBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var location: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var etapeViewModel: EtapeViewModel
    private var firstLoc = true
    private var lastEtape:Etape? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = context?.let { LocationServices.getFusedLocationProviderClient(it) }!!

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                location = locationResult.lastLocation

                if(firstLoc){
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(location.latitude, location.longitude)))
                    firstLoc = false;
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
        binding = FragmentCreateBinding.inflate(inflater, container, false)
        var mapFragment =  childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(callback)
        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper())
        activity?.title = "Cr??er un nouveau parcours"
        var act = activity as MainActivity
        act.setMenuButtonVisibility(R.id.createItem,false)
        act.setMenuButtonVisibility(R.id.resetItem,true)
        act.onReset = {
            lifecycleScope.launch {
                withContext(Dispatchers.IO)
                {
                    val etapes = etapeViewModel.getAllEtapeByRead(false)
                    etapes.forEach { etape -> etapeViewModel.deleteEtape(etape) }
                    withContext(Dispatchers.Main){
                        lastEtape = null
                        googleMap.clear();
                        Toast.makeText(context, "Parcours remis ?? zero", Toast.LENGTH_LONG).show()
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
                val etapes = etapeViewModel.getAllEtapeByRead(false)
                lastEtape = etapes.lastOrNull()
                withContext(Dispatchers.Main){
                    etapes.forEach { etape -> googleMap.addMarker(MarkerOptions().position(etape.location)) }
                }
            }
        }
    }

    override fun onNFC(tag: Tag, ndef: Ndef, rawMsgs: Array<Parcelable>?, context: Context) {
        if(!ndef.isWritable){
            Toast.makeText(context, "Ce tag n'est pas modifiable", Toast.LENGTH_LONG).show()
        }
        else{

            val dimension = 2
            val ndefRecords = arrayOfNulls<NdefRecord>(dimension)

            var msgTxt =  lastEtape?.let {
                "${it.location.latitude};${it.location.longitude}"
            } ?: ""
            var mimeType = "application/parcoursnfc" // your MIME type
            var ndefRecord = NdefRecord.createMime(
                mimeType,
                msgTxt.toByteArray(Charset.forName("UTF-8"))
            )
            ndefRecords[0] = ndefRecord

            msgTxt = binding.descField.text.toString()
            mimeType = "application/parcoursnfc" // your MIME type
            ndefRecord = NdefRecord.createMime(
                mimeType,
                msgTxt.toByteArray(Charset.forName("UTF-8"))
            )
            ndefRecords[1] = ndefRecord

            val ndefMessage = NdefMessage(ndefRecords)
            val messageSize = ndefMessage.toByteArray().size

            if (ndef.maxSize >= messageSize) {
                try {
                    ndef.connect()
                    ndef.writeNdefMessage(ndefMessage)
                    ndef.close()
                    Toast.makeText(context, "Message ??crit avec succ??s", Toast.LENGTH_SHORT).show()
                    binding.descField.setText("")
                    //On r??cup??re la localisation actuelle et on l'??crit en base de donn??e

                    lastEtape = Etape("", LatLng(location.latitude, location.longitude), false)
                    etapeViewModel.insert(lastEtape!!)

                    googleMap.addMarker( MarkerOptions().position(lastEtape!!.location))

                } catch (e1: IOException) {
                    e1.printStackTrace()
                    Toast.makeText(context, "Erreur ?? l'??criture du tag. Merci de r??essayer.", Toast.LENGTH_SHORT).show()

                } catch (e2: FormatException) {
                    e2.printStackTrace()
                    Toast.makeText(context, "Erreur ?? l'??criture du tag. Merci de r??essayer.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}