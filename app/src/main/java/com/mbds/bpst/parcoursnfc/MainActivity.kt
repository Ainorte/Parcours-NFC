package com.mbds.bpst.parcoursnfc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.mbds.bpst.parcoursnfc.databinding.ActivityMainBinding
import com.mbds.bpst.parcoursnfc.fragments.PlayFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(baseContext))
        changeFragment(PlayFragment(), false)
    }

    fun changeFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
            if (addToBackStack)
                addToBackStack(null)
        }.commit()
    }
}