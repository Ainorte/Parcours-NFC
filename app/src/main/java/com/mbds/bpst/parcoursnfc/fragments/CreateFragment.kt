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
import com.mbds.bpst.parcoursnfc.data.ParcoursRoomDatabase_Impl
import com.mbds.bpst.parcoursnfc.data.dao.ParcoursDao_Impl
import com.mbds.bpst.parcoursnfc.data.entities.Etape
import com.mbds.bpst.parcoursnfc.data.models.EtapeViewModel
import com.mbds.bpst.parcoursnfc.data.repository.EtapeRepository
import com.mbds.bpst.parcoursnfc.data.repository.ParcoursRepository
import com.mbds.bpst.parcoursnfc.databinding.FragmentCreateBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*


class CreateFragment : Fragment(), ActionNFC {

    private lateinit var binding: FragmentCreateBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var location: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var etapeViewModel: EtapeViewModel
    private var firstLoc = true

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
        activity?.title = "Créer un nouveau parcours"
        var act = activity as MainActivity
        act.setMenuCreateButtonVisibility(false)
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
        if(!ndef.isWritable){
            Toast.makeText(context, "Ce tag n'est pas modifiable", Toast.LENGTH_LONG).show()
        }
        else{
            var lastEtape = etapeViewModel.getAllEtapeByRead(false).last()

            val dimension = 2
            val ndefRecords = arrayOfNulls<NdefRecord>(dimension)

            var msgTxt = "${lastEtape.location.latitude};${lastEtape.location.longitude}"
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
                    Toast.makeText(context, "Message écrit avec succès", Toast.LENGTH_SHORT).show()
                    //On récupère la localisation actuelle et on l'écrit en base de donnée

                    val newEtape = Etape("", LatLng(location.latitude, location.longitude), false)
                    etapeViewModel.insert(newEtape)
                    googleMap.addMarker(MarkerOptions().position(lastEtape.location))

                } catch (e1: IOException) {
                    e1.printStackTrace()
                    Toast.makeText(context, "Erreur à l'écriture du tag. Merci de réessayer.", Toast.LENGTH_SHORT).show()

                } catch (e2: FormatException) {
                    e2.printStackTrace()
                    Toast.makeText(context, "Erreur à l'écriture du tag. Merci de réessayer.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}