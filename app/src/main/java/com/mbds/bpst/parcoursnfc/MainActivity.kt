package com.mbds.bpst.parcoursnfc

import android.Manifest.permission
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.mbds.bpst.parcoursnfc.data.ParcoursApplication
import com.mbds.bpst.parcoursnfc.data.models.ParcoursViewModel
import com.mbds.bpst.parcoursnfc.data.models.ParcoursViewModelFactory
import com.mbds.bpst.parcoursnfc.databinding.ActivityMainBinding
import com.mbds.bpst.parcoursnfc.fragments.ActionNFC
import com.mbds.bpst.parcoursnfc.fragments.CreateFragment
import com.mbds.bpst.parcoursnfc.fragments.PlayFragment
import java.io.UnsupportedEncodingException
import kotlin.experimental.and


class MainActivity : AppCompatActivity() {

    private var fragment:Fragment? = null

    private lateinit var binding: ActivityMainBinding
    private var menu: Menu? = null
    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null

    public val parcoursViewModel: ParcoursViewModel by viewModels {
        ParcoursViewModelFactory((application as ParcoursApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(baseContext))

        launchNFC()

        if( ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            //Demander la permission d'utiliser le GPS
            if(shouldShowRequestPermissionRationale(permission.ACCESS_FINE_LOCATION)){
                //Nous avons déjà demandé la permission
                explainForPermission()
            }else{
                //On demande pour la première fois
                askForPermission()
            }
        }else{
            //c'est ok pour le GPS
            changeFragment(PlayFragment(), false)
            nfcTrigger(intent)
        }

    }

    private fun launchNFC(){
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        // check NFC feature:
        if (nfcAdapter == null) {
            needNfc()
        }

        // single top flag avoids activity multiple instances launching
        pendingIntent = PendingIntent.getActivity(this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)
    }

    override fun onResume() {
        super.onResume()

        val adapter = nfcAdapter ?: return run {
            needNfc()
        }

        if (!adapter.isEnabled) {
            // process error NFC not activated…
            Toast.makeText(this, "Votre capteur NFC est désactivé", Toast.LENGTH_LONG).show()
            finish()
        }
        // Activer la découverte de tag en --> Android va nous envoyer directement les tags détéctés
        adapter.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()

        // Soyons sympa en désactivant le NFC quand l'activité n'est plus visible
        nfcAdapter?.disableForegroundDispatch(this)
    }

    fun changeFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        this.fragment = fragment

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
            if (addToBackStack)
                addToBackStack(null)
        }.commit()
    }

    private fun needNfc() {
        Toast.makeText(this, "Cette application ne fonctionne que sur un téléphone NFC", Toast.LENGTH_LONG).show()
    }


    private fun askForPermission() {
        requestPermissions(arrayOf(permission.ACCESS_FINE_LOCATION), 0)
    }

    private fun explainForPermission() {
        Snackbar.make(
                findViewById(android.R.id.content),
                "Autoriser le GPS pour utiliser l'application",
                Snackbar.LENGTH_LONG
        ).setAction("Autoriser")
            { askForPermission() }
        .show()
    }

    private fun explainForOption(){
        Snackbar.make(findViewById(android.R.id.content), "Vous devez autoriser le GPS dans les paramètres", Snackbar.LENGTH_LONG)
            .setAction("Paramètres") {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }.show()

    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ){
        if(requestCode == 0)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                //Ok pour le GPS
                changeFragment(PlayFragment(), false)
            }
            else if (shouldShowRequestPermissionRationale(permission.ACCESS_FINE_LOCATION)) {
                // permission refusée avec demande de ne pas redemander
                explainForOption()
            }
            else
            {
                // permission refusée
                explainForPermission()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.menu, menu)
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.createItem -> {
                changeFragment(CreateFragment(), true)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun setMenuCreateButtonVisibility(visibility: Boolean){
        menu?.findItem(R.id.createItem)?.isVisible  = visibility
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        nfcTrigger(intent)
    }

    private fun nfcTrigger(intent: Intent){
        //Appelé quand on approche un tag nfc.

        val action = intent.action
        // check the event was triggered by the tag discovery
        if (NfcAdapter.ACTION_TAG_DISCOVERED == action || NfcAdapter.ACTION_TECH_DISCOVERED == action || NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            if(tag != null){
                val ndef = Ndef.get(tag)

                val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)

                if(fragment is ActionNFC){
                    (fragment as ActionNFC).onNFC(tag, ndef, rawMsgs, this)
                }
            }
        }
    }
}