package com.brm.mycolleagues.ui.fragment.profile

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.brm.mycolleagues.R
import com.brm.mycolleagues.databinding.FragmentProfileBinding
import com.brm.mycolleagues.ui.fragment.profile.model.WeekResponse
import com.brm.mycolleagues.ui.fragment.profile.vm.ProfileViewModel
import com.brm.mycolleagues.utils.BaseModel
import com.brm.mycolleagues.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import me.itangqi.waveloadingview.WaveLoadingView
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    companion object{
        private const val TOTAL_HOURS : Double = 40.0
    }

    private val profileViewModel by viewModels<ProfileViewModel>()
    private lateinit var waveLoading: WaveLoadingView
    private lateinit var weekWave: WaveLoadingView

    private lateinit var dialog: Dialog

    private val monthObserver = Observer<BaseModel<Long>>{
        when(it.status){
            Status.LOADING ->{}
            Status.SUCCESS ->{
                dialog.dismiss()
                val titleValue: Long = it.response?.data!!
                val title: Double = (titleValue / 3600000).toDouble()
                val progress : Int = ((title / 160) * 100).toInt()
                val waveTitle = DecimalFormat("0.0").format(title)
                waveLoading.apply {
                    progressValue = progress
                    centerTitle = "$waveTitle часов"
                    setShapeType(WaveLoadingView.ShapeType.CIRCLE)
                    setAmplitudeRatio(60)
                    setTopTitleStrokeColor(Color.GREEN)
                    setTopTitleStrokeWidth(3f)
                    setAnimDuration(3000)
                    startAnimation()
                }
            }
            Status.ERROR ->{
                if (!dialog.isShowing){
                    dialog.show()
                }
            }
        }
    }

    private val weekObserver = Observer<BaseModel<WeekResponse>>{
        when(it.status){
            Status.LOADING ->{}
            Status.SUCCESS ->{
                dialog.dismiss()
                val workedTime: Double = (it.response?.data!!.worked_time).toDouble()
                val lastVisit: Long = it.response.data.last_visit
                if (lastVisit == 0L){
                    binding.time = "Обновлений нет..."
                }
                else{
                    val df = Date(lastVisit)
                    val vv: String = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(df)
                    binding.time = vv
                }
                val title: Double = (workedTime / 3600000)
                val waveTitle = DecimalFormat("0.0").format(title)
                val progress : Int = ((title/ TOTAL_HOURS) * 100).toInt()
                weekWave.apply {
                    centerTitle = "$waveTitle часов"
                    setShapeType(WaveLoadingView.ShapeType.CIRCLE)
                    progressValue = progress
                    setAmplitudeRatio(60)
                    setTopTitleStrokeColor(Color.GREEN)
                    setTopTitleStrokeWidth(3f)
                    setAnimDuration(3000)
                    startAnimation()
                }
            }
            Status.ERROR ->{
                if (!dialog.isShowing){
                    dialog.show()
                }
            }
        }
    }


    private var _binding : FragmentProfileBinding? = null
    val binding get() = _binding!!

    private val myNav by navArgs<ProfileFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding =  FragmentProfileBinding.inflate(layoutInflater, container, false)
        binding.model = myNav.model
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = this

        profileViewModel.apply {
            month_status.observe(viewLifecycleOwner, monthObserver)
            week_status.observe(viewLifecycleOwner, weekObserver)
        }



        dialog = Dialog(requireContext(), R.style.AppTheme_NoActionbar)
            .apply {
                setContentView(R.layout.dialog_server_error)
            }
        val btnRetry = dialog.findViewById<TextView>(R.id.dialogServerRetryText)
        val btnCancel = dialog.findViewById<TextView>(R.id.dialogServerCancel)

        waveLoading = binding.waveLoadingViewMonth
        weekWave = binding.waveLoadingView

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        btnRetry.setOnClickListener {
            profileViewModel.getMonth(myNav.model.username)
            profileViewModel.getWeek(myNav.model.username)
            dialog.dismiss()
        }

        profileViewModel.getMonth(myNav.model.username)
        profileViewModel.getWeek(myNav.model.username)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        profileViewModel.month_status.removeObserver(monthObserver)
        profileViewModel.week_status.removeObserver(weekObserver)
    }
}