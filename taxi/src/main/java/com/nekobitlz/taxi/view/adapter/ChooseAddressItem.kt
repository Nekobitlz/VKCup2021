package com.nekobitlz.taxi.view.adapter

import com.nekobitlz.taxi.data.StreetMap

data class ChooseAddressItem(
    val map: StreetMap,
) {
    val address: String
        get() = map.displayName ?: ""

    val place: String
        get() = map.address?.shop
            ?: map.address?.road
            ?: map.displayName?.split(",")?.firstOrNull()
            ?: ""

}