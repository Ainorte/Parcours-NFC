package com.mbds.bpst.parcoursnfc.data

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng

class LocationConverters {
    @TypeConverter
    fun fromLatLngToString(value: LatLng): String {
        return value.latitude.toString() + ';' + value.longitude.toString()
    }

    @TypeConverter
    fun fromStringToLatLng(value: String): LatLng {
        return LatLng(value.substringBefore(';').toDouble(), value.substringAfter(';').toDouble())
    }

}