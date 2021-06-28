package com.brm.mycolleagues.ui.fragment.profile

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.brm.mycolleagues.R
import com.brm.mycolleagues.databinding.FragmentProfileBinding
import me.itangqi.waveloadingview.WaveLoadingView


class ProfileFragment : Fragment() {
    companion object{
        private const val TOTAL_HOURS : Double = 40.0
    }


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

        val progress : Int = ((14.0 / TOTAL_HOURS) * 100).toInt()
        Log.d("oldschool", progress.toString())
        val waveLoading = binding.waveLoadingView
        waveLoading.setShapeType(WaveLoadingView.ShapeType.CIRCLE)
        waveLoading.progressValue = progress
        waveLoading.setAmplitudeRatio(60)
        waveLoading.setTopTitleStrokeColor(Color.GREEN)
        waveLoading.setTopTitleStrokeWidth(3f)
        waveLoading.setAnimDuration(3000)
        waveLoading.startAnimation()


    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}