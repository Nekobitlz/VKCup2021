package com.nekobitlz.taxi.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nekobitlz.taxi.data.StreetMap
import com.nekobitlz.taxi.repository.RetrofitService
import com.nekobitlz.taxi.view.adapter.ChooseAddressItem
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

class TaxiViewModel : ViewModel() {

    private val retrofitService: RetrofitService = RetrofitService()

    private val _addressFromState = MutableLiveData<AddressFromState>()
    val addressFromState: LiveData<AddressFromState>
        get() = _addressFromState

    private val _searchState = MutableLiveData<AddressSearchState>()
    val searchState: LiveData<AddressSearchState>
        get() = _searchState

    fun getAddress(lat: Double, lon: Double) {
        _addressFromState.value = AddressFromState.Loading
        retrofitService.getAddress(lat, lon)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _addressFromState.value = AddressFromState.Success(it.displayName ?: "")
            }, {
                _addressFromState.value = AddressFromState.Error(it)
            })
    }

    fun search(query: String) {
        _searchState.value = AddressSearchState.Loading
        retrofitService.search(query)
            .observeOn(AndroidSchedulers.mainThread())
            .map { list ->
                list.map {
                    ChooseAddressItem(
                        it.address?.shop ?: it.address?.road ?: it.address?.town
                        ?: it.displayName?.split(",")?.firstOrNull() ?: "",
                        it.displayName ?: ""
                    )
                }
            }
            .subscribe({
                _searchState.value = AddressSearchState.Success(it ?: listOf())
            }, {
                _searchState.value = AddressSearchState.Error(it)
            })
    }
}

sealed class AddressFromState {
    object Loading : AddressFromState()
    class Success(val address: String) : AddressFromState()
    class Error(val throwable: Throwable) : AddressFromState()
}

sealed class AddressSearchState {
    object Loading : AddressSearchState()
    class Success(val addresses: List<ChooseAddressItem>) : AddressSearchState()
    class Error(val throwable: Throwable) : AddressSearchState()
}