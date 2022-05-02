package com.alashow.datmusic.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Radio(
    val name: String,
    val url: String,
    val favicon: String,
): Parcelable

@Parcelize
data class RadioList(
    val radios: List<Radio>
): Parcelable
