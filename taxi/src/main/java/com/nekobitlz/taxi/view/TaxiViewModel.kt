package com.nekobitlz.taxi.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nekobitlz.taxi.repository.RetrofitService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

class TaxiViewModel : ViewModel() {

    private val retrofitService: RetrofitService = RetrofitService()

    private val _state = MutableLiveData<AddressFromState>()
    val state: LiveData<AddressFromState>
        get() = _state

    fun getAddress(lat: Double, lon: Double) {
        _state.value = AddressFromState.Loading
        retrofitService.getAddress(lat, lon)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _state.value = AddressFromState.Success(it.displayName ?: "")
            }, {
                _state.value = AddressFromState.Error(it)
            })
    }
}

sealed class AddressFromState {
    object Loading : AddressFromState()
    class Success(val address: String) : AddressFromState()
    class Error(val throwable: Throwable) : AddressFromState()
}