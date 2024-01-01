package com.digitalarchitects.data.rental

import com.digitalarchitects.data.requests.UpdateRentalRequest
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class MongoRentalDataSource(
    db: CoroutineDatabase
): RentalDataSource {

    private val rentals = db.getCollection<Rental>()
    override suspend fun getRentalsByVehicleId(vehicleId: Int): Rental? {
        return rentals.findOne(Rental::vehicleId eq vehicleId)
    }

    override suspend fun getRentalsByUserId(userId: Int): Rental? {
        return rentals.findOne(Rental::userId eq userId)
    }

    override suspend fun insertRental(rental: Rental): Boolean {
        return rentals.insertOne(rental).wasAcknowledged()
    }

    override suspend fun getRentals(): List<Rental> {
        return rentals.find().toList()
    }

    override suspend fun getRentalById(id: String): Rental? {
        return rentals.findOne(Rental::rentalId eq id)
    }

    override suspend fun updateRental(id: String, updatedRental: UpdateRentalRequest): Boolean {
        val rental = getRentalById(id) ?: return false

        val updatedDocument = rental.copy(
            vehicleId = updatedRental.vehicleId,
            userId = updatedRental.userId,
            date = updatedRental.date,
            price = updatedRental.price,
            latitude = updatedRental.latitude,
            longitude = updatedRental.longitude,
            status = updatedRental.status,
            distanceTravelled = updatedRental.distanceTravelled,
            score = updatedRental.score
        )

        val updateResult = rentals.replaceOne(Rental::rentalId eq id, updatedDocument)

        return updateResult.wasAcknowledged()
    }

    override suspend fun deleteRentalById(id: String): Boolean {
        return rentals.deleteOneById(id).wasAcknowledged()
    }

}