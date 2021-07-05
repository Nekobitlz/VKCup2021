package com.nekobitlz.taxi.view

import android.Manifest
import android.annotation.SuppressLint
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
import androidx.lifecycle.ViewModelProvider
import com.nekobitlz.taxi.R
import com.nekobitlz.taxi.databinding.FragmentTaxiBinding
import com.nekobitlz.vkcup.commons.gone
import com.nekobitlz.vkcup.commons.visible
import org.osmdroid.api.IMapController
import org.osmdroid.events.DelayedMapListener
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
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

        val viewModel = ViewModelProvider(this).get(TaxiViewModel::class.java)
        viewModel.state.observe(viewLifecycleOwner, {
            when (it) {
                AddressFromState.Loading -> {
                    binding.pickCityView.apply {
                        etFrom.setText("")
                        progressBarFrom.visible()
                    }
                }
                is AddressFromState.Success -> {
                    binding.pickCityView.apply {
                        etFrom.setText(it.address)
                        progressBarFrom.gone()
                    }
                }
                is AddressFromState.Error -> {
                    binding.pickCityView.apply {
                        etFrom.setText("Ошибка")
                        progressBarFrom.gone()
                    }
                }
            }
        })

        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        requestLocation()

        binding.mapView.apply {
            setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
            setMultiTouchControls(true)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
            mapController = controller
            addMapListener(DelayedMapListener(object : MapListener {
                override fun onZoom(e: ZoomEvent): Boolean {
                    return true
                }

                override fun onScroll(e: ScrollEvent): Boolean {
                    val geoPoint = MOSCOW_CENTER_LOCATION
                    getMapCenter(geoPoint)
                    viewModel.getAddress(geoPoint.latitude, geoPoint.longitude)
                    return true
                }
            }, 1000))
        }
        mapController.setZoom(ZOOM_LEVEL)
        mapController.setCenter(MOSCOW_CENTER_LOCATION)

        binding.btnCurrentLocation.setOnClickListener {
            requestLocation()
        }
    }

    @SuppressLint("MissingPermission")
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

    @SuppressLint("MissingPermission")
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
        private const val ZOOM_LEVEL = 20.0
        private val MOSCOW_CENTER_LOCATION = GeoPoint(55.751244, 37.618423)
    }
}