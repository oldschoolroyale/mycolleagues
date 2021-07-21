package com.brm.mycolleagues.service

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.location.Location
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog

class LocationHelper(val context: Context, val activity: Activity): EasyPermissions.PermissionCallbacks {
    companion object{
        private const val REQUEST_CHECK_SETTING = 1001
        private const val PERMISSION_LOCATION_REQUEST = 1
    }

     fun locationRequest(){
        REQUEST_CHECK_SETTING
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 2000

        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result = LocationServices.getSettingsClient(context)
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
                            resolvableApi.startResolutionForResult(activity, REQUEST_CHECK_SETTING)
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
                                Toast.makeText(context, "Вы вне минимального радиуса к рабочему месту", Toast.LENGTH_LONG).show()
                            }
                            else{
                                Toast.makeText(context, "Начало работы", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
            LocationServices.getFusedLocationProviderClient(context)
                    .requestLocationUpdates(mLocationRequest, mLocationCallback, null)
        }
        else{
            requestLocationPermission()
        }
    }

    private fun hasLocationPermission()=
            EasyPermissions.hasPermissions(
                    context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            )

    private fun requestLocationPermission(){
        EasyPermissions.requestPermissions(
                activity,
                "Это приложение не может работать без локации",
                PERMISSION_LOCATION_REQUEST,
                android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(activity, perms)){
            SettingsDialog.Builder(activity).build().show()
        }
        else{
            requestLocationPermission()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Toast.makeText(context, "Permission Granted", Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}