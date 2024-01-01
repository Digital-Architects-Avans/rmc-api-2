package com.digitalarchitects.data.vehicle

import com.digitalarchitects.data.requests.UpdateUserRequest
import com.digitalarchitects.data.requests.UpdateVehicleRequest
import com.digitalarchitects.data.user.User
import org.bson.types.ObjectId
import org.litote.kmongo.Id
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class MongoVehicleDataSource(
    db: CoroutineDatabase
) : VehicleDataSource {

    private val vehicles = db.getCollection<Vehicle>()

    override suspend fun getVehicleByLicensePlate(licensePlate: String): Vehicle? {
        return vehicles.findOne(Vehicle::licensePlate eq licensePlate)
    }

    override suspend fun insertVehicle(vehicle: Vehicle): Boolean {
        return vehicles.insertOne(vehicle).wasAcknowledged()
    }

    override suspend fun getVehicles(): List<Vehicle> {
        return vehicles.find().toList()
    }

    override suspend fun getVehicleById(id: String): Vehicle? {
        return vehicles.findOne(Vehicle::vehicleId eq id)
    }

    override suspend fun updateVehicle(id: String, updatedVehicle: UpdateVehicleRequest): Boolean {
        val vehicle = getVehicleById(id) ?: return false

        val updatedDocument = vehicle.copy(
            userId = updatedVehicle.userId,
            brand = updatedVehicle.brand,
            model = updatedVehicle.model,
            year = updatedVehicle.year,
            vehicleClass = updatedVehicle.vehicleClass,
            engineType = updatedVehicle.engineType,
            licensePlate = updatedVehicle.licensePlate,
            imgLink = updatedVehicle.imgLink,
            latitude = updatedVehicle.latitude,
            longitude = updatedVehicle.longitude,
            price = updatedVehicle.price,
            availability = updatedVehicle.availability
        )

        val updateResult = vehicles.replaceOne(Vehicle::vehicleId eq id, updatedDocument)

        return updateResult.wasAcknowledged()
    }

    override suspend fun deleteVehicleById(id: String): Boolean {
        return vehicles.deleteOneById(id).wasAcknowledged()
    }
}