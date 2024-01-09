package com.digitalarchitects.route.rental

import com.digitalarchitects.data.rental.Rental
import com.digitalarchitects.data.rental.RentalDataSource
import com.digitalarchitects.data.rental.RentalStatus
import com.digitalarchitects.data.requests.CreateRentalRequest
import com.digitalarchitects.data.requests.UpdateRentalRequest
import com.digitalarchitects.data.user.MongoUserDataSource
import com.digitalarchitects.data.user.UserDataSource
import com.digitalarchitects.data.vehicle.VehicleDataSource
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.rentalRoutes(
    userDataSource: UserDataSource,
    rentalDataSource: RentalDataSource,
    vehicleDataSource: VehicleDataSource
) {
    authenticate {
        get("/rentals") {
            val rentals = rentalDataSource.getRentals()
            call.respond(rentals)
        }

        get("/rentals/{id}") {
            val id = call.parameters["id"] ?: run {
                call.respondText("Invalid rental id", status = HttpStatusCode.BadRequest)
                return@get
            }

            try {
                val rental = rentalDataSource.getRentalById(id)

                if (rental != null) {
                    call.respond(rental)
                } else {
                    call.respondText("Rental with id: $id not found", status = HttpStatusCode.NotFound)
                }

            } catch (e: IllegalArgumentException) {
                call.respondText(e.message ?: "Invalid rental id format", status = HttpStatusCode.BadRequest)
            } catch (e: Exception) {
                e.printStackTrace()
                call.respondText("Unexpected error: $e", status = HttpStatusCode.InternalServerError)
            }
        }

        get("/rentals/user/{userId}") {
            val userId = call.parameters["userId"] ?: run {
                call.respondText("Invalid userId", status = HttpStatusCode.BadRequest)
                return@get
            }

            try {
                val rental = rentalDataSource.getRentalsByUserId(userId)

                if (rental.isNotEmpty()) {
                    call.respond(rental)
                } else {
                    call.respondText("No Rentals found for user with id: $userId", status = HttpStatusCode.NotFound)
                }

            } catch (e: Exception) {
                call.respondText("An error occurred: $e", status = HttpStatusCode.InternalServerError)
            }
        }

        get("/rentals/vehicle/{vehicleId}") {
            val vehicleId = call.parameters["vehicleId"] ?: run {
                call.respondText("Invalid vehicleId", status = HttpStatusCode.BadRequest)
                return@get
            }

            try {
                val rental = rentalDataSource.getRentalsByVehicleId(vehicleId)

                if (rental.isNotEmpty()) {
                    call.respond(rental)
                } else {
                    call.respondText(
                        "No Rentals found for vehicle with id: $vehicleId",
                        status = HttpStatusCode.NotFound
                    )
                }

            } catch (e: Exception) {
                call.respondText("An error occurred: $e", status = HttpStatusCode.InternalServerError)
            }
        }

        post("/rentals") {
            try {
                val request = call.receiveNullable<CreateRentalRequest>() ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                // Check if the user exists in the database
                val user = userDataSource.getUserById(request.userId)
                if (user == null) {
                    call.respondText("User does not exist in the database", status = HttpStatusCode.BadRequest)
                    return@post
                }

                // Check if the vehicle exists in the database
                val vehicle = vehicleDataSource.getVehicleById(request.vehicleId)
                if (vehicle == null) {
                    call.respondText("Vehicle does not exist in the database", status = HttpStatusCode.BadRequest)
                    return@post
                }

                val rental = Rental(
                    vehicleId = request.vehicleId,
                    userId = request.userId,
                    date = request.date,
                    price = request.price,
                    latitude = request.latitude,
                    longitude = request.longitude,
                    status = RentalStatus.PENDING,
                    distanceTravelled = request.distanceTravelled,
                    score = request.score
                )

                val rentalId  = rentalDataSource.insertRental(rental)

                if (rentalId != null) {
                    rentalDataSource.getRentalById(rentalId)?.let { response ->
                        call.respond(HttpStatusCode.Created, response)
                    } ?: run {
                        call.respondText("Failed to insert rental", status = HttpStatusCode.InternalServerError)
                    }
                } else {
                    call.respondText("Failed to insert rental", status = HttpStatusCode.InternalServerError)
                }
            } catch (e: Exception) {
                call.respondText("An error occurred: $e", status = HttpStatusCode.InternalServerError)
            }
        }

        put("/rentals/{rentalId}") {
            val rentalId = call.parameters["rentalId"] ?: run {
                call.respondText("Invalid rentalId", status = HttpStatusCode.BadRequest)
                return@put
            }

            try {
                val updatedRental = call.receive<UpdateRentalRequest>()

                // Check if the user exists in the database
                val user = userDataSource.getUserById(updatedRental.userId)
                if (user == null) {
                    call.respondText("User does not exist in the database", status = HttpStatusCode.BadRequest)
                }

                // Check if the vehicle exists in the database
                val vehicle = vehicleDataSource.getVehicleById(updatedRental.vehicleId)
                if (vehicle == null) {
                    call.respondText("Vehicle does not exist in the database", status = HttpStatusCode.BadRequest)
                }

                val rentalIsUpdated = rentalDataSource.updateRental(rentalId, updatedRental)

                if (rentalIsUpdated) {
                    call.respondText("Vehicle updated successfully", status = HttpStatusCode.OK)
                } else {
                    call.respondText("Rental with id: $rentalId not found", status = HttpStatusCode.NotFound)
                }

            } catch (e: Exception) {
                call.respondText("An error occurred: $e", status = HttpStatusCode.InternalServerError)
            }
        }

        get("/rentals/{rentalId}/{status}") {
            val rentalId = call.parameters["rentalId"] ?: run {
                call.respondText("Invalid rentalId", status = HttpStatusCode.BadRequest)
                return@get
            }

            val status = call.parameters["status"] ?: run {
                call.respondText("Invalid status", status = HttpStatusCode.BadRequest)
                return@get
            }

            try {
                val rentalStatus = RentalStatus.valueOf(status)
                if (rentalStatus !in RentalStatus.entries.toTypedArray()) {
                    call.respondText("Invalid rental status", status = HttpStatusCode.BadRequest)
                    return@get
                }

                val statusIsUpdated = rentalDataSource.setRentalStatus(rentalId, rentalStatus)

                if (statusIsUpdated) {
                    call.respondText("Vehicle status successfully updated to $status", status = HttpStatusCode.OK)
                } else {
                    call.respondText("Failed to update rental status", status = HttpStatusCode.InternalServerError)
                }
            } catch (e: IllegalArgumentException) {
                call.respondText("Invalid rental status", status = HttpStatusCode.BadRequest)
            } catch (e: Exception) {
                call.respondText("An error occurred: $e", status = HttpStatusCode.InternalServerError)
            }
        }


        delete("/rentals/{id}") {
            val id = call.parameters["id"] ?: run {
                call.respondText("Invalid rental id", status = HttpStatusCode.BadRequest)
                return@delete
            }

            try {
                val deleted = rentalDataSource.deleteRentalById(id)
                if (deleted) {
                    call.respondText("Rental deleted successfully", status = HttpStatusCode.OK)
                } else {
                    call.respondText("Rental not found", status = HttpStatusCode.NotFound)
                }
            } catch (e: IllegalArgumentException) {
                call.respondText(e.message ?: "Invalid rental id format", status = HttpStatusCode.BadRequest)
            } catch (e: Exception) {
                e.printStackTrace()
                call.respondText("Unexpected error: $e", status = HttpStatusCode.InternalServerError)
            }
        }
    }
}
