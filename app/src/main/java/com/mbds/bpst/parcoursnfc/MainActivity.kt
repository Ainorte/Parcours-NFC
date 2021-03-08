package com.mbds.bpst.parcoursnfc

import android.Manifest.permission
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.mbds.bpst.parcoursnfc.databinding.ActivityMainBinding
import com.mbds.bpst.parcoursnfc.fragments.PlayFragment


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(baseContext))
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
        }
    }

    fun changeFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
            if (addToBackStack)
                addToBackStack(null)
        }.commit()
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


}