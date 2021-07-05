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
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.jakewharton.rxbinding4.widget.TextViewTextChangeEvent
import com.jakewharton.rxbinding4.widget.textChangeEvents
import com.nekobitlz.taxi.R
import com.nekobitlz.taxi.databinding.FragmentTaxiBinding
import com.nekobitlz.taxi.view.adapter.ChooseAddressAdapter
import com.nekobitlz.vkcup.commons.gone
import com.nekobitlz.vkcup.commons.visible
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import org.osmdroid.api.IMapController
import org.osmdroid.events.DelayedMapListener
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import java.util.concurrent.TimeUnit


class TaxiFragment : Fragment(R.layout.fragment_taxi), LocationListener {

    private var _binding: FragmentTaxiBinding? = null
    private val binding: FragmentTaxiBinding
        get() = _binding!!

    private var locationManager: LocationManager? = null
    private lateinit var viewModel: TaxiViewModel
    private lateinit var mapController: IMapController

    private val adapter = ChooseAddressAdapter()
    private lateinit var sheetBehaviour: BottomSheetBehavior<LinearLayout>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTaxiBinding.bind(view)

        initViewModel()
        initMap()
        initBottomSheet()
        binding.apply {
            btnCurrentLocation.setOnClickListener {
                requestLocation()
            }
        }
    }

    private fun initBottomSheet() = with(binding) {
        sheetBehaviour = BottomSheetBehavior.from(chooseAddrssesBottomSheet.root)
        chooseAddrssesBottomSheet.apply {
            root.doOnLayout {
                val showing: View = (it as ViewGroup).getChildAt(1)
                sheetBehaviour.setPeekHeight(showing.bottom)
            }
            sheetBehaviour.addBottomSheetCallback(object : BottomSheetCallback() {
                override fun onStateChanged(view: View, newState: Int) {
                    when (newState) {
                        STATE_EXPANDED -> {
                            appBar.visible()
                            btnCurrentLocation.gone()
                        }
                        STATE_COLLAPSED -> {
                            appBar.gone()
                            btnCurrentLocation.visible()
                            pickCityView.etTo.clearFocus()
                            pickCityView.etFrom.clearFocus()
                        }
                    }
                }

                override fun onSlide(view: View, v: Float) {}
            })
            toolbar.setNavigationOnClickListener {
                sheetBehaviour.state = STATE_COLLAPSED
            }
            rvAddresses.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = this@TaxiFragment.adapter
            }
            pickCityView.etFrom.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) sheetBehaviour.state = STATE_EXPANDED
            }
            pickCityView.etTo.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) sheetBehaviour.state = STATE_EXPANDED
            }
            subscribeToSearch(pickCityView.etFrom)
            subscribeToSearch(pickCityView.etTo)
        }
    }

    private fun initMap() {
        locationManager = requireActivity()
            .getSystemService(Context.LOCATION_SERVICE) as LocationManager
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
    }

    private fun initViewModel(): TaxiViewModel {
        viewModel = ViewModelProvider(this).get(TaxiViewModel::class.java)
        viewModel.addressFromState.observe(viewLifecycleOwner, {
            when (it) {
                AddressFromState.Loading -> {
                    binding.chooseAddrssesBottomSheet.pickCityView.apply {
                        etFrom.setText("")
                        progressBarFrom.visible()
                    }
                }
                is AddressFromState.Success -> {
                    binding.chooseAddrssesBottomSheet.pickCityView.apply {
                        etFrom.setText(it.address)
                        progressBarFrom.gone()
                    }
                }
                is AddressFromState.Error -> {
                    binding.chooseAddrssesBottomSheet.pickCityView.apply {
                        etFrom.setText("Ошибка")
                        progressBarFrom.gone()
                    }
                }
            }
        })
        viewModel.searchState.observe(viewLifecycleOwner, {
            when (it) {
                AddressSearchState.Loading -> {
                    binding.chooseAddrssesBottomSheet.apply {
                        progressBar.visible()
                        rvAddresses.gone()
                    }
                }
                is AddressSearchState.Success -> {
                    binding.chooseAddrssesBottomSheet.apply {
                        progressBar.gone()
                        rvAddresses.visible()
                        adapter.submitList(it.addresses)
                    }
                }
                is AddressSearchState.Error -> {
                    binding.chooseAddrssesBottomSheet.apply {
                        progressBar.gone()
                        rvAddresses.gone()
                    }
                }
            }
        })
        return viewModel
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

    private fun subscribeToSearch(editText: EditText) {
        editText.textChangeEvents()
            .skipInitialValue()
            .distinctUntilChanged()
            .debounce(200, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { event: TextViewTextChangeEvent? ->
                val text = event?.text
                viewModel.search(text.toString())
            }
    }

    companion object {
        private const val REQUEST_PERMISSION = 101
        private const val ZOOM_LEVEL = 20.0
        private val MOSCOW_CENTER_LOCATION = GeoPoint(55.751244, 37.618423)
    }
}