package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.android.politicalpreparedness.BuildConfig
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListener
import com.example.android.politicalpreparedness.utils.Utils
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.util.Locale

class DetailFragment : Fragment(), RepresentativeListener {

    companion object {
        private val REQUEST_CODE_FOREANDBACKGROUND = 1
        private val REQUEST_CODE_FOREGROUND_ONLY = 2
        private val REQUEST_TURN_DEVICE_LOCATION_ON = 29
        //TODO: Add Constant for Location request
    }
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var binding: FragmentRepresentativeBinding
    private lateinit var viewModel: RepresentativeViewModel
    private lateinit var adapter: RepresentativeListAdapter

    //TODO: Declare ViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentRepresentativeBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val representativeViewModelFactory = RepresentativeViewModelFactory(RepRepository())
        viewModel = ViewModelProvider(this, representativeViewModelFactory).get(RepresentativeViewModel::class.java)
        binding.viewModel = viewModel
        fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())
        adapter = RepresentativeListAdapter(this)
        binding.recyclerRepresentatives.adapter = adapter
        binding.buttonSearch.setOnClickListener {
            //Timber.d("searching...")

            val address1 = binding.addressLine1.text
            val address2 = binding.addressLine2.text
            val state = binding.state.getItemAtPosition(binding.state.selectedItemPosition)
            val city = binding.city.text
            val zip = binding.zip.text

            val completeAddress = "$address2 $address1, $city, $state, $zip"
            viewModel.getRepresentative(completeAddress)
        }

        binding.buttonLocation.setOnClickListener {
            if (checkLocationPermissions()) {
                checkLocationSettings()
            }
        }

        viewModel.repList.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        viewModel.address.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.getRepresentatives(it)
            }
        })

        viewModel.status.observe(viewLifecycleOwner, Observer {
            it.let {
                when(it){
                    RepresentativeViewModel.RepAPIStatus.LOADING -> binding.repLoadingBar.visibility = View.VISIBLE
                    RepresentativeViewModel.RepAPIStatus.DONE->binding.repLoadingBar.visibility = View.GONE
                    RepresentativeViewModel.RepAPIStatus.ERROR->{
                        binding.repLoadingBar.visibility = View.GONE
                        Utils.useSnackBar(requireActivity().findViewById(android.R.id.content), getString(R.string.loading_error_rep))
                    }
                }
            }
        })


        //TODO: Establish bindings

        //TODO: Define and assign Representative adapter

        //TODO: Populate Representative adapter

        //TODO: Establish button listeners for field and location search

        return binding.root
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            REQUEST_CODE_FOREANDBACKGROUND->
            if (grantResults.isNotEmpty() || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationSettings()
            } else {
                Snackbar.make(requireView(), "Permission Denied", Snackbar.LENGTH_LONG)
                        .setAction("Settings") {
                            startActivity(Intent().apply {
                                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            })

                        }.show()
            }
        }
        //TODO: Handle location permission result to get location on permission granted
    }

    private fun checkLocationSettings() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
            getCurrentLocation()

           // Timber.d("done. ${locationSettingsResponse}")
        }

        task.addOnFailureListener{ exception->
            if (exception is ResolvableApiException){
                //Timber.d("request denied.")
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(requireActivity(),
                            80)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
        task.addOnCanceledListener {
           // _viewModel.showErrorMessage.value="Request denied."

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.d("on activity resu;t/")
        when (requestCode) {
            80 -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        Timber.d("OK")
                        checkLocationSettings()
                    }
                }
            }
        }
    }

    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            true
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_CODE_FOREANDBACKGROUND)
            false
        }
    }

    private fun isPermissionGranted() : Boolean {

        val foregroundPermission = (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)

        //access background location permission handled for api 29 or + using runningQorLater boolean.
        val backgroundPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        return foregroundPermission && backgroundPermission

       // return false
        //TODO: Check if permission is already granted and return (true = granted, false = denied/other)
    }

    private fun geoCodeLocationAndInputFields(location: Location): Address {
        val address = geoCodeLocation(location.latitude, location.longitude)
        //Timber.d("got addresss: $address")
        viewModel.getAddress(address)
        return address
    }

    private fun getCurrentLocation() {
        try {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val lastKnownLocation = task.result

                   // Timber.d("last known location? $lastKnownLocation")
                    if (lastKnownLocation == null) {
                        makeLocationRequest()
                    }
                    lastKnownLocation?.let {
                        geoCodeLocationAndInputFields(it)
                    }
                } else {
//                    Timber.e("Exception:${task.exception}")
//                    Timber.d("Current location is null. Using defaults.")
                }
            }

        } catch (e: SecurityException) {
            //Timber.e(e)
        }
        //TODO: Get location from LocationServices
        //TODO: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
    }

    @SuppressLint("MissingPermission")
    private fun makeLocationRequest() {

        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 20 * 1000
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (location != null) {
                        val lat = location.latitude
                        val long = location.longitude
                        geoCodeLocationAndInputFields(location)
                        fusedLocationProviderClient.removeLocationUpdates(this)
                    }
                }
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun geoCodeLocation(lat: Double, long: Double): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(lat, long, 1)
                .map { address ->
                    Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                }
                .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    override fun openUrl(url: String) {
        val webpage: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        }
    }

}