package com.nekobitlz.taxi.repository

import com.nekobitlz.taxi.data.StreetMap
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url


interface NominatimApi {

    @GET
    fun getResponseUrl(@Url url: String?): Single<List<StreetMap>>

    @GET("/search")
    fun search(@Query("q") query: String, @Query("format") format: String): Single<List<StreetMap>>

    @GET("/reverse")
    fun getAddress(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("zoom") zoom: Int = 20,
        @Query("format") format: String = "json",
        @Query("addressdetails") addressDetails: Int = 1,
    ): Single<StreetMap>
}