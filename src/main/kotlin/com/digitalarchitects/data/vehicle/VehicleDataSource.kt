package com.digitalarchitects.data.vehicle

import com.digitalarchitects.data.requests.UpdateVehicleRequest

interface VehicleDataSource {

    suspend fun getVehicleByLicensePlate(licensePlate: String): Vehicle?
    suspend fun insertVehicle(vehicle: Vehicle): Boolean
    suspend fun getVehicles(): List<Vehicle>
    suspend fun getVehicleById(id: String): Vehicle?
    suspend fun updateVehicle(id: String, updatedVehicle: UpdateVehicleRequest): Boolean
    suspend fun deleteVehicleById(id: String): Boolean
}