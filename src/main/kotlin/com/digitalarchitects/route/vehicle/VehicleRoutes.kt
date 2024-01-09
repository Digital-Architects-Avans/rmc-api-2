package com.digitalarchitects.route.vehicle

import com.digitalarchitects.data.requests.CreateVehicleRequest
import com.digitalarchitects.data.requests.UpdateVehicleRequest
import com.digitalarchitects.data.user.UserDataSource
import com.digitalarchitects.data.vehicle.Vehicle
import com.digitalarchitects.data.vehicle.VehicleDataSource
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.lang.reflect.InvocationTargetException

fun Route.vehicleRoutes(
    userDataSource: UserDataSource,
    vehicleDataSource: VehicleDataSource
) {
    authenticate {
        get("/vehicles") {
            val vehicles = vehicleDataSource.getVehicles()
            call.respond(vehicles)
        }

        get("/vehicles/{id}") {
            val id = call.parameters["id"] ?: run {
                call.respondText("Invalid vehicle id", status = HttpStatusCode.BadRequest)
                return@get
            }

            try {
                val vehicle = vehicleDataSource.getVehicleById(id)

                if (vehicle != null) {
                    call.respond(vehicle)
                } else {
                    call.respondText("Vehicle with id: $id not found", status = HttpStatusCode.NotFound)
                }

            } catch (e: IllegalArgumentException) {
                call.respondText(e.message ?: "Invalid vehicle id format", status = HttpStatusCode.BadRequest)
            } catch (e: Exception) {
                e.printStackTrace()
                call.respondText("Unexpected error: $e", status = HttpStatusCode.InternalServerError)
            }
        }

        get("/vehicles/licensePlate/{licensePlate}") {
            val licensePlate = call.parameters["licensePlate"] ?: run {
                call.respondText("Invalid license plate", status = HttpStatusCode.BadRequest)
                return@get
            }

            try {
                val vehicle = vehicleDataSource.getVehicleByLicensePlate(licensePlate)

                if (vehicle != null) {
                    call.respond(vehicle)
                } else {
                    call.respondText("Vehicle not found", status = HttpStatusCode.NotFound)
                }

            } catch (e: Exception) {
                call.respondText("An error occurred: $e", status = HttpStatusCode.InternalServerError)
            }
        }

        post("/vehicles") {
            try {
                val request = call.receiveNullable<CreateVehicleRequest>() ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                // Check if the user exists in the database
                val user = userDataSource.getUserById(request.userId)
                if (user == null) {
                    call.respondText("User does not exist in the database", status = HttpStatusCode.BadRequest)
                    return@post
                }

                if (vehicleDataSource.getVehicleByLicensePlate(request.licensePlate) != null) {
                    call.respond(
                        HttpStatusCode.Conflict,
                        "Vehicle with license plate ${request.licensePlate} already exists"
                    )
                    return@post
                }

                val vehicle = Vehicle(
                    userId = request.userId,
                    brand = request.brand,
                    model = request.model,
                    year = request.year,
                    vehicleClass = request.vehicleClass,
                    engineType = request.engineType,
                    licensePlate = request.licensePlate,
                    imgLink = request.imgLink,
                    latitude = request.latitude,
                    longitude = request.longitude,
                    price = request.price,
                    availability = request.availability
                )

                val inserted = vehicleDataSource.insertVehicle(vehicle)

                if (inserted) {
                    vehicleDataSource.getVehicleByLicensePlate(vehicle.licensePlate)?.let { response ->
                        call.respond(HttpStatusCode.Created, response)
                    } ?: run {
                        call.respondText("Failed to insert vehicle", status = HttpStatusCode.InternalServerError)
                    }
                } else {
                    call.respondText("Failed to insert vehicle", status = HttpStatusCode.InternalServerError)
                }
            } catch (e: Exception) {
                call.respondText("An error occurred: $e", status = HttpStatusCode.InternalServerError)
            }
        }

        put("/vehicles/{id}") {
            val id = call.parameters["id"] ?: run {
                call.respondText("Invalid vehicle id", status = HttpStatusCode.BadRequest)
                return@put
            }

            try {
                val updateVehicleRequest = call.receive<UpdateVehicleRequest>()

                // Check if the user exists in the database
                val user = userDataSource.getUserById(updateVehicleRequest.userId)
                if (user == null) {
                    call.respondText("User does not exist in the database", status = HttpStatusCode.BadRequest)
                }

                val updated = vehicleDataSource.updateVehicle(id, updateVehicleRequest)

                if (updated) {
                    call.respondText("Vehicle updated successfully", status = HttpStatusCode.OK)
                } else {
                    call.respondText("Vehicle not found", status = HttpStatusCode.NotFound)
                }

            } catch (e: InvocationTargetException) {
                call.respondText(e.message ?: "Invalid vehicle id format", status = HttpStatusCode.BadRequest)
            } catch (e: Exception) {
                e.printStackTrace()
                call.respondText("Unexpected error: $e", status = HttpStatusCode.InternalServerError)
            }
        }

        delete("/vehicles/{id}") {
            val id = call.parameters["id"] ?: run {
                call.respondText("Invalid vehicle id", status = HttpStatusCode.BadRequest)
                return@delete
            }

            try {
                val deleted = vehicleDataSource.deleteVehicleById(id)
                if (deleted) {
                    call.respondText("Vehicle deleted successfully", status = HttpStatusCode.OK)
                } else {
                    call.respondText("Vehicle not found", status = HttpStatusCode.NotFound)
                }
            } catch (e: IllegalArgumentException) {
                call.respondText(e.message ?: "Invalid vehicle id format", status = HttpStatusCode.BadRequest)
            } catch (e: Exception) {
                e.printStackTrace()
                call.respondText("Unexpected error: $e", status = HttpStatusCode.InternalServerError)
            }
        }
    }
}
