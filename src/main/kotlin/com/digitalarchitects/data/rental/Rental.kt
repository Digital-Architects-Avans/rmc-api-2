package com.digitalarchitects.data.rental

import com.digitalarchitects.data.ObjectIdAsStringSerializer
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import org.litote.kmongo.newId

enum class RentalStatus(val status: String) {
    PENDING("PENDING"),
    APPROVED("APPROVED"),
    DENIED("DENIED"),
    CANCELLED("CANCELLED")
}

@Serializable
data class Rental(
    val vehicleId: Int,
    val userId: Int,
    val date: LocalDate,
    val price: Double,
    val latitude: Float,
    val longitude: Float,
    val status: RentalStatus,
    val distanceTravelled: Double,
    val score: Int,
    @SerialName("_id") @Serializable(with = ObjectIdAsStringSerializer::class) val objectId: Id<Rental> = newId(),
    @SerialName("rentalId") val rentalId: String = objectId.toString()
)