package com.digitalarchitects.data.requests

import com.digitalarchitects.data.vehicle.EngineType
import kotlinx.serialization.Serializable

@Serializable
data class UpdateVehicleRequest(
    val userId: Int,
    val brand: String,
    val model: String,
    val year: Int,
    val vehicleClass: String,
    val engineType: EngineType,
    val licensePlate: String,
    val imgLink: Int,
    val latitude: Float,
    val longitude: Float,
    val price: Double,
    val availability: Boolean
)