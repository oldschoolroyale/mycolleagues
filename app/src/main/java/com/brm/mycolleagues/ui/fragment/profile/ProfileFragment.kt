package com.brm.mycolleagues.ui.fragment.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.brm.mycolleagues.R
import com.brm.mycolleagues.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {


    private var _binding : FragmentProfileBinding? = null
    val binding get() = _binding!!

    private val myNav by navArgs<ProfileFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =  FragmentProfileBinding.inflate(layoutInflater, container, false)
        binding.model = myNav.model
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}