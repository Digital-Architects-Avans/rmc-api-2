package com.digitalarchitects.data.requests

import com.digitalarchitects.data.vehicle.EngineType
import kotlinx.serialization.Serializable

@Serializable
data class CreateVehicleRequest(
    val userId: String,
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
    val availability: Boolean,
    val address: String,
    val description: String
)