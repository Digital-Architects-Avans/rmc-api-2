package com.digitalarchitects.data.rental

import com.digitalarchitects.data.requests.UpdateRentalRequest
import com.mongodb.client.model.Filters.*
import com.mongodb.client.model.Updates.set
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.bson.types.ObjectId
import org.litote.kmongo.Id
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.id.toId

class MongoRentalDataSource(
    db: CoroutineDatabase
): RentalDataSource {

    private val rentals = db.getCollection<Rental>()
    override suspend fun getRentalsByVehicleId(vehicleId: String): List<Rental> {
        return rentals.find(Rental::vehicleId eq vehicleId).toList()
    }

    override suspend fun getRentalsByUserId(userId: String): List<Rental> {
        return rentals.find(Rental::userId eq userId).toList()
    }

    override suspend fun insertRental(rental: Rental): String? {
        val result = rentals.insertOne(rental)
        return if (result.wasAcknowledged()) {
            rental.rentalId
        } else {
            null
        }
    }

    override suspend fun getRentals(): List<Rental> {
        checkRentalDataForExpiration()
        return rentals.find().toList()
    }

    private suspend fun checkRentalDataForExpiration() {
        val today = Clock.System.now().toLocalDateTime(
            TimeZone.currentSystemDefault()
        ).date

        val filter = and(
            eq("status", RentalStatus.PENDING),
            lt("date", today)
        )

        val update = set("status", RentalStatus.CANCELLED)

        rentals.updateMany(filter, update)
    }

    override suspend fun getRentalById(rentalId: String): Rental? {
        return rentals.findOne(Rental::rentalId eq rentalId)
    }

    override suspend fun updateRental(rentalId: String, updatedRental: UpdateRentalRequest): Boolean {
        val rental = getRentalById(rentalId) ?: return false

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

        val updateResult = rentals.replaceOne(Rental::rentalId eq rentalId, updatedDocument)

        return updateResult.wasAcknowledged()
    }

    override suspend fun setRentalStatus(rentalId: String, status: RentalStatus): Boolean {
        val rental = getRentalById(rentalId) ?: return false

        val updatedDocument = rental.copy(
            status = status
        )

        val updateResult = rentals.replaceOne(Rental::rentalId eq rentalId, updatedDocument)

        return updateResult.wasAcknowledged()
    }


    override suspend fun deleteRentalById(rentalId: String): Boolean {
        try {
            val rentalIdAsId: Id<Rental> = ObjectId(rentalId).toId()
            val result = rentals.deleteOne(Rental::rentalId eq rentalId)
            println("Deleting rental with userId $rentalId, rentalIdAsId $rentalIdAsId, result $result")
            return result.wasAcknowledged()
        } catch (e: Exception) {
            // Log the exception or print its details
            println("Error deleting rental with ID $rentalId: ${e.message}")
            return false
        }
    }

}