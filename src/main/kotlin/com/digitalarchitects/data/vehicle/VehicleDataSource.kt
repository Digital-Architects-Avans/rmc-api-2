package com.digitalarchitects.data.vehicle

import com.digitalarchitects.data.requests.UpdateVehicleRequest

interface VehicleDataSource {

    suspend fun getVehicleByLicensePlate(licensePlate: String): Vehicle?
    suspend fun insertVehicle(vehicle: Vehicle): String?
    suspend fun getVehicles(): List<Vehicle>
    suspend fun getVehicleById(vehicleId: String): Vehicle?
    suspend fun updateVehicle(vehicleId: String, updatedVehicle: UpdateVehicleRequest): Boolean
    suspend fun deleteVehicleById(vehicleId: String): Boolean
}