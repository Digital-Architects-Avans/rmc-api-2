package com.digitalarchitects.data.rental

import com.digitalarchitects.data.requests.UpdateRentalRequest
import org.bson.types.ObjectId

interface RentalDataSource {

    suspend fun getRentalsByVehicleId(vehicleId: String): List<Rental>
    suspend fun getRentalsByUserId(userId: String): List<Rental>
    suspend fun insertRental(rental: Rental): String?
    suspend fun getRentals(): List<Rental>
    suspend fun getRentalById(rentalId: String): Rental?
    suspend fun updateRental(rentalId: String, updatedRental: UpdateRentalRequest): Boolean
    suspend fun setRentalStatus(rentalId: String, status: RentalStatus): Boolean
    suspend fun deleteRentalById(rentalId: String): Boolean
}