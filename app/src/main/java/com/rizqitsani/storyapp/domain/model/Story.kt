package com.rizqitsani.storyapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Story(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val photoUrl: String = "",
    val lat: Double? = null,
    val lon: Double? = null
) : Parcelable