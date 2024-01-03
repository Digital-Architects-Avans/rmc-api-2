package com.digitalarchitects.data.requests

import com.digitalarchitects.data.rental.RentalStatus
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class CreateRentalRequest(
    val vehicleId: String,
    val userId: String,
    val date: LocalDate,
    val price: Double,
    val latitude: Float,
    val longitude: Float,
    val status: RentalStatus,
    val distanceTravelled: Double,
    val score: Int
)