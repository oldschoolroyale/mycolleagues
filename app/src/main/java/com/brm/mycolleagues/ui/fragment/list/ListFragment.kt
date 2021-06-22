package com.brm.mycolleagues.ui.fragment.list

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.brm.mycolleagues.R
import com.brm.mycolleagues.databinding.FragmentListBinding
import com.brm.mycolleagues.ui.fragment.list.model.PersonModel
import com.brm.mycolleagues.ui.fragment.list.vm.ListViewModel
import com.brm.mycolleagues.utils.BaseModel
import com.brm.mycolleagues.utils.Status
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import kotlinx.android.synthetic.main.fragment_list.view.*


class ListFragment : Fragment(), SearchView.OnQueryTextListener, EasyPermissions.PermissionCallbacks {
    companion object{
        private const val REQUEST_CHECK_SETTING = 1001
        private const val PERMISSION_LOCATION_REQUEST = 1
    }

    private var _binding : FragmentListBinding? = null
    val binding get() = _binding!!

    private val listViewModel by viewModels<ListViewModel>()
    private val adapter = com.brm.mycolleagues.ui.fragment.list.adapter.ListAdapter()

    private lateinit var fusedLocation: FusedLocationProviderClient


    private val listObservable = Observer<BaseModel<List<PersonModel>>>{
        when(it.status){
            Status.LOADING ->{}
            Status.SUCCESS ->{adapter.newList(it.response!!.data!!)}
            Status.ERROR ->{}
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        _binding =  FragmentListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.fragmentListRecycler.adapter = adapter
        view.fragmentListRecycler.layoutManager = LinearLayoutManager(requireContext())
        listViewModel.loadList()
        listViewModel.loading_status.observe(viewLifecycleOwner, listObservable)

        fusedLocation   = LocationServices.getFusedLocationProviderClient(requireContext())

        binding.fragmentListFab.setOnClickListener {
                val dialogBuilder = AlertDialog.Builder(view.context)
                dialogBuilder.setMessage(view.context.getString(R.string.start_work_question))
                    // if the dialog is cancelable
                    .setCancelable(true)
                    .setPositiveButton(view.context.getString(R.string.start), DialogInterface.OnClickListener {
                            dialog, id -> locationRequest()
                    })
                    .setNegativeButton(view.context.getString(R.string.cancel)) { _, _ ->}
                val alert = dialogBuilder.create()
                alert.show()

        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null){
            adapter.filter.filter(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null){
            adapter.filter.filter(newText)
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_menu, menu)
        val search = menu.findItem(R.id.paymentAddMenuSearch)
        val searchView = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTING){
            when(resultCode){
                Activity.RESULT_OK ->{showMessage("Gps is on, ListFragment")}
                Activity.RESULT_CANCELED ->{showMessage("Gps is required, ListFragment")}
            }
        }
    }

    private fun showMessage(message:String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun locationRequest(){
        REQUEST_CHECK_SETTING
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 2000

        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result = LocationServices.getSettingsClient(requireContext())
            .checkLocationSettings(builder.build())
        result.addOnCompleteListener {
            try {
                getLastLocation()
            }
            catch (e: ApiException){
                when(e.statusCode){
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->{
                        try {
                            val resolvableApi = e as ResolvableApiException
                            resolvableApi.startResolutionForResult(requireActivity(), REQUEST_CHECK_SETTING)
                        }
                        catch (ex: IntentSender.SendIntentException){
                           Log.d("brm", "Error: Settings change unavailable")
                        }
                        return@addOnCompleteListener
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE ->{
                        Log.d("brm", "Settings change unavailable")
                        return@addOnCompleteListener
                    }
                }
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {

        Log.d("oldschool", "tut")
        if (hasLocationPermission()){
            val mLocationRequest = LocationRequest.create()
            mLocationRequest.interval = 60000
            mLocationRequest.fastestInterval = 5000
            mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            val mLocationCallback: LocationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    if (locationResult == null) {
                        return
                    }
                    for (location in locationResult.locations) {
                        if (location != null) {
                            val dist = FloatArray(1)
                            Location.distanceBetween(location.latitude, location.longitude, 41.342770, 69.344153, dist)
                            if(dist[0]/1000 > 1){
                                Toast.makeText(requireContext(), "Вы вне минимального радиуса к рабочему месту", Toast.LENGTH_LONG).show()
                            }
                            else{
                                Toast.makeText(requireContext(), "Начало работы", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
            LocationServices.getFusedLocationProviderClient(requireContext())
                .requestLocationUpdates(mLocationRequest, mLocationCallback, null)
        }
        else{
            requestLocationPermission()
        }
    }

    private fun hasLocationPermission()=
        EasyPermissions.hasPermissions(
            requireContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )

    private fun requestLocationPermission(){
        EasyPermissions.requestPermissions(
            requireActivity(),
            "Это приложение не может работать без локации",
            PERMISSION_LOCATION_REQUEST,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    }


    override fun onDestroyView() {
        _binding = null
        listViewModel.loading_status.removeObserver(listObservable)
        super.onDestroyView()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(requireActivity(), perms)){
            SettingsDialog.Builder(requireActivity()).build().show()
        }
        else{
            requestLocationPermission()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_LONG).show()
    }
}