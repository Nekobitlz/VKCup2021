package com.nekobitlz.taxi.repository

import com.nekobitlz.taxi.data.StreetMap
import io.reactivex.rxjava3.core.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitService {

    private val nominatimApi: NominatimApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()

        nominatimApi = retrofit.create(NominatimApi::class.java)
    }

    fun getAddress(lat: Double, lon: Double): Single<StreetMap> {
        return nominatimApi.getAddress(lat, lon)
    }

    fun search(query: String): Single<List<StreetMap>> {
        return nominatimApi.search(query)
    }
}