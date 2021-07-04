package com.nekobitlz.taxi.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.nekobitlz.taxi.R
import com.nekobitlz.taxi.databinding.FragmentTaxiBinding
import org.osmdroid.api.IMapController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController


class TaxiFragment : Fragment(R.layout.fragment_taxi), LocationListener {

    private var _binding: FragmentTaxiBinding? = null
    private val binding: FragmentTaxiBinding
        get() = _binding!!

    private var locationManager: LocationManager? = null
    private lateinit var mapController: IMapController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTaxiBinding.bind(view)

        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        requestLocation()

        binding.mapView.apply {
            setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
            mapController = controller
        }
        mapController.setZoom(20.0)
        mapController.setCenter(MOSCOW_CENTER_LOCATION)

        binding.btnCurrentLocation.setOnClickListener {
            requestLocation()
        }
    }

    private fun requestLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                REQUEST_PERMISSION
            )
        } else {
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, this)
        }
    }

    override fun onLocationChanged(location: Location) {
        val center = GeoPoint(location.latitude, location.longitude)
        mapController.animateTo(center)
        locationManager?.removeUpdates(this)
        Log.d("TAG_LOC", "$location")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_PERMISSION -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        context,
                        getString(R.string.no_permission_error),
                        Toast.LENGTH_LONG
                    ).show()
                }
                return
            }
        }
    }

    companion object {
        private const val REQUEST_PERMISSION = 101
        private val MOSCOW_CENTER_LOCATION = GeoPoint(55.751244, 37.618423)
    }
}