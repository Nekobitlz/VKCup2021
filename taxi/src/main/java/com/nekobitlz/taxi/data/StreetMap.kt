package com.nekobitlz.taxi.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StreetMap {
    @SerializedName("place_id")
    @Expose
    var placeId: Long? = null

    @SerializedName("licence")
    @Expose
    var licence: String? = null

    @SerializedName("osm_type")
    @Expose
    var osmType: String? = null

    @SerializedName("osm_id")
    @Expose
    var osmId: Long? = null

    @SerializedName("boundingbox")
    @Expose
    var boundingbox: List<String>? = null

    @SerializedName("lat")
    @Expose
    var lat: String? = null

    @SerializedName("lon")
    @Expose
    var lon: String? = null

    @SerializedName("display_name")
    @Expose
    var displayName: String? = null

    @SerializedName("class")
    @Expose
    var class_: String? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("importance")
    @Expose
    var importance: Double? = null

    @SerializedName("icon")
    @Expose
    var icon: String? = null

    @SerializedName("address")
    @Expose
    var address: Address? = null
}