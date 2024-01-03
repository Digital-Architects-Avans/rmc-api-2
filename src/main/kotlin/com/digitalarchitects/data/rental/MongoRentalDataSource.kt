package com.digitalarchitects.data.rental

import com.digitalarchitects.data.requests.UpdateRentalRequest
import com.digitalarchitects.data.user.User
import org.bson.types.ObjectId
import org.litote.kmongo.Id
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

    override suspend fun insertRental(rental: Rental): Boolean {
        return rentals.insertOne(rental).wasAcknowledged()
    }

    override suspend fun getRentals(): List<Rental> {
        return rentals.find().toList()
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

    override suspend fun deleteRentalById(rentalId: String): Boolean {
        val rentalIdAsId: Id<User> = ObjectId(rentalId).toId()
        return rentals.deleteOneById(rentalIdAsId).wasAcknowledged()
    }

}