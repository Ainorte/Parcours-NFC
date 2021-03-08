package com.mbds.bpst.parcoursnfc.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mbds.bpst.parcoursnfc.R
import com.mbds.bpst.parcoursnfc.databinding.FragmentPlayBinding


class PlayFragment : Fragment() {

    private lateinit var binding:FragmentPlayBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPlayBinding.inflate(inflater, container, false)
        return binding.root
    }
}