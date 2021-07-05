package com.nekobitlz.taxi.viewmodel

import android.app.Application
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nekobitlz.taxi.R
import com.nekobitlz.taxi.data.StreetMap
import com.nekobitlz.taxi.repository.RetrofitService
import com.nekobitlz.taxi.view.adapter.ChooseAddressItem
import com.nekobitlz.vkcup.commons.SingleLiveData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline
import kotlin.math.roundToInt

private const val PRICE_FOR_MINUTE = 10
private const val MIN_PRICE = 80

class TaxiViewModel(application: Application) : AndroidViewModel(application) {

    private val roadManager: RoadManager = OSRMRoadManager(application.applicationContext, "VKTaxi")
    private val retrofitService: RetrofitService = RetrofitService()

    private val _buildRoadEvent = SingleLiveData<BuildRoadState>()
    val buildRoadEvent: LiveData<BuildRoadState>
        get() = _buildRoadEvent

    private val _addressFromState = MutableLiveData<AddressState>()
    val addressFromState: LiveData<AddressState>
        get() = _addressFromState

    private val _addressToState = MutableLiveData<AddressState>()
    val addressToState: LiveData<AddressState>
        get() = _addressToState

    private val _searchState = MutableLiveData<AddressSearchState>()
    val searchState: LiveData<AddressSearchState>
        get() = _searchState

    fun getAddress(lat: Double, lon: Double) {
        _addressFromState.value = AddressState.Loading
        retrofitService.getAddress(lat, lon)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _addressFromState.value = AddressState.Success(it.displayName ?: "", it)
                val addressTo = _addressToState.value
                if (addressTo is AddressState.Success) {
                    val map = addressTo.map
                    buildRoad(
                        GeoPoint(lat, lon),
                        GeoPoint(map.lat!!.toDouble(), map.lon!!.toDouble())
                    )
                }
            }, {
                _addressFromState.value = AddressState.Error(it)
            })
    }

    fun search(query: String) {
        _searchState.value = AddressSearchState.Loading
        retrofitService.search(query)
            .observeOn(AndroidSchedulers.mainThread())
            .map { list -> list.map { ChooseAddressItem(it) } }
            .subscribe({
                _searchState.value = AddressSearchState.Success(it ?: listOf())
            }, {
                _searchState.value = AddressSearchState.Error(it)
            })
    }

    fun onAddressSelect(item: ChooseAddressItem, mapCenter: GeoPoint) {
        _addressToState.value = AddressState.Success(
            if (item.place.isNotBlank()) item.place else item.address,
            item.map
        )
        val endPoint = GeoPoint(item.map.lat!!.toDouble(), item.map.lon!!.toDouble())
        buildRoad(mapCenter, endPoint)
    }

    private fun buildRoad(mapCenter: GeoPoint, endPoint: GeoPoint) {
        Observable.fromCallable {
            _buildRoadEvent.postValue(BuildRoadState.Loading)
            val waypoints = ArrayList<GeoPoint>()
            waypoints.add(mapCenter)
            waypoints.add(endPoint)

            val road: Road = roadManager.getRoad(waypoints)
            if (road.mStatus != Road.STATUS_OK) {
                _buildRoadEvent.postValue(BuildRoadState.Error)
            }
            val price = maxOf((road.mDuration / 60 * PRICE_FOR_MINUTE).roundToInt(), MIN_PRICE)
            return@fromCallable RoadManager.buildRoadOverlay(
                road,
                ContextCompat.getColor(getApplication(), R.color.purple_color),
                10.0f
            ) to price
        }.subscribeOn(Schedulers.io())
            .map { BuildRoadState.Success(it.first, it.second) }
            .subscribe({
                _buildRoadEvent.postValue(it)
            }, {
                _buildRoadEvent.postValue(BuildRoadState.Error)
            })
    }
}

sealed class AddressState {
    object Loading : AddressState()
    class Success(val address: String, val map: StreetMap) : AddressState()
    class Error(val throwable: Throwable) : AddressState()
}

sealed class AddressSearchState {
    object Loading : AddressSearchState()
    class Success(val addresses: List<ChooseAddressItem>) : AddressSearchState()
    class Error(val throwable: Throwable) : AddressSearchState()
}

sealed class BuildRoadState {
    object Loading : BuildRoadState()
    data class Success(val polyline: Polyline, val price: Int) : BuildRoadState()
    object Error : BuildRoadState()
}