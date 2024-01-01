package com.digitalarchitects.route.rental

import com.digitalarchitects.data.rental.Rental
import com.digitalarchitects.data.rental.RentalDataSource
import com.digitalarchitects.data.rental.RentalStatus
import com.digitalarchitects.data.requests.CreateRentalRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.rentalRoutes(
    rentalDataSource: RentalDataSource
) {

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
        val userId = call.parameters["userId"]?.toIntOrNull() ?: run {
            call.respondText("Invalid userId", status = HttpStatusCode.BadRequest)
            return@get
        }

        try {
            val rental = rentalDataSource.getRentalsByUserId(userId)

            if (rental != null) {
                call.respond(rental)
            } else {
                call.respondText("Rental not found for user with id: $userId", status = HttpStatusCode.NotFound)
            }

        } catch (e: Exception) {
            call.respondText("An error occurred: $e", status = HttpStatusCode.InternalServerError)
        }
    }

    get("/rentals/vehicle/{vehicleId}") {
        val vehicleId = call.parameters["vehicleId"]?.toIntOrNull() ?: run {
            call.respondText("Invalid vehicleId", status = HttpStatusCode.BadRequest)
            return@get
        }

        try {
            val rental = rentalDataSource.getRentalsByVehicleId(vehicleId)

            if (rental != null) {
                call.respond(rental)
            } else {
                call.respondText("Rental not found for vehicle with id: $vehicleId", status = HttpStatusCode.NotFound)
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

            val inserted = rentalDataSource.insertRental(rental)

            if (inserted) {
                call.respondText("Rental inserted successfully", status = HttpStatusCode.Created)
            } else {
                call.respondText("Failed to insert rental", status = HttpStatusCode.InternalServerError)
            }
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
