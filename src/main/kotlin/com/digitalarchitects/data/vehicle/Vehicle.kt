package com.digitalarchitects.data.vehicle

import com.digitalarchitects.data.ObjectIdAsStringSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import org.litote.kmongo.newId

enum class EngineType{
    ICE, BEV, FCEV
}

@Serializable
data class Vehicle(
    @SerialName("_id") @Serializable(with = ObjectIdAsStringSerializer::class) val objectId: Id<Vehicle> = newId(),
    @SerialName("vehicleId") val vehicleId: String = objectId.toString(),
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
